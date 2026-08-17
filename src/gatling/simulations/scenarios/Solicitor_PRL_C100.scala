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

    group("XUI_PRL_C100_030_CreateCase") {

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

    .group("XUI_PRL_C100_040_SelectCaseType") {
      exec(http("XUI_FPL_040_005_StartApplication")
        .get("/data/internal/case-types/PRLAPPS/event-triggers/solicitorCreate?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("solicitorCreate")))

      .exec(Common.userDetails)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
      .exec(getCookieValue(CookieKey("__auth__").saveAs("authToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Type of Application (C100 or FL401) - C100
    ======================================================================================*/

    .group("XUI_PRL_C100_050_SelectApplicationType") {
      exec(http("XUI_PRL_C100_050_005_SelectApplicationType")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
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
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLConfidentialityStatement.json"))
        .check(substring("c100ConfidentialityStatementDisclaimer")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Solicitor App - Save and Continue
    ======================================================================================*/

    .group("XUI_PRL_C100_065_SolicitorAppSaveAndContinue") {
      exec(http("XUI_PRL_C100_065_005_SolicitorAppSaveAndContinue")
        .post("/data/case-types/PRLAPPS/validate?pageId=solicitorCreate6")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCaseName.json"))
        .check(substring("validate?pageId=solicitorCreate6")))

      .exec(Common.userDetails)

      .exec(http("XUI_PRL_C100_065_010_SolicitorAppSaveAndContinue")
        .post("/data/case-types/PRLAPPS/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCreateShellCase.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(jsonPath("$.callback_response_status_code").is("200"))
        .check(jsonPath("$.callback_response_status").is("CALLBACK_COMPLETED")))

      .exec(http("XUI_PRL_C100_065_015_SolicitorAppSaveAndContinue")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("Application for a court order to make arrangements for a child")))

      .exec(Common.manageLabellingRoleAssignment)
    }
    .pause(MinThinkTime, MaxThinkTime)

  val TypeOfApplication =

    /*======================================================================================
    * Click on 'Type of Application' link
    ======================================================================================*/

    group("XUI_PRL_C100_070_CreateTypeOfApplicationEvent") {
      exec(http("XUI_PRL_C100_070_005_CreateTypeOfApplicationWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/selectApplicationType/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_070_010_CreateTypeOfApplicationEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/selectApplicationType?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("selectApplicationType"))
        .check(substring("Type of application")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Type of Application Profile
    ======================================================================================*/

    .group("XUI_PRL_C100_080_TypeOfApplicationProfile") {
      exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * What order(s) are you applying for? - Child Arrangements, Spend Time with Order
    ======================================================================================*/

    .group("XUI_PRL_C100_090_SelectOrders") {
      exec(http("XUI_PRL_C100_090_005_SelectOrders")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSelectOrders.json"))
        .check(substring("typeOfChildArrangementsOrder")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Draft Consent Order Upload
    ======================================================================================*/

    .group("XUI_PRL_C100_100_ConsentOrderUpload") {
      exec(http("XUI_PRL_C100_100_005_ConsentOrderUpload")
        .post("/documents")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "#{XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "PRLAPPS")
        .formParam("jurisdictionId", "PRIVATELAW")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("DocumentURL")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Do you have a draft consent order? - Yes
    ======================================================================================*/

    .group("XUI_PRL_C100_110_ConsentOrder") {
      exec(http("XUI_PRL_C100_110_005_ConsentOrder")
        .post("/data/case-types/PRLAPPS/validate?pageId=selectApplicationType2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLConsentOrders.json"))
        .check(substring("consentOrder")))
    }

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100_120_PermissionUpload") {
        exec(http("XUI_PRL_C100_120_005_PermissionUpload")
          .post("/documents")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .header("x-xsrf-token", "#{XSRFToken}")
          .bodyPart(RawFileBodyPart("files", "7PageDoc.pdf")
            .fileName("7PageDoc.pdf")
            .transferEncoding("binary"))
          .asMultipartForm
          .formParam("classification", "PUBLIC")
          .formParam("caseTypeId", "PRLAPPS")
          .formParam("jurisdictionId", "PRIVATELAW")
          .check(substring("originalDocumentName"))
          .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("DocumentURL_permission")))
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
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLPermissionRequired.json"))
        .check(substring("orderInPlacePermissionRequired")))

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
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLProvideBriefDetails.json"))
        .check(substring("applicationDetails")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_C100_150_CheckYourAnswers") {
      exec(http("XUI_PRL_C100_150_005_CheckYourAnswers")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCheckYourAnswersTypeOfApplication.json"))
        .check(substring("applicationPermissionRequired"))
        .check(jsonPath("$.state").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(http("XUI_PRL_C100_150_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='selectApplicationType')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
      .exec(Common.manageLabellingRoleAssignment)

    }

    .pause(MinThinkTime, MaxThinkTime)

  val HearingUrgency =

    /*======================================================================================
    * Click on 'Hearing Urgency'
    ======================================================================================*/

    group("XUI_PRL_C100_160_HearingUrgency") {
      exec(http("XUI_PRL_C100_160_005_HearingUrgencyWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/hearingUrgency/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_160_010_HearingUrgencyEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/hearingUrgency?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("hearingUrgency")))

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Profile
    ======================================================================================*/

    .group("XUI_PRL_C100_170_HearingUrgencyProfile") {
      exec(Common.profile)
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Questions
    ======================================================================================*/

    .group("XUI_PRL_C100_180_HearingUrgencyQuestions") {
      exec(http("XUI_PRL_C100_180_005_HearingUrgencyQuestions")
        .post("/data/case-types/PRLAPPS/validate?pageId=hearingUrgency1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLHearingUrgency.json"))
        .check(substring("areRespondentsAwareOfProceedings")))

      .exec(Common.userDetails)
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_C100_190_HearingUrgencyCheckYourAnswers") {
      exec(http("XUI_PRL_C100_190_005_HearingUrgencyCheckYourAnswers")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLHearingUrgencyAnswers.json"))
        .check(substring("trigger/hearingUrgency")))

      .exec(http("XUI_PRL_C100_190_010_HearingUrgencyViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='hearingUrgency')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }
    .pause(MinThinkTime, MaxThinkTime)

  val ApplicantDetails =

    /*======================================================================================
    * Click on 'Applicant Details'
    ======================================================================================*/

    group("XUI_PRL_C100_200_ApplicantDetails") {
      exec(http("XUI_PRL_C100_200_005_ApplicantDetailsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/applicantsDetails/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_200_010_ApplicantDetailsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/applicantsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[?(@.id=='applicants')].value[0].id").saveAs("applicantId"))
        .check(jsonPath("$.case_fields[?(@.id=='applicants')].value[1].id").optional.saveAs("applicantIdTwo"))     
        .check(jsonPath("$.id").is("applicantsDetails")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Applicant Details Profile
    ======================================================================================*/

    .group("XUI_PRL_C100_210_ApplicantDetailsProfile") {
      exec(Common.profile)
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Applicant Add New - 2 applicants to be added
    ======================================================================================*/

    .group("XUI_PRL_C100_220_ApplicantDetails") {
      exec(Common.caseShareOrgs)
      .exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_220_005_ApplicantDetailValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=applicantsDetails1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLApplicantDetails2.json"))
        .check(substring("dxNumber")))

      .exec(Common.userDetails)
      .exec(Common.caseShareOrgs)
    }


    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Applicant Details Check Your Answers
    ======================================================================================*/

    .group("XUI_PRL_C100_230_ApplicantDetailsCheckYourAnswers") {
      exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_230_005_ApplicantDetailsCheckYourAnswers")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLApplicantDetailsAnswers.json"))
        .check(substring("trigger/applicantsDetails")))

      .exec(http("XUI_PRL_C100_230_010_ApplicantDetailsViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='applicantsDetails')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val ChildDetails =

    /*======================================================================================
    * Click on 'Child Details'
    ======================================================================================*/

    group("XUI_PRL_C100_280_ChildDetails") {
      exec(http("XUI_PRL_C100_280_005_ChildDetailsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/childDetailsRevised/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_280_010_ChildDetailsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/childDetailsRevised?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[1].value[0].value.whoDoesTheChildLiveWith.list_items[*].code").findAll.saveAs("childLiveWithCode"))
        .check(jsonPath("$.case_fields[1].value[0].value.whoDoesTheChildLiveWith.list_items[*].label").findAll.saveAs("childLiveWithLabel"))
        .check(jsonPath("$.case_fields[1].value[0].id").saveAs("childLiveWithId"))        
        .check(jsonPath("$.id").is("childDetailsRevised")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Profile
    ======================================================================================*/

    .group("XUI_PRL_C100_290_ChildDetailsProfile") {
      exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Add New Child
    ======================================================================================*/

    .group("XUI_PRL_C100_300_ChildDetailsAddNew") {
      exec(http("XUI_PRL_C100_300_005_ChildDetailsAddNew")
        .post("/data/case-types/PRLAPPS/validate?pageId=childDetailsRevised1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildDetails.json"))
        .check(substring("newChildDetails")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Additional Details
    ======================================================================================*/

    .group("XUI_PRL_C100_310_ChildDetailsAdditionalDetails") {
      exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_310_005_ChildDetailsAdditionalDetails")
        .post("/data/case-types/PRLAPPS/validate?pageId=childDetailsRevised2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetails.json"))
        .check(substring("childrenKnownToLocalAuthority")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Answer Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_320_ChildDetailsAdditionalDetailsSubmit") {
      exec(http("XUI_PRL_C100_320_005_ChildDetailsAdditionalDetailsSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildDetailsEvent.json"))
        .check(substring("trigger/childDetailsRevised")))

      .exec(http("XUI_PRL_C100_320_010_ChildDetailsAdditionalDetailsViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='childDetailsRevised')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val RespondentDetails =

    /*======================================================================================
    * Click on 'Respondent Details'
    ======================================================================================*/

    group("XUI_PRL_C100_240_RespondentDetails") {
      exec(http("XUI_PRL_C100_240_005_RespondentDetailsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/respondentsDetails/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_240_010_RespondentDetailsCaseEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/respondentsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(Common.savePartyIds)
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Details of the respondents in the case")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Respondent Details Profile
    ======================================================================================*/

    .group("XUI_PRL_C100_250_RespondentDetailsProfile") {
      exec(Common.profile)
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Respondent Details Add Respondent Details
    ======================================================================================*/

    .group("XUI_PRL_C100_260_RespondentDetailsAddNew") {
      exec(Common.caseShareOrgs)
      .exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_260_005_RespondentDetailsAddNew")
        .post("/data/case-types/PRLAPPS/validate?pageId=respondentsDetails1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLRespondentDetails.json"))
        .check(substring("isAtAddressLessThan5YearsWithDontKnow")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Respondent Details Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_270_RespondentDetailsSubmit") {
      exec(http("XUI_PRL_C100_270_005_RespondentDetailsSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetailsSubmit.json"))
        .check(substring("trigger/respondentsDetails")))

      .exec(http("XUI_PRL_C100_270_010_RespondentDetailsSubmitViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='respondentsDetails')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val MIAM =

    /*======================================================================================
    * Click on 'Miam'
    ======================================================================================*/

    group("XUI_PRL_C100_540_MIAM") {

      exec(http("XUI_PRL_C100_540_005_MIAMWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/miamPolicyUpgrade/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_540_010_MIAM")
        .get("/data/internal/cases/#{caseId}/event-triggers/miamPolicyUpgrade?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("content-type","application/json")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Has any application been made for a care order = No
    * Has the applicant attended a Mediation Information & Assessment Meeting (MIAM)? = Yes
    ======================================================================================*/

    .group("XUI_PRL_C100_550_MIAMValidate") {
      exec(http("XUI_PRL_C100_550_005_MIAMValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=miamPolicyUpgrade1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLMIAMDetails.json")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * MIAM Policy Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_560_MIAMSubmitEvent") {
      exec(http("XUI_PRL_C100_560_005_MIAMSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLMIAMDetailsSubmit.json"))
        .check(jsonPath("$.callback_response_status_code").is("200")))

      .exec(http("XUI_PRL_C100_560_010_MIAMViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='miamPolicyUpgrade')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))
    
      .exec(Common.manageLabellingRoleAssignment)
    }
    .pause(MinThinkTime, MaxThinkTime)

  val AllegationsOfHarm =

    /*======================================================================================
    * Click on 'Allegations Of Harm'
    ======================================================================================*/

    group("XUI_PRL_C100_330_AllegationsOfHarm") {
      exec(http("XUI_PRL_C100_330_005_AllegationsOfHarmWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/allegationsOfHarmRevised/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_330_010_AllegationsOfHarmEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/allegationsOfHarmRevised?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Are there Allegations of Harm?
    ======================================================================================*/

    .group("XUI_PRL_C100_340_AllegationsOfHarm") {
      exec(http("XUI_PRL_C100_340_005_AllegationsOfHarm")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarmRevised1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAreThereAllegationsOfHarm.json"))
        .check(regex(""""allegationsOfHarmYesNo":"Yes"""").exists))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Allegations of Harm details
    ======================================================================================*/

    .group("XUI_PRL_C100_350_AllegationsOfHarmDetails") {
      exec(http("XUI_PRL_C100_350_005_AllegationsOfHarmDetails")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarmRevised2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAllegationsOfHarmDetails.json"))
        .check(regex(""""ordersNonMolestation":"No"""").exists))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Allegations of Harm Behaviour
    ======================================================================================*/

    .group("XUI_PRL_C100_360_AllegationsOfHarmBehaviour") {
      exec(http("XUI_PRL_C100_360_005_AllegationsOfHarmBehaviour")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarmRevised3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAllegationsOfHarmBehaviour.json"))
        .check(regex(""""behavioursApplicantSoughtHelp":"No"""").exists))

    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Allegations of Harm Other Concerns
    ======================================================================================*/

    .group("XUI_PRL_C100_370_AllegationsOfHarmOther") {
      exec(http("XUI_PRL_C100_370_005_AllegationsOfHarmOther")
        .post("/data/case-types/PRLAPPS/validate?pageId=allegationsOfHarmRevised11")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAllegationsOfHarmOther.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Allegations of Harm Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_380_AllegationsOfHarmSubmit") {
      exec(http("XUI_PRL_C100_380_005_AllegationsOfHarmSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAreThereAllegationsOfHarmSubmit.json")))

      .exec(http("XUI_PRL_C100_380_010_AllegationsOfHarmViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='allegationsOfHarmRevised')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.manageLabellingRoleAssignment)
      .exec(Common.waJurisdictions)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val OtherChildrenNotInCase =

    /*======================================================================================
    * Click on 'Other children not in the case'
    ======================================================================================*/

    group("XUI_PRL_C100_390_OtherChildrenNotInCase") {
      exec(http("XUI_PRL_C100_390_005_OtherChildrenNotInCaseWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/otherChildNotInTheCase/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_390_010_OtherChildrenNotInCase")
        .get("/data/internal/cases/#{caseId}/event-triggers/otherChildNotInTheCase?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Other children not in the case")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * No other children in application
    ======================================================================================*/

    .group("XUI_PRL_C100_400_OtherChildrenNotInCaseValidate") {
      exec(http("XUI_PRL_C100_400_005_OtherChildrenNotInCaseValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=otherChildNotInTheCase1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOtherChildrenValidate.json"))
        .check(substring("childrenNotPartInTheCaseYesNo")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit Other Children Not In Case event
    ======================================================================================*/

    .group("XUI_PRL_C100_410_OtherChildrenNotInCaseSubmit") {
      exec(http("XUI_PRL_C100_410_005_OtherChildrenNotInCaseSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLOtherChildrenSubmit.json"))
        .check(substring("trigger/otherChildNotInTheCase")))

      .exec(http("XUI_PRL_C100_410_010_OtherChildrenNotInCaseViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.events[?(@.event_id=='otherChildNotInTheCase')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.manageLabellingRoleAssignment)
      .exec(Common.waJurisdictions)
      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val OtherPeopleInCase = 

    group("XUI_PRL_C100_420_OtherPeopleInTheCase") {
      exec(http("XUI_PRL_C100_420_005_OtherPeopleInTheCaseWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/otherPeopleInTheCaseRevised/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_420_010_OtherPeopleInTheCase")
        .get("/data/internal/cases/#{caseId}/event-triggers/otherPeopleInTheCaseRevised?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Other people in the case")))
    }
    
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100_430_OtherPeopleInTheCaseValidate") {
      exec(http("XUI_PRL_C100_430_005_OtherPeopleInTheCaseValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=otherPeopleInTheCaseRevised1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOtherPeopleValidate.json"))
        .check(substring("isPlaceOfBirthKnown"))
        .check(substring("otherPersonRelationshipToChildren")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100_440_OtherPeopleInTheCaseSubmit") {
      exec(http("XUI_PRL_C100_440_005_OtherPeopleInTheCaseSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLOtherPeopleSubmit.json"))
        .check(substring("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(http("XUI_PRL_C100_440_010_OtherPeopleInTheCaseViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("Application for a court order to make arrangements for a child")))
            
      .exec(Common.waJurisdictions)
      .exec(Common.manageLabellingRoleAssignment)
    }

  val ChildrenAndApplicants = 

    /*======================================================================================
    * Click on 'Children and applicants'
    ======================================================================================*/

    group("XUI_PRL_C100_450_ChildrenAndApplicants") {
      exec(http("XUI_PRL_C100_450_005_ChildrenAndApplicantsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/childrenAndApplicants/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_450_010_ChildrenAndApplicants")
        .get("/data/internal/cases/#{caseId}/event-triggers/childrenAndApplicants?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[?(@.id=='buffChildAndApplicantRelations')].value[0].id").saveAs("applicant_one"))
        .check(jsonPath("$.case_fields[?(@.id=='buffChildAndApplicantRelations')].value[1].id").saveAs("applicant_two"))
        .check(jsonPath("$.case_fields[?(@.id=='buffChildAndApplicantRelations')].formatted_value[0].value.applicantId").saveAs("applicant_oneId"))
        .check(jsonPath("$.case_fields[?(@.id=='buffChildAndApplicantRelations')].formatted_value[1].value.applicantId").saveAs("applicant_twoId"))
        .check(jsonPath("$.case_fields[?(@.id=='buffChildAndApplicantRelations')].formatted_value[1].value.applicantId").saveAs("childId"))
        .check(substring("Create a Relation between Children and Applicants")))

    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Review and confirm details'
    ======================================================================================*/

    .group("XUI_PRL_C100_460_ChildrenAndApplicantsValidate") {
      exec(http("XUI_PRL_C100_460_005_ChildrenAndApplicantsValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=childrenAndApplicants1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndApplicantValidate.json"))
        .check(substring("buffChildAndApplicantRelations")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit the case event
    ======================================================================================*/

    .group("XUI_PRL_C100_470_ChildrenAndApplicantsSubmit") {
      exec(http("XUI_PRL_C100_470_005_ChildrenAndApplicantsSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndApplicantSubmit.json"))
        .check(substring("trigger/childrenAndApplicants")))

      .exec(http("XUI_PRL_C100_470_010_ChildrenAndApplicantsViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.events[?(@.event_id=='childrenAndApplicants')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.manageLabellingRoleAssignment)
      .exec(Common.waJurisdictions)
      .exec(Common.userDetails)
    }

  .pause(MinThinkTime, MaxThinkTime)

  val ChildrenAndRespondents = 

    /*======================================================================================
    * Click on 'Children and respondents'
    ======================================================================================*/
    
    group("XUI_PRL_C100_480_ChildrenAndRespondents") {
      exec(http("XUI_PRL_C100_480_005_ChildrenAndRespondentsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/childrenAndRespondents/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_480_010_ChildrenAndRespondents")
        .get("/data/internal/cases/#{caseId}/event-triggers/childrenAndRespondents?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[2].value[0].value.respondentFullName").saveAs("respondentName"))
        .check(jsonPath("$.case_fields[2].value[0].value.childFullName").saveAs("childName"))
        .check(jsonPath("$.case_fields[2].value[0].id").saveAs("respondentId"))
        .check(jsonPath("$.case_fields[2].value[0].value.respondentId").saveAs("respondentNameId"))
        .check(jsonPath("$.case_fields[2].value[0].value.childId").saveAs("childId"))
        .check(substring("Create a Relation between Children and Respondents")))

    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter details of a Respondent and click Continue
    ======================================================================================*/

    .group("XUI_PRL_C100_490_ChildrenAndRespondentsValidate") {
      exec(http("XUI_PRL_C100_490_005_ChildrenAndRespondentsValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=childrenAndRespondents1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndRespondentsValidate.json"))
        .check(substring("buffChildAndRespondentRelations")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confirm details and Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_500_ChildrenAndRespondentsSubmit") {
      exec(http("XUI_PRL_C100_500_005_ChildrenAndRespondentsSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndRespondentsSubmit.json"))
        .check(substring("trigger/childrenAndRespondents")))


      .exec(http("XUI_PRL_C100_500_010_ChildrenAndRespondentsViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.events[?(@.event_id=='childrenAndRespondents')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.manageLabellingRoleAssignment)
      .exec(Common.waJurisdictions)
      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val ChildrenAndOtherPeople = 

    group("XUI_PRL_C100_510_ChildrenAndOtherPeople") {
      exec(http("XUI_PRL_C100_510_005_ChildrenAndOtherPeopleWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/childrenAndOtherPeople/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_510_010_ChildrenAndOtherPeople")
        .get("/data/internal/cases/#{caseId}/event-triggers/childrenAndOtherPeople?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[2].value[0].value.otherPeopleFullName").saveAs("otherPeopleFullName0"))
        .check(jsonPath("$.case_fields[2].value[0].value.otherPeopleId").saveAs("otherPeopleSubId0"))
        .check(jsonPath("$.case_fields[2].value[0].value.childFullName").saveAs("childFullName"))
        .check(jsonPath("$.case_fields[2].value[0].value.childId").saveAs("childId"))
        .check(jsonPath("$.case_fields[2].value[0].id").saveAs("otherPeopleId0"))
        .check(jsonPath("$.case_fields[2].value[1].value.otherPeopleFullName").saveAs("otherPeopleFullName1"))
        .check(jsonPath("$.case_fields[2].value[1].value.otherPeopleId").saveAs("otherPeopleSubId1"))
        .check(jsonPath("$.case_fields[2].value[1].id").saveAs("otherPeopleId1"))
        .check(substring("Create a Relation between Children and Other People")))
    }
        
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100_520_ChildrenAndOtherPeopleValidate") {
      exec(http("XUI_PRL_C100_520_005_ChildrenAndOtherPeopleValidate")
        .post("/data/case-types/PRLAPPS/validate?pageId=childrenAndOtherPeople1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndOtherPeopleValidate.json"))
        .check(substring("buffChildAndOtherPeopleRelations")))
    }
      
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100_530_ChildrenAndOtherPeopleSubmit") {
      exec(http("XUI_PRL_C100_530_005_ChildrenAndOtherPeopleSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndOtherPeopleSubmit.json"))
        .check(substring("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(http("XUI_PRL_C100_530_010_ChildrenAndOtherPeopleViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("Application for a court order to make arrangements for a child")))
            
      .exec(Common.waJurisdictions)
      .exec(Common.manageLabellingRoleAssignment)
    }

    .pause(MinThinkTime, MaxThinkTime)


  val MIAMDetails =

    /*======================================================================================
    * Click on 'MIAM'
    ======================================================================================*/

    group("XUI_PRL_C100_540_MIAM") {
      exec(http("XUI_PRL_C100_540_005_MIAM")
        .get("/data/internal/cases/#{caseId}/event-triggers/miamPolicyUpgrade?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

    }


  val ViewPdfApplication =

    /*======================================================================================
    * Click on 'View PDF Application'
    ======================================================================================*/

    group("XUI_PRL_C100_570_ViewPdfApplication") {
      exec(http("XUI_PRL_C100_570_005_ViewPdfApplicationWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/viewPdfDocument/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_570_010_ViewPdfApplicationEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/viewPdfDocument?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.case_fields[?(@.id=='submitAndPayDownloadApplicationLink')].value.document_url").saveAs("DocumentUrl"))
        .check(jsonPath("$.case_fields[?(@.id=='submitAndPayDownloadApplicationLink')].value.document_filename").saveAs("DocumentFileName"))
        .check(jsonPath("$.case_fields[?(@.id=='submitAndPayDownloadApplicationLink')].value.document_hash").saveAs("DocumentHash"))
        .check(jsonPath("$.event_token").saveAs("event_token")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * View PDF Profile
    ======================================================================================*/

    .group("XUI_PRL_C100_580_ViewPdfProfile") {
      exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * View PDF Continue
    ======================================================================================*/

    .group("XUI_PRL_C100_590_ViewPdfContinue") {
      exec(Common.caseShareOrgs)

      .exec(http("XUI_PRL_C100_590_005_ViewPdfContinue")
        .post("/data/case-types/PRLAPPS/validate?pageId=viewPdfDocument1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLViewPdfContinue.json"))
        .check(substring("isEngDocGen")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * View PDF Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_600_ViewPdfSubmit") {
      exec(http("XUI_PRL_C100_600_005_ViewPdfSubmitEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLViewPdfContinueSubmit.json")))

      .exec(http("XUI_PRL_C100_600_010_ViewPdfSubmitViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='viewPdfDocument')]"))
        .check(jsonPath("$.state.id").is("AWAITING_SUBMISSION_TO_HMCTS")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  val SubmitAndPay =

    /*======================================================================================
    * Click on 'SubmitAndPay'
    ======================================================================================*/

    group("XUI_PRL_C100_610_SubmitAndPay") {
      exec(http("XUI_PRL_C100_610_005_SubmitAndPayWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/submitAndPay/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_610_010_SubmitAndPayEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/submitAndPay?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("submitAndPay")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Profile
    ======================================================================================*/

    .group("XUI_PRL_C100_620_SubmitAndPayProfile") {
      exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Confidentiality Statement
    ======================================================================================*/

    .group("XUI_PRL_C100_630_SubmitAndPayConfidentialityStatement") {
      exec(http("XUI_PRL_C100_630_005_SubmitAndPayConfidentialityStatement")
        .post("/data/case-types/PRLAPPS/validate?pageId=submitAndPay1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayConfidentialityStatement.json"))
        .check(substring("applicantSolicitorEmailAddress")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Declaration
    ======================================================================================*/

    .group("XUI_PRL_C100_640_SubmitAndPayDeclaration") {
      exec(http("XUI_PRL_C100_640_005_SubmitAndPayDeclaration")
        .post("/data/case-types/PRLAPPS/validate?pageId=submitAndPay2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayDeclaration.json"))
        .check(substring("feeAmount")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Continue
    ======================================================================================*/

    .group("XUI_PRL_C100_650_SubmitAndPayContinue") {
      exec(http("XUI_PRL_C100_650_005_SubmitAndPayContinue")
        .post("/data/case-types/PRLAPPS/validate?pageId=submitAndPay3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayContinue.json"))
        .check(substring("submitAndPay3")))

      .exec(Common.userDetails)
    }
    
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Now
    ======================================================================================*/

    .group("XUI_PRL_C100_660_SubmitAndPayNow") {
      exec(http("XUI_PRL_C100_660_005_SubmitAndPayNow")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayNow.json"))
        .check(substring("created_on")))

      .exec(http("XUI_PRL_C100_660_010_SubmitAndPayNowViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.events[?(@.event_id=='submitAndPay')]"))
        .check(jsonPath("$.state.id").is("SUBMITTED_NOT_PAID")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    val DummyPaymentConfirmation = 

      /*======================================================================================
    * Click on 'Dummy Payment Confirmation' Next Step Dropdown
    ======================================================================================*/

    group("XUI_PRL_C100_670_DummyPaymentConfirmation") {
      exec(http("XUI_PRL_C100_670_005_DummyPaymentConfirmation")
        .get("/workallocation/case/tasks/#{caseId}/event/testingSupportPaymentSuccessCallback/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("XUI_PRL_C100_670_010_DummyPaymentConfirmation")
        .get("/data/internal/cases/#{caseId}/event-triggers/testingSupportPaymentSuccessCallback?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("testingSupportPaymentSuccessCallback")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select make the payment
    ======================================================================================*/

    .group("XUI_PRL_C100_680_DummyPaymentConfirmationEvent") {
      exec(http("XUI_PRL_C100_680_005_DummyPaymentConfirmationEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitDummyPayment.json"))
        .check(jsonPath("$.state").is("SUBMITTED_NOT_PAID")))

      .exec(http("XUI_PRL_C100_680_010_DummyPaymentConfirmationViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("Submitted")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    val HearingsTab = 

    /*======================================================================================
    * Click on the Hearings tab to view any Hearings
    ======================================================================================*/

    group("XUI_PRL_C100_290_HearingsTab") {
      exec(http("XUI_PRL_C100_290_005_GetHearings")
        .get("/api/hearings/getHearings?caseId=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(status.in(200, 403)))

      .exec(http("XUI_PRL_C100_290_010_GetHearingsJurisdiction")
        .post("/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"caseReference":"#{caseId}"}"""))
        .check(substring("hearing-facilities")))

      .exec(http("XUI_PRL_C100_290_015_GetRoleAssignments")
        .get("/api/user/details?refreshRoleAssignments=undefined")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*"))

      .exec(http("XUI_PRL_C100_290_020_GetHearingTypes")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingType")))
    }

    .pause(MinThinkTime, MaxThinkTime)

}