package scenarios

import io.gatling.core.Predef.{exec, substring, _}
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}
import scala.concurrent.duration._



/*======================================================================================
* Create a new General Application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_CivilGeneral {
  
  val IdamUrl = Environment.idamURL
  val BaseURL = Environment.baseURL
  
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  
  val CreateGeneralApplication =
  
  //set session variables
    exec(_.setAll(
      "applicant1FirstName" -> ("App1" + Common.randomString(5)),
      "applicant1LastName" -> ("Test" + Common.randomString(5)),
      "applicant2FirstName" -> ("App2" + Common.randomString(5)),
      "applicant2LastName" -> ("Test" + Common.randomString(5)),
      "marriageDay" -> Common.getDay(),
      "marriageMonth" -> Common.getMonth(),
      "Idempotencynumber" -> Common.getIdempotency(),
      "currentDate" -> Common.getDate(),
      "futureDate" -> Common.getFutureDate(),
      "currentDateTime" -> Common.getCurrentDateTime(),
      "requestId" -> Common.getRequestId(),
      "marriageYear" -> Common.getMarriageYear()))
  
      /*======================================================================================
              Click On Case - To Get the case details
              ======================================================================================*/
  
      //  val getcasedetailspage=
      .group("XUI_GA_30_CaseDetails") {
        exec(http("XUI_GA_30_005_CaseDetails")
          .get("/data/internal/cases/#{caseId}")
          .headers(Headers.commonHeader)
          .check(substring("CASE_ISSUED"))
          .check(status.in(200, 201, 304))
        )
          .exec(http("XUI_GA_30_010_RoleAssignment")
            .get("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
            .headers(Headers.commonHeader)
            .check(status.in(200, 201, 304))
          )
          .exec(http("XUI_GA_30_015_CaseDetailsWA")
            .get("/api/wa-supported-jurisdiction/get")
            .headers(Headers.commonHeader)
            .check(status.in(200, 201, 204, 304))
          )
      }
      
      
      /*======================================================================================
         Click On Create Application - To Create a General Application
         ======================================================================================*/
      .group("XUI_GA_040_InitiateGACase") {
        exec(http("XUI_GA_040_005_InitiateGACase")
          .get("/workallocation/case/tasks/#{caseId}/event/INITIATE_GENERAL_APPLICATION/caseType/CIVIL/jurisdiction/CIVIL")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .check(status.in(200,201,304)))
          
          .exec(http("XUI_GA_040_010_CreateGACase")
            .get("/data/internal/cases/#{caseId}/event-triggers/INITIATE_GENERAL_APPLICATION?ignore-warning=false")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
            .check(substring("INITIATE_GENERAL_APPLICATION"))
            .check(status.in(200,201,304))
            .check(jsonPath("$.event_token").optional.saveAs("event_token_ga")))
          
          .exec(http("XUI_GA_040_015_GetGACase")
            .get("/workallocation/case/tasks/#{caseId}/event/INITIATE_GENERAL_APPLICATION/caseType/CIVIL/jurisdiction/CIVIL")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
          .check(status.in(200,201,304)))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
    * Jurisdiction = Civil; Case Type = General; Event = Application Type
    ======================================================================================*/
      
      .group("XUI_GA_050_ApplicationType") {
        exec(http("XUI_GA_050_005_ApplicationType")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONGATypePage")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationType.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONGATypePage"))
          .check(status.in(200,201,304))
        )
        
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
    * Choose Hearing date as No - Hearing screen
    ======================================================================================*/
      
      .group("XUI_GA_060_HearingDate") {
        exec(http("XUI_GA_060_005_HearingDate")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONGAHearingDate")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralHearingDate.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONGAHearingDate"))
          .check(status.in(200,201,304)))
        
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      /*======================================================================================
    * Has the respondent agreed to the order that you want the judge to make? Yes
    ======================================================================================*/
      
      .group("XUI_GA_070_RespondentAgree") {
        exec(http("XUI_GA_070_005_RespondentAgree")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONGARespondentAgreementPage")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralRespondentAgreement.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONGARespondentAgreementPage"))
          .check(status.in(200,201,304)))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
    * Is this an urgent application? No
    ======================================================================================*/
      
      .group("XUI_GA_080_IsUrgent") {
        exec(http("XUI_GA_080_005_IsUrgent")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONGAUrgencyRecordPage")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GAUrgencyRecord.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONGAUrgencyRecordPage"))
          .check(status.in(200,201,304)))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
  
      /*======================================================================================
    * Is this an urgent application? No
    ======================================================================================*/
  
      .group("XUI_GA_090_JudgeNoticePage") {
        exec(http("XUI_GA_090_005_JudgeNoticePage")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONGAWithOrWithoutNoticePage")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GAJudgeNotice.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONGAWithOrWithoutNoticePage"))
          .check(status.in(200, 201, 304)))
      }
  
      .pause(MinThinkTime, MaxThinkTime)
      /*======================================================================================
         * Make An Application - Upload a File
         ======================================================================================*/
      .group("XUI_GA_90_UploadDoc") {
        exec(http("XUI_GA_90_005_UploadCertificate")
          .post("/documentsv2")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .header("x-xsrf-token", "#{XSRFToken}")
          .bodyPart(RawFileBodyPart("files", "pdf-sample.pdf")
            .fileName("pdf-sample.pdf")
            .transferEncoding("binary"))
          .asMultipartForm
          .formParam("classification", "PUBLIC")
          .formParam("caseTypeId", "CIVIL")
          .formParam("jurisdictionId", "CIVIL")
          .check(substring("originalDocumentName"))
          .check(jsonPath("$.documents[0].hashToken").saveAs("documentHash1"))
          .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL1"))
          .check(status.in(200,201,304)))
        
      }
      
      /*======================================================================================
    * Make An Application - Statement Of Truth
    ======================================================================================*/
      
      .group("XUI_GA_100_StatementOfTruth") {
        exec(http("XUI_GA_100_005_StatementOfTruth")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONStatementOfTruth")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralSOT.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONStatementOfTruth"))
          .check(status.in(200,201,304)))
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
    * Make An Application - Hearing Details
    ======================================================================================*/
      
      .group("XUI_GA_110_HearingDetails") {
        exec(http("XUI_GA_110_005_HearingDetails")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONHearingDetails")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilgeneralHearingDetails.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONHearingDetails"))
          .check(status.in(200,201,304)))
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
    * Make An Application - Paying For an Application
    ======================================================================================*/
      
      .group("XUI_GA_120_PayForApplication") {
        exec(http("XUI_GA_110_005_PayForApplication")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONGAPBADetailsGAspec")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilgeneralPBADetails.json"))
          .check(substring("INITIATE_GENERAL_APPLICATIONGAPBADetailsGAspec"))
          .check(status.in(200,201,304)))
        
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
    * Submit Application
    ======================================================================================*/
      
      .group("XUI_GA_130_SubmitApplication") {
        exec(http("XUI_GA_130_005_SubmitGA")
          .post("/data/cases/#{caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralCheckAnswers.json"))
          .check(substring("SUCCESS"))
          
          .check(status.in(200,201,304)))
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      .pause(30)
      
      /*======================================================================================
             * Get back to case details
        ======================================================================================*/
      
      .group("XUI_GA_140_GACaseDetails") {
        exec(http("XUI_GA_130_005_RoleAssignment")
          .post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .check(status.in(200,201,204,304)))
          
          .exec(http("XUI_GA_130_010_WA")
            .get("/api/wa-supported-jurisdiction/get")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .check(substring("CIVIL"))
            .check(status.in(200,201,304)))
          
          .exec(http("XUI_GA_130_015_GetGAJurisDetails")
            .get("/api/wa-supported-jurisdiction/get")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
          .check(status.in(200,201,304)))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      
      
      /*======================================================================================
    * Refresh Application Page
    ======================================================================================*/
      .group("XUI_GA_140_RefreshCaseDetails") {
        exec(http("XUI_GA_140_05_GetGADetailsRefresh")
          .get("/cases/case-details/#{caseId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .header("x-xsrf-token", "#{XSRFToken}")
          .check(status.in(200,201,304)))
          
          
          .exec(Common.configurationui)
          
          .exec(Common.configUI)
          
          .exec(Common.TsAndCs)
          
          .exec(Common.userDetails)
          
          .exec(Common.isAuthenticated)
          
          .exec(http("XUI_GA_140_010_GetApplicationDetails")
            .get("/data/internal/cases/#{caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .header("x-xsrf-token", "#{XSRFToken}")
           // .check(jsonPath("$.caseLink.CaseReference").optional.saveAs("ga_case_id"))
            //.check(jsonPath("$.caseLink.CaseReference").findAll.optional.saveAs("gacases"))
            .check(jsonPath("$..value[0].value.caseLink.CaseReference").optional.saveAs("ga_case_id"))
  
            .check(status.in(200,201,304))
          )
         /* .exec { session =>
           
            println(s"First Occurrence: "+session("ga_case_id").toString)
            session
          }*/
      }
  
  
  /*======================================================================================
    * Making a Payment using Payment Service Request - click on Service Request
    ======================================================================================*/
  val PBSPayment =
  // payment pba fee payment
    group("XUI_GA_150_ServiceRequestForPay") {
        exec(http("CIVIL_CreateGA_150_ServiceRequestToPay")
          .get("/pay-bulkscan/cases/#{ga_case_id}")
          .headers(Headers.commonHeader)
          .check(status.in(200, 304))
        )
    }
      
      /*======================================================================================
            * Click On pay from Service Page
       ==========================================================================================*/
      
      .group("XUI_GA_160_BackToCaseDetails") {
        exec(http("CIVIL_CreateGA_160_005_CaseDetails")
          .get("/payments/cases/#{ga_case_id}/paymentgroups")
          .headers(Headers.commonHeader)
          .check(status.in(200, 201, 304))
        )
          
          .exec(http("GA_Create_Case_160_010_Caselists")
            .get("/payments/case-payment-orders?case_ids=#{ga_case_id}")
            .headers(Headers.commonHeader)
            .check(jsonPath("$.content[0].orderReference").optional.saveAs("serviceRef"))
            .check(status.in(200,201, 304))
          )
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      //choose PBA Payments
      .group("XUI_GA_170_ChoosePBAPayments") {
        exec(http("CIVIL_CreateGA_170_005_ChoosePBAPayments")
          .get("/payments/pba-accounts")
          .headers(Headers.commonHeader)
          .check(substring("organisationEntityResponse"))
          
          .check(status.in(200, 201, 304))
        )
      }
      
      //Final Payment
      
      .group("CIVIL_CreateGA_180_FinalPayment") {
        exec(http("CIVIL_CreateGA_180_005_CaseDetails")
          .post("/payments/service-request/#{serviceRef}/pba-payments")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralPBAPayment.json"))
          .check(substring("success"))
          .check(status.in(200, 201, 304))
        )
      }
      .pause(MinThinkTime, MaxThinkTime)
      .pause(50)
      
      val defResponseToGA=
  /*======================================================================================
    Login as respondent and go to applications click on application to respond
    ======================================================================================*/
  
      /*======================================================================================
    * Refresh Application Page
    ======================================================================================*/
      
      group("XUI_GA_190_RefreshApplicationDetailsn") {
        exec(http("XUI_GA_190_05_GetApplicationDetails")
          .get("/cases/case-details/#{ga_case_id}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .header("x-xsrf-token", "#{XSRFToken}")
          .check(status.in(200, 201, 304)))
          .exec(Common.configurationui)
      
          .exec(Common.configUI)
      
          .exec(Common.TsAndCs)
      
          .exec(Common.userDetails)
      
          .exec(Common.isAuthenticated)
      
          .exec(http("XUI_GA_190_05_GetApplicationDetails")
            .get("/data/internal/cases/#{ga_case_id}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .header("x-xsrf-token", "#{XSRFToken}")
            .check(status.in(200, 201, 304))
          )
      }
  
  /*======================================================================================
     * Respond to Application from WA
     ======================================================================================*/
     
      .group("XUI_GA_200_InitiateResponse") {
        exec(http("XUI_GA_200_005_InitiateResponse")
          .get("/workallocation/case/tasks/#{ga_case_id}/event/RESPOND_TO_APPLICATION/caseType/GENERALAPPLICATION/jurisdiction/CIVIL")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .check(status.in(200, 201, 304)))
      
          .exec(http("XUI_GA_200_010_CreateResponse")
            .get("/data/internal/cases/#{ga_case_id}/event-triggers/RESPOND_TO_APPLICATION?ignore-warning=false")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
            .check(status.in(200, 201, 304))
            .check(jsonPath("$.event_token").optional.saveAs("event_token_ga_defresponse"))
            .check(substring("RESPOND_TO_APPLICATION"))
          .check(status.in(200, 201, 304))
          )
      
          .exec(http("XUI_GA_200_015_CreateResponse")
            .get("/workallocation/case/tasks/#{ga_case_id}/event/RESPOND_TO_APPLICATION/caseType/GENERALAPPLICATION/jurisdiction/CIVIL")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
            .check(status.in(200, 201, 304)))
    
      }
  
      .pause(MinThinkTime, MaxThinkTime)
  
  
      /*======================================================================================
    * Has the respondent confirm agreed to the order that you want the judge to make? Yes
    ======================================================================================*/
  
      .group("XUI_GA_210_RespondentAgree") {
        exec(http("XUI_GA_210_005_RespondentAgree")
          .post("/data/case-types/GENERALAPPLICATION/validate?pageId=RESPOND_TO_APPLICATIONGARespondent1RespScreen")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GADefResponseConsent.json"))
          .check(substring("RESPOND_TO_APPLICATIONGARespondent1RespScreen"))
          .check(status.in(200, 201, 304)))
    
      }
  
      .pause(MinThinkTime, MaxThinkTime)
  
      /*======================================================================================
    * Choose Hearing date as No - Hearing screen
    ======================================================================================*/
  
      .group("XUI_GA_220_HearingDate") {
        exec(http("XUI_GA_220_005_HearingDate")
          .post("/data/case-types/GENERALAPPLICATION/validate?pageId=RESPOND_TO_APPLICATIONGARespHearingScreen")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GAHearingScreen.json"))
          .check(substring("RESPOND_TO_APPLICATIONGARespHearingScreen"))
          .check(status.in(200, 201, 304)))
      }
  
      .pause(MinThinkTime, MaxThinkTime)
  
      /*======================================================================================
    * Submit Def Response for GA Application
    ======================================================================================*/
  
      .group("XUI_GA_230_SubmitResponseApplication") {
        exec(http("XUI_GA_230_005_SubmitApplication")
          .post("/data/cases/#{ga_case_id}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GASubmitDefResponse.json"))
          .check(substring("SUCCESS"))
          
          .check(status.in(200, 201, 304)))
       
          .exec(http("XUI_GA_230_010_SubmitApplication")
            .get("/data/internal/cases/#{ga_case_id}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .header("x-xsrf-token", "#{XSRFToken}")
          .check(status.in(200, 201, 304)))
    
      }
      .pause(MinThinkTime, MaxThinkTime)
  
  val judgeIssueOrder=
  
  /*======================================================================================
           Login as as judge and issue the order
    ======================================================================================*/

  /*======================================================================================
                   Access case which is required access
   ======================================================================================*/
    exec(_.setAll(
      "applicant1FirstName" -> ("App1" + Common.randomString(5)),
      "applicant1LastName" -> ("Test" + Common.randomString(5)),
      "applicant2FirstName" -> ("App2" + Common.randomString(5)),
      "applicant2LastName" -> ("Test" + Common.randomString(5)),
      "marriageDay" -> Common.getDay(),
      "marriageMonth" -> Common.getMonth(),
      "Idempotencynumber" -> Common.getIdempotency(),
      "currentDate" -> Common.getDate(),
      "currentDateTime" -> Common.getCurrentDateTime(),
      "requestId" -> Common.getRequestId(),
      "marriageYear" -> Common.getMarriageYear()))
    
    .group("XUI_GA_240_AccessCase") {
      exec(http("XUI_GA_240_005_AccessCase")
        .post("/api/role-access/roles/manageLabellingRoleAssignment/${ga_case_id}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("Request-Id", "|${requestId}."+Common.getRequestId())
        .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
        // .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationJudgeCaseAccess.json"))
        .check(status.in(200, 201,204, 304)))
      
        .exec(http("XUI_GA_240_010_AccessCase")
          .get("/api/user/details?refreshRoleAssignments=undefined")
          .headers(Headers.commonHeader)
          .header("Request-Id", "|${requestId}."+Common.getRequestId())
          .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
          .header("x-xsrf-token", "#{XSRFToken}")
          .check(status.in(200, 201,204, 304)))
    
    }
      .pause(MinThinkTime, MaxThinkTime)
  
      
  /*======================================================================================
                  Judge- click on Request Access
  ======================================================================================*/
        .group("XUI_GA_250_ClickOnRequestAccess") {
          exec(http("XUI_GA_250_005_ClickOnRequestAccess")
            .post("/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
            .header("Request-Id", "|${requestId}."+Common.getRequestId())
            .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
           // .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .check(substring("roleAssignmentInfo"))
            .check(status.in(200, 201,204, 304)))
        }
        .pause(MinThinkTime, MaxThinkTime)
      .pause(20)
  
  /*======================================================================================
            Judge- Request Access Approval
   ======================================================================================*/
  .group("XUI_GA_260_ApproveRequestAccessForJudge") {
    exec(http("XUI_GA_260_005_RequestAccessForJudge")
      .post("/api/challenged-access-request")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("Request-Id", "|${requestId}."+Common.getRequestId())
      .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
      // .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationRequestAccess.json"))
      .check(substring("Stage 1 approved"))
      .check(status.in(200, 201,204, 304)))
      
      .exec(http("XUI_GA_260_010_ApproveRequestAccessForJudge")
        .get("/data/internal/cases/#{ga_case_id}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("Request-Id", "|${requestId}."+Common.getRequestId())
        .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(status.in(200, 201,204,304)))
    
  }
    .pause(MinThinkTime, MaxThinkTime)
      .pause(20)
      /*======================================================================================
                         Judge- ViewFile - after request access successful
 ======================================================================================*/
  
        .group("XUI_GA_270_ViewFileAfterAccess") {
          exec(http("XUI_GA_270_005_ViewFileAfterAccess")
            .post("/api/role-access/roles/manageLabellingRoleAssignment/#{ga_case_id}")
            .headers(Headers.commonHeader)
            .header("Request-Id", "|${requestId}."+Common.getRequestId())
            .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
            // .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationJudgeCaseAccess.json"))
            .check(status.in(200, 201,204, 304)))
    
            .exec(http("XUI_GA_270_010_ViewFileAfterAccess")
              .get("/api/wa-supported-jurisdiction/get")
              .headers(Headers.commonHeader)
              .header("Request-Id", "|${requestId}."+Common.getRequestId())
              .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
              // .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
              .header("x-xsrf-token", "#{XSRFToken}")
              .check(substring("CIVIL"))
              .check(status.in(200, 201,204,304)))
  
          .exec(http("XUI_GA_270_015_ViewFileAfterAccess")
            .post("/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
            .header("Request-Id", "|${requestId}."+Common.getRequestId())
            .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
            // .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .check(status.in(200, 201, 204, 304)))
        }
      .pause(MinThinkTime, MaxThinkTime)
      

        /*======================================================================================
       * Judge- Initiate Judge Response WA
       ======================================================================================*/
  
        .group("XUI_GA_280_InitiateJudgeResponse") {
          exec(http("XUI_GA_280_005_InitiateResponse")
            .get("/workallocation/case/tasks/#{ga_case_id}/event/MAKE_DECISION/caseType/GENERALAPPLICATION/jurisdiction/CIVIL")
            .headers(Headers.commonHeader)
            .header("Request-Id", "|${requestId}."+Common.getRequestId())
            .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
            .header("accept", "application/json")
            .check(status.in(200, 201, 304)))
         
      
            .exec(http("XUI_GA_280_010_InitiateJudgeResponse")
              .get("/data/internal/cases/#{ga_case_id}/event-triggers/MAKE_DECISION?ignore-warning=false")
              .headers(Headers.commonHeader)
              .header("Request-Id", "|${requestId}."+Common.getRequestId())
              .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
              .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
              .check(substring("Make decision"))
              .check(status.in(200, 201, 304))
              .check(jsonPath("$.event_token").optional.saveAs("event_token_judge")))
      
            .exec(http("XUI_GA_280_015_InitiateJudgeResponse")
              .get("/workallocation/case/tasks/#{ga_case_id}/event/MAKE_DECISION/caseType/GENERALAPPLICATION/jurisdiction/CIVIL")
              .headers(Headers.commonHeader)
              .header("accept", "application/json")
              .header("Request-Id", "|${requestId}." + Common.getRequestId())
              .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
              .check(status.in(200, 201, 304)))
  
            .exec(http("XUI_GA_280_020_InitiateJudgeResponse")
              .get("/data/internal/profile")
              .headers(Headers.commonHeader)
              .header("Request-Id", "|${requestId}." + Common.getRequestId())
              .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
              .header("accept", "application/json")
              .check(substring("create"))
              .check(status.in(200, 201, 304)))
        }
  
        .pause(MinThinkTime, MaxThinkTime)
  
  
  /*======================================================================================
        * Judge- What is your decision on this application?
        ======================================================================================*/
        .group("XUI_GA_230_DecisionOnApplication") {
          exec(http("XUI_GA_230_005_RequestAccessForJudge")
            .post("/data/case-types/GENERALAPPLICATION/validate?pageId=MAKE_DECISIONGAJudicialDecision")
            .headers(Headers.commonHeader)
            .header("Request-Id", "|${requestId}." + Common.getRequestId())
            .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
             .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationMakeAnOrder.json"))
            .check(substring("MAKE_DECISIONGAJudicialDecision"))
            .check(status.in(200, 201, 304)))
        }
        .pause(MinThinkTime, MaxThinkTime)
  
 
  
  /*======================================================================================
         * Judge- cloak the order
         ======================================================================================*/
  
        .group("XUI_GA_230_MakeADecisionScreen") {
          exec(http("XUI_GA_230_005_MakeADecisionScreen")
            .post("/data/case-types/GENERALAPPLICATION/validate?pageId=MAKE_DECISIONGAJudicialMakeADecisionScreen")
            .headers(Headers.commonHeader)
            .header("Request-Id", "|${requestId}." + Common.getRequestId())
            .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationDesicionScreen.json"))
            .check(substring("MAKE_DECISIONGAJudicialMakeADecisionScreen"))
            .check(status.in(200, 201, 304))
            .check(jsonPath("$..judicialMakeOrderDocPreview.document_url").optional.saveAs("previewDocURL"))
            .check(jsonPath("$..judicialMakeOrderDocPreview.document_binary_url").optional.saveAs("previewDocBinaryURL"))
            .check(jsonPath("$..judicialMakeOrderDocPreview.document_filename").optional.saveAs("previewFilename"))
            .check(jsonPath("$..judicialMakeOrderDocPreview.document_hash").optional.saveAs("previewDocHash"))
          )
        }
        .pause(MinThinkTime, MaxThinkTime)
  
  /*======================================================================================
           * Judge- preview
           ======================================================================================*/
        .group("XUI_GA_230_Preview") {
          exec(http("XUI_GA_230_005_XUI_GA_230_Preview")
            .post("/data/case-types/GENERALAPPLICATION/validate?pageId=MAKE_DECISIONGAJudicialMakeADecisionDocPreview")
            .headers(Headers.commonHeader)
            .header("Request-Id", "|${requestId}." + Common.getRequestId())
            .header("Request-Context", "appId=cid-v1:7922b140-fa5f-482d-89b4-e66e9e6d675a")
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationDesicionPreview.json"))
            .check(substring("MAKE_DECISIONGAJudicialMakeADecisionDocPreview"))
            .check(status.in(200, 201, 304)))
        }
        .pause(MinThinkTime, MaxThinkTime)
  
        /*======================================================================================
      * Submit Judge Issue Order
      ======================================================================================*/
  
        .group("XUI_GA_230_SubmitJudgeDecision") {
          exec(http("XUI_GA_230_005_SubmitApplication")
            .post("/data/cases/#{ga_case_id}/events")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationSubmitJudgeDecision.json"))
            .check(substring("Your order has been made"))
            
            .check(status.in(200, 201, 304)))
      
            .exec(http("XUI_GA_230_010_SubmitJudgeDecision")
              .get("/data/internal/cases/#{ga_case_id}")
              .headers(Headers.commonHeader)
              .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
              .header("x-xsrf-token", "#{XSRFToken}")
              .check(substring("Assigning of GA roles"))
              .check(status.in(200, 201, 304)))
    
        }
        .pause(MinThinkTime, MaxThinkTime)
  
}