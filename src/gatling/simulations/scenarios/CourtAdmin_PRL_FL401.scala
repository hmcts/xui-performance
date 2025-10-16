package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.Barrister_PRL_C100.{MaxThinkTime, MinThinkTime}
import scenarios.CourtAdmin_PRL_C100.{MaxThinkTime, MinThinkTime}
import utils.{Common, CsrfCheck, Environment, Headers}

import java.io.{BufferedWriter, FileWriter}

/*===============================================================================================================
* Court Admin FL401 case progression. Send to local court --> Sent to Gatekeeper --> Add an order --> Serve 
================================================================================================================*/

object CourtAdmin_PRL_FL401 {
  
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

    //set session variables
    exec(_.setAll(
      "PRLRandomString" -> (Common.randomString(7)),
      "JudgeFirstName" -> (Common.randomString(4) + "judgefirst"),
      "JudgeLastName" -> (Common.randomString(4) + "judgeLast"),
      "PRLAppDobDay" -> Common.getDay(),
      "PRLAppDobMonth" -> Common.getMonth(),
      "todayDate" -> Common.getDate(),
      "LegalAdviserName" -> (Common.randomString(4) + " " + Common.randomString(4) + "legAdv")))
  
  val CourtAdminCheckApplication =

    exec(http("XUI_PRL_FL401Progress_030_SearchCase")
      .get(BaseURL + "/data/internal/cases/#{caseId}")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .check(jsonPath("$.tabs[0].fields[0].value").saveAs("caseName"))
      .check(jsonPath("$.case_id").is("#{caseId}")))

    .exec(Common.waJurisdictions)
    .exec(Common.activity)
    .exec(Common.userDetails)
    .exec(Common.caseActivityGet)
    .exec(Common.isAuthenticated)

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

  /*=====================================================================================
  * Select Issue and send to local Court
  ======================================================================================*/

  .exec(http("XUI_PRL_FL401Progress_040_IssueAndSendToLocalCourt")
    .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/issueAndSendToLocalCourtCallback/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
    .headers(Headers.navigationHeader)
    .header("accept", "application/json")
    .check(jsonPath("$.task_required_for_event").is("false")))

  .exec(Common.activity)
  .exec(Common.profile)

  .exec(http("XUI_PRL_FL401Progress_050_IssueAndSendToLocalCourtEventTrigger")  //*** SAVE THE Courtlist response here for use in later post requests **
    .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/issueAndSendToLocalCourtCallback?ignore-warning=false")
    .headers(Headers.commonHeader)
    .header("Accept", "application/json, text/plain, */*")
    .check(jsonPath("$.event_token").saveAs("event_token"))
    .check(jsonPath("$.id").is("issueAndSendToLocalCourtCallback"))
    .check(status.in(200, 403)))

  .exec(http("XUI_PRL_FL401Progress_060_IssueAndSendToLocalCourtEvent")
    .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/issueAndSendToLocalCourtCallback/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
    .headers(Headers.navigationHeader)
    .header("accept", "application/json"))
    
  .exec(Common.userDetails)
  .exec(Common.activity)

  .pause(MinThinkTime, MaxThinkTime)

  /*=====================================================================================
  * Select Court from dropdown and submit
  ======================================================================================*/

  .exec(http("XUI_PRL_FL401Progress_070_SelectCourt")
    .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=issueAndSendToLocalCourtCallback1")
    .headers(Headers.commonHeader)
    .header("Accept", "application/json, text/plain, */*")
    .header("x-xsrf-token", "#{XSRFToken}")
    .body(ElFileBody("bodies/prl/fl401/PRLLocalCourt.json"))
    .check(jsonPath("$.data.courtList.value.code").is("234946:")))  //Value does not change for now. 

  .pause(MinThinkTime, MaxThinkTime)

  .exec(Common.activity)
  .exec(Common.userDetails)
  .exec(Common.activity)

