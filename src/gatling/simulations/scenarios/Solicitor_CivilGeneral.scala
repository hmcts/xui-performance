package scenarios

import io.gatling.core.Predef.{exec, substring, _}
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}
import scala.concurrent.duration._



/*======================================================================================
* Create a new Divorce application as a professional user (e.g. solicitor)
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
      "marriageYear" -> Common.getMarriageYear()))
  
      /*======================================================================================
              Click On Case - To Get the case details
              ======================================================================================*/
  
      //  val getcasedetailspage=
      .group("XUI_GA_30_CaseDetails") {
        exec(http("XUI_GA_30_005_CaseDetails")
          .get("/data/internal/cases/${caseId}")
          .headers(Headers.commonHeader)
          .check(status.in(200, 201, 304))
        )
          .exec(http("XUI_GA_30_010_RoleAssignment")
            .get("/api/role-access/roles/manageLabellingRoleAssignment/${caseId}")
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
          .get("/workallocation/case/tasks/${caseId}/event/INITIATE_GENERAL_APPLICATION/caseType/CIVIL/jurisdiction/CIVIL")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .check(status.in(200,201,304)))
          
          .exec(http("XUI_GA_040_010_CreateGACase")
            .get("/data/internal/cases/${caseId}/event-triggers/INITIATE_GENERAL_APPLICATION?ignore-warning=false")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
            .check(status.in(200,201,304))
            .check(jsonPath("$.event_token").optional.saveAs("event_token_ga")))
          
          .exec(http("XUI_GA_040_015_GetGACase")
            .get("/workallocation/case/tasks/${caseId}/event/INITIATE_GENERAL_APPLICATION/caseType/CIVIL/jurisdiction/CIVIL")
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationType.json"))
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralHearingDate.json"))
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralRespondentAgreement.json"))
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GeneralApplicationUrgencyRecord.json"))
          .check(status.in(200,201,304)))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
         * Make An Application - Upload a File
         ======================================================================================*/
      .group("XUI_GA_90_UploadDoc") {
        exec(http("XUI_GA_90_005_UploadMarriageCertificate")
          .post("/documentsv2")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .header("x-xsrf-token", "${XSRFToken}")
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralSOT.json"))
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilgeneralHearingDetails.json"))
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilgeneralPBADetails.json"))
          .check(status.in(200,201,304)))
        
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
    * Submit Application
    ======================================================================================*/
      
      .group("XUI_GA_130_SubmitApplication") {
        exec(http("XUI_GA_130_005_SubmitGA")
          .post("/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralCheckAnswers.json"))
          .check(status.in(200,201,304)))
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      .pause(30)
      
      /*======================================================================================
             * Get back to case details
        ======================================================================================*/
      
      .group("XUI_GA_140_GACaseDetails") {
        exec(http("XUI_GA_130_005_RoleAssignment")
          .post("/api/role-access/roles/manageLabellingRoleAssignment/${caseId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .check(status.in(200,201,204,304)))
          
          .exec(http("XUI_GA_130_010_WA")
            .get("/api/wa-supported-jurisdiction/get")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "${XSRFToken}")
          .check(status.in(200,201,304)))
          
          .exec(http("XUI_GA_130_015_GetGAJurisDetails")
            .get("/api/wa-supported-jurisdiction/get")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "${XSRFToken}")
          .check(status.in(200,201,304)))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      
      
      /*======================================================================================
    * Refresh Application Page
    ======================================================================================*/
      .group("XUI_GA_140_RefreshCaseDetails") {
        exec(http("XUI_GA_140_05_GetGADetailsRefresh")
          .get("/cases/case-details/${caseId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .header("x-xsrf-token", "${XSRFToken}")
          .check(status.in(200,201,304)))
          
          
          .exec(Common.configurationui)
          
          .exec(Common.configUI)
          
          .exec(Common.TsAndCs)
          
          .exec(Common.userDetails)
          
          .exec(Common.isAuthenticated)
          
          .exec(http("XUI_GA_140_010_GetApplicationDetails")
            .get("/data/internal/cases/${caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .header("x-xsrf-token", "${XSRFToken}")
           // .check(jsonPath("$.caseLink.CaseReference").optional.saveAs("ga_case_id"))
            //.check(jsonPath("$.caseLink.CaseReference").findAll.optional.saveAs("gacases"))
            .check(jsonPath("$..value[0].value.caseLink.CaseReference").optional.saveAs("ga_case_id"))
  
            .check(status.in(200,201,304))
          )
          .exec { session =>
           
            println(s"First Occurrence: "+session("ga_case_id").toString)
            session
          }
      }
  
  
  /*======================================================================================
    * Making a Payment using Payment Service Request - click on Service Request
    ======================================================================================*/
  val PBSPayment =
  // payment pba fee payment
    group("XUI_GA_150_ServiceRequestForPay") {
        exec(http("CIVIL_CreateGA_150_ServiceRequestToPay")
          .get("/pay-bulkscan/cases/${ga_case_id}")
          .headers(Headers.commonHeader)
          .check(status.in(200, 304))
        )
    }
      
      
      /*======================================================================================
            * Click On pay from Service Page
       ==========================================================================================*/
      
      .group("XUI_GA_160_BackToCaseDetails") {
        exec(http("CIVIL_CreateGA_160_005_CaseDetails")
          .get("/payments/cases/${ga_case_id}/paymentgroups")
          .headers(Headers.commonHeader)
          .check(status.in(200, 201, 304))
        )
          
          .exec(http("GA_Create_Case_160_010_Caselists")
            .get("/payments/case-payment-orders?case_ids=${ga_case_id}")
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
          .check(status.in(200, 201, 304))
        )
      }
      
      //Final Payment
      
      .group("CIVIL_CreateGA_180_FinalPayment") {
        exec(http("CIVIL_CreateGA_180_005_CaseDetails")
          .post("/payments/service-request/${serviceRef}/pba-payments")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralPBAPayment.json"))
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
          .get("/cases/case-details/${ga_case_id}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .header("x-xsrf-token", "${XSRFToken}")
          .check(status.in(200, 201, 304)))
      
      
          .exec(Common.configurationui)
      
          .exec(Common.configUI)
      
          .exec(Common.TsAndCs)
      
          .exec(Common.userDetails)
      
          .exec(Common.isAuthenticated)
      
          .exec(http("XUI_GA_190_05_GetApplicationDetails")
            .get("/data/internal/cases/${ga_case_id}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .header("x-xsrf-token", "${XSRFToken}")
            .check(status.in(200, 201, 304))
          )
      }
  
  /*======================================================================================
     * Respond to Application from WA
     ======================================================================================*/
     
      .group("XUI_GA_200_InitiateResponse") {
        exec(http("XUI_GA_200_005_InitiateResponse")
          .get("/workallocation/case/tasks/${ga_case_id}/event/RESPOND_TO_APPLICATION/caseType/GENERALAPPLICATION/jurisdiction/CIVIL")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .check(status.in(200, 201, 304)))
      
          .exec(http("XUI_GA_200_010_CreateResponse")
            .get("/data/internal/cases/${ga_case_id}/event-triggers/RESPOND_TO_APPLICATION?ignore-warning=false")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
            .check(status.in(200, 201, 304))
            .check(jsonPath("$.event_token").optional.saveAs("event_token_ga_defresponse")))
      
          .exec(http("XUI_GA_200_015_CreateResponse")
            .get("/workallocation/case/tasks/${ga_case_id}/event/RESPOND_TO_APPLICATION/caseType/GENERALAPPLICATION/jurisdiction/CIVIL")
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
          .post("/data/case-types/GENERALAPPLICATION/validate?pageId=RESPOND_TO_APPLICATIONGARespondentConsent")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GADefResponseConsent.json"))
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
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GAHearingScreen.json"))
          .check(status.in(200, 201, 304)))
    
    
      }
  
      .pause(MinThinkTime, MaxThinkTime)
  
      /*======================================================================================
    * Submit Def Response for GA Application
    ======================================================================================*/
  
      .group("XUI_GA_230_SubmitResponseApplication") {
        exec(http("XUI_GA_230_005_ApplicationType")
          .post("/data/cases/${ga_case_id}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/GASubmitDefResponse.json"))
          .check(status.in(200, 201, 304)))
      
          .exec(http("XUI_GA_230_010_GetcaseDetails")
            .get("/data/internal/cases/${caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "${XSRFToken}"))
    
      }
  
      .pause(MinThinkTime, MaxThinkTime)
  
  
  /*======================================================================================
           Login as as judge and issue the order
           ======================================================================================*/
  
}