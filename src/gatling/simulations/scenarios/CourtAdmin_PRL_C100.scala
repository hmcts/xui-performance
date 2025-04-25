package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, CsrfCheck, Environment, Headers}
import java.io.{BufferedWriter, FileWriter}

/*===============================================================================================================
* Court Admin C100 case progression. Send to local court --> Send to Gatekeeper --> Add an order --> Serve 
================================================================================================================*/

object CourtAdmin_PRL_C100 {
  
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

    exec(_.setAll(
      "PRLRandomString" -> (Common.randomString(7)),
      "PRLRandomPhone" -> (Common.randomNumber(8)),
      "PRLAppDobDay" -> Common.getDay(),
      "PRLAppDobMonth" -> Common.getMonth(),
      "JudgeFirstName" -> (Common.randomString(4) + "judgefirst"),
      "JudgeLastName" -> (Common.randomString(4) + "judgeLast"),
      "todayDate" -> Common.getDate(),
      "LegalAdviserName" -> (Common.randomString(4) + " " + Common.randomString(4) + "legAdv")))
  
  val CourtAdminCheckApplication =

    exec(http("XUI_PRL_C100Progress_030_SearchCase")
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
   * Select task tab 
   ======================================================================================*/

    .exec(http("XUI_PRL_C100Progress_040_SelectCase")
      .get(BaseURL + "/cases/case-details/#{caseId}/task")
      .headers(Headers.commonHeader)
      .check(substring("HMCTS Manage cases")))

    .exec(http("XUI_PRL_C100Progress_050_SelectCaseTask")
      .get(BaseURL + "/workallocation/case/task/#{caseId}")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .check(jsonPath("$[0].id").optional.saveAs("taskId"))
      .check(jsonPath("$[0].type").optional.saveAs("taskType")))

    //Save taskType from response
    .exec(session => {
      // Initialise task type in session if it's not already present, ensure the variable exists before entering Loop
      session("taskType").asOption[String] match {
        case Some(taskType) => session
        case None => session.set("taskType", "")
      }
    })

    // Loop until the task type matches "checkApplicationC100" *For Cases which selected HWF different steps are required here
    .asLongAs(session => session("taskType").as[String] != "checkApplicationC100") {
      exec(http("XUI_PRL_C100Progress_055_SelectCaseTaskRepeat")
        .get(BaseURL + "/workallocation/case/task/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$[0].id").optional.saveAs("taskId"))
        .check(jsonPath("$[0].type").optional.saveAs("taskType")))

      .pause(5, 10) // Wait between retries

    //   // Log task Type
    //   .exec (session => {
    //     println(s"Current Task Type: ${session("taskType").as[String]}")
    //     session
    // })
  }

  /*=====================================================================================
  * Claim the task
  ======================================================================================*/

  .exec(http("XUI_PRL_C100Progress_060_ClaimTask")
      .post(BaseURL + "/workallocation/task/#{taskId}/claim")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(StringBody("""{}"""))
      .check(status.in(200, 204)))

  .pause(MinThinkTime, MaxThinkTime)

  /*=====================================================================================
  * Select Issue and send to local Court
  ======================================================================================*/

    .exec(http("XUI_PRL_C100Progress_070_IssueAndSendToLocalCourt")
      .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/issueAndSendToLocalCourtCallback/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
      .headers(Headers.navigationHeader)
      .header("accept", "application/json")
      .check(jsonPath("$.task_required_for_event").is("true")))

    .exec(Common.activity)
    .exec(Common.profile)

    .exec(http("XUI_PRL_C100Progress_080_IssueAndSendToLocalCourtEventTrigger")  //*** SAVE THE Courtlist response here for use in later post requests **
      .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/issueAndSendToLocalCourtCallback?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(jsonPath("$.id").is("issueAndSendToLocalCourtCallback"))
      .check(status.in(200, 403)))

    .exec(http("XUI_PRL_C100Progress_090_IssueAndSendToLocalCourtEvent")
      .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/issueAndSendToLocalCourtCallback/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
      .headers(Headers.navigationHeader)
      .header("accept", "application/json"))
      //.check(substring("PRIVATELAW")))
    
    // .exec(Common.caseActivityPost)
    .exec(Common.userDetails)
    //.exec(Common.caseActivityOnlyGet)

    .pause(MinThinkTime, MaxThinkTime)

  /*=====================================================================================
   * Select Court from dropdown and submit
   ======================================================================================*/

