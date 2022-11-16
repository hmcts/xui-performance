package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new Private Law application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_PRL_Court {
  
  val BaseURL = Environment.baseURL
  val PRLURL = "https://privatelaw.ithc.platform.hmcts.net"

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CreatePrivateLawCase =


    /*======================================================================================
    * Select C100 case
    ======================================================================================*/

    group("XUI_PRL_C100_470_SelectCase") {

      exec(_.setAll(
        "PRLRandomString" -> (Common.randomString(7)),
        "PRLGateKeeperEmail" -> ("${PRLRandomString}" + "@gmail.com"),
        "PRLAppDobDay" -> Common.getDay(),
        "PRLAppDobMonth" -> Common.getMonth(),
        "PRLAppDobYear" -> Common.getDobYear(),
      "caseId" -> "1668027747570467"))

      .exec(http("XUI_PRL_C100_470_005_SelectCase")
        .get(BaseURL + "/cases/case-details/${caseId}")
        .headers(Headers.navigationHeader))
     //   .check(substring("Allocated judge")))

    //    .exec(Common.TsAndCs)

     ///   .exec(Common.configUI)

     //   .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select 'Issue and send to local court'
    ======================================================================================*/

    .group("XUI_PRL_C100_480_SendToLocalCourt") {

      exec(Common.profile)

      .exec(http("XUI_PRL_C100_480_005_SendToLocalCourt")
        .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/issueAndSendToLocalCourtCallback?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("LocalCourtId"))
        .check(jsonPath("$.id").is("issueAndSendToLocalCourtCallback")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Type of Application (C100 or FL401) - C100
    ======================================================================================*/

    .group("XUI_PRL_C100_490_SendToLocalCourtContinue") {
      exec(http("XUI_PRL_C100_490_005_SendToLocalCourtContinue")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=issueAndSendToLocalCourtCallback1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/C100-Continued/SendToLocalCourt.json"))
        .check(substring("localCourtAdmin")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Issue and send to local court Submit
    ======================================================================================*/

    .group("XUI_PRL_C100_500_SendToLocalCourt_Submit") {
      exec(http("XUI_PRL_C100_500_005_SendToLocalCourt_Submit")
        .post(BaseURL + "/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/C100-Continued/PRLSendToLocalCourtSubmit.json"))
        .check(jsonPath("$.state").is("CASE_ISSUE")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

 // val GateKeeper =

    /*======================================================================================
    * Select 'Send to Gate Keeper'
    ======================================================================================*/

    .group("XUI_PRL_C100_510_SendToGateKeeper") {

      exec(http("XUI_PRL_C100_510_SendToGateKeeper")
        .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/sendToGateKeeper?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
     //   .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("LocalCourtId"))
        .check(jsonPath("$.id").is("sendToGateKeeper")))

        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Send to Gatekeeper Enter Email
    ======================================================================================*/

    .group("XUI_PRL_C100_520_SendToGateKeeper_Email") {
      exec(http("XUI_PRL_C100_520_005_SendToGateKeeper_Email")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=sendToGateKeeper1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/prl/C100-Continued/PRLGateKeeper.json"))
        .check(substring("gatekeeper")))

        .exec(Common.userDetails)

    }

    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Send to Gatekeeper Enter Save and Continue
    ======================================================================================*/

      .group("XUI_PRL_C100_530_SendToGateKeeper_Save") {
        exec(http("XUI_PRL_C100_530_005_SendToGateKeeper_Save")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLGateKeeperSubmit.json"))
          .check(substring("gatekeeper")))

          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
  * Click on 'Manage Orders'
  ======================================================================================*/

      .group("XUI_PRL_C100_540_ManageOrders") {
        exec(http("XUI_PRL_C100_540_005_ManageOrders")
          .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/manageOrders?ignore-warning=false")
          .headers(Headers.navigationHeader)
          .check(jsonPath("$.event_token").saveAs("event_token"))
          //   .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("LocalCourtId"))
          .check(jsonPath("$.id").is("manageOrders")))

          .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
  * Create an Order
  ======================================================================================*/

      .group("XUI_PRL_C100_550_CreateAnOrder") {
        exec(http("XUI_PRL_C100_550_005_CreateAnOrder")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders1")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLCreateOrder.json"))
          .check(substring("manageOrdersOptions")))

          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Select 'Special guardianship order (C43A)'
======================================================================================*/

      .group("XUI_PRL_C100_560_CreateAnOrderOptions") {
        exec(http("XUI_PRL_C100_560_005_CreateAnOrderOptions")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders2")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLCreateOrderOptions.json"))
          .check(substring("specialGuardianShip")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Order Details - not sure if needed in script
======================================================================================*/

      .group("XUI_PRL_C100_570_CreateAnOrderDetails") {
        exec(http("XUI_PRL_C100_570_005_CreateAnOrderDetails")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders4")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLCreateOrderDetails.json"))
          .check(substring("previewOrderDoc")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Guardian Details
======================================================================================*/

      .group("XUI_PRL_C100_580_GuardianDetails") {
        exec(http("XUI_PRL_C100_580_005_GuardianDetails")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders10")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .check(jsonPath("$.data.previewOrderDoc.document_url").saveAs("document_url"))
          .check(jsonPath("$.data.previewOrderDoc.document_filename").saveAs("document_filename"))
          .check(jsonPath("$.data.previewOrderDoc.document_hash").saveAs("document_hash"))
          .body(ElFileBody("bodies/prl/C100-Continued/PRLGuardianDetails.json"))
          .check(substring("previewOrderDoc")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Check Your Order
======================================================================================*/

      .group("XUI_PRL_C100_590_CheckYourOrder") {
        exec(http("XUI_PRL_C100_590_005_CheckYourOrder")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders16")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLCheckYourOrder.json"))
          .check(substring("previewOrderDoc")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)

//pdf check??

      /*======================================================================================
* Confirm Recipients
======================================================================================*/

      .group("XUI_PRL_C100_600_ConfirmRecipients") {
        exec(http("XUI_PRL_C100_600_005_ConfirmRecipients")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders17")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLConfirmRecipients.json"))
          .check(substring("orderRecipients")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)



      /*======================================================================================
* Case Order Submit
======================================================================================*/

      .group("XUI_PRL_C100_610_CaseOrderSubmit") {
        exec(http("XUI_PRL_C100_610_005_CaseOrderSubmit")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLCaseOrderSubmit.json"))
          .check(substring("GATE_KEEPING")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)



      /*======================================================================================
* Click on 'Service of Application'
======================================================================================*/

      .group("XUI_PRL_C100_620_ServiceOfApplication") {
        exec(http("XUI_PRL_C100_620_005_ServiceOfApplication")
          .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/serviceOfApplication?ignore-warning=false")
          .headers(Headers.navigationHeader)
          .check(jsonPath("$.event_token").saveAs("event_token"))
          //   .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("LocalCourtId"))
          .check(jsonPath("$.id").is("serviceOfApplication")))

          .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Tick Special guardianship order (C43A) and upload two files
======================================================================================*/

      .group("XUI_PRL_C100_630_PD36QUpload") {

        exec(http("XUI_PRL_C100_630_005_PD36QUpload")
          .post("/documentsv2")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .header("x-xsrf-token", "${XSRFToken}")
          .bodyPart(RawFileBodyPart("files", "PD36Q letter.pdf")
            .fileName("PD36Q letter.pdf")
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

      .group("XUI_PRL_C100_640_SpecialArrangementsLetter") {

        exec(http("XUI_PRL_C100_640_005_SpecialArrangementsLetter")
          .post("/documentsv2")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .header("x-xsrf-token", "${XSRFToken}")
          .bodyPart(RawFileBodyPart("files", "Special arrangements letter.pdf")
            .fileName("Special arrangements letter.pdf")
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


      .group("XUI_PRL_C100_650_AdditionalDocuments") {

        exec(http("XUI_PRL_C100_650_005_AdditionalDocuments")
          .post("/documentsv2")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .header("x-xsrf-token", "${XSRFToken}")
          .bodyPart(RawFileBodyPart("files", "Draft-Consent-Order.pdf")
            .fileName("Draft-Consent-Order.pdf")
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

      .group("XUI_PRL_C100_660_ServiceOfApplicationUpload") {

        exec(http("XUI_PRL_C100_650_005_ServiceOfApplicationUpload")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplicationorderDetails")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLServiceOfApplicationUpload.json"))
          .check(substring("additionalDocuments")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)


      .group("XUI_PRL_C100_670_ServiceRecipients") {

        exec(http("XUI_PRL_C100_670_005_ServiceRecipients")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplicationconfirmRecipients")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLServiceRecipients.json"))
          .check(substring("confirmRecipients")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_C100_680_ServiceSubmit") {

        exec(http("XUI_PRL_C100_680_005_ServiceSubmit")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLServiceSubmit.json"))
          .check(substring("CASE_HEARING")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)

  val PRLPart2 =

  /*======================================================================================
  * Citizen home page
  ======================================================================================*/

    group("XUI_PRL_C100_690_SelectCase") {

      exec(_.setAll(
        "PRLRandomString" -> (Common.randomString(7)),
        "PRLGateKeeperEmail" -> ("${PRLRandomString}" + "@gmail.com"),
        "PRLAppDobDay" -> Common.getDay(),
        "PRLAppDobMonth" -> Common.getMonth(),
        "PRLAppDobYear" -> Common.getDobYear(),
        "caseId" -> "1668027747570467"))

        .exec(http("XUI_PRL_C100_470_005_SelectCase")
          .get(PRLURL + "/citizen-home")
          .headers(Headers.navigationHeader)
         .check(substring("Enter your access details")))

    }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
* Enter your access details
======================================================================================*/


      .group("XUI_PRL_C100_700_AccessDetails") {

        exec(http("XUI_PRL_C100_700_005_AccessDetails")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "${XSRFToken}")
          .body(ElFileBody("bodies/prl/C100-Continued/PRLServiceSubmit.json"))
          .check(substring("CASE_HEARING")))

          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)


}