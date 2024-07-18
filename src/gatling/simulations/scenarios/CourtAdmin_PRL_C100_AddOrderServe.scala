package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, CsrfCheck, Environment, Headers}
import java.io.{BufferedWriter, FileWriter}

/*===============================================================================================================
* Court Admin C100 case progression. Send to local court --> Sent to Gatekeeper --> Add an order --> Serve 
================================================================================================================*/

object CourtAdmin_PRL_C100_AddOrderServe {
  
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*======================================================================================
  * Fast & dirty event trigger and event requests to create C100 cases as a Solicitor to submitted state
  =======================================================================================*/

  val CaseCreationSolicitor =                                                 //**** For Quick Search

      // Set Vars
      exec(_.setAll(
        "C100ApplicantFirstName1" -> (Common.randomString(5) + "APP"),
        "C100ApplicantLastName1" -> (Common.randomString(5) + "TEST"),
        "C100ApplicantFirstName2" -> (Common.randomString(5) + "APP"),
        "C100ApplicantLastName2" -> (Common.randomString(5) + "TEST"),
        "C100RespondentFirstName" -> (Common.randomString(5) + "RESP"),
        "C100RespondentLastName" -> (Common.randomString(5) + "TEST"),
        "C100ChildFirstName" -> (Common.randomString(5) + "CHILD"),
        "C100ChildLastName" -> (Common.randomString(5) + "TEST"),
        "C100RepresentativeFirstName" -> (Common.randomString(5) + "REP"),
        "C100RepresentativeLastName" -> (Common.randomString(5) + "TEST"),
        "C100SoleTraderName" -> (Common.randomString(5) + "SOLE"),
        "C100SolicitorName" -> (Common.randomString(5) + "SOLIC"),
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

    /*======================================================================================
    * Click the Create Case link
     ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("PRIVATELAW")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Jurisdiction = Family Private Law; Case Type = C100 & FL401 Applications; Event = Solicitor Application
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_010_StartApplication")
        .get("/data/internal/case-types/PRLAPPS/event-triggers/solicitorCreate?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("solicitorCreate")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_020_CheckYourAnswers")
        .post("/data/case-types/PRLAPPS/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCheckYourAnswers.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(jsonPath("$.state").is("AWAITING_SUBMISSION_TO_HMCTS")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Type of Application' link
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_030_CreateTypeOfApplicationEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/selectApplicationType?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("selectApplicationType")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Draft Consent Order Upload
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_040_ConsentOrderUpload")
        .post("/documentsv2")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("documentHash"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_050_CheckYourAnswers")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCheckYourAnswersTypeOfApplication.json"))
        .check(substring("applicationPermissionRequired"))
        .check(jsonPath("$.state").is("AWAITING_SUBMISSION_TO_HMCTS")))
    
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Hearing Urgency'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_060_HearingUrgencyEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/hearingUrgency?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("hearingUrgency")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Check Your Answers
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_070_HearingUrgencyCheckYourAnswers")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLHearingUrgencyAnswers.json"))
        .check(substring("trigger/hearingUrgency")))

    .pause(MinThinkTime, MaxThinkTime)

   /*======================================================================================
    * Click on 'Applicant Details'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_080_ApplicantDetailsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/applicantsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("applicantsDetails")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Applicant Details Check Your Answers
    ======================================================================================*/

      .exec(Common.postcodeLookup)

      .exec(http("XUI_PRL_C100_XXX_090_ApplicantDetailsCheckYourAnswers")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLApplicantDetailsAnswers.json"))
        .check(substring("trigger/applicantsDetails")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Respondent Details'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_100_RespondentDetailsTrigger")
        .get("/cases/case-details/#{caseId}/trigger/respondentsDetails/respondentsDetails1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(http("XUI_PRL_C100_XXX_110_RespondentDetailsEventTrigger")
        .get("/data/internal/cases/#{caseId}/event-triggers/respondentsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Details of the respondents in the case")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Respondent Details Submit
    ======================================================================================*/
      .exec(http("XUI_PRL_C100_XXX_120_RespondentDetailsSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetailsSubmit.json"))
        .check(substring("trigger/respondentsDetails")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Child Details'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_120_ChildDetailsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/childDetailsRevised?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[1].value[0].value.whoDoesTheChildLiveWith.list_items[*].code").findAll.saveAs("liveWithListItemsCode"))
        .check(jsonPath("$.case_fields[1].value[0].value.whoDoesTheChildLiveWith.list_items[*].label").findAll.saveAs("liveWithListItemsLabel"))
        .check(jsonPath("$.case_fields[1].value[0].value.whoDoesTheChildLiveWith.list_items").findAll.saveAs("liveWithListItems"))
        .check(jsonPath("$.case_fields[1].value[0].id").saveAs("childDetailsID"))
        .check(jsonPath("$.id").is("childDetailsRevised")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Answer Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_130_ChildDetailsAdditionalDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetails.json"))
        .check(substring("trigger/childDetailsRevised")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Miam'
    ======================================================================================*/

    .exec(http("XUI_PRL_C100_XXX_140_MIAMTrigger")
        .get("/cases/case-details/trigger/miam/miam1")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

    .exec(http("XUI_PRL_C100_XXX_150_MIAMEventTrigger")
        .get("/data/internal/cases/#{caseId}/event-triggers/respondentsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("submissionRequiredFieldsInfo1")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * MIAM Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_150_MIAMSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLMIAMDetailsSubmit.json"))
        .check(substring("trigger/miam")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Allegations Of Harm'
    ======================================================================================*/
     
      .exec(http("XUI_PRL_C100_XXX_160_AllegationsOfHarmEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/allegationsOfHarmRevised?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Allegations of Harm Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_170_AllegationsOfHarmSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAreThereAllegationsOfHarmSubmit.json")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Other children not in the case'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_180_OtherChildrenNotInCase")
        .get("/data/internal/cases/#{caseId}/event-triggers/otherChildNotInTheCase?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Other children not in the case")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit Other Children Not In Case event
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_190_OtherChildrenNotInCaseSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLOtherChildrenSubmit.json"))
        .check(substring("trigger/otherChildNotInTheCase")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Children and applicants'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_200_ChildrenAndApplicants")
        .get("/data/internal/cases/#{caseId}/event-triggers/childrenAndApplicants?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[2].value[0].id").saveAs("applicant_one"))
        .check(jsonPath("$.case_fields[2].value[1].id").saveAs("applicant_two"))
        .check(jsonPath("$.case_fields[2].formatted_value[0].value.applicantId").saveAs("applicant_oneId"))
        .check(jsonPath("$.case_fields[2].formatted_value[1].value.applicantId").saveAs("applicant_twoId"))
        .check(jsonPath("$.case_fields[2].formatted_value[1].value.applicantId").saveAs("childId"))
        .check(substring("Create a Relation between Children and Applicants")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit the case event
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_210_ChildrenAndApplicantsSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndApplicantSubmit.json"))
        .check(substring("trigger/childrenAndApplicants")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Children and respondents'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_220_ChildrenAndRespondents")
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

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confirm details and Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_230_ChildrenAndRespondentsSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/prl/c100/PRLChildrenAndRespondentsSubmit.json"))
        .check(substring("trigger/childrenAndRespondents")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'View PDF Application'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_240_ViewPdfApplicationRedirectEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/viewPdfDocument?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.case_fields[?(@.id=='submitAndPayDownloadApplicationLink')].value.document_url").saveAs("DocumentUrl"))
        .check(jsonPath("$.case_fields[?(@.id=='submitAndPayDownloadApplicationLink')].value.document_filename").saveAs("DocumentFileName"))
        .check(jsonPath("$.case_fields[?(@.id=='submitAndPayDownloadApplicationLink')].value.document_hash").saveAs("DocumentHash"))
        .check(jsonPath("$.event_token").saveAs("event_token")))

     .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * View PDF Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_250_ViewPdfSubmitViewCase")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLViewPdfContinueSubmit.json")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'SubmitAndPay'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_260_SubmitAndPayRedirectEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/submitAndPay?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("submitAndPay")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Now
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_270_SubmitAndPayNow")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSubmitAndPayNow.json"))
        .check(substring("created_on")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Dummy Payment Confirmation from Next Step & Make Payment *Temp*
    ======================================================================================*/

    .group("XUI_PRL_C100_XXX_280_MakeDummyPayment") {
      exec(http("XUI_PRL_C100_XXX_280_MakePaymentEventTrigger")
        .get("/data/internal/cases/#{caseId}/event-triggers/testingSupportPaymentSuccessCallback?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("testingSupportPaymentSuccessCallback"))
        .check(status.in(200, 403)))

      .exec(http("XUI_PRL_C100_XXX_280_005_MakePaymentEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLDummyPaymentEvent.json"))
        .check(jsonPath("$.state").is("SUBMITTED_NOT_PAID"))) 
      
    }
  
  .pause(MinThinkTime, MaxThinkTime)

  val ProgressCaseCourtAdmin =                                                                                //**** For Quick Search

  //session => session.set("taskId","#{respTaskId}")

  /*=====================================================================================
   * Select case
   ======================================================================================*/

   exec(http("XUI_PRL_XXX_290_SelectCase")
      .get(BaseURL + "/data/internal/cases/#{caseId}")
      .headers(Headers.commonHeader)
      .check(jsonPath("$.case_id").is("#{caseId}")))

      .exec(Common.waJurisdictions)
      .exec(Common.activity)
      .exec(Common.userDetails)
      .exec(Common.caseActivityGet)
      .exec(Common.isAuthenticated)

  .pause(MinThinkTime, MaxThinkTime)

   /*=====================================================================================
   * Select task tab 
   ======================================================================================*/

    .exec(http("XUI_PRL_XXX_300_SelectCase")
      .get(BaseURL + "/cases/case-details/#{caseId}/task")
      .headers(Headers.commonHeader)
      .check(substring("HMCTS Manage cases")))

     .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
      
    .exec(Common.activity)
    .exec(Common.configUI)
    .exec(Common.configJson)
    .exec(Common.userDetails)

    .exec(http("XUI_PRL_XXX_310_SelectCaseTask")
      .get(BaseURL + "/workallocation/case/task/#{caseId}")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .check(regex(""""id":"(.*)","name":""").optional.saveAs("respTaskId"))
      .check(regex(""","type":"(.*)","task_state":""").optional.saveAs("respTaskType")))
      //Save taskType from response
    .exec(session => {
      // Initialise task type in session if it's not already present, ensure the variable exists before entering Loop
      session("respTaskType").asOption[String] match {
      case Some(taskType) => session
      case None => session.set("respTaskType", "")
    }
  })
    // Loop until the task type matches "checkApplicationC100"
    .asLongAs(session => session("respTaskType").as[String] != "checkApplicationC100") {
      exec(http("XUI_PRL_XXX_310_SelectCaseTaskRepeat")
        .get(BaseURL + "/workallocation/case/task/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(regex(""""id":"(.*)","name":""").optional.saveAs("respTaskId"))
        .check(regex(""","type":"(.*)","task_state":""").optional.saveAs("respTaskType")))
      .pause(5, 10) // Wait between retries
    // Log task Type
    .exec (session => {
      println(s"Current respTaskType: ${session("respTaskType").as[String]}")
      session
    })
  }
    .exec(Common.userDetails)
    .exec(Common.waUsersByServiceName)
    .exec(Common.caseActivityGet)
    .exec(Common.monitoringTools)
    .exec(Common.isAuthenticated)

    .exec(http("XUI_PRL_XXX_320_SelectCase")
      .get(BaseURL + "/data/internal/cases/#{caseId}")
      .headers(Headers.commonHeader)
      .check(jsonPath("$.case_type.name").is("C100 & FL401 Applications")))

    .exec(Common.activity)

.pause(MinThinkTime, MaxThinkTime)

/*=====================================================================================
* Select Assign to me
======================================================================================*/

    .exec(http("XUI_PRL_XXX_330_AssignToMeClaim")
      .post(BaseURL + "/workallocation/task/#{respTaskId}/claim")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .check(status.in(200, 204)))

    .exec(http("XUI_PRL_XXX_340_AssignToMe")
      .post(BaseURL + "/workallocation/case/task/#{caseId}")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(StringBody("""{"refined":true}"""))
      .check(regex("""","assignee":"(.*)","type":""").saveAs("asigneeUserId"))
      .check(regex("""","task_state":"(.*)","task_system":"""").is("assigned")))

    .exec(Common.isAuthenticated)
    .exec(Common.caseActivityPost)
    .exec(Common.caseActivityOnlyGet)

  .pause(MinThinkTime, MaxThinkTime)

  /*=====================================================================================
   * Select Complete task
   ======================================================================================*/

    .exec(http("XUI_PRL_XXX_350_SelectCaseTasks&Complete")
      .post(BaseURL + "/workallocation/case/task/#{caseId}/complete")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(StringBody("""{"hasNoAssigneeOnComplete":false}""")))


    .exec(Common.isAuthenticated)
    .exec(Common.manageLabellingRoleAssignment)
    .exec(Common.waJurisdictions)
 
    .exec(http("XUI_PRL_XXX_360_MarkAsDone")
      .post(BaseURL + "/workallocation/case/task/#{caseId}")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(StringBody("""{"refined":true}"""))
      .check(regex("""","assignee":"(.*)","type":""").saveAs("asigneeUserId"))
      .check(regex("""","task_state":"(.*)","task_system":"""").is("assigned")))

    .exec(Common.activity)
    .exec(Common.userDetails)
    .exec(Common.activity)

  .pause(MinThinkTime, MaxThinkTime)

  /*=====================================================================================
  * Select Issue and send to local Court
  ======================================================================================*/

    .exec(http("XUI_PRL_XXX_360_IssueAndSendToLocalCourt")
        .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/issueAndSendToLocalCourtCallback/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json")
        .check(jsonPath("$.task_required_for_event").is("false")))

      .exec(Common.activity)
      .exec(Common.profile)

      .exec(http("XUI_PRL_XXX_370_IssueAndSendToLocalCourtEventTrigger")  //*** SAVE THE Courtlist response here for use in later post requests **
        .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/issueAndSendToLocalCourtCallback?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("issueAndSendToLocalCourtCallback"))
        //.check(jsonPath("$.case_fields[1].value.list_items").saveAs("courtListItems"))
        .check(status.in(200, 403)))

      .exec(http("XUI_PRL_XXX_380_IssueAndSendToLocalCourtEvent")
        .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/issueAndSendToLocalCourtCallback/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.navigationHeader)
        .header("accept", "application/json"))
        //.check(substring("PRIVATELAW")))
      
      .exec(Common.caseActivityPost)
      .exec(Common.userDetails)
      .exec(Common.caseActivityOnlyGet)

    .pause(MinThinkTime, MaxThinkTime)

  /*=====================================================================================
   * Select Court from dropdown and submit
   ======================================================================================*/

    .exec(http("XUI_PRL_XXX_390_SelectCourt")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=issueAndSendToLocalCourtCallback1")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/courtAdmin/PRLLocalCourt.json"))
      .check(jsonPath("$.data.courtList.value.code").is("234946:")))  //Value does not change for now. 

  .pause(MinThinkTime, MaxThinkTime)

    .exec(Common.activity)
    .exec(Common.userDetails)
    .exec(Common.activity)

    .exec(http("XUI_PRL_XXX_400_SubmitToLocalCourt")
      .get(BaseURL + "/workallocation/task/#{respTaskId}")
      .headers(Headers.navigationHeader)
      .header("accept", "application/json")
      //.check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("gateKeeper_id"))
      .check(regex("""","task_state":"(.*)","task_system":"""").is("assigned")))

    .exec(http("XUI_PRL_XXX_410_SubmitToLocalCourtEvent")
      .post(BaseURL + "/data/cases/#{caseId}/events")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/courtAdmin/PRLLocalCourtSubmit.json"))
      .check(jsonPath("$.data.courtList.value.code").is("234946:")))  //Value does not change for now. 


/*======================================================================================
  * Click on 'Send to Gate Keeper'
  ======================================================================================*/

    .exec(http("XUI_PRL_XXX_420_SendToGateKeeper")
      .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/sendToGateKeeper?ignore-warning=false")
      .headers(Headers.navigationHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      //.check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("gateKeeper_id"))
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(jsonPath("$.id").is("sendToGateKeeper")))

      .exec(Common.userDetails)
      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Add Gate Keeper
  ======================================================================================*/

    .exec(http("XUI_PRL_XXX_430_AddGateKeeper")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=sendToGateKeeper1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLAddGateKeeper.json"))
        .check(substring("gatekeeper")))

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Send to Gate Keeper Submit
  ======================================================================================*/

    .exec(http("XUI_PRL_XXX_440_GateKeeperSubmit")
        .post(BaseURL + "/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLAddGateKeeperSubmit.json"))
        .check(substring("GATE_KEEPING")))

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Click on 'Manage Orders'
  ======================================================================================*/

    .group("XUI_PRL_XXX_450_ManageOrders") {
      exec(http("XUI_PRL_XXX_450_005_ManageOrders")
        .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/manageOrders?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("manageOrders")))

        .exec(Common.userDetails)
        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Create an Order
  ======================================================================================*/

    .group("XUI_PRL_XXX_460_CreateOrder") {
      exec(http("XUI_PRL_XXX_460_005_reateOrder")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLCreateOrder.json"))
        .check(substring("SearchCriteria")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Select Order - Special guardianship order (C43A)
  ======================================================================================*/

    .group("XUI_PRL_XXX_470_SelectOrder") {
      exec(http("XUI_PRL_XXX_470_005_SelectOrder")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin//PRLSelectOrder.json"))
        .check(substring("abductionChildHasPassport")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order Details
  ======================================================================================*/

      .group("XUI_PRL_XXX_480_OrderDetails") {
        exec(http("XUI_PRL_XXX_480_005_OrderDetails")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders4")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/courtAdmin/PRLOrderDetails.json"))
          .check(substring("isEngDocGen")))
      }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Guardian Name
  ======================================================================================*/

      .group("XUI_PRL_XXX_490_GuardianName") {
        exec(http("XUI_PRL_XXX_490_005_GuardianName")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders10")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/courtAdmin/PRLGuardianName.json"))
          .check(jsonPath("$.data.previewOrderDoc.document_url").saveAs("document_url"))
          .check(jsonPath("$.data.previewOrderDoc.document_hash").saveAs("document_hash"))
          .check(substring("previewOrderDoc")))
      }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Check Your Order
  ======================================================================================*/

    .group("XUI_PRL_XXX_500_CheckYourOrder") {
      exec(http("XUI_PRL_XXX_500_005_CheckYourOrder")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders16")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLCheckOrder.json"))
        .check(substring("previewOrderDoc")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order Recipients
  ======================================================================================*/

    .group("XUI_PRL_XXX_510_OrderRecipients") {
      exec(http("XUI_PRL_XXX_510_005_OrderRecipients")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders17")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLOrderRecipients.json"))
        .check(substring("orderRecipients")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order Submit
  ======================================================================================*/

    .group("XUI_PRL_XXX_520_OrderSubmit") {
      exec(http("XUI_PRL_XXX_520_005_OrderSubmit")
        .post(BaseURL + "/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLOrderSubmit.json"))
        .check(substring("GATE_KEEPING")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Click on 'Service of Application'
  ======================================================================================*/

    .group("XUI_PRL_XXX_530_ServiceOfApplication") {
      exec(http("XUI_PRL_XXX_530_005_ServiceOfApplication")
        .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/serviceOfApplication?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("serviceOfApplication")))

        .exec(Common.userDetails)

        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * PD36Q letter Upload
  ======================================================================================*/

    .group("XUI_PRL_XXX_540_PD36QUpload") {
      exec(http("XUI_PRL_XXX_540_005_PD36QUpload")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
      .header("x-xsrf-token", "${XSRFToken}")
      .bodyPart(RawFileBodyPart("files", "TestFile.pdf")
        .fileName("TestFile.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "PRLAPPS")
      .formParam("jurisdictionId", "PRIVATELAW")
      .check(substring("originalDocumentName"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("documentHashPD36Q"))
      .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLPD36Q")))

     }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Special arrangements letter  Upload
  ======================================================================================*/

    .group("XUI_PRL_XXX_550_SpecialArrangementsUpload") {
      exec(http("XUI_PRL_XXX_550_005_SpecialArrangementsUpload")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TestFile2.pdf")
          .fileName("TestFile2.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "PRLAPPS")
        .formParam("jurisdictionId", "PRIVATELAW")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("documentHashSpecial"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLSpecial")))

    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Additional documents Upload
  ======================================================================================*/

    .group("XUI_PRL_XXX_560_AdditionalDocumentsUpload") {
      exec(http("XUI_PRL_XXX_560_005_AdditionalDocumentsUpload")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
         .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TestFile3.pdf")
          .fileName("TestFile3.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "PRLAPPS")
        .formParam("jurisdictionId", "PRIVATELAW")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("documentHashAdditional"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLAdditional")))

    }

  .pause(MinThinkTime, MaxThinkTime)
    //Need to change these files when perftest acc works

  /*======================================================================================
  * Service of Application document uploads
  ======================================================================================*/

    .group("XUI_PRL_XXX_570_DocumentUpload") {
      exec(http("XUI_PRL_570_005_DocumentUpload")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplicationorderDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLSoADocuments.json"))
        .check(substring("additionalDocuments")))
    }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Service of Application Confirm recipients
  ======================================================================================*/

    .group("XUI_PRL_XXX_580_ServiceRecipients") {
      exec(http("XUI_PRL_XXX_580_005_ServiceRecipients")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplicationconfirmRecipients")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLSoARecipients.json"))
        .check(substring("confirmRecipients")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Service of Application Submit
  ======================================================================================*/

    .group("XUI_PRL_XXX_590_ServiceSubmit") {
      exec(http("XUI_PRL_XXX_590_005_ServiceSubmit")
        .post(BaseURL + "/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/courtAdmin/PRLSoASubmit.json"))
        .check(regex("""accessCode":"(\w{8})""").saveAs("prlAccessCode")))


      .exec(Common.waJurisdictions)
      .exec(Common.configurationui)
      .exec(Common.configUI)
      .exec(Common.configJson)
      .exec(Common.TsAndCs)
      .exec(Common.isAuthenticated)
      .exec(Common.monitoringTools)
      .exec(Common.isAuthenticated)
      .exec(Common.activity)
      .exec(Common.caseActivityOnlyGet)
      .exec(Common.userDetails)
      .exec(Common.userDetails)    //Duplicate 
      .exec(Common.activity)
      .exec(Common.userDetails)
      .exec(Common.caseActivityPost)
      .exec(Common.caseActivityOnlyGet)

}  
}