    .exec(http("XUI_PRL_C100Progress_100_SelectCourt")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=issueAndSendToLocalCourtCallback1")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/c100/PRLLocalCourt.json"))
      .check(jsonPath("$.data.courtList.value.code").is("234946:")))  //Value does not change for now. 

    .pause(MinThinkTime, MaxThinkTime)

    .exec(Common.activity)
    .exec(Common.userDetails)
    .exec(Common.activity)

    .exec(http("XUI_PRL_C100Progress_110_SubmitToLocalCourtEvent")
      .post(BaseURL + "/data/cases/#{caseId}/events")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/c100/PRLLocalCourtSubmit.json"))
      .check(jsonPath("$.data.courtList.value.code").is("234946:")))  //Value does not change for now. 

    .exec(http("XUI_PRL_C100Progress_120_SubmitToLocalCourtCompleteTask")
      .post(BaseURL + "/workallocation/task/#{taskId}/complete")
      .headers(Headers.navigationHeader)
      .header("Content-Type", "application/json")
      .header("x-xsrf-token", "#{XSRFToken}")
      .header("accept", "application/json") //No check available for this request
      .body(StringBody("""{"actionByEvent":true,"eventName":"Issue and send to local court"}""")))

    .pause(MinThinkTime, MaxThinkTime)

  val CourtAdminSendToGateKeeper = 

    exec(http("XUI_PRL_C100Progress_130_SelectCase")
      .get(BaseURL + "/cases/case-details/#{caseId}/task")
      .headers(Headers.commonHeader)
      .check(substring("HMCTS Manage cases")))

    //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .exec(Common.activity)
    .exec(Common.configUI)
    .exec(Common.configJson)
    .exec(Common.userDetails)

    .exec(http("XUI_PRL_C100Progress_140_SelectCaseTask")
      .get(BaseURL + "/workallocation/case/task/#{caseId}")
      .headers(Headers.commonHeader)
      .header("Accept", "application/json, text/plain, */*")
      .header("x-xsrf-token", "#{XSRFToken}")
      .check(jsonPath("$[0].id").optional.saveAs("taskId"))
      .check(jsonPath("$[0].type").optional.saveAs("taskType"))
      )

    // Log task Type
      // .exec (session => {
      //   println(s"Current respTaskType: ${session("respTaskType").as[String]}")
      //   println(s"Current respTaskId: ${session("respTaskId").as[String]}")
      //   session
      // })

    //Save taskType from response
    .exec(session => {
      // Initialise task type in session if it's not already present, ensure the variable exists before entering Loop
      session("taskType").asOption[String] match {
        case Some(taskType) => session
        case None => session.set("taskType", "")
      }
    })

    // Loop until the task type matches "sendToGateKeeperC100"
    .asLongAs(session => session("taskType").as[String] != "sendToGateKeeperC100") {
      exec(http("XUI_PRL_C100Progress_150_SelectCaseTaskRepeat")
        .get(BaseURL + "/workallocation/case/task/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$[0].id").optional.saveAs("taskId"))
        .check(jsonPath("$[0].type").optional.saveAs("taskType")))

      .pause(5, 10) // Wait between retries

    //   // Log task Type
    //   .exec (session => {
    //     println(s"Current respTaskType: ${session("respTaskType").as[String]}")
    //     println(s"Current respTaskId: ${session("respTaskId").as[String]}")
    //     session
    // })
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Click on 'Send to Gate Keeper'
  ======================================================================================*/

    .exec(http("XUI_PRL_C100Progress_160_SendToGateKeeper")
      .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/sendToGateKeeper?ignore-warning=false")
      .headers(Headers.navigationHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(jsonPath("$.id").is("sendToGateKeeper")))

      .exec(Common.userDetails)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
      //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Add Gate Keeper
  ======================================================================================*/

    .exec(http("XUI_PRL_C100Progress_170_AddGateKeeper")
      .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=sendToGateKeeper1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/prl/c100/PRLAddGateKeeper.json"))
      .check(substring("isJudgeOrLegalAdviserGatekeeping")))

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Send to Gate Keeper Submit
  ======================================================================================*/

    .group("XUI_PRL_C100Progress_180_GateKeeperSubmit") {
      exec(http("XUI_PRL_C100Progress_180_005_GateKeeperSubmit")
        .post(BaseURL + "/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLAddGateKeeperSubmit.json"))
        .check(substring("gatekeepingDetails")))

      .exec(http("XUI_PRL_C100Progress_180_010_GateKeeperSubmitCompleteTask")
        .post(BaseURL + "/workallocation/task/#{taskId}/complete")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"actionByEvent":true,"eventName":"Send to Gatekeeper"}""")))

      .exec(http("XUI_PRL_C100Progress_180_015_SelectCase")
        .get(BaseURL + "/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(jsonPath("$.case_type.name").is("C100 & FL401 Applications")))
    } 

    .pause(MinThinkTime, MaxThinkTime)

  val CourtAdminManageOrders = 

  exec(_.setAll(
      "PRLRandomString" -> (Common.randomString(7)),
      //"PRLRandomPhone" -> (Common.randomNumber(8)),
      //"PRLAppDobDay" -> Common.getDay(),
      //"PRLAppDobMonth" -> Common.getMonth(),
      //"JudgeFirstName" -> (Common.randomString(4) + "judgefirst"),
      "JudgeLastName" -> (Common.randomString(4) + "judgeLast"),
      "todayDate" -> Common.getDate(),
      "OrderDateYear" -> Common.getCurrentYear(),
      "OrderDateMonth" -> Common.getCurrentMonth(),
      "OrderDateDay" -> Common.getCurrentDay(),
      "LegalAdviserName" -> (Common.randomString(4) + " " + Common.randomString(4) + "legAdv"))) //*/

    /*======================================================================================
    * Click on 'Manage Orders'
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_190_ManageOrders") {
      exec(http("XUI_PRL_C100Progress_190_005_ManageOrders")
        .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/manageOrders?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("manageOrders")))

        .exec(Common.userDetails)
        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
        //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Create an Order
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_200_CreateOrder") {
      exec(http("XUI_PRL_C100Progress_200_005_CreateOrder")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCreateOrder.json"))
        .check(substring("isSdoSelected")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Order - Special guardianship order (C43A)
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_210_SelectOrder") {
      exec(http("XUI_PRL_C100Progress_210_005_SelectOrder")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSelectOrder.json"))
        .check(substring("otherPartyInTheCaseRevised")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Order Details
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_220_OrderDetails") {
      exec(http("XUI_PRL_C100Progress_220_005_OrderDetails")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders5")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOrderDetails.json"))
        .check(jsonPath("$.data.appointedGuardianName[0].id").saveAs("guardianId"))
        .check(substring("isEngDocGen")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Guardian Name
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_230_GuardianName") {
      exec(http("XUI_PRL_C100Progress_230_005_GuardianName")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders11")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLGuardianName.json"))
        .check(jsonPath("$.data.previewOrderDoc.document_url").saveAs("document_url"))
        .check(jsonPath("$.data.previewOrderDoc.document_hash").saveAs("document_hash"))
        .check(substring("previewOrderDoc")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Check Your Order
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_240_CheckYourOrder") {
      exec(http("XUI_PRL_C100Progress_240_005_CheckYourOrder")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders20")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLCheckOrder.json"))
        .check(substring("previewOrderDoc")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Order Recipients
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_250_OrderRecipients") {
      exec(http("XUI_PRL_C100Progress_250_005_OrderRecipients")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders24")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOrderRecipients.json"))
        .check(substring("amendOrderSelectCheckOptions")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Order Serve
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_260_OrderServe") {
      exec(http("XUI_PRL_C100Progress_260_005_OrderServe")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders26")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOrderServe.json"))
        .check(jsonPath("$.data.serveOrderDynamicList.value[0].code").saveAs("orderCode"))
        .check(jsonPath("$.data.serveOrderDynamicList.value[0].label").saveAs("orderLabel"))
        .check(substring("orderRecipients")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Order To Serve List
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_270_OrderServe") {
      exec(http("XUI_PRL_C100Progress_270_005_OrderToServeList")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders27")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOrderToServeList.json"))
        .check(substring("otherPartyInTheCaseRevised")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Serve to Respondent Options
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_280_ServeToRespondentOptions") {
      exec(http("XUI_PRL_C100Progress_280_005_ServeToRespondentOptions")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=manageOrders28")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOrderToServeRespondentOptions.json"))
        .check(substring("otherPartyInTheCaseRevised")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Order Submit
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_290_OrderSubmit") {
      exec(http("XUI_PRL_C100Progress_290_005_OrderSubmit")
        .post(BaseURL + "/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLOrderSubmit.json"))
        .check(substring("JUDICIAL_REVIEW")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  val CourtAdminServiceApplication =

    /*======================================================================================
    * Click on 'Service of Application'
    ======================================================================================*/

    group("XUI_PRL_C100Progress_300_ServiceOfApplication") {
      exec(http("XUI_PRL_C100Progress_300_005_ServiceOfApplication")
        .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/serviceOfApplication?ignore-warning=false")
        .headers(Headers.navigationHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[7].value.list_items[0].code")saveAs("serviceOfApplicationScreenCode"))
        .check(jsonPath("$.case_fields[7].value.list_items[0].label")saveAs("serviceOfApplicationScreenLabel"))
        .check(jsonPath("$.case_fields[30].value.list_items[0].code")saveAs("serviceOfApplicationApplicantCode"))
        .check(jsonPath("$.case_fields[30].value.list_items[0].label")saveAs("serviceOfApplicationApplicantName"))
        .check(jsonPath("$.case_fields[30].value.list_items[1].code")saveAs("serviceOfApplicationRespondentCode"))
        .check(jsonPath("$.case_fields[30].value.list_items[1].label")saveAs("serviceOfApplicationRespondentName"))
        .check(jsonPath("$.id").is("serviceOfApplication")))

      .exec(http("XUI_Common_000_UserDetails")
        .get("/api/user/details?refreshRoleAssignments=undefined")
        .headers(Headers.commonHeader)
        .header("Cache-Control", "no-cache") 
        .header("Pragma", "no-cache")
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$.roleAssignmentInfo[0].primaryLocation")saveAs("locationId"))
        .check(status.in(200)))

        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * PD36Q letter Upload
    ======================================================================================*/

    .group("XUI_PRL_C100Progress_310_PD36QUpload") {
      exec(http("XUI_PRL_C100Progress_310_005_PD36QUpload")
        .post(BaseURL + "/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
      .header("x-xsrf-token", "#{XSRFToken}")
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

  .exec(session => {
  for (_ <- 1 to 10) { // Adjust the loop count as needed
    Thread.sleep(500)  // 500ms delay per iteration
    println("Debug: Delaying the script...")
  }
  session
  })

    

  /*======================================================================================
  * Special arrangements letter  Upload
  ======================================================================================*/

    .group("XUI_PRL_C100Progress_320_SpecialArrangementsUpload") {
      exec(http("XUI_PRL_C100Progress_320_SpecialArrangementsUpload")
        .post(BaseURL + "/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "#{XSRFToken}")
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
  * Service of Application document uploads
  ======================================================================================*/

    .group("XUI_PRL_C100Progress_330_DocumentUpload") {
      exec(http("XUI_PRL_C100Progress_330_005_DocumentUpload")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplication2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSoADocuments.json"))
        .check(substring("additionalDocuments")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Service of Application Confirm recipients
  ======================================================================================*/

    .group("XUI_PRL_C100Progress_340_ServiceRecipients") {
      exec(http("XUI_PRL_C100Progress_340_005_ServiceRecipients")
        .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=serviceOfApplication4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSoARecipients.json"))
        .check(substring("otherPartyInTheCaseRevised")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Service of Application Submit
  ======================================================================================*/

    .group("XUI_PRL_C100Progress_350_ServiceSubmit") {
      exec(http("XUI_PRL_C100Progress_350_005_ServiceSubmit")
        .post(BaseURL + "/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLSoASubmit.json"))
        .check(jsonPath("$.data.caseInvites[0].value.accessCode").saveAs("prlAccessCodeApplicant"))
        .check(jsonPath("$.data.caseInvites[1].value.accessCode").saveAs("prlAccessCodeRespondent"))
        .check(jsonPath("$.data.respondentTable[0].id").saveAs("respondentPartyId"))
        .check(jsonPath("$.data.respondents[0].value.solicitorPartyId").saveAs("respondentSolicitorPartyId"))
        .check(jsonPath("$.data.applicantTable[0].id").saveAs("applicantPartyId"))
        .check(jsonPath("$.data.applicants[0].value.solicitorPartyId").saveAs("applicantSolicitorPartyId"))
        .check(jsonPath("$.data.applicants[0].value.solicitorOrgUuid").saveAs("solicitorOrgId")))
    }

val CourtAdminListHearing =

/*======================================================================================
* Click the Hearing Tab
======================================================================================*/

    group("XUI_PRL_C100Progress_370_HearingsTab") {
      exec(http("XUI_PRL_C100Progress_370_005_HearingsTab")
        .get("/api/hearings/getHearings?caseId=#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.is(200)))

      .exec(http("XUI_PRL_C100Progress_370_010_GetHearingsJurisdiction")
        .post("/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"caseReference":"#{caseId}"}"""))
        .check(substring("hearing-facilities")))

      .exec(http("XUI_PRL_C100Progress_370_015_GetHearingTypes")
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

    .group("XUI_PRL_C100Progress_380_RequestHearing") {

      exec(Common.caseActivityPost)
      .exec(Common.isAuthenticated)

      .exec(http("XUI_PRL_C100Progress_380_005_GetCaseFlag")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))

      .exec(http("XUI_PRL_C100Progress_380_010_LocationById")
        .get("/api/prd/location/getLocationById?epimms_id=#{locationId}&serviceCode=null") 
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))
  
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Navigate through hearing screens and submit hearing request
=======================================================================================*/

      .exec(http("XUI_PRL_C100Progress_390_GetCaseFlag")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))


      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_C100Progress_400_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_C100Progress_410_LocationByIdServiceCode")
        .get("/api/prd/location/getLocationById?epimms_id=#{locationId}&serviceCode=ABA5") 
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_C100Progress_420_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=JudgeType&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("JudgeType")))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI_PRL_C100Progress_430_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingPriority")))

    .group("XUI_PRL_C100Progress_440_ListHearing") {
       exec(http("XUI_PRL_C100Progress_440_005_LoadServiceLinkedCases")
        .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .body(StringBody("""{"caseReference":"#{caseId}","hearingId": ""}"""))
        .check(status.is(200)))

       .exec(http("XUI_PRL_C100Progress_440_010_CaseLinkingReasonCode")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingPriority")))
     }

      .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100Progress_450_ListHearing") {
      exec(http("XUI_PRL_C100Progress_390_005_GetCaseFlag")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))


      .exec(http("XUI_PRL_C100Progress_450_010_GetHearingChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .exec(http("XUI_PRL_C100Progress_450_015_GetHearingSubChannel")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingSubChannel")))

      .exec(http("XUI_PRL_C100Progress_450_020_LocationByIdServiceCode")
        .get("/api/prd/location/getLocationById?epimms_id=#{locationId}&serviceCode=ABA5") 
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.in(200, 304)))

    }

      .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Submit hearing request
