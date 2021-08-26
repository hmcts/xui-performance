package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new FR application as a professional user (e.g. solicitor) for a specified Divorce case
======================================================================================*/

object EXUI_FR_Applicant  {

  val minThinkTime = Environment.minThinkTimeFR
  val maxThinkTime = Environment.maxThinkTimeFR

  /*======================================================================================
  * Click the Create Case link
  ======================================================================================*/

  val createFRCase =

    group("XUI_FR_030_CreateCase") {
      exec(Common.healthcheck("%2Fcases%2Fcase-filter"))

      .exec(http("XUI_FR_030_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("DIVORCE")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
     * Jurisdiction = Family Divorce; Case Type = Financial Rem Consented Resp; Event = Consent Order Application
     ======================================================================================*/

    .group("XUI_FR_040_SelectCaseType") {
      exec(Common.healthcheck("%2Fcases%2Fcase-create%2FDIVORCE%2FFinancialRemedyMVP2%2FFR_solicitorCreate"))

        .exec(http("XUI_FR_040_005_StartApplication")
          .get("/data/internal/case-types/FinancialRemedyMVP2/event-triggers/FR_solicitorCreate?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(jsonPath("$.id").is("FR_solicitorCreate")))

        .exec(Common.healthcheck("%2Fcases%2Fcase-create%2FDIVORCE%2FFinancialRemedyMVP2%2FFR_solicitorCreate%2FFR_solicitorCreate1"))

        .exec(Common.profile)

        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(Environment.baseDomain).saveAs("XSRFToken")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Click Continue
    ======================================================================================*/

    .group("XUI_FR_060_ContinueToApplication") {
      exec(http("XUI_FR_060_005_ContinueToApplication")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRContinueToApplication.json"))
        .check(jsonPath("$.data.solicitorFirm").saveAs("firmName"))
        .check(jsonPath("$.data.solicitorReference").saveAs("firmRef"))
        .check(jsonPath("$.data.solicitorAddress.AddressLine1").saveAs("firmAddress1"))
        .check(jsonPath("$.data.solicitorAddress.AddressLine2").saveAs("firmAddress2"))
        .check(jsonPath("$.data.solicitorAddress.AddressLine3").saveAs("firmAddress3"))
        .check(jsonPath("$.data.solicitorAddress.PostTown").saveAs("firmPostTown"))
        .check(jsonPath("$.data.solicitorAddress.County").saveAs("firmCounty"))
        .check(jsonPath("$.data.solicitorAddress.PostCode").saveAs("firmPostcode")))

      //select applicant and respondent solicitor orgs now, as this call will be retrieved from cache in future
      //requests, where checks aren't performed by Gatling https://gatling.io/docs/gatling/reference/current/http/protocol/#caching
      .exec(http("XUI_FR_060_010_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(regex(""""name":"(.+?)","organisationIdentifier":"([0-9A-Z]+?)"""").ofType[(String, String)].findRandom.saveAs("applicantOrgs"))
        .check(regex(""""name":"(.+?)","organisationIdentifier":"([0-9A-Z]+?)"""").ofType[(String, String)].findRandom.saveAs("respondentOrgs")))

    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Complete Solicitor details (some are pre-populated) and click Continue
    ======================================================================================*/

    .group("XUI_FR_070_AddSolicitorDetails") {
      exec(http("XUI_FR_070_005_AddSolicitorDetails")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRAddSolicitorDetails.json"))
        .check(substring("solicitorAgreeToReceiveEmails")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Complete Divorce case details (FamilyMan code = EZ12D91234) and click Continue
    ======================================================================================*/

    .group("XUI_FR_080_AddDivorceCaseDetails") {
      exec(http("XUI_FR_080_005_AddDivorceCaseDetails")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRAddDivorceCaseDetails.json"))
        .check(jsonPath("$.data.divorceStageReached").is("Petition Issued")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Enter the Applicant's details and click Continue
    ======================================================================================*/

    .group("XUI_FR_090_AddApplicantDetails") {
      exec(http("XUI_FR_090_005_AddApplicantDetails")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRAddApplicantDetails.json"))
        .check(jsonPath("$.data.applicantFMName").is("ApplicantPerf")))

      .exec(http("XUI_FR_090_010_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*"))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Postcode Lookup for Respondent's Solicitor
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Enter the Respondent's details and click Continue
    ======================================================================================*/

      .exec { session =>
        println(session)
        session
      }


    .group("XUI_FR_100_AddRespondentDetails") {
      exec(http("XUI_FR_100_005_AddRespondentDetails")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate5")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRAddRespondentDetails.json"))
        .check(jsonPath("$.data.RespondentOrganisationPolicy.OrgPolicyCaseAssignedRole").is("[RESPSOLICITOR]")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Select Lump Sum Order as the Nature of the Application and click Continue
    ======================================================================================*/

    .group("XUI_FR_110_AddNatureOfApplication") {
      exec(http("XUI_FR_110_005_AddNatureOfApplication")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate6")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/AddNatureOfApplication.json"))
        .check(jsonPath("$.data.natureOfApplication2[0]").is("Lump Sum Order")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Upload Consent Order PDF
    ======================================================================================*/

    .group("XUI_FR_120_UploadConsentOrder") {
      exec(http("XUI_FR_120_005_UploadConsentOrder")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "null")
        .formParam("jurisdictionId", "null")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("ConsentOrderDocumentHash"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("ConsentOrderDocumentURL")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Click Continue to submit Consent Order document
    ======================================================================================*/

    .group("XUI_FR_130_SubmitConsentOrderDocument") {
      exec(http("XUI_FR_130_005_SubmitConsentOrderDocument")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate8")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRSubmitConsentOrderDocument.json"))
        .check(jsonPath("$.data.consentOrder.document_hash").is("${ConsentOrderDocumentHash}")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Select Yes to Uploading a Joint D81 and upload the D81 PDF
    ======================================================================================*/

    .group("XUI_FR_140_UploadJointD81") {
      exec(http("XUI_FR_140_005_UploadJointD81")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "null")
        .formParam("jurisdictionId", "null")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("D81DocumentHash"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("D81DocumentURL")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Click Continue to submit D81 document
    ======================================================================================*/

    .group("XUI_FR_150_SubmitD81Document") {
      exec(http("XUI_FR_150_005_SubmitD81Document")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate9")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRSubmitD81Document.json"))
        .check(jsonPath("$.data.d81Joint.document_hash").is("${D81DocumentHash}")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Other Documents - click Continue
    ======================================================================================*/

    .group("XUI_FR_160_OtherDocuments") {
      exec(http("XUI_FR_160_005_OtherDocuments")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate11")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FROtherDocuments.json"))
        .check(substring("otherCollection")))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Consent Order Application - click Continue
    ======================================================================================*/

    .group("XUI_FR_170_ContinueToCheckYourAnswers") {
      exec(http("XUI_FR_170_005_ContinueToCheckYourAnswers")
        .post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate12")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRContinueToCheckYourAnswers.json"))
        .check(substring(""""data":{}""")))

      .exec(http("XUI_FR_170_010_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*"))
    }

    .pause(minThinkTime, maxThinkTime)

    /*======================================================================================
    * Review Application - click Submit
    ======================================================================================*/

    .group("XUI_FR_180_SubmitApplication") {
      exec(http("XUI_FR_180_005_SubmitApplication")
        .post("/data/case-types/FinancialRemedyMVP2/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fr/FRSubmitApplication.json"))
        .check(jsonPath("$.state").is("caseAdded"))
        .check(jsonPath("$.id").saveAs("caseId")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_FR_180_010_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.state.id").is("caseAdded")))

      .exec(http("XUI_FR_180_015_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*"))
    }

    .pause(minThinkTime, maxThinkTime)

}
