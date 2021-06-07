package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Environment, ProbateHeader}

/*======================================================================================
* Create a new Probate application as a professional user (e.g. solicitor)
======================================================================================*/

object EXUIProbateMC {

  val MinThinkTime = Environment.minThinkTimePROB
  val MaxThinkTime = Environment.maxThinkTimePROB

  val postcodeFeeder = csv("postcodes.csv").random

  val getXSRF =
    exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(Environment.baseDomain).saveAs("XSRFToken")))

  def healthcheck(path: String) =
    exec(http("XUI${service}_000_Healthcheck")
      .get(s"/api/healthCheck?path=${path}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("""{"healthState":true}""")))

  val activity =
    exec(http("XUI${service}_000_Activity")
      .get("/activity/cases/${caseId}/activity")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("content-type", "application/json")
      .header("sec-fetch-site", "same-site")
      .check(status.in(200, 403)))

  val configurationui =
    exec(http("XUI${service}_000_ConfigurationUI")
      .get("/external/configuration-ui/")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "*/*")
      .check(substring("ccdGatewayUrl")))

  val configJson =
    exec(http("XUI${service}_000_ConfigJson")
      .get("/assets/config/config.json")
      .header("accept", "application/json, text/plain, */*")
      .check(substring("caseEditorConfig")))

  val TsAndCs =
    exec(http("XUI${service}_000_TsAndCs")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("false")))

  val userDetails =
    exec(http("XUI${service}_000_UserDetails")
      .get("/api/user/details")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("userInfo")))

  val configUI =
    exec(http("XUI${service}_000_ConfigUI")
      .get("/external/config/ui")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("ccdGatewayUrl")))

  val isAuthenticated =
    exec(http("XUI${service}_000_IsAuthenticated")
      .get("/auth/isAuthenticated")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("true")))

  val profile =
    exec(http("XUI${service}_000_Profile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .check(jsonPath("$.user.idam.id").notNull))

  val monitoringTools =
    exec(http("XUI${service}_000_MonitoringTools")
      .get("/api/monitoring-tools")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(jsonPath("$.user.idam.id").notNull))

  val workAllocationGet =
    exec(http("XUI${service}_000_WorkAllocationGet")
      .get("/workallocation/searchForCompletable")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .check(substring("message"))
      .check(status.in(200, 401)))

  val workAllocationPost =
    exec(http("XUI${service}_000_WorkAllocationPost")
      .post("/workallocation/searchForCompletable")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"solicitorUpdateApplication","jurisdiction":"PROBATE","caseTypeId":"GrantOfRepresentation"}}"""))
      .check(substring("message"))
      .check(status.in(200, 401)))

  val casecreation =

    /*======================================================================================
    * Click the Create Case link
    ======================================================================================*/
    exec(_.set("caseId", "0")) // initialise the caseId to 0 for the activity calls

    .group("XUI${service}_040_CreateCase") {
      exec(http("XUI${service}_040_005_CaseFilter")
        .get("/cases/case-filter")
        .headers(ProbateHeader.probate_navigationHeader)
        .check(substring("HMCTS Manage cases")))

      .exec(configurationui)

      .exec(configJson)

      .exec(TsAndCs)

      .exec(userDetails)

      .exec(configUI)

      .exec(isAuthenticated)

      .exec(healthcheck("%2Fcases%2Fcase-filter"))

      .exec(activity)

      .exec(http("XUI${service}_040_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/json")
        //.header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(substring("PROBATE")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Jurisdiction = Manage Probate Applications; Case Type = Grant Of Representation; Event = Apply For Probate
    ======================================================================================*/

    .group("XUI${service}_050_SelectCaseType") {
      exec(healthcheck("%2Fcases%2Fcase-create%2FPROBATE%2FGrantOfRepresentation%2FsolicitorCreateApplication"))

      .exec(http("XUI${service}_050_005_StartApplication")
        .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        //.header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token_probate"))
        .check(jsonPath("$.id").is("solicitorCreateApplication")))

      .exec(healthcheck("%2Fcases%2Fcase-create%2FPROBATE%2FGrantOfRepresentation%2FsolicitorCreateApplication%2FsolicitorCreateApplicationsolicitorCreateApplicationPage1"))

      .exec(profile)

      .exec(getXSRF)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Save & Continue
    ======================================================================================*/

    .feed(Feeders.createCaseData)
      .group("XUI${service}_060_CreateApplication") {
        exec(http("XUI${service}_060_005_CreateApplication")
          .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage1")
          .headers(ProbateHeader.probate_commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("content-type", "application/json")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/probate/ProbateCreateApplication1.json"))
          .check(substring("""{"data":{}""")))

        .exec(getXSRF)

        .exec(http("XUI${service}_060_010_CreateApplicationDraft")
          .post("/data/internal/case-types/GrantOfRepresentation/drafts/")
          .headers(ProbateHeader.probate_commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-draft-create.v2+json;charset=UTF-8")
          .header("content-type", "application/json")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/probate/ProbateCreateApplication1Draft.json"))
          .check(jsonPath("$.id").saveAs("draftId")))
      }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Postcode Lookup
    ======================================================================================*/

    .group("XUI${service}_070_AddressLookup") {
      feed(postcodeFeeder)
        .exec(http("XUI${service}_070_AddressLookup")
          .get("/api/addresses?postcode=${postcode}")
          .headers(ProbateHeader.probate_commonHeader)
          .header("accept", "application/json")
          .header("content-type", "application/json")
          .check(jsonPath("$.totalresults").ofType[Int].gt(0))
          .check(regex(""""BUILDING_(?:.+)" : "(.+?)",\n      "THOROUGHFARE_NAME" : "(.+?)",\n      "POST_TOWN" : "(.+?)",\n      "POSTCODE" : "(.+?)",\n      """")
            .ofType[(String, String, String, String)].findRandom.saveAs("addressLines")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Complete the basic details for the application
    ======================================================================================*/

    .group("XUI${service}_080_CreateApplication2") {
      exec(http("XUI${service}_080_005_CreateApplication2")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage2")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("content-type", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/probate/ProbateCreateApplication2.json")))

      .exec(http("XUI${service}_080_010_Draft")
        .put("/data/internal/case-types/GrantOfRepresentation/drafts/${draftId}")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-draft-update.v2+json;charset=UTF-8")
        .header("content-type", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/probate/ProbateCreateApplication2Draft.json")))

      .exec(profile)

      .exec(monitoringTools)
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers - Click Save & Continue
    ======================================================================================*/

    .group("XUI${service}_090_CaseSubmitted") {
      exec(http("XUI${service}_090_005_CaseSubmitted")
        .post("/data/case-types/GrantOfRepresentation/cases?ignore-warning=false")
        .headers(ProbateHeader.probate_commonHeader)
        //MIGHT NEED TO ADD TWO ADDITIONAL HEADERS FROM THIS CALL ONWARDS
        //request-context: appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a
        //request-id: |fB7IE.wzkzc
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("content-type", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/probate/ProbateCaseSubmitted.json"))
        .check(jsonPath("$.id").optional.saveAs("caseId")))

      .exec(workAllocationGet)

      .exec(healthcheck("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI${service}_090_010_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("content-type", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.state.name").is("Application created (deceased details)"))
        .check(regex("""Add Probate practitioner details(?:.+?)title=."COMPLETED."(?:.+?)Add deceased details""")))

      .exec(activity)
    }
    
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Click 'Add deceased details' link
  ======================================================================================*/

  .group("XUI${service}_100_AddDeceasedDetails") {
    exec(http("XUI${service}_100_005_DeceasedDetails")
      .get("/cases/case-details/${caseId}/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1")
      .headers(ProbateHeader.probate_navigationHeader)
      .check(substring("HMCTS Manage cases")))

    .exec(configurationui)

    .exec(configJson)

    .exec(TsAndCs)

    .exec(userDetails)

    .exec(configUI)

    .exec(isAuthenticated)

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateApplication%2FsolicitorUpdateApplicationsolicitorUpdateApplicationPage1"))

    .exec(http("XUI${service}_100_010_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .check(jsonPath("$.state.name").is("Application created (deceased details)")))

    .exec(http("XUI${service}_100_015_UpdateApplication")
      .get("/data/internal/cases/${caseId}/event-triggers/solicitorUpdateApplication?ignore-warning=false")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .check(substring("solicitorUpdateApplication")))

    .exec(activity)

    .exec(profile)
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Deceased Details - Name, DOD, DOB
  ======================================================================================*/

  //first generate the Name, DOB and DOD
  .exec(_.setAll( "deceasedForename" -> ("Perf" + Feeders.randomAlphanumericString(5)),
                  "deceasedSurname" -> ("Test" + Feeders.randomAlphanumericString(5)),
                  "dobDay" -> Feeders.getDay(),
                  "dobMonth" -> Feeders.getMonth(),
                  "dobYear" -> Feeders.getDobYear(),
                  "dodDay" -> Feeders.getDay(),
                  "dodMonth" -> Feeders.getMonth(),
                  "dodYear" -> Feeders.getDodYear()))

  .group("XUI${service}_105_SubmitNameDodDob") {
    exec(http("XUI${service}_105_010_SubmitNameDodDob")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage1")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateDeceasedNameDodDob.json"))
      .check(substring("deceasedDateOfDeath")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateApplication%2FsolicitorUpdateApplicationsolicitorUpdateApplicationPage2"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Postcode Lookup
  ======================================================================================*/

  .group("XUI${service}_110_AddressLookup") {
    feed(postcodeFeeder)
      .exec(http("XUI${service}_110_AddressLookup")
        .get("/api/addresses?postcode=${postcode}")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/json")
        .header("content-type", "application/json")
        .check(jsonPath("$.totalresults").ofType[Int].gt(0))
        .check(regex(""""BUILDING_(?:.+)" : "(.+?)",\n      "THOROUGHFARE_NAME" : "(.+?)",\n      "POST_TOWN" : "(.+?)",\n      "POSTCODE" : "(.+?)",\n      """")
          .ofType[(String, String, String, String)].findRandom.saveAs("addressLines")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Deceased Details - Domicile, Address and Tax
  ======================================================================================*/

  .group("XUI${service}_120_SubmitNameDodDob") {
    exec(http("XUI${service}_120_010_SubmitNameDodDob")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage2")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateDeceasedAddressIHT.json"))
      .check(substring("deceasedAddress")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateApplication%2FsolicitorUpdateApplicationsolicitorUpdateApplicationPage3"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Type of Application
  ======================================================================================*/

    .group("XUI${service}_130_SubmitTypeOfApplication") {
      exec(http("XUI${service}_130_010_SubmitTypeOfApplication")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage3")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("content-type", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/probate/ProbateTypeOfApplication.json"))
        .check(substring("solsWillType")))

      .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateApplication%2FsolicitorUpdateApplicationsolicitorUpdateApplicationPage4"))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Estate in England or Wales
  ======================================================================================*/

  .group("XUI${service}_140_SubmitEnglandOrWales") {
    exec(http("XUI${service}_140_010_SubmitEnglandOrWales")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage4")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateEnglandOrWales.json"))
      .check(substring("appointExec")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateApplication%2Fsubmit"))

    .exec(profile)
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Confirm deceased details - check your answers
  ======================================================================================*/

  .group("XUI${service}_150_ConfirmDeceasedDetails") {
    exec(http("XUI${service}_150_010_ConfirmDeceasedDetails")
      .post("/data/cases/${caseId}/events")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateConfirmDeceasedDetails.json"))
      .check(jsonPath("$.state").is("SolProbateCreated")))

    .exec(activity)

    .exec(workAllocationPost)

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI${service}_150_020_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("content-type", "application/json")
      .check(jsonPath("$.state.name").is("Grant of probate created")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Create Grant of Probate Details Event
  ======================================================================================*/

  .group("XUI${service}_160_GrantOfProbateDetails") {
    exec(http("XUI${service}_160_010_GrantOfProbateDetails")
      .get("/cases/case-details/${caseId}/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1")
      .headers(ProbateHeader.probate_navigationHeader)
      .check(substring("HMCTS Manage cases")))

    .exec(configurationui)

    .exec(configJson)

    .exec(TsAndCs)

    .exec(userDetails)

    .exec(configUI)

    .exec(isAuthenticated)

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateProbate%2FsolicitorUpdateProbatesolicitorUpdateProbatePage1"))

    .exec(http("XUI${service}_160_020_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("content-type", "application/json")
      .check(jsonPath("$.state.name").is("Grant of probate created")))

    .exec(http("XUI${service}_160_030_GrantOfProbateDetails")
      .get("/data/internal/cases/${caseId}/event-triggers/solicitorUpdateProbate?ignore-warning=false")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .check(substring("solicitorUpdateProbate")))

    .exec(profile)
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Grant of Probate Details
  ======================================================================================*/

  //first set the will and codicil dates
  .exec(_.setAll( "willYear" -> ("${dodYear}".toInt - 10).toString,
                  "codicilYear" -> ("${dodYear}".toInt - 8).toString))

  .group("XUI${service}_170_SubmitGrantOfProbateDetails") {
    exec(http("XUI${service}_170_010_SubmitGrantOfProbateDetails")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage1")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateGrantOfProbateDetails.json"))
      .check(substring("willHasCodicils"))
      .check(jsonPath("$.data.codicilAddedDateList[0].id").saveAs("newCodicilId")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateProbate%2FsolicitorUpdateProbatesolicitorUpdateProbatePage2"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Postcode Lookup for Firm
  ======================================================================================*/

  .group("XUI${service}_175_AddressLookup") {
    feed(postcodeFeeder)
      .exec(http("XUI${service}_175_AddressLookup")
        .get("/api/addresses?postcode=${postcode}")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/json")
        .header("content-type", "application/json")
        .check(jsonPath("$.totalresults").ofType[Int].gt(0))
        .check(regex(""""BUILDING_(?:.+)" : "(.+?)",\n      "THOROUGHFARE_NAME" : "(.+?)",\n      "POST_TOWN" : "(.+?)",\n      "POSTCODE" : "(.+?)",\n      """")
          .ofType[(String, String, String, String)].findRandom.saveAs("addressLines")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Executor Details
  ======================================================================================*/

  .group("XUI${service}_180_SubmitExecutorDetails") {
    exec(http("XUI${service}_180_010_SubmitExecutorDetails")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage2")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateExecutorDetails.json"))
      .check(jsonPath("$.data.titleAndClearingType").is("TCTPartSuccPowerRes"))
      .check(jsonPath("$.data.otherPartnersApplyingAsExecutors[0].id").saveAs("newSolExecId")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateProbate%2FsolicitorUpdateProbatesolicitorUpdateProbatePage4"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Postcode Lookup for new executor
  ======================================================================================*/

  .group("XUI${service}_182_AddressLookup") {
    feed(postcodeFeeder)
      .exec(http("XUI${service}_182_AddressLookup")
        .get("/api/addresses?postcode=${postcode}")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/json")
        .header("content-type", "application/json")
        .check(jsonPath("$.totalresults").ofType[Int].gt(0))
        .check(regex(""""BUILDING_(?:.+)" : "(.+?)",\n      "THOROUGHFARE_NAME" : "(.+?)",\n      "POST_TOWN" : "(.+?)",\n      "POSTCODE" : "(.+?)",\n      """")
          .ofType[(String, String, String, String)].findRandom.saveAs("addressLines")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Extra Executor Details
  ======================================================================================*/

  .group("XUI${service}_185_SubmitExtraExecDetails") {
    exec(http("XUI${service}_185_010_SubmitExtraExecDetails")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage4")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateExtraExecDetails.json"))
      .check(jsonPath("$.data.solsAdditionalExecutorList[0].id").saveAs("newExecId")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateProbate%2FsolicitorUpdateProbatesolicitorUpdateProbatePage5"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Extra Executor Comments
  ======================================================================================*/

  .group("XUI${service}_187_SubmitExtraEvidence") {
    exec(http("XUI${service}_187_010_SubmitExtraEvidence")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage5")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateExtraEvidence.json"))
      .check(substring("furtherEvidenceForApplication")))

      .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateProbate%2FsolicitorUpdateProbatesolicitorUpdateProbatePage6"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Extra Information
  ======================================================================================*/

  .group("XUI${service}_190_SubmitExtraInfo") {
    exec(http("XUI${service}_190_010_SubmitExtraInfo")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage6")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateExtraInfo.json"))
      .check(substring("solsAdditionalInfo")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorUpdateProbate%2Fsubmit"))

    .exec(profile)
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Confirm grant of probate details - check your answers
  ======================================================================================*/

  .group("XUI${service}_200_ConfirmGrantOfProbateDetails") {
    exec(http("XUI${service}_200_010_ConfirmGrantOfProbateDetails")
      .post("/data/cases/${caseId}/events")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateConfirmGrantOfProbateDetails.json"))
      .check(jsonPath("$.state").is("SolAppUpdated")))

    .exec(http("XUI${service}_200_015_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"solicitorUpdateProbate","jurisdiction":"PROBATE","caseTypeId":"GrantOfRepresentation"}}"""))
      .check(substring("message"))
      .check(status.in(200, 401)))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI${service}_200_020_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("content-type", "application/json")
      .check(jsonPath("$.state.name").is("Application updated")))

    .exec(activity)
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Click 'Review and sign legal statement and submit application'
  ======================================================================================*/

  .group("XUI${service}_210_CompleteAppliction") {
    exec(http("XUI${service}_210_010_CompleteApplication")
      .get("/cases/case-details/1623056630472980/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1")
      .headers(ProbateHeader.probate_navigationHeader)
      .check(substring("HMCTS Manage cases")))
      //.check(jsonPath("$.case_fields[?(@.id=='solsLegalStatementDocument')].value.document_url").saveAs("documentURL"))
      //.check(jsonPath("$.id").is("solicitorReviewAndConfirm")))

    .exec(activity)

    .exec(configurationui)

    .exec(configJson)

    .exec(TsAndCs)

    .exec(userDetails)

    .exec(configUI)

    .exec(isAuthenticated)

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorReviewAndConfirm%2FsolicitorReviewAndConfirmsolicitorReviewLegalStatementPage1"))

    .exec(http("XUI${service}_210_020_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("content-type", "application/json")
      .check(jsonPath("$.state.name").is("Application updated")))

    .exec(http("XUI${service}_210_030_ReviewAndConfirm")
      .get("/data/internal/cases/${caseId}/event-triggers/solicitorReviewAndConfirm?ignore-warning=false")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .check(jsonPath("$.case_fields[?(@.id=='solsLegalStatementDocument')].value.document_url").saveAs("documentURL"))
      .check(jsonPath("$.id").is("solicitorReviewAndConfirm")))

    .exec(profile)
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Legal Statement
  ======================================================================================*/

  .group("XUI${service}_220_SubmitLegalStatement") {
    exec(http("XUI${service}_220_010_SubmitLegalStatement")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateLegalStatement.json"))
      .check(substring("solsLegalStatementDocument")))
  }

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorReviewAndConfirm%2FsolicitorReviewAndConfirmsolicitorReviewLegalStatementPage3"))

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Upload Document
  ======================================================================================*/

  .group("XUI${service}_225_UploadStatementOfTruth") {
    exec(http("XUI${service}_225_010_UploadStatementOfTruth")
      .post("/documents")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("content-type", "multipart/form-data")
      .header("x-xsrf-token", "${XSRFToken}")
      .bodyPart(RawFileBodyPart("file", "3MB.pdf")
        .fileName("3MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .check(substring("originalDocumentName"))
      .check(regex("""\/([0-9a-z-]+?)\/binary""").saveAs("SOTDocumentURL")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
   * Submit Legal Statement
   ======================================================================================*/

  .group("XUI${service}_227_SubmitSOTDoc") {
    exec(http("XUI${service}_227_010_SubmitSOTDoc")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorReviewLegalStatementPage3")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateSOTDoc.json"))
      .check(substring("document_filename")))
  }

  .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorReviewAndConfirm%2FsolicitorReviewAndConfirmsolicitorReviewLegalStatementPage4"))

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit IHT Submission Date
  ======================================================================================*/

  .group("XUI${service}_230_SubmitStatementOfTruth") {
    exec(http("XUI${service}_230_010_SubmitStatementOfTruth")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorReviewLegalStatementPage4")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateReviewAndConfirm.json"))
      .check(substring("BelieveTrue")))
  }

  .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorReviewAndConfirm%2FsolicitorReviewAndConfirmsolicitorConfirmPage2"))

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Extra Copies
  ======================================================================================*/

  .group("XUI${service}_260_SubmitExtraCopies") {
    exec(http("XUI${service}_260_010_SubmitExtraCopies")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorConfirmPage2")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateExtraCopies.json"))
      .check(substring("extraCopiesOfGrant")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorReviewAndConfirm%2FsolicitorReviewAndConfirmsolicitorConfirmPage3"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Payment Method
  ======================================================================================*/

  .group("XUI${service}_270_SubmitPaymentMethod") {
    exec(http("XUI${service}_270_010_SubmitPaymentMethod")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorConfirmPage3")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbatePaymentMethod.json"))
      .check(substring("solsPaymentMethods")))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorReviewAndConfirm%2Fsubmit"))

    .exec(profile)
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Application
  ======================================================================================*/

  .group("XUI${service}_280_SubmitApplication") {
    exec(http("XUI${service}_280_010_SubmitApplication")
      .post("/data/cases/${caseId}/events")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/probate/ProbateSubmitApplication.json"))
      .check(jsonPath("$.state").is("CaseCreated")))

    .exec(http("XUI${service}_280_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"solicitorReviewAndConfirm","jurisdiction":"PROBATE","caseTypeId":"GrantOfRepresentation"}}"""))
      .check(substring("message"))
      .check(status.in(200, 401)))

    .exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsolicitorReviewAndConfirm%2Fconfirm"))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Return to Case
  ======================================================================================*/

  .group("XUI${service}_290_ViewCase") {
    exec(healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI${service}_290_010_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("content-type", "application/json")
      .check(jsonPath("$.state.id").is("CaseCreated")))
  }

  .pause(MinThinkTime, MaxThinkTime)

}