=======================================================================================*/

      .exec(http("XUI_PRL_C100Progress_460_SubmitHearingRequest")
        .post("/api/hearings/submitHearingRequest")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/prl/c100/PRLC100SubmitHearing.json"))
        .check(jsonPath("$.hearingRequestID").saveAs("hearingRequestId"))
        .check(status.is(201)))

  //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
  //.exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

val CourtAdminHearingsTab = 

    /*======================================================================================
    * Click on the Hearings tab to view any Hearings
    ======================================================================================*/

    group("XUI_PRL_C100Progress_470_HearingsTab") {
      exec(http("XUI_PRL_C100_470_005_HearingsTab")
        .get(BaseURL + "/cases/case-details/#{caseId}/hearings")
        .headers(Headers.commonHeader)
        .check(status.is(200)))

      .exec(Common.configUI)
      .exec(Common.userDetails)
      .exec(Common.isAuthenticated)

      .exec(http("XUI_PRL_C100Progress_470_010_HearingsTabGetCaseData")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.is(200)))

      .exec(Common.monitoringTools)
      .exec(Common.manageLabellingRoleAssignment)
      .exec(Common.waJurisdictions)

      .exec(http("XUI_PRL_C100Progress_470_015_HearingsTabCaseWorkerJurisdictions")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(status.is(200)))

      .exec(Common.activity)

      .exec(http("XUI_PRL_C100Progress_470_020_HearingsTabGetHearings")
        .get("/api/hearings/getHearings?caseId=#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("LISTED"))
        .check(status.is(200)))

      .exec(http("XUI_PRL_C100Progress_470_025_HearingsTabLoadHearingValues")
        .post("/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"caseReference":"#{caseId}"}"""))
        .check(substring("hearing-facilities")))

      .exec(http("XUI_PRL_C100Progress_470_030_GetHearingTypes")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("HearingType")))

      .exec(Common.caseActivityPost)
    }
    .pause(MinThinkTime, MaxThinkTime)
}


/*
//Write applicant access code to file
    .exec { session =>
      val fw = new BufferedWriter(new FileWriter("C100caseNumberAndCodeApplicant.csv", true))
      try {
        fw.write(session("caseId").as[String] + "," + session("prlAccessCodeApplicant").as[String] + "\r\n")
      } finally fw.close()
      session
    }
    //Write respondent access code to file
    .exec { session =>
      val fw = new BufferedWriter(new FileWriter("C100caseNumberAndCodeRespondent.csv", true))
      try {
        fw.write(session("caseId").as[String] + "," + session("prlAccessCodeRespondent").as[String] + "\r\n")
      } finally fw.close()
      session
    }
}*/

// -- ADD HEARINGS NAVIGATION AND ADD HEARING 