  .exec(http("XUI_PRL_FL401Progress_080_SubmitToLocalCourtEvent")
    .post(BaseURL + "/data/cases/#{caseId}/events")
    .headers(Headers.commonHeader)
    .header("Accept", "application/json, text/plain, */*")
    .header("x-xsrf-token", "#{XSRFToken}")
    .body(ElFileBody("bodies/prl/fl401/PRLLocalCourtSubmit.json"))
    .check(jsonPath("$.data.courtList.value.code").is("234946:")))  //Value does not change for now. 

  .pause(MinThinkTime, MaxThinkTime)

  val CourtAdminSendToGateKeeper = 

  exec(http("XUI_PRL_FL401Progress_090_SelectCase")
    .get(BaseURL + "/cases/case-details/#{caseId}/task")
    .headers(Headers.commonHeader)
    .check(substring("HMCTS Manage cases")))

  .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

  .exec(Common.activity)
  .exec(Common.configUI)
  .exec(Common.configJson)
  .exec(Common.userDetails)

/*======================================================================================
* Click on 'Send to Gate Keeper'
======================================================================================*/

  .exec(http("XUI_PRL_FL401Progress_100_SendToGateKeeper")
    .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/sendToGateKeeper?ignore-warning=false")
    .headers(Headers.navigationHeader)
    .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
    .check(jsonPath("$.event_token").saveAs("event_token"))
    .check(jsonPath("$.id").is("sendToGateKeeper")))

    .exec(Common.userDetails)
    //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

  .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Add Gate Keeper
======================================================================================*/

  .exec(http("XUI_PRL_FL401Progress_110_AddGateKeeper")
    .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=sendToGateKeeper1")
    .headers(Headers.commonHeader)
    .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
    .header("x-xsrf-token", "#{XSRFToken}")
    .body(ElFileBody("bodies/prl/fl401/PRLAddGateKeeper.json"))
    .check(substring("isJudgeOrLegalAdviserGatekeeping")))

  .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Send to Gate Keeper Submit
======================================================================================*/

  .group("XUI_PRL_FL401Progress_120_GateKeeperSubmit") {
    exec(http("XUI_PRL_FL401Progress_120_GateKeeperSubmit")
      .post(BaseURL + "/data/cases/#{caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLAddGateKeeperSubmit.json"))
      .check(substring("gatekeepingDetails")))

