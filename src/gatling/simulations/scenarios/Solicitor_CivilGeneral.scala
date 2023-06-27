package scenarios

import io.gatling.core.Predef.{exec, substring, _}
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

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
      "marriageYear" -> Common.getMarriageYear()))
      
      
      /*======================================================================================
         Click On Create Application - To Create a General Application
         ======================================================================================*/
      .group("XUI_GA_030_InitiateGACase") {
        exec(http("XUI_GA_030_005_InitiateGACase")
          .get("/workallocation/case/tasks/${caseId}/event/INITIATE_GENERAL_APPLICATION/caseType/CIVIL/jurisdiction/CIVIL")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .check(status.in(200,201,304)))
          
          .exec(http("XUI_GA_030_005_CreateGACase")
            .get("/data/internal/cases/${caseId}/event-triggers/INITIATE_GENERAL_APPLICATION?ignore-warning=false)")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
            .check(status.in(200,201,304))
            .check(jsonPath("$.event_token").optional.saveAs("event_token_ga")))
          
          .exec(http("XUI_GA_030_005_GetGACase")
            .get("/workallocation/case/tasks/${caseId}/event/INITIATE_GENERAL_APPLICATION/caseType/CIVIL/jurisdiction/CIVIL")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
          .check(status.in(200,201,304)))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
    * Jurisdiction = Civil; Case Type = General; Event = Application Type
    ======================================================================================*/
      
      .group("XUI_GA_040_ApplicationType") {
        exec(http("XUI_GA_040_005_ApplicationType")
          .post("/data/case-types/CIVIL/validate?pageId=INITIATE_GENERAL_APPLICATIONGATypePage")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralApplicationType.json"))
          .check(substring("applicationType"))
          .check(status.in(200,201,304))
        )
        
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
    * Choose Hearing date as No
    ======================================================================================*/
      
      .group("XUI_GA_050_HearingDate") {
        exec(http("XUI_GA_050_005_HearingDate")
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
      
      .group("XUI_GA_060_RespondentAgree") {
        exec(http("XUI_GA_060_005_RespondentAgree")
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
      
      .group("XUI_GA_070_IsUrgent") {
        exec(http("XUI_GA_070_005_IsUrgent")
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
      .group("XUI_Divorce_80_UploadDoc") {
        exec(http("XUI_Divorce_80_005_UploadMarriageCertificate")
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
      
      .group("XUI_GA_090_StatementOfTruth") {
        exec(http("XUI_GA_090_005_StatementOfTruth")
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
      
      .group("XUI_GA_100_HearingDetails") {
        exec(http("XUI_GA_100_005_HearingDetails")
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
      
      .group("XUI_GA_110_PayForApplication") {
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
      
      .group("XUI_GA_120_SubmitApplication") {
        exec(http("XUI_GA_120_005_ApplicationType")
          .post("/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralCheckAnswers.json"))
          .check(status.in(200,201,304)))
        
        .exec(http("XUI_GA_130_005_GetcaseDetails")
          .get("/data/internal/cases/${caseId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}"))
        
      }
      
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
             * Get back to case details
        ======================================================================================*/
      
      .group("XUI_GA_120_SubmitApplication") {
        exec(http("XUI_GA_120_005_ApplicationType")
          .post("/api/role-access/roles/manageLabellingRoleAssignment/${caseId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralCheckAnswers.json"))
          .check(status.in(200,201,304)))
          
          .exec(http("XUI_GA_130_005_GetcaseDetails")
            .get("/api/wa-supported-jurisdiction/get")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "${XSRFToken}")
          .check(status.in(200,201,304)))
          
          .exec(http("XUI_GA_130_005_GetcaseDetails")
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
      .group("XUI_GA_120_SubmitApplication") {
        exec(http("XUI_GA_130_05_GetApplicationDetails")
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
          
          .exec(http("XUI_GA_130_05_GetApplicationDetails")
            .get("/data/internal/cases/${caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .header("x-xsrf-token", "${XSRFToken}")
            .check(jsonPath("$.caseLink.CaseReference").optional.saveAs("ga_case_id"))
            .check(status.in(200,201,304))
          )
      }
  
  
  /*======================================================================================
    * Making a Payment using Payment Service Request - click on paynow from Service Request
    ======================================================================================*/
  val PBSPayment =
  // payment pba fee payment
    group("XUI_GA_140_BackToCaseDetails") {
      exec(http("CD_CreateClaim_300_CaseDetails")
        .get("/payments/cases/${ga_case_id}/paymentgroups")
        .headers(Headers.commonHeader)
        .check(status.in(200,201,304))
      )
        
        .exec(http("CD_CreateClaim_300_CaseDetails")
          .get("pay-bulkscan/cases/${ga_case_id}")
          .headers(Headers.commonHeader)
          .check(status.in(200, 304))
        )
    }
      
      
      /*======================================================================================
            * Click On pay
            ======================================================================================*/
      
      .group("XUI_GA_140_BackToCaseDetails") {
        exec(http("CD_CreateClaim_300_CaseDetails")
          .get("/payments/pba-accounts")
          .headers(Headers.commonHeader)
          .check(status.in(200,201, 304))
        )
          
          .exec(http("CD_CreateClaim_300_CaseDetails")
            .get("pay-bulkscan/cases/${ga_case_id}")
            .headers(Headers.commonHeader)
            .check(status.in(200,201, 304))
          )
          
          
          .exec(http("CD_CreateClaim_300_CaseDetails")
            .get("/payments/case-payment-orders?case_ids=${caseId}")
            .headers(Headers.commonHeader)
            .check(status.in(200,201, 304))
          )
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      //pba payment final
      
      .group("CD_CreateClaim_300_BackToCaseDetails") {
        exec(http("CD_CreateClaim_300_CaseDetails")
          .post("/payments/service-request/2023-${caseId}/pba-payments")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/civilgeneral/CivilGeneralPBAPayment.json"))
          .check(status.in(200, 201, 304))
        )
      }
      .pause(MinThinkTime, MaxThinkTime)
      .pause(50)
  
  
  
  
  
  /*======================================================================================
    Login as respondent and go to applications click on application to respond
    ======================================================================================*/
  
  /*======================================================================================
           Login as as judge and issue the order
           ======================================================================================*/
  
}