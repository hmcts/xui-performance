package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new Divorce application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_NFD {

  val IdamUrl = Environment.idamURL
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CreateNFDCase =

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
    * Click the Create Case link
    ======================================================================================*/

    .group("XUI_NFD_030_CreateCase") {
      exec(Common.healthcheck("%2Fcases%2Fcase-filter"))

      .exec(http("XUI_NFD_030_005_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("DIVORCE")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Jurisdiction = Family Divorce; Case Type = New Law Case - v115.00; Event = Apply: divorce or dissolution
    ======================================================================================*/

    .group("XUI_NFD_040_SelectCaseType") {
      exec(Common.healthcheck("%2Fcases%2Fcase-create%2FDIVORCE%2FNFD%2Fsolicitor-create-application"))

      .exec(http("XUI_NFD_040_005_StartApplication")
        .get("/data/internal/case-types/NFD/event-triggers/solicitor-create-application?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("solicitor-create-application")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-create%2FDIVORCE%2FNFD%2Fsolicitor-create-application%2Fsolicitor-create-applicationhowDoYouWantToApplyForDivorce"))

      .exec(Common.userDetails)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select case type - Divorce (Sole)
    ======================================================================================*/

    .group("XUI_NFD_050_ChooseSoleOrJoint") {
      exec(http("XUI_NFD_050_005_ChooseSoleOrJoint")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationhowDoYouWantToApplyForDivorce")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDCaseType.json"))
        .check(substring("applicationType")))

      //select respondent solicitor org now, as this call will be retrieved from cache in future
      //requests, where checks aren't performed by Gatling https://gatling.io/docs/gatling/reference/current/http/protocol/#caching
      .exec(http("XUI_NFD_050_010_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * About the Solicitor
    ======================================================================================*/

    .group("XUI_NFD_060_AboutTheSolicitor") {
      exec(http("XUI_NFD_060_005_AboutTheSolicitor")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationSolAboutTheSolicitor")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDApplicant1SolicitorDetails.json"))
        .check(substring("applicant1SolicitorName")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Has the Marriage broken down?
    ======================================================================================*/

    .group("XUI_NFD_070_MarriageBrokenDown") {
      exec(http("XUI_NFD_070_005_MarriageBrokenDown")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationMarriageIrretrievablyBroken")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDMarriageBrokenDown.json"))
        .check(substring("applicant1ScreenHasMarriageBroken")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * About the Applicant
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_NFD_080_AboutTheApplicant") {
      exec(http("XUI_NFD_080_005_AboutTheApplicant")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationSolAboutApplicant1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDApplicant1Details.json"))
        .check(substring("applicant1FirstName")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * About the Other Party
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_NFD_090_AboutTheOtherParty") {
      exec(http("XUI_NFD_090_005_AboutTheOtherParty")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationSolAboutApplicant2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDApplicant2Details.json"))
        .check(substring("applicant2FirstName")))

      .exec(http("XUI_NFD_090_010_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Is the Respondent Represented by a Solicitor?
    ======================================================================================*/

    //Not calling postcode lookup here, as it will override the applicant's address in the session.
    // Will just use the same address for both applicants

    //.exec(Common.postcodeLookup)

    .group("XUI_NFD_100_RespondentRepresented") {
      exec(http("XUI_NFD_100_005_RespondentRepresented")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationApplicant2ServiceDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDApplicant2SolicitorDetails.json"))
        .check(substring("applicant2SolicitorName")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Marriage Certificate Details
    ======================================================================================*/

    .group("XUI_NFD_110_MarriageCertificateDetails") {
      exec(http("XUI_NFD_110_005_MarriageCertificateDetails")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationMarriageCertificateDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDMarriageCertificateDetails.json"))
        .check(substring("marriageDate")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Jurisdiction - Both applicants domiciled in the UK (connection = F)
    ======================================================================================*/

    .group("XUI_NFD_120_Jurisdiction") {
      exec(http("XUI_NFD_120_005_Jurisdiction")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationJurisdictionApplyForDivorce")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDJurisdiction.json"))
        .check(substring("jurisdictionConnections")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Other Legal Proceedings - No
    ======================================================================================*/

    .group("XUI_NFD_130_OtherLegalProceedings") {
      exec(http("XUI_NFD_130_005_OtherLegalProceedings")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationOtherLegalProceedings")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDOtherLegalProceedings.json"))
        .check(substring("applicant1LegalProceedings")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Financial Order - No
    ======================================================================================*/

    .group("XUI_NFD_140_FinancialOrder") {
      exec(http("XUI_NFD_140_005_FinancialOrder")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationFinancialOrders")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDFinancialOrder.json"))
        .check(substring("applicant1FinancialOrder")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Upload Document
    ======================================================================================*/

    .group("XUI_NFD_150_UploadDocument") {
      exec(http("XUI_NFD_150_005_UploadDocument")
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
    * Submit Document
    ======================================================================================*/

    .group("XUI_NFD_160_SubmitDocument") {
      exec(http("XUI_NFD_160_005_SubmitDocument")
        .post("/data/case-types/NFD/validate?pageId=solicitor-create-applicationUploadSupportingDocuments")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDSubmitDocument.json"))
        .check(substring("applicant1DocumentsUploaded")))

      .exec(Common.userDetails)

      .exec(http("XUI_NFD_160_010_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*"))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Answers
    ======================================================================================*/

    .group("XUI_NFD_170_CheckYourAnswers") {
      exec(http("XUI_NFD_170_005_CheckYourAnswers")
        .post("/data/case-types/NFD/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDCheckYourAnswers.json"))
        .check(jsonPath("$.state").is("Draft"))
        .check(jsonPath("$.id").saveAs("caseId")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_Divorce_170_010_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.state.id").is("Draft")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select 'Sign and submit' from the dropdown
    ======================================================================================*/

    .group("XUI_NFD_180_StartEventSignAndSubmit") {
      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fsolicitor-submit-application"))

      .exec(Common.profile)

      .exec(http("XUI_NFD_180_005_StartEventSignAndSubmit")
        .get("/data/internal/cases/${caseId}/event-triggers/solicitor-submit-application?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("solicitor-submit-application"))
        .check(jsonPath("$.case_fields[?(@.id=='solApplicationFeeInPounds')].value").saveAs("feeInPounds"))
        .check(jsonPath("$.case_fields[?(@.id=='applicationFeeOrderSummary')].value.Fees[0].value.FeeAmount").saveAs("feeAmount"))
        .check(jsonPath("$.case_fields[?(@.id=='applicationFeeOrderSummary')].value.Fees[0].value.FeeCode").saveAs("feeCode"))
        .check(jsonPath("$.case_fields[?(@.id=='applicationFeeOrderSummary')].value.Fees[0].value.FeeDescription").saveAs("feeDescription"))
        .check(jsonPath("$.case_fields[?(@.id=='applicationFeeOrderSummary')].value.Fees[0].value.FeeVersion").saveAs("feeVersion"))
        .check(jsonPath("$.case_fields[?(@.id=='applicationFeeOrderSummary')].value.PaymentTotal").saveAs("paymentTotal")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fsolicitor-submit-application%2Fsolicitor-submit-applicationSolStatementOfTruth"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Statement Of Truth
    ======================================================================================*/

    .group("XUI_NFD_190_StatementOfTruth") {
      exec(http("XUI_NFD_190_005_StatementOfTruth")
        .post("/data/case-types/NFD/validate?pageId=solicitor-submit-applicationSolStatementOfTruth")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDStatementOfTruth.json"))
        .check(substring("solSignStatementOfTruth")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fsolicitor-submit-application%2Fsolicitor-submit-applicationSolPayment"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Payment Method
    ======================================================================================*/

    .group("XUI_NFD_200_PaymentMethod") {
      exec(http("XUI_NFD_200_005_PaymentMethod")
        .post("/data/case-types/NFD/validate?pageId=solicitor-submit-applicationSolPayment")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDPaymentMethod.json"))
        .check(substring("pbaNumbers")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fsolicitor-submit-application%2Fsolicitor-submit-applicationSolPayAccount"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select PBA Account
    ======================================================================================*/

    .group("XUI_NFD_210_PaymentDetails") {
      exec(http("XUI_NFD_210_005_PaymentDetails")
        .post("/data/case-types/NFD/validate?pageId=solicitor-submit-applicationSolPayAccount")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDPaymentDetails.json"))
        .check(substring("feeAccountReference")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fsolicitor-submit-application%2Fsolicitor-submit-applicationSolPaymentSummary"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Order Summary
    ======================================================================================*/

    .group("XUI_NFD_220_OrderSummary") {
      exec(http("XUI_NFD_220_005_OrderSummary")
        .post("/data/case-types/NFD/validate?pageId=solicitor-submit-applicationSolPaymentSummary")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDPaymentSummary.json"))
        .check(substring("applicationFeeOrderSummary")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2Fsolicitor-submit-application%2Fsubmit"))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Sign & Submit: Check Your Answers
    ======================================================================================*/

    .group("XUI_NFD_230_SignCheckYourAnswers") {
      exec(http("XUI_NFD_230_005_SignCheckYourAnswers")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/nfd/NFDSignCheckYourAnswers.json"))
        .check(jsonPath("$.state").is("Submitted")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_Divorce_230_010_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.state.id").is("Submitted")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)


    .exec {
      session =>
        println(session)
        session
    }

}
