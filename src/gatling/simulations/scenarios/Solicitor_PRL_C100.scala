package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new Private Law application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_PRL_C100 {
  
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CreatePrivateLawCase =


    /*======================================================================================
    * Click the Create Case link
    ======================================================================================*/

    group("XUI_PRL_C100_C100_030_CreateCase") {

      exec(_.setAll(
        "C100ApplicantFirstName1" -> ("App" + Common.randomString(5)),
        "C100ApplicantLastName1" -> ("Test" + Common.randomString(5)),
        "C100ApplicantFirstName2" -> ("App" + Common.randomString(5)),
        "C100ApplicantLastName2" -> ("Test" + Common.randomString(5)),
        "C100RespondentFirstName" -> ("Resp" + Common.randomString(5)),
        "C100RespondentLastName" -> ("Test" + Common.randomString(5)),
        "C100ChildFirstName" -> ("Child" + Common.randomString(5)),
        "C100ChildLastName" -> ("Test" + Common.randomString(5)),
        "C100RepresentativeFirstName" -> ("Rep" + Common.randomString(5)),
        "C100RepresentativeLastName" -> ("Test" + Common.randomString(5)),
        "C100SoleTraderName" -> ("Sole" + Common.randomString(5)),
        "C100SolicitorName" -> ("Soli" + Common.randomString(5)),
        "C100AppDobDay" -> Common.getDay(),
        "C100AppDobMonth" -> Common.getMonth(),
        "C100AppDobYear" -> Common.getDobYear(),
        "C100AppDobDay2" -> Common.getDay(),
        "C100AppDobMonth2" -> Common.getMonth(),
        "C100AppDobYear2" -> Common.getDobYear(),
        "C100ChildAppDobDay" -> Common.getDay(),
        "C100ChildAppDobMonth" -> Common.getMonth(),
        "C100ChildDobYear" -> Common.getDobYearChild(),
        "C100RespDobDay" -> Common.getDay(),
        "C100RespDobMonth" -> Common.getMonth(),
        "C100RespDobYear" -> Common.getDobYear()))

      .exec(Common.healthcheck("%2Fcases%2Fcase-filter"))

      .exec(http("XUI_PRL_C100_030_CreateCase")
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

    .group("XUI_PRL_C100_C100_040_SelectCaseType") {
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

    .group("XUI_PRL_C100_C100_050_SelectApplicationType") {
      exec(http("XUI_PRL_C100_C100_050_005_SelectApplicationType")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSelectApplicationType.json"))
        .check(substring("caseTypeOfApplication")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confidentiality Statement
    ======================================================================================*/

    .group("XUI_PRL_C100_060_ConfidentialityStatement") {
      exec(http("XUI_PRL_C100_060_005_ConfidentialityStatement")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate6")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLConfidentialityStatement.json"))
        .check(substring("c100ConfidentialityStatementDisclaimer")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Case Name
    ======================================================================================*/

    .group("XUI_PRL_C100_070_CaseName") {
      exec(http("XUI_PRL_C100_070_005_CaseName")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCaseName.json"))
        .check(substring("applicantCaseName")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_C100_080_CheckYourAnswers") {
      exec(http("XUI_PRL_C100_080_005_CheckYourAnswers")
        .post("/data/case-types/PRLAPPS/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCheckYourAnswers.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(jsonPath("$.state").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(http("XUI_PRL_C100_080_010_WorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"solicitorCreate","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_080_015_ViewCase")
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

    group("XUI_PRL_C100_090_CreateTypeOfApplicationEvent") {

      exec(http("XUI_PRL_C100_160_005_HearingUrgencyRedirect")
        .get("/cases/case-details/${caseId}/trigger/selectApplicationType/selectApplicationType1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

        .exec(Common.configurationui)

        .exec(Common.configJson)

        .exec(Common.TsAndCs)

        .exec(Common.configUI)

        .exec(Common.userDetails)

        .exec(Common.isAuthenticated)

        .exec(Common.monitoringTools)

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType1"))

        .exec(http("XUI_PRL_C100_160_010_HearingUrgencyViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .header("x-xsrf-token", "${XSRFToken}"))

        .exec(Common.caseActivityGet)

        .exec(Common.profile)

        .exec(http("XUI_PRL_C100_160_015_HearingUrgencyEvent")
          .get("/data/internal/cases/${caseId}/event-triggers/selectApplicationType?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(jsonPath("$.id").is("selectApplicationType")))

        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

        .exec(Common.userDetails)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * What order(s) are you applying for? - Child Arrangements, Spend Time with Order
    ======================================================================================*/

    .group("XUI_PRL_C100_100_SelectOrders") {
      exec(http("XUI_PRL_C100_100_005_SelectOrders")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSelectOrders.json"))
        .check(substring("typeOfChildArrangementsOrder")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType2"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Draft Consent Order Upload
    ======================================================================================*/

    .group("XUI_PRL_C100_110_ConsentOrderUpload") {
      exec(http("XUI_PRL_C100_110_005_ConsentOrderUpload")
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

    .group("XUI_PRL_C100_120_ConsentOrder") {
      exec(http("XUI_PRL_C100_120_005_ConsentOrder")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLConsentOrders.json"))
        .check(substring("consentOrder")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType3"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Have you applied to the court for permission to make this application? - Yes
    ======================================================================================*/

    .group("XUI_PRL_C100_130_PermissionForApplication") {
      exec(http("XUI_PRL_C100_130_005_PermissionForApplication")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLPermissionRequired.json"))
        .check(substring("applicationPermissionRequired")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2FselectApplicationType4"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Provide Brief Details of Application
    ======================================================================================*/

    .group("XUI_PRL_C100_140_ProvideBriefDetails") {
      exec(http("XUI_PRL_C100_140_005_ProvideBriefDetails")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLProvideBriefDetails.json"))
        .check(substring("applicationDetails")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FselectApplicationType%2Fsubmit"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_C100_150_CheckYourAnswers") {
      exec(http("XUI_PRL_C100_150_005_CheckYourAnswers")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCheckYourAnswersTypeOfApplication.json"))
        .check(substring("applicationPermissionRequired"))
        .check(jsonPath("$.state").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(http("XUI_PRL_C100_150_010_WorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"selectApplicationType","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_150_015_ViewCase")
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
    * Hearing Urgency Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_160_HearingUrgency") {
      exec(http("XUI_PRL_C100_160_005_HearingUrgencyRedirect")
        .get("/cases/case-details/${caseId}/trigger/hearingUrgency/hearingUrgency1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FhearingUrgency%2FhearingUrgency1"))

      .exec(http("XUI_PRL_C100_160_010_HearingUrgencyViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.caseActivityGet)

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_160_015_HearingUrgencyEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/hearingUrgency?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("hearingUrgency")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

      .exec(Common.userDetails)

      .exec(Common.userDetails)

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Questions
    ======================================================================================*/

    .group("XUI_PRL_C100_170_HearingUrgencyQuestions") {
      exec(http("XUI_PRL_C100_170_005_HearingUrgencyQuestions")
        .post("/data/case-types/PRLAPPS/validate?pageId=hearingUrgency1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLHearingUrgency.json"))
        .check(substring("areRespondentsAwareOfProceedings")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FhearingUrgency%2Fsubmit"))

      .exec(Common.userDetails)
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_C100_180_HearingUrgencyCheckYourAnswers") {
      exec(http("XUI_PRL_C100_180_005_HearingUrgencyCheckYourAnswers")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLHearingUrgencyAnswers.json")))

      .exec(http("XUI_PRL_C100_180_010_HearingUrgencyWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"hearingUrgency","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_180_015_HearingUrgencyViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='hearingUrgency')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }
    .pause(MinThinkTime, MaxThinkTime)


  val ApplicantDetails =

    /*======================================================================================
    * Applicant Details Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_190_ApplicantDetails") {
      exec(http("XUI_PRL_C100_190_005_ApplicantDetailsRedirect")
        .get("/cases/case-details/${caseId}/trigger/applicantsDetails/applicantsDetails1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FapplicantsDetails%2FapplicantsDetails1"))

      .exec(Common.caseActivityGet)

      .exec(http("XUI_PRL_C100_190_005_ApplicantDetailsViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_190_010_ApplicantDetailsEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/applicantsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("applicantsDetails")))

      .exec(Common.userDetails)
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Applicant Add New - 2 applicants to be added
    ======================================================================================*/

    .group("XUI_PRL_C100_210_ApplicantDetails") {

      exec(Common.caseShareOrgs)

      .exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_210_015_ApplicantDetailValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=applicantsDetails1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLApplicantDetails.json"))
        .check(substring("dxNumber")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FapplicantsDetails%2Fsubmit"))

      .exec(Common.userDetails)

      .exec(Common.caseShareOrgs)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Applicant Details Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_C100_220_ApplicantDetailsCheckYourAnswers") {

      exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_220_005_ApplicantDetailsCheckYourAnswers")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLApplicantDetailsAnswers.json")))


      .exec(http("XUI_PRL_C100_220_010_ApplicantDetailsWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"applicantsDetails","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))


      .exec(http("XUI_PRL_C100_220_015_ApplicantDetailsViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='applicantsDetails')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)




  val ChildDetails =

    /*======================================================================================
    * Child Details Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_230_ChildDetailsRedirect") {
      exec(http("XUI_PRL_C100_230_005_ChildDetailsRedirect")
        .get("/case-details/${caseId}/trigger/childDetails/childDetails1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FapplicantsDetails%2FapplicantsDetails1"))

      .exec(Common.caseActivityGet)

      .exec(http("XUI_PRL_C100_230_010_ChildDetailsCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_230_015_ChildDetailsEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/childDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("childDetails")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Add New Child
    ======================================================================================*/

    .group("XUI_PRL_C100_240_ChildDetailsAddNew") {
      exec(http("XUI_PRL_C100_240_005_ChildDetailsAddNew")
          .post("/data/case-types/PRLAPPS/validate?pageId=childDetails1")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/c100/PRLChildDetails.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FchildDetails%2FchildDetails2"))

      .exec(Common.userDetails)


    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Additional Details
    ======================================================================================*/

    .group("XUI_PRL_C100_250_ChildDetailsAdditionalDetails") {

      exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_250_005_ChildDetailsAdditionalDetails")
        .post("/data/case-types/PRLAPPS/validate?pageId=childDetails2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetails.json"))
        .check(substring("childrenKnownToLocalAuthority")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FchildDetails%2Fsubmit"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Answer Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_260_ChildDetailsAdditionalDetails") {
      exec(http("XUI_PRL_C100_260_005_ChildDetailsAdditionalDetails")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetails.json")))

      .exec(http("XUI_PRL_C100_260_010_ChildDetailsAdditionalDetailsWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"childDetails","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_260_010_ChildDetailsAdditionalDetailsViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='childDetails')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


  val RespondentDetails =

    /*======================================================================================
    * Respondent Details Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_270_RespondentDetailsRedirect") {
      exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_270_005_RespondentDetailsRedirect")
        .get("/cases/case-details/${caseId}/trigger/respondentsDetails/respondentsDetails1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FrespondentsDetails%2FrespondentsDetails1"))

      .exec(Common.caseActivityGet)

      .exec(http("XUI_PRL_C100_270_010_RespondentDetailsCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_270_015_RespondentDetailsCaseEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/respondentsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Respondent Details Add Respondent Details
    ======================================================================================*/

    .group("XUI_PRL_C100_280_RespondentDetailsAddNew") {

      exec(Common.caseShareOrgs)

      .exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_280_005_RespondentDetailsAddNew")
        .post("/data/case-types/PRLAPPS/validate?pageId=respondentsDetails1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLRespondentDetails.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FrespondentsDetails%2Fsubmit"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Respondent Details Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_290_RespondentDetailsSubmit") {
      exec(http("XUI_PRL_C100_290_005_RespondentDetailsSubmit")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetailsSubmit.json")))

      .exec(http("XUI_PRL_C100_290_010_RespondentDetailsSubmitWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"respondentsDetails","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_290_015_RespondentDetailsSubmitViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='respondentsDetails')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


  val MIAM =

    /*======================================================================================
    * Miam Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_300_MIAMRedirect") {
      exec(http("XUI_PRL_C100_300_005_MIAMRedirect")
        .get("/cases/case-details//trigger/miam/miam1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FrespondentsDetails%2Fmiam%2Fmiam1"))

      .exec(Common.caseActivityGet)

      .exec(http("XUI_PRL_C100_300_010_MIAMCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_300_015_MIAMCaseEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/respondentsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Has the applicant attended a Mediation information & Assessment Meeting (MIAM)?
    ======================================================================================*/

    .group("XUI_PRL_C100_310_AttendedMIAM") {

      exec(Common.caseShareOrgs)

      .exec(http("XUI_PRL_C100_310_005_AttendedMIAM")
        .post("/data/case-types/PRLAPPS/validate?pageId=miam1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAttendedMIAM.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fmiam%2Fmiam7"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    *MIAM certificate Upload
    ======================================================================================*/

    .group("XUI_PRL_C100_320_MIAMUpload") {

      exec(http("XUI_PRL_C100_320_005_MIAMUpload")
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
    * MIAM Details
    ======================================================================================*/

    .group("XUI_PRL_C100_320_MIAMdetails") {

      exec(http("XUI_PRL_C100_320_005_MIAMdetails")
        .post("/data/case-types/PRLAPPS/validate?pageId=miam1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLMIAMDetails.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fmiam%2Fsubmit"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * MIAM Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_330_MIAMSubmit") {
      exec(http("XUI_PRL_C100_330_005_MIAMSubmit")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLMIAMDetailsSubmit.json")))

      .exec(http("XUI_PRL_C100_330_005_MIAMWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"miam","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_330_005_MIAMViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='miam')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


  val AllegationsOfHarm =

    /*======================================================================================
    * Allegations Of Harm Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_340_AllegationsOfHarmRedirect") {
      exec(http("XUI_PRL_C100_340_005_AllegationsOfHarmRedirect")
        .get("/cases/case-details/${caseId}/trigger/allegationsOfHarm/allegationsOfHarm1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallegationsOfHarm%2FallegationsOfHarm1"))

      .exec(Common.caseActivityGet)

      .exec(http("XUI_PRL_C100_340_010_AllegationsOfHarmRedirectCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_340_015_AllegationsOfHarmEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/allegationsOfHarm?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Are there Allegations of Harm?
    ======================================================================================*/

    .group("XUI_PRL_C100_350_AllegationsOfHarm") {

      exec(Common.caseShareOrgs)

      .exec(http("XUI_PRL_C100_350_005_AllegationsOfHarm")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarm1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAreThereAllegationsOfHarm.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallegationsOfHarm%2Fsubmit"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Allegations of Harm details
    ======================================================================================*/

    .group("XUI_PRL_C100_360_AllegationsOfHarmDetails") {

      exec(Common.caseShareOrgs)

      .exec(http("XUI_PRL_C100_360_005_AllegationsOfHarmDetails")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarm2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAllegationsOfHarmDetails.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallegationsOfHarm%2FallegationsOfHarm3"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Allegations of Harm Behaviour
    ======================================================================================*/

    .group("XUI_PRL_C100_370_AllegationsOfHarmBehaviour") {

      exec(Common.caseShareOrgs)

      .exec(http("XUI_PRL_C100_370_005_AllegationsOfHarmBehaviour")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarm3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAllegationsOfHarmBehaviour.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallegationsOfHarm%2FallegationsOfHarm4"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Allegations of Harm Other Concerns
    ======================================================================================*/

    .group("XUI_PRL_C100_380_AllegationsOfHarmOther") {

      exec(Common.caseShareOrgs)

      .exec(http("XUI_PRL_C100_380_005_AllegationsOfHarmOther")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarm4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAllegationsOfHarmOther.json")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallegationsOfHarm%2Fsubmit"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Allegations of Harm Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_390_AllegationsOfHarmSubmit") {
      exec(http("XUI_PRL_C100_390_005_AllegationsOfHarmSubmit")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAreThereAllegationsOfHarmSubmit.json")))

      .exec(http("XUI_PRL_C100_390_010_AllegationsOfHarmWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"allegationsOfHarm","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_390_015_AllegationsOfHarmViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='allegationsOfHarm')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

  val ViewPdfApplication =

    /*======================================================================================
    * View PDF Application Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_400_ViewPdfApplicationRedirect") {
      exec(http("XUI_PRL_C100_400_005_ViewPdfApplicationRedirect")
        .get("/cases/case-details/${caseId}/trigger/viewPdfDocument/viewPdfDocument1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FviewPdfDocument%2FviewPdfDocument1"))

      .exec(Common.caseActivityGet)

      .exec(http("XUI_PRL_C100_400_010_ViewPdfApplicationRedirectCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_400_015_ViewPdfApplicationRedirectEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/viewPdfDocument?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.case_fields[?(@.id=='draftOrderDoc')].value.document_url").saveAs("DocumentUrl"))
        .check(jsonPath("$.case_fields[?(@.id=='draftOrderDoc')].value.document_filename").saveAs("DocumentFileName"))
        .check(jsonPath("$.case_fields[?(@.id=='draftOrderDoc')].value.document_hash").saveAs("DocumentHash"))
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * View PDF Continue
    ======================================================================================*/

    .group("XUI_PRL_C100_410_ViewPdfContinue") {

      exec(Common.caseShareOrgs)

      .exec(http("XUI_PRL_C100_410_005_ViewPdfContinue")
        .post("/data/case-types/PRLAPPS/validate?pageId=viewPdfDocument1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLViewPdfContinue.json"))
        .check(substring("isEngDocGen")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallegationsOfHarm%2Fsubmit"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * View PDF Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_420_ViewPdfSubmit") {
      exec(http("XUI_PRL_C100_420_005_ViewPdfSubmitViewCase")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLViewPdfContinueSubmit.json")))

      .exec(http("XUI_PRL_C100_420_010_ViewPdfSubmitWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"viewPdfDocument","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_PRL_C100_420_015_ViewPdfSubmit")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='viewPdfDocument')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


  val SubmitAndPay =

    /*======================================================================================
    * SubmitAndPay Redirect
    ======================================================================================*/

    group("XUI_PRL_C100_430_SubmitAndPayRedirect") {
      exec(http("XUI_PRL_C100_430_005_SubmitAndPayRedirect")
        .get("/cases/case-details/${caseId}/trigger/submitAndPay/submitAndPay1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "${XSRFToken}"))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitAndPay%2FsubmitAndPay1"))

      .exec(Common.caseActivityGet)

      .exec(http("XUI_PRL_C100_430_010_SubmitAndPayRedirectCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='viewPdfDocument')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.profile)

      .exec(http("XUI_PRL_C100_430_015_SubmitAndPayRedirectEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/submitAndPay?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("submitAndPay")))

      .exec(Common.userDetails)
      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Submit and Pay Confidentiality Statement
    ======================================================================================*/

    .group("XUI_PRL_C100_440_SubmitAndPayConfidentialityStatement") {

      exec(http("XUI_PRL_C100_440_005_SubmitAndPayConfidentialityStatement")
        .post("/data/case-types/PRLAPPS/validate?pageId=submitAndPay1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayConfidentialityStatement.json"))
        .check(jsonPath("$.data.paymentServiceRequestReferenceNumber").saveAs("paymentReferenceNumber"))
        .check(substring("paymentServiceRequestReferenceNumber")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitAndPay%2FsubmitAndPay2"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Declaration
    ======================================================================================*/

    .group("XUI_PRL_C100_445_SubmitAndPayDeclaration") {


      exec(http("XUI_PRL_C100_445_005_SubmitAndPayDeclaration")
        .post("/data/case-types/PRLAPPS/validate?pageId=submitAndPay2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayDeclaration.json"))
        .check(substring("feeAmount")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitAndPay%2FsubmitAndPay3"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Submit and Pay Continue
    ======================================================================================*/

    .group("XUI_PRL_C100_450_SubmitAndPayContinue") {

      exec(http("XUI_PRL_C100_450_005_SubmitAndPayContinue")
        .post("/data/case-types/PRLAPPS/validate?pageId=submitAndPay3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayContinue.json"))
        .check(substring("paymentServiceRequestReferenceNumber")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitAndPay%2FsubmitAndPay2"))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Now
    ======================================================================================*/

    .group("XUI_PRL_C100_460_SubmitAndPayNow") {
      exec(http("XUI_PRL_C100_460_005_SubmitAndPayNow")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayNow.json"))
        .check(substring("created_on")))

      .exec(http("XUI_PRL_C100_460_010_SubmitAndPayNowWorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"submitAndPay","jurisdiction":"PRIVATELAW","caseTypeId":"PRLAPPS"}}"""))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XXUI_PRL_C100_460_015_SubmitAndPayNowViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='submitAndPay')]"))
        .check(jsonPath("$.state.id").is("SUBMITTED_NOT_PAID")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

}
