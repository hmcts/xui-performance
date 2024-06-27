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

  val CaseCreationSolicitor =

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

      .exec(http("XUI_FPL_XXX_001_StartApplication")
        .get("/data/internal/case-types/PRLAPPS/event-triggers/solicitorCreate?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("solicitorCreate")))

     // .exec(Common.userDetails)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_002_CheckYourAnswers")
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

      .exec(http("XUI_PRL_C100_XXX_003_CreateTypeOfApplicationEvent")
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

      .exec(http("XUI_PRL_C100_XXX_004_ConsentOrderUpload")
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

      .exec(http("XUI_PRL_C100_XXX_005_CheckYourAnswers")
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

      .exec(http("XUI_PRL_C100_XXX_006_HearingUrgencyEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/hearingUrgency?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("hearingUrgency")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Urgency Check Your Answers
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_007_HearingUrgencyCheckYourAnswers")
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

      .exec(http("XUI_PRL_C100_XXX_008_ApplicantDetailsEvent")
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

      .exec(http("XUI_PRL_C100_XXX_009_ApplicantDetailsCheckYourAnswers")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLApplicantDetailsAnswers.json"))
        .check(substring("trigger/applicantsDetails")))

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Click on 'Child Details'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_010_ChildDetailsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/childDetailsRevised?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("childDetailsRevised")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Child Details Answer Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_011_ChildDetailsAdditionalDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetails.json"))
        .check(substring("trigger/childDetailsRevised")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Respondent Details'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_012_RespondentDetailsCaseEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/respondentsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Details of the respondents in the case")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Respondent Details Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_013_RespondentDetailsSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLChildAdditionalDetailsSubmit.json"))
        .check(substring("trigger/respondentsDetails")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Miam'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_014_MIAMCaseEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/respondentsDetails?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("submissionRequiredFieldsInfo1")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * MIAM Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_015_MIAMSubmit")
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
     
      .exec(http("XUI_PRL_C100_XXX_016_AllegationsOfHarmEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/allegationsOfHarmRevised?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Allegations of Harm Submit
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_017_AllegationsOfHarmSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAreThereAllegationsOfHarmSubmit.json")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'Other children not in the case'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_018_OtherChildrenNotInCase")
        .get("/data/internal/cases/#{caseId}/event-triggers/otherChildNotInTheCase?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Other children not in the case")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit Other Children Not In Case event
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_019_OtherChildrenNotInCaseSubmit")
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

      .exec(http("XUI_PRL_C100_XXX_020_ChildrenAndApplicants")
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

      .exec(http("XUI_PRL_C100_XXX_021_ChildrenAndApplicantsSubmit")
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

      .exec(http("XUI_PRL_C100_XXX_022_ChildrenAndRespondents")
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

      .exec(http("XUI_PRL_C100_XXX_023_ChildrenAndRespondentsSubmit")
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

      .exec(http("XUI_PRL_C100_XXX_024_ViewPdfApplicationRedirectEvent")
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

      .exec(http("XUI_PRL_C100_XXX_025_ViewPdfSubmitViewCase")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLViewPdfContinueSubmit.json")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on 'SubmitAndPay'
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_026_SubmitAndPayRedirectEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/submitAndPay?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("submitAndPay")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit and Pay Now
    ======================================================================================*/

      .exec(http("XUI_PRL_C100_XXX_027_SubmitAndPayNow")
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

    .group("XUI_PRL_C100_XXX_MakeDummyPayment") {
      exec(http("XUI_PRL_C100_XXX_028_MakePaymentEventTrigger")
        .get("/data/internal/cases/#{caseId}/event-triggers/testingSupportPaymentSuccessCallback?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("testingSupportPaymentSuccessCallback"))
        .check(status.in(200, 403)))

      .exec(http("XUI_PRL_C100_XXX_029_MakePaymentEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLDummyPaymentEvent.json"))
        .check(jsonPath("$.state").is("SUBMITTED_NOT_PAID"))) 
      
    }
  
    .pause(MinThinkTime, MaxThinkTime)

}










