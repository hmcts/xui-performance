package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new Private Law application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_PRL {

  val IdamUrl = Environment.idamURL
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CreatePrivateLawCase =


    /*======================================================================================
    * Click the Create Case link
    ======================================================================================*/

    group("XUI_PRL_030_CreateCase") {
      exec(Common.healthcheck("%2Fcases%2Fcase-filter"))

      .exec(http("XUI_Divorce_030_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("PRIVATELAW")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Jurisdiction = Family Private Law; Case Type = C100 & FL401 Applications; Event = Solicitor Application
    ======================================================================================*/

    .group("XUI_PRL_040_SelectCaseType") {
      exec(Common.healthcheck("%2Fcases%2Fcase-create%2FPRIVATELAW%2FPRLAPPS%2FsolicitorCreate"))

      .exec(http("XUI_FPL_040_005_StartApplication")
        .get("/data/internal/case-types/PRLAPPS/event-triggers/solicitorCreate?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("solicitorCreate")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-create%2FPRIVATELAW%2FPRLAPPS%2FsolicitorCreate%2FsolicitorCreate2"))

      .exec(Common.userDetails)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Type of Application (C100 or FL401) - C100
    ======================================================================================*/

    .group("XUI_PRL_050_SelectApplicationType") {
      exec(http("XUI_PRL_050_005_SelectApplicationType")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLSelectApplicationType.json"))
        .check(substring("caseTypeOfApplication")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confidentiality Statement
    ======================================================================================*/

    .group("XUI_PRL_060_ConfidentialityStatement") {
      exec(http("XUI_PRL_060_005_ConfidentialityStatement")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate6")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLConfidentialityStatement.json"))
        .check(substring("c100ConfidentialityStatementDisclaimer")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Case Name
    ======================================================================================*/

    .group("XUI_PRL_070_CaseName") {
      exec(http("XUI_PRL_070_005_CaseName")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLCaseName.json"))
        .check(substring("applicantCaseName")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_080_CheckYourAnswers") {
      exec(http("XUI_PRL_080_005_CheckYourAnswers")
        .post("/data/case-types/PRLAPPS/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLCheckYourAnswers.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(jsonPath("$.state").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(http("XUI_PRL_080_010_WorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"solicitorCreate","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_080_015_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val TypeOfApplication =

    /*======================================================================================
    * Select "Type of Application" in the dropdown
    ======================================================================================*/

    group("XUI_PRL_090_CreateTypeOfApplicationEvent") {
      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType"))

      .exec(Common.profile)

      .exec(http("XUI_PRL_090_005_CreateTypeOfApplicationEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/selectApplicationType?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("selectApplicationType")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType1"))

      .exec(Common.userDetails)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * What order(s) are you applying for? - Child Arrangements, Spend Time with Order
    ======================================================================================*/

    .group("XUI_PRL_100_SelectOrders") {
      exec(http("XUI_PRL_100_005_SelectOrders")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLSelectOrders.json"))
        .check(substring("typeOfChildArrangementsOrder")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType2"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Draft Consent Order Upload
    ======================================================================================*/

    .group("XUI_PRL_110_ConsentOrderUpload") {
      exec(http("XUI_PRL_110_005_ConsentOrderUpload")
        .post("/documents")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("DocumentURL")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Do you have a draft consent order? - Yes
    ======================================================================================*/

    .group("XUI_PRL_120_ConsentOrder") {
      exec(http("XUI_PRL_120_005_ConsentOrder")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLConsentOrders.json"))
        .check(substring("consentOrder")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType3"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Have you applied to the court for permission to make this application? - Yes
    ======================================================================================*/

    .group("XUI_PRL_130_PermissionForApplication") {
      exec(http("XUI_PRL_130_005_PermissionForApplication")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLPermissionRequired.json"))
        .check(substring("applicationPermissionRequired")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType4"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Provide Brief Details of Application
    ======================================================================================*/

    .group("XUI_PRL_140_ProvideBriefDetails") {
      exec(http("XUI_PRL_140_005_ProvideBriefDetails")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLProvideBriefDetails.json"))
        .check(substring("applicationDetails")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2Fsubmit"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_150_CheckYourAnswers") {
      exec(http("XUI_PRL_150_005_CheckYourAnswers")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/PRLCheckYourAnswersTypeOfApplication.json"))
        .check(substring("applicationPermissionRequired"))
        .check(jsonPath("$.state").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(http("XUI_PRL_150_010_WorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"selectApplicationType","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_150_015_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='selectApplicationType')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    val HearingUrgency =
    /*======================================================================================
    * Hearing Urgency
    ======================================================================================*/

    group("XUI_PRL_160_HearingUrgency") {
      exec(http("XUI_PRL_160_005_HearingUrgency")
        .get("/cases/case-details/${caseId}/trigger/hearingUrgency/hearingUrgency1")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

      .exec(Common.configurationui)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FhearingUrgency%2FhearingUrgency1"))

      .exec(http("XUI_PRL_160_010_HearingUrgency")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

        .exec(Common.profile)

        .exec(http("XUI_PRL_160_015_HearingUrgency")
          .get("/data/internal/cases/${caseId}/event-triggers/hearingUrgency?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}"))

        .exec(Common.userDetails)

        .exec(Common.userDetails)

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Check Your Answers
    ======================================================================================*/

      .group("XUI_PRL_170_HearingUrgencyCheckYourAnswers") {
        exec(http("XUI_PRL_170_005_HearingUrgencyCheckYourAnswers")
          .post("/data/case-types/PRLAPPS/validate?pageId=hearingUrgency1")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/PRLHearingUrgency.json"))
          .check(substring("areRespondentsAwareOfProceedings")))

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FhearingUrgency%2Fsubmit"))

        .exec(Common.userDetails)
      }
      .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Hearing Urgency Submit
    ======================================================================================*/

      .group("XUI_PRL_180_HearingUrgencySubmit") {
        exec(http("XUI_PRL_180_005_HearingUrgencySubmit")
          .post("/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/PRLHearingUrgencyAnswers.json")))

        .exec(http("XUI_PRL_180_010_HearingUrgencySubmit")
          .post("/workallocation/searchForCompletable")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .header("x-xsrf-token", "${XSRFToken}"))


        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

          .exec(http("XUI_PRL_180_015_HearingUrgencySubmit")
            .get("/data/internal/cases/${caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .header("x-xsrf-token", "${XSRFToken}"))

        .exec(Common.userDetails)
      }
      .pause(MinThinkTime, MaxThinkTime)

}
