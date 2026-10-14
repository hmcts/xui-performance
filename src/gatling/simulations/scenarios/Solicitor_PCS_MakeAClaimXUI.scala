package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.util.UUID
import io.gatling.http.check.HttpCheck
import utils.{Common, Environment, Headers, MakeAClaimCaseRecorder}

object XUI_SolicitorPCSMakeaClaim {

  val BaseURL = Environment.baseURL
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

// Validates a claim page by sending its request body to the API,
// checking the response status and any additional specified checks,
// then pauses for the configured think time.
  private def validatePage(groupName: String, requestName: String, pageId: String, bodyFile: String, extraChecks: Seq[HttpCheck] = Seq.empty) =
    group(groupName) {
      exec(http(requestName)
        .post(s"/data/case-types/PCS/validate?pageId=$pageId")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody(s"bodies/pcs/makeaclaim/$bodyFile"))
        .check(status.in(200, 201))
        .check(extraChecks: _*))
    }
    .pause(MinThinkTime, MaxThinkTime)

// Saves the uploaded document's ID, hash token and file name
// from the session for use in subsequent requests.
  private def mapUpload(prefix: String, idKey: String, hashKey: String, nameKey: String) =
    exec { session =>
      val url = session(prefix + "DocumentURL").as[String]
      val id = url.split("/").last
      session
        .set(idKey, id)
        .set(hashKey, session(prefix + "DocumentHashToken").as[String])
        .set(nameKey, session(prefix + "DocumentFileName").as[String])
    }

  /*====================================================================================
  * Extract organisation details used across claim pages
  *=====================================================================================*/

  val ExtractOrganisation =

    // Login already GETs /api/organisation (XUI_Login_OrgDetails). Gatling then caches the ETag and
    // re-issues a conditional request here that can return 304. On 304, Gatling skips Body checks
    // (jsonPath saveAs), so orgName would never be stored while the request still counts as OK.
    // Flush the per-user HTTP cache and require 200 so the organisation JSON body is extracted.
    exec(flushHttpCache)
    .group("XUI_PCS_01_ExtractOrganisation") {
      exec(http("XUI_PCS_01_001_Organisation")
        .get("/api/organisation")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("cache-control", "no-cache")
        .check(status.is(200))
        .check(jsonPath("$.name").saveAs("orgName"))
        .check(jsonPath("$.contactInformation[0].addressLine1").saveAs("orgAddressLine1"))
        .check(jsonPath("$.contactInformation[0].addressLine2").optional.saveAs("orgAddressLine2"))
        .check(jsonPath("$.contactInformation[0].townCity").saveAs("orgPostTown"))
        .check(jsonPath("$.contactInformation[0].postCode").saveAs("orgPostCode")))
    }
    // optional.saveAs can store a JSON null when addressLine2 is present but null. Session.contains
    // is then true, so a missing-key fallback never runs, and later #{orgAddressLine2} fails with
    // "Attribute orgAddressLine2's value is null". Normalize null/missing to "".
    .exec { session =>
      val normalized = session.attributes.get("orgAddressLine2") match {
        case Some(value: String) => value
        case Some(null) | None   => ""
        case Some(other)         => other.toString
      }
      session.set("orgAddressLine2", normalized)
    }
    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  * Create possession claim (start trigger → validates → create case)
  * Phase-level val aligned with FR Contested / NFD / Probate / PRL conventions.
  *=====================================================================================*/

  val CreatePossessionClaim =

    group("XUI_PCS_02_StartCreateClaim") {
      exec(http("XUI_PCS_02_001_StartCreateClaim")
        .get("/data/internal/case-types/PCS/event-triggers/createPossessionClaim?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("createClaimEventToken"))
        .check(regex("""feeAmount","label":"","hidden":null,"value":"(.*?)","metadata""").saveAs("feeAmount")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .exec(validatePage(
      "XUI_PCS_03_StartTheService",
      "XUI_PCS_03_001_StartTheService",
      "createPossessionClaimstartTheService",
      "StartTheService.json"
    ))

    .group("XUI_PCS_04_PropertyAddress") {      
      exec(Common.postcodeLookupFromSession)
      .exec { session =>
        val lines = session("addressLines").as[(String, String, String, String)]
        session
          .set("buildingNumber", lines._1)
          .set("buildingName", lines._2)
          .set("postTown", lines._3)
          .set("postCode", lines._4)
      }
      .exec(http("XUI_PCS_04_001_PropertyAddress")
        .post("/data/case-types/PCS/validate?pageId=createPossessionClaimenterPropertyAddress")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/makeaclaim/PropertyAddress.json"))
        .check(status.in(200, 201)))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PCS_05_CreateCase") {
      exec(http("XUI_PCS_05_001_CreateCase")
        .post("/data/case-types/PCS/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/makeaclaim/CreateCase.json"))
        .check(status.in(200, 201))
        .check(jsonPath("$.id").saveAs("caseId")))
      
      .exec { session =>
        if (session.contains("caseId") && !session.isFailed) {
          val user = session("user").as[String]
          val account = if (session.contains("accounts")) session("accounts").as[String] else ""
          val caseId = session("caseId").as[String]
          MakeAClaimCaseRecorder.recordSuccess(user, account, caseId)
        }
        session
      }

      .exec(http("XUI_PCS_05_002_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.case_id").is("#{caseId}")))

      .exec(Common.waJurisdictions)
      .exec(Common.manageLabellingRoleAssignment)
    }
    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  * Resume possession claim (start event → page validates / uploads → submit)
  * Phase-level val aligned with FR Contested / NFD / Probate / PRL conventions.
  *=====================================================================================*/

  val ResumePossessionClaim =

    group("XUI_PCS_06_ResumeClaim") {
      exec(Common.profile)
      .exec(http("XUI_PCS_06_001_ResumeClaim")
        .get("/data/internal/cases/#{caseId}/event-triggers/resumePossessionClaim?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("eventToken")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .exec(validatePage("XUI_PCS_07_ClaimantInformation", "XUI_PCS_07_001_ClaimantInformation",
      "resumePossessionClaimclaimantInformation", "ClaimantInformation.json"))

    .exec(validatePage("XUI_PCS_08_SelectClaimantType", "XUI_PCS_08_001_SelectClaimantType",
      "resumePossessionClaimselectClaimantType", "SelectClaimantType.json"))

    .exec(validatePage("XUI_PCS_09_SelectClaimType", "XUI_PCS_09_001_SelectClaimType",
      "resumePossessionClaimselectClaimType", "SelectClaimType.json"))

    .exec(validatePage("XUI_PCS_10_ContactPreferences", "XUI_PCS_10_001_ContactPreferences",
      "resumePossessionClaimcontactPreferences", "ContactPreferences.json"))
   
    .exec(validatePage(
      "XUI_PCS_11_DefendantDetails",
      "XUI_PCS_11_001_DefendantDetails",
      "resumePossessionClaimdefendantsDetails",
      "DefendantDetails.json"
    ))

    .group("XUI_PCS_12_TenancyUploads") {    
      pause(MinThinkTime, MaxThinkTime)
      .exec(Common.uploadFile("1mb.docx", "PUBLIC", "PCS", "PCS", "tenancyPdf"))
      .exec(mapUpload("tenancyPdf", "documentPdf", "hashTokenPdf", "filenamePdf"))     
      .pause(MinThinkTime, MaxThinkTime)
      .exec(Common.uploadFile("1mb.jpg", "PUBLIC", "PCS", "PCS", "tenancyJpeg"))
      .exec(mapUpload("tenancyJpeg", "documentJpeg", "hashTokenJpeg", "filenameJpeg"))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .exec(validatePage(
      "XUI_PCS_13_TenancyLicenceDetails",
      "XUI_PCS_13_001_TenancyLicenceDetails",
      "resumePossessionClaimtenancyLicenceDetails",
      "TenancyLicenceDetails.json",
      Seq(
        jsonPath("$..tenancy_TenancyLicenceDocuments[0].id").saveAs("documentIdPdf"),
        jsonPath("$..tenancy_TenancyLicenceDocuments[1].id").saveAs("documentIdJpeg")
      )
    ))

    .exec(validatePage("XUI_PCS_14_GroundsForPossession", "XUI_PCS_14_001_GroundsForPossession",
      "resumePossessionClaimgroundsForPossession", "GroundsForPossession.json"))

    .exec(validatePage("XUI_PCS_15_GroundForPossessionRentArrears", "XUI_PCS_15_001_GroundForPossessionRentArrears",
      "resumePossessionClaimgroundForPossessionRentArrears", "GroundForPossessionRentArrears.json"))

    .exec(validatePage("XUI_PCS_16_PreActionProtocol", "XUI_PCS_16_001_PreActionProtocol",
      "resumePossessionClaimpreActionProtocol", "PreActionProtocol.json"))

    .exec(validatePage("XUI_PCS_17_MediationAndSettlement", "XUI_PCS_17_001_MediationAndSettlement",
      "resumePossessionClaimmediationAndSettlement", "MediationAndSettlement.json"))

    .exec(validatePage("XUI_PCS_18_CheckingNotice", "XUI_PCS_18_001_CheckingNotice",
      "resumePossessionClaimcheckingNotice", "CheckingNotice.json"))

    .group("XUI_PCS_19_NoticeUpload") {      
      pause(MinThinkTime, MaxThinkTime)
      .exec(Common.uploadFile("1mb.docx", "PUBLIC", "PCS", "PCS", "notice"))
      .exec(mapUpload("notice", "noticeDoc", "noticeHashToken", "noticeFilename"))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PCS_20_NoticeDetails") {
      exec(http("XUI_PCS_20_001_NoticeDetails")
        .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimnoticeDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/makeaclaim/NoticeDetails.json"))
        .check(status.in(200, 201))
        .check(jsonPath("$..notice_Documents[0].id").saveAs("noticeIdDoc")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .exec(validatePage("XUI_PCS_21_RentDetails", "XUI_PCS_21_001_RentDetails",
      "resumePossessionClaimrentDetails", "RentDetails.json"))

    .group("XUI_PCS_22_RentUpload") {
      // Rent-arrears statement: temporary 5mb.pdf fixture for sanity; session keys remain *Pdf for body EL.
      // Common.uploadFile applies requestTimeout(180.seconds) for this transfer.
      // Temporary sanity-run think time: reduce back-to-back uploads while investigating intermittent HTTP 429.
      pause(MinThinkTime, MaxThinkTime)
      .exec(Common.uploadFile("5mb.pdf", "PUBLIC", "PCS", "PCS", "rent"))
      .exec(mapUpload("rent", "rentDocPdf", "rentHashPdf", "rentFilenamePdf"))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PCS_23_RentArrears") {
      exec(http("XUI_PCS_23_001_RentArrears")
        .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimrentArrears")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/makeaclaim/RentArrears.json"))
        .check(status.in(200, 201))
        .check(jsonPath("$..rentArrears_StatementDocuments[0].id").saveAs("rentIdPdf")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .exec(validatePage("XUI_PCS_24_MoneyJudgment", "XUI_PCS_24_001_MoneyJudgment",
      "resumePossessionClaimmoneyJudgment", "MoneyJudgment.json"))

    .exec(validatePage("XUI_PCS_25_ClaimantCircumstances", "XUI_PCS_25_001_ClaimantCircumstances",
      "resumePossessionClaimclaimantCircumstances", "ClaimantCircumstances.json"))

    .exec(validatePage("XUI_PCS_26_DefendantCircumstances", "XUI_PCS_26_001_DefendantCircumstances",
      "resumePossessionClaimdefendantCircumstances", "DefendantCircumstances.json"))

    .exec(validatePage("XUI_PCS_27_AlternativesToPossession", "XUI_PCS_27_001_AlternativesToPossession",
      "resumePossessionClaimalternativesToPossession", "AlternativesToPossession.json"))

    .exec(validatePage("XUI_PCS_28_HousingActOptions", "XUI_PCS_28_001_HousingActOptions",
      "resumePossessionClaimsuspensionOfRightToBuyHousingActOptions", "HousingActOptions.json"))

    .exec(validatePage("XUI_PCS_29_SuspensionOrderReason", "XUI_PCS_29_001_SuspensionOrderReason",
      "resumePossessionClaimsuspensionOfRightToBuyOrderReason", "SuspensionOrderReason.json"))

    .exec(validatePage("XUI_PCS_30_AdditionalReasons", "XUI_PCS_30_001_AdditionalReasons",
      "resumePossessionClaimadditionalReasonsForPossession", "AdditionalReasons.json"))

    .exec(validatePage("XUI_PCS_31_UnderlesseeMortgagee", "XUI_PCS_31_001_UnderlesseeMortgagee",
      "resumePossessionClaimunderlesseeMortgageeEntitledToClaimRelief", "UnderlesseeMortgagee.json"))

    .exec(validatePage("XUI_PCS_32_WantToUploadDocuments", "XUI_PCS_32_001_WantToUploadDocuments",
      "resumePossessionClaimwantToUploadDocuments", "WantToUploadDocuments.json"))

    .group("XUI_PCS_33_AdditionalUpload") {
      // Temporary sanity-run think time: reduce back-to-back uploads while investigating intermittent HTTP 429.
      pause(MinThinkTime, MaxThinkTime)
      .exec(Common.uploadFile("1mb.docx", "PUBLIC", "PCS", "PCS", "additional"))
      .exec(mapUpload("additional", "additionalPdfDoc", "additionalPdfHash", "additionalFilenamePdf"))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PCS_34_UploadAdditionalDocuments") {
      exec(http("XUI_PCS_34_001_UploadAdditionalDocuments")
        .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimuploadAdditionalDocuments")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/makeaclaim/UploadAdditionalDocuments.json"))
        .check(status.in(200, 201))
        .check(jsonPath("$..additionalDocuments[0].id").saveAs("additionalPdfId")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .exec(validatePage("XUI_PCS_35_GeneralApplication", "XUI_PCS_35_001_GeneralApplication",
      "resumePossessionClaimgeneralApplication", "GeneralApplication.json"))

    .exec(validatePage("XUI_PCS_36_LanguageUsed", "XUI_PCS_36_001_LanguageUsed",
      "resumePossessionClaimlanguageUsed", "LanguageUsed.json"))

    .exec(validatePage("XUI_PCS_37_CompletingYourClaim", "XUI_PCS_37_001_CompletingYourClaim",
      "resumePossessionClaimcompletingYourClaim", "CompletingYourClaim.json"))

    .exec(validatePage("XUI_PCS_38_StatementOfTruth", "XUI_PCS_38_001_StatementOfTruth",
      "resumePossessionClaimstatementOfTruth", "StatementOfTruth.json"))

    .group("XUI_PCS_39_SubmitClaim") {
      exec(http("XUI_PCS_39_001_SubmitClaim")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/makeaclaim/SubmitEvents.json"))
        .check(status.in(200, 201)))

      .exec(http("XUI_PCS_39_002_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.case_id").is("#{caseId}")))
    }
    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  * Pay Now (PBA)
  * Think time: JMX EXUI_SC02_PayNow uses ConstantTimer 5000 ms between open-claim /
  * service-request / PBA-accounts user actions. Project Min/MaxThinkTime (5–7 s)
  * covers that floor between payment steps.
  *=====================================================================================*/

  val PayNow =

    group("XUI_PCS_40_OpenClaimForPayment") {
      exec(http("XUI_PCS_40_001_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.case_id").is("#{caseId}")))
      .exec(Common.waJurisdictions)
      .exec(Common.manageLabellingRoleAssignment)
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PCS_41_ServiceRequest") {
      exec(http("XUI_PCS_41_001_PayBulkScan")
        .get("/pay-bulkscan/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(status.in(200, 201, 204, 404)))

      // After SubmitClaim, /paymentgroups often returns an empty body until the service request
      // is ready. jsonPath on an empty body crashes ("No content to map due to end-of-input").
      // Poll without failing checks: exitBlockOnFail would abort tryMax retries on the first KO,
      // and Gatling may cache empty GET responses. Parse the body in Session once it is non-empty.
      // Wait briefly before polling payment groups again to allow the payment
      // service time to populate the groups after the payment request.
      // Thread.sleep is used instead of Gatling think time because this is a technical polling delay, not user think time.

      .asLongAsDuring(session => !session.contains("serviceRequestRef"), 60) {
        exec(flushHttpCache)
        .exec(http("XUI_PCS_41_002_PaymentGroups_Poll")
          .get("/payments/cases/#{caseId}/paymentgroups")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("cache-control", "no-cache")
          .silent
          .check(status.in(200, 204))
          .check(bodyString.saveAs("paymentGroupsBody")))
        .exec { session =>
          val body = session.attributes.get("paymentGroupsBody") match {
            case Some(value: String) => value
            case Some(other) if other != null => other.toString
            case _ => ""
          }
          if (body.trim.isEmpty) {
            Thread.sleep(2000)
            session
          } else {
            val ref = """"payment_group_reference"\s*:\s*"([^"]+)"""".r.findFirstMatchIn(body).map(_.group(1))
            val fee = """"calculated_amount"\s*:\s*([0-9]+(?:\.[0-9]+)?)""".r.findFirstMatchIn(body).map(_.group(1))
            (ref, fee) match {
              case (Some(serviceRequestRef), Some(feeAmountRaw)) =>
                session
                  .set("serviceRequestRef", serviceRequestRef)
                  .set("feeAmount1", feeAmountRaw.split("\\.").headOption.getOrElse(feeAmountRaw))
              case (Some(serviceRequestRef), None) =>
                session.set("serviceRequestRef", serviceRequestRef)
              case _ =>
                Thread.sleep(2000)
                session
            }
          }
        }
      }
      .exec { session =>
        if (session.contains("serviceRequestRef")) session
        else session.markAsFailed
      }
      .exec(http("XUI_PCS_41_003_PaymentGroups")
        .get("/payments/cases/#{caseId}/paymentgroups")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("cache-control", "no-cache")
        // Payment API returns payment_group_reference (HAR Make a claim)
        .check(status.in(200, 204))
        .check(bodyLength.gt(0))
        .check(jsonPath("$.payment_groups[0].payment_group_reference").saveAs("serviceRequestRef"))
        .check(jsonPath("$.payment_groups[0].fees[0].calculated_amount")
          .transform(raw => raw.split("\\.").headOption.getOrElse(raw))
          .saveAs("feeAmount1")))

      .exec(http("XUI_PCS_41_004_CasePaymentOrders")
        .get("/payments/case-payment-orders?case_ids=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(status.in(200, 201, 204, 404)))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PCS_42_PbaAccounts") {
      exec(http("XUI_PCS_42_001_PbaAccounts")
        .get("/payments/pba-accounts")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(status.in(200, 201)))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PCS_43_ConfirmPbaPayment") {
      exec(_.set("idempotencyKey", UUID.randomUUID().toString))
      .exec(http("XUI_PCS_43_001_ConfirmPbaPayment")
        .post("/payments/service-request/#{serviceRequestRef}/pba-payments")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/makeaclaim/PbaPayment.json"))
        // Capture Pay Now outcome before status.in may fail the request. Under exitBlockOnFail,
        // a later Session exec would be skipped on KO, so update the recorder here. This is also used for further scrits where they need claim created.
        .check(
          status.transformWithSession { (code, session) =>
            val success = code == 200 || code == 201 || code == 202
            if (session.contains("caseId")) {
              MakeAClaimCaseRecorder.updatePayNowResult(session("caseId").as[String], success)
            }
            success.toString
          }.saveAs("payNowSuccess")
        )
        .check(status.in(200, 201, 202)))
    }
    .pause(MinThinkTime, MaxThinkTime)

}