    .exec(http("XUI_PRL_FL401Progress_130_SelectCase")
      .get(BaseURL + "/data/internal/cases/#{caseId}")
      .headers(Headers.commonHeader)
      .check(jsonPath("$.case_type.name").is("C100 & FL401 Applications")))
  } 

  .pause(MinThinkTime, MaxThinkTime)

  val CourtAdminManageOrders = 

  //set session variables
  exec(_.setAll(
    "PRLRandomString" -> (Common.randomString(7)),
    "JudgeFirstName" -> (Common.randomString(4) + "judgefirst"),
    "JudgeLastName" -> (Common.randomString(4) + "judgeLast"),
    "PRLAppDobDay" -> Common.getDay(),
    "PRLAppDobMonth" -> Common.getMonth(),
    "todayDate" -> Common.getDate(),
    "LegalAdviserName" -> (Common.randomString(4) + " " + Common.randomString(4) + "legAdv")))

  /*======================================================================================
  * Open Case
  ======================================================================================*/

  .exec(http("XUI_PRL_FL401Progress_130_SelectCase")
    .get(BaseURL + "/data/internal/cases/#{caseId}")
    .headers(Headers.commonHeader)
    .check(jsonPath("$.tabs[6].fields[0].value.firstName").saveAs("ApplicantFirstName"))
    .check(jsonPath("$.tabs[6].fields[0].value.lastName").saveAs("ApplicantLastName"))
    .check(jsonPath("$.tabs[6].fields[1].value.firstName").saveAs("RespondentFirstName"))
    .check(jsonPath("$.tabs[6].fields[1].value.lastName").saveAs("RespondentLastName"))
    .check(jsonPath("$.case_id").is("#{caseId}")))

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Click on 'Manage Orders'
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_140_ManageOrders") {
    exec(http("XUI_PRL_FL401Progress_140_005_ManageOrders")
      .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/manageOrders?ignore-warning=false")
      .headers(Headers.navigationHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(jsonPath("$.id").is("manageOrders")))

      .exec(Common.userDetails)
  }
  //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
  .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Create an Order
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_150_CreateOrder") {
    exec(http("XUI_PRL_FL401Progress_150_005_CreateOrder")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLCreateOrder.json"))
      .check(substring("isSdoSelected")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Select Order - Blank Order
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_160_SelectOrder") {
    exec(http("XUI_PRL_FL401Progress_160_005_SelectOrder")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders2")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401//PRLSelectOrderFL401.json"))
      .check(jsonPath("$.data.ordersHearingDetails[0].id").saveAs("hearingDetailsID"))
      .check(substring("caApplicant3InternalFlags")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order Details
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_170_OrderDetails") {
    exec(http("XUI_PRL_FL401Progress_170_005_OrderDetails")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders5")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLOrderDetailsFL401.json"))
      .check(substring("isEngDocGen")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Hearing Outcome
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_180_HearingOutcome") {
    exec(http("XUI_PRL_FL401Progress_180_005_HearingOutcome")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders12")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLHearingOutcome.json"))
      .check(jsonPath("$.data.previewOrderDoc.document_url").saveAs("document_url"))
      .check(jsonPath("$.data.previewOrderDoc.document_hash").saveAs("document_hash"))
      .check(substring("OrgPolicyCaseAssignedRole")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Hearing Type
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_190_HearingType") {
    exec(http("XUI_PRL_FL401Progress_190_HearingType")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders19")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLHearingType.json"))
      .check(jsonPath("$.data.previewOrderDoc.document_url").saveAs("document_url"))
      .check(jsonPath("$.data.previewOrderDoc.document_filename").saveAs("document_filename"))
      .check(jsonPath("$.data.previewOrderDoc.document_hash").saveAs("document_hash"))
      .check(jsonPath("$.data.ordersHearingDetails[0].id").saveAs("hearingId"))
      .check(substring("previewOrderDoc")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Check Your Order
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_200_CheckYourOrder") {
    exec(http("XUI_PRL_FL401Progress_200_005_CheckYourOrder")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders20")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLCheckOrderFL401.json"))
      .check(substring("previewOrderDoc")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order Recipients
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_210_OrderRecipients") {
    exec(http("XUI_PRL_FL401Progress_210_005_OrderRecipients")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders24")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLOrderRecipientsFL401.json"))
      .check(substring("amendOrderSelectCheckOptions")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order Serve
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_220_OrderServe") {
    exec(http("XUI_PRL_FL401Progress_220_005_OrderServe")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders26")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLOrderServeFL401.json"))
      .check(jsonPath("$.data.serveOrderDynamicList.value[0].code").saveAs("orderCode"))
      .check(jsonPath("$.data.serveOrderDynamicList.value[0].label").saveAs("orderLabel"))
      .check(substring("orderRecipients")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order To Serve List
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_230_OrderServe") {
    exec(http("XUI_PRL_FL401Progress_230_005_OrderToServeList")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders27")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLOrderToServeListFL401.json"))
      .check(substring("orderWithoutGivingNoticeToRespondent")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Serve to Respondent Options
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_240_ServeToRespondentOptions") {
    exec(http("XUI_PRL_FL401Progress_240_005_ServeToRespondentOptions")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders28")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLOrderToServeRespondentOptionsFL401.json"))
      .check(substring("submitCountyCourtSelection")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Order Submit
  ======================================================================================*/

  .group("XUI_PRL_FL401Progress_250_OrderSubmit") {
    exec(http("XUI_PRL_FL401Progress_250_005_OrderSubmit")
      .post(BaseURL + "/data/cases/#{caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/fl401/PRLOrderSubmitFL401.json"))
      .check(substring("JUDICIAL_REVIEW")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  val CourtAdminServiceApplication =

  /*======================================================================================
  * Click on 'Service of Application'
  ======================================================================================*/

    group("XUI_PRL_FL401Progress_260_ServiceOfApplication") {
      exec(http("XUI_PRL_FL401Progress_260_005_ServiceOfApplication")
        .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/serviceOfApplication?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[7].value.list_items[0].code")saveAs("serviceOfApplicationScreenCode"))
        .check(jsonPath("$.case_fields[7].value.list_items[0].label")saveAs("serviceOfApplicationScreenLabel"))
        .check(jsonPath("$.id").is("serviceOfApplication")))

      .exec(http("XUI_Common_000_UserDetails")
        .get("/api/user/details?refreshRoleAssignments=undefined")
        .headers(Headers.commonHeader)
        .header("Cache-Control", "no-cache") 
        .header("Pragma", "no-cache")
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$.roleAssignmentInfo[0].primaryLocation")saveAs("locationId"))
        .check(status.in(200)))
      
    }

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Safe Of Notice letter Upload
  ======================================================================================*/

    .group("XUI_PRL_FL401Progress_270_SoNUpload") {
      exec(http("XUI_PRL_FL401Progress_270_005_SoNUpload")
        .post(BaseURL + "/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "#{XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "120KB.pdf")
        .fileName("120KB.pdf")
        .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "PRLAPPS")
        .formParam("jurisdictionId", "PRIVATELAW")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("documentHashSoN"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLSoN")))
     }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Service of Application document uploads
  ======================================================================================*/

    .group("XUI_PRL_FL401Progress_280_DocumentUpload") {
      exec(http("XUI_PRL_FL401Progress_280_005_DocumentUpload")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplication2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/fl401/PRLSoADocumentsFL401.json"))
        .check(substring("additionalDocuments")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Service of Application Confirm recipients
  ======================================================================================*/

    .group("XUI_PRL_FL401Progress_290_ServiceRecipients") {
      exec(http("XUI_PRL_FL401Progress_290_005_ServiceRecipients")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplication4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/fl401/PRLSoARecipientsFL401.json"))
        .check(substring("soaServingRespondents")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Service of Application Submit
  ======================================================================================*/

    .group("XUI_PRL_FL401Progress_300_ServiceSubmit") {
      exec(http("XUI_PRL_FL401Progress_300_005_ServiceSubmit")
        .post(BaseURL + "/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/fl401/PRLSoASubmitFL401.json"))
        .check(jsonPath("$.data.caseInvites[0].value.accessCode").saveAs("prlAccessCodeApplicant"))
        .check(jsonPath("$.data.caseInvites[1].value.accessCode").saveAs("prlAccessCodeRespondent"))
        .check(jsonPath("$.data.respondentsFL401.partyId").saveAs("respondentPartyId"))
        .check(jsonPath("$.data.respondentsFL401.solicitorPartyId").saveAs("respondentSolicitorPartyId"))
        .check(jsonPath("$.data.applicantsFL401.partyId").saveAs("applicantPartyId"))
        .check(jsonPath("$.data.applicantsFL401.solicitorPartyId").saveAs("applicantSolicitorPartyId"))
        .check(jsonPath("$.data.applicantsFL401.solicitorOrgUuid").saveAs("solicitorOrgId"))
        .check(status.is(201)))
    }

  val CourtAdminListHearing =

/*======================================================================================
* Click the Hearing Tab
======================================================================================*/

    group("XUI_PRL_FL401Progress_310_HearingsTab") {
      exec(http("XUI_PRL_FL401Progress_310_005_HearingsTab")
        .get("/api/hearings/getHearings?caseId=#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")

        .check(status.is(200)))

      .exec(http("XUI_PRL_FL401Progress_310_010_GetHearingsJurisdiction")
        .post("/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"caseReference":"#{caseId}"}"""))
        .check(substring("hearing-facilities")))

      .exec(http("XUI_PRL_FL401Progress_310_015_GetHearingTypes")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingType")))

      .exec(Common.caseActivityPost)
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Select Request a Hearing
=======================================================================================*/

    .group("XUI_PRL_FL401Progress_320_RequestHearing") {

      exec(Common.caseActivityPost)
      .exec(Common.isAuthenticated)

      .exec(http("XUI_PRL_FL401Progress_320_005_GetCaseFlag")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))

      .exec(http("XUI_PRL_FL401Progress_320_010_LocationById")
        .get("/api/prd/location/getLocationById?epimms_id=#{locationId}&serviceCode=null") //*CORELE
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))
  
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Navigate through hearing screens and submit hearing request
=======================================================================================*/

      .exec(http("XUI_PRL_FL401Progress_330_GetCaseFlag")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))


      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_FL401Progress_340_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_FL401Progress_350_LocationByIdServiceCode")
        .get("/api/prd/location/getLocationById?epimms_id=#{locationId}&serviceCode=ABA5") 
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_FL401Progress_360_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=JudgeType&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("JudgeType")))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_FL401Progress_370_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingPriority")))

    .group("XUI_PRL_FL401Progress_380_ListHearing") {
       exec(http("XUI_PRL_FL401Progress_380_005_LoadServiceLinkedCases")
        .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .body(StringBody("""{"caseReference":"#{caseId}","hearingId": ""}"""))
        .check(status.is(200)))

       .exec(http("XUI_PRL_FL401Progress_380_010_CaseLinkingReasonCode")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingPriority")))
     }

      .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_FL401Progress_390_ListHearing") {
      exec(http("XUI_PRL_FL401Progress_390_005_GetCaseFlag")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))


      .exec(http("XUI_PRL_FL401Progress_390_010_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .exec(http("XUI_PRL_FL401Progress_390_015_GetHearingSubChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingSubChannel")))

      .exec(http("XUI_PRL_FL401Progress_390_020_LocationByIdServiceCode")
        .get("/api/prd/location/getLocationById?epimms_id=#{locationId}&serviceCode=ABA5") 
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))

    }

      .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Submit hearing request
=======================================================================================*/

      .exec(http("XUI_PRL_FL401Progress_400_SubmitHearingRequest")
        .post("/api/hearings/submitHearingRequest")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/fl401/PRLFL401SubmitHearing.json"))
        .check(jsonPath("$.hearingRequestID").saveAs("hearingRequestId"))
        .check(status.is(201)))

  //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
  //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

val CourtAdminHearingsTab = 

    /*======================================================================================
    * Click on the Hearings tab to view any Hearings
    ======================================================================================*/

 /*======================================================================================
    * Click on the Hearings tab to view any Hearings
    ======================================================================================*/

    group("XUI_PRL_FL401Progress_410_HearingsTab") {
      exec(http("XUI_PRL_FL401_410_005_HearingsTab")
        .get(BaseURL + "/cases/case-details/#{caseId}/hearings")
        .headers(Headers.commonHeader)
        .check(status.is(200)))

      .exec(Common.configUI)
      .exec(Common.userDetails)
      .exec(Common.isAuthenticated)

      .exec(http("XUI_PRL_FL401Progress_410_010_HearingsTabGetCaseData")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.is(200)))

      .exec(Common.monitoringTools)
      .exec(Common.manageLabellingRoleAssignment)
      .exec(Common.waJurisdictions)

      .exec(http("XUI_PRL_FL401Progress_410_015_HearingsTabCaseWorkerJurisdictions")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.is(200)))

      .exec(Common.activity)

      .exec(http("XUI_PRL_FL401Progress_410_020_HearingsTabGetHearings")
        .get("/api/hearings/getHearings?caseId=#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("LISTED"))
        .check(status.is(200)))

      .exec(http("XUI_PRL_FL401Progress_410_025_HearingsTabLoadHearingValues")
        .post("/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"caseReference":"#{caseId}"}"""))
        .check(substring("hearing-facilities")))

      .exec(http("XUI_PRL_FL401Progress_410_030_GetHearingTypes")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingType")))

      .exec(Common.caseActivityPost)
    }
    .pause(MinThinkTime, MaxThinkTime)


  val ServiceOfApplicationFL401 =

    /*======================================================================================
    * Run ServiceOfApplication task
    ======================================================================================*/

      group("XUI_PRL_FL401progress_400_010_ServiceOfApplicationPrivateLaw") {
        exec(http("XUI_PRL_FL401progress_400_010_ServiceOfApplicationPrivateLaw")
          .get("/workallocation/case/tasks/#{caseId}/event/adminAddBarrister/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .check(substring("task_required_for_event"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_FL401progress_400_020_ServiceOfApplicationTask_EventTrigger") {
        exec(http("XUI_PRL_FL401progress_400_020_ServiceOfApplicationTask_EventTrigger")
            .get("/data/internal/cases/#{caseId}/event-triggers/serviceOfApplication?ignore-warning=false")
            .headers(Headers.commonHeader)
            .header("Accept", "application/json, text/plain, */*")
            .check(jsonPath("$.case_fields[7].value.list_items[0].code").saveAs("appCodeOrder1"))
            .check(jsonPath("$.case_fields[7].value.list_items[0].label").saveAs("appLabelOrder1"))
            .check(jsonPath("$.case_fields[30].value.list_items[0].code").saveAs("appCode1"))
            .check(jsonPath("$.case_fields[30].value.list_items[0].label").saveAs("appLabel1"))
            .check(jsonPath("$.case_fields[30].value.list_items[1].code").saveAs("appCode2"))
            .check(jsonPath("$.case_fields[30].value.list_items[1].label").saveAs("appLabel2"))
            .check(jsonPath("$.event_token").saveAs("event_token"))
            .check(substring("Event to serve the parties"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

          /*======================================================================================
          * Document Upload
          ======================================================================================*/
      .group("XUI_PRL_FL401progress_200_030_ServiceOfApplicationTask_Upload") {
          exec(http("XUI_PRL_FL401progress_200_030_ServiceOfApplicationTask_Upload")
            .post("/documents")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("content-type", "multipart/form-data")
            .bodyPart(RawFileBodyPart("files", "120KB.pdf")
              .fileName("120KB.pdf")
              .transferEncoding("binary"))
            .asMultipartForm
            .formParam("classification", "PUBLIC")
            .formParam("caseTypeId", "PRLAPPS")
            .formParam("jurisdictionId", "PRIVATELAW")
            .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("document_url"))
            .check(substring("originalDocumentName"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

          /*======================================================================================
          * Select the Draft Order
          ======================================================================================*/
      .group("XUI_PRL_FL401progress_400_040_ServiceOfApplicationDetails") {
        exec(http("XUI_PRL_FL401progress_400_040_ServiceOfApplicationDetails")
            .post("/data/case-types/PRLAPPS/validate?pageId=serviceOfApplication2")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/fl401/PRLServiceOfApplicationDetails.json"))
            .check(substring("serviceOfApplicationHeader"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

          /*======================================================================================
          * Enter final decision details
          ======================================================================================*/
      .group("XUI_PRL_FL401progress_400_040_ServiceOfApplicationDetailsSubmit") {
          exec(http("XUI_PRL_FL401progress_400_040_ServiceOfApplicationDetailsSubmit")
              .post("/data/case-types/PRLAPPS/validate?pageId=serviceOfApplication4")
              .headers(Headers.commonHeader)
              .header("Content-Type", "application/json; charset=utf-8")
              .header("Accept", "application/json, text/plain, */*")
              .body(ElFileBody("bodies/prl/fl401/PRLServiceOfApplicationDetailsSubmit.json"))
              .check(substring("FPRL Child Arrangements"))
              .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

          /*======================================================================================
          * ServiceOfApplication Event
          ======================================================================================*/
      .group("XUI_PRL_FL401progress_400_050_ServiceOfApplicationEvent") {
        exec(http("XUI_PRL_FL401progress_400_050_ServiceOfApplicationEvent")
          .post("/data/cases/#{caseId}/events")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/fl401/PRLServiceOfApplicationEvent.json"))
          .check(substring("I understand that proceedings for contempt of court"))
          .check(status.is(201)))
      }

      .pause(MinThinkTime, MaxThinkTime)

  val FinalDecisionFL401 =

    /*======================================================================================
    * Run recordFinalDecision task
    ======================================================================================*/

      group("XUI_PRL_FL401progress_500_010_FinalOrderPrivateLaw") {
        exec(http("XUI_PRL_FL401progress_500_010_FinalOrderPrivateLaw")
          .get("/workallocation/case/tasks/#{caseId}/event/adminAddBarrister/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .check(substring("task_required_for_event"))
          .check(status.is(200)))
     }

    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_FL401progress_500_020_FinalOrderTask_EventTrigger") {
      exec(http("XUI_PRL_FL401progress_500_020_FinalOrderTask_EventTrigger")
          .get("/data/internal/cases/#{caseId}/event-triggers/recordFinalDecision?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(substring("Record final decision"))
          .check(status.is(200)))
    }

    .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Upload Doc
        ======================================================================================*/
    .group("XUI_PRL_FL401progress_500_030_FinalOrderTask_Upload") {
      exec(http("XUI_PRL_FL401progress_500_030_FinalOrderTask_Upload")
          .post("/documents")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .bodyPart(RawFileBodyPart("files", "120KB.pdf")
            .fileName("120KB.pdf")
            .transferEncoding("binary"))
          .asMultipartForm
          .formParam("classification", "PUBLIC")
          .formParam("caseTypeId", "PRLAPPS")
          .formParam("jurisdictionId", "PRIVATELAW")
          .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("document_url"))
          .check(substring("originalDocumentName"))
          .check(status.is(200)))
    }

    .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Select final order
        ======================================================================================*/
    .group("XUI_PRL_FL401progress_500_040_FinalOrderDetails") {
      exec(http("XUI_PRL_FL401progress_500_040_FinalOrderDetails")
          .post("/data/case-types/PRLAPPS/validate?pageId=recordFinalDecision1")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/fl401/PRLFinalOrder.json"))
          .check(substring("fl401UploadWitnessDocuments"))
          .check(status.is(200)))
    }

    .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Enter final order details
        ======================================================================================*/
    .group("XUI_PRL_FL401progress_500_040_FinalOrderDetailsSubmit") {
      exec(http("XUI_PRL_FL401progress_500_040_FinalOrderDetailsSubmit")
          .post("/data/case-types/PRLAPPS/validate?pageId=recordFinalDecision2")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/fl401/PRLFinalOrderDetails.json"))
          .check(substring("solicitorRepresented"))
          .check(status.is(200)))
    }

    .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Final order Event
        ======================================================================================*/
    .group("XUI_PRL_FL401progress_500_050_FinalOrderEvent") {
      exec(http("XUI_PRL_FL401progress_500_050_FinalOrderEvent")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/prl/fl401/PRLFinalOrderEvent.json"))
        .check(substring("ALL_FINAL_ORDERS_ISSUED"))
        .check(status.is(201)))
    }

    .pause(MinThinkTime, MaxThinkTime)

}

//Write applicant access code to file
    // .exec { session =>
    //   val fw = new BufferedWriter(new FileWriter("FL401caseNumberAndCodeApplicant.csv", true))
    //   try {
    //     fw.write(session("caseId").as[String] + "," + session("prlAccessCodeApplicant").as[String] + "\r\n")
    //   } finally fw.close()
    //   session
    // }
    // //Write respondent access code to file
    // .exec { session =>
    //   val fw = new BufferedWriter(new FileWriter("FL401caseNumberAndCodeRespondent.csv", true))
    //   try {
    //     fw.write(session("caseId").as[String] + "," + session("prlAccessCodeRespondent").as[String] + "\r\n")
    //   } finally fw.close()
    //   session
    // }

// -- ADD HEARINGS NAVIGATION AND ADD HEARING 