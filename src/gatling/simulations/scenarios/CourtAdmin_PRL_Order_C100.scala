package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.io.{BufferedWriter, FileWriter}

/*===============================================================================================================
* Court Admin C100 case progression. Send to local court --> Send to Gatekeeper --> Add an order --> Serve 
================================================================================================================*/

object CourtAdmin_PRL_Order_C100 {

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

  val CreateDraftOrder =

    group("XUI_PRL_LA_CreateOrder_010_ManageOrder_Task") {
      exec(http("XUI_PRL_LA_CreateOrder_220_005_ManageOrder_Task")
        .get("/workallocation/case/tasks/#{caseId}/event/manageOrders/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("task_required_for_event"))
        .check(status.is(200)))

        .exec(http("XUI_PRL_LA_CreateOrder_010_010_ManageOrder_EventTrigger")
          .get("/data/internal/cases/#{caseId}/event-triggers/manageOrders?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          //.check(jsonPath("$.case_fields[2].value.list_items[0].code").saveAs("transfer_code"))
          //.check(jsonPath("$.case_fields[2].value.list_items[0].label").saveAs("transfer_label"))
          //.check(jsonPath("$.case_fields[0].value[0].id").saveAs("id"))
          .check(substring("Manage orders"))
          .check(status.is(200)))
    }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_020_ManageOrder_Validate1") {
        exec(http("XUI_PRL_LA_CreateOrder_020_005_ManageOrder_Validate")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders1")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders1_validate.json"))
          //.check(jsonPath("$.data.localAuthorityQuarantineDocsList[0].value.localAuthorityQuarantineDocument.document_filename").saveAs("transfer_filename"))
          //.check(jsonPath("$.data.docToBeReviewed").saveAs("docToBeReviewed"))
          //.check(jsonPath("$.data.docLabel").saveAs("docLabel"))
          .check(substring("customOrderDoc"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_030_ManageOrder_Validate2") {
        exec(http("XUI_PRL_LA_CreateOrder_030_005_ManageOrder_Validate2")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders2")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders2_validate.json"))
          .check(jsonPath("$.data.ordersHearingDetails[0].id").saveAs("idOrder"))
          .check(jsonPath("$.data.ordersHearingDetails[0].value.applicantName1").saveAs("applicantName1"))
          .check(jsonPath("$.data.ordersHearingDetails[0].value.applicantName2").saveAs("applicantName2"))
          .check(jsonPath("$.data.ordersHearingDetails[0].value.applicantSolicitor1").saveAs("applicantSolicitor1"))
          .check(jsonPath("$.data.ordersHearingDetails[0].value.respondentName1").saveAs("respondentName1"))
          .check(jsonPath("$.data.ordersHearingDetails[0].value.respondentSolicitor1").saveAs("respondentSolicitor1"))
          .check(jsonPath("$.data.ordersHearingDetails[0].value.applicantSolicitor2").saveAs("applicantSolicitor2"))
          .check(substring("otherPartyInTheCaseRevised"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_040_ManageOrder_Validate3") {
        exec(http("XUI_PRL_LA_CreateOrder_040_005_ManageOrder_Validate3")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders5")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders5_validate.json"))
          .check(substring("responseToAllegationsOfHarmYesOrNoResponse"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_050_ManageOrder_Validate4") {
        exec(http("XUI_PRL_LA_CreateOrder_050_005_ManageOrder_Validate4")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders10")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders10_validate.json"))
          .check(substring("otherPartyInTheCaseRevised"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_060_ManageOrder_Validate5") {
        exec(http("XUI_PRL_LA_CreateOrder_060_005_ManageOrder_Validate5")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders19")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders19_validate.json"))
          .check(jsonPath("$.data.previewOrderDoc.document_hash").saveAs("document_hash_order"))
          .check(jsonPath("$.data.previewOrderDoc.document_url").saveAs("document_url_orderdoc"))
          .check(jsonPath("$.data.ordersHearingDetails[0].id").saveAs("id_orderdoc"))
          .check(substring("isAtAddressLessThan5Years"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_070_ManageOrder_Validate6") {
        exec(http("XUI_PRL_LA_CreateOrder_070_005_ManageOrder_Validate6")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders20")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders20_validate.json"))
          .check(substring("willingToAttendMiam"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_080_ManageOrder_Validate7") {
        exec(http("XUI_PRL_LA_CreateOrder_080_005_ManageOrder_Validate7")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders24")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders24_validate.json"))
          .check(substring("amendOrderSelectCheckOptions"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_090_ManageOrder_Validate8") {
        exec(http("XUI_PRL_LA_CreateOrder_090_005_ManageOrder_Validate8")
          .post("/data/case-types/PRLAPPS/validate?pageId=manageOrders26")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders26_validate.json"))
          .check(substring("isAtAddressLessThan5YearsWithDontKnow"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_LA_CreateOrder_100_ManageOrder_Event") {
        exec(http("XUI_PRL_LA_CreateOrder_100_005_ManageOrder_Event")
          .post("/data/cases/#{caseId}/events")
          .headers(Headers.commonHeader)
          .header("Content-Type", "application/json; charset=utf-8")
          .header("Accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/prl/c100/manageorders_manageorders_event.json"))
          .check(substring("caApplicantBarrister2ExternalFlags"))
          .check(status.is(201)))
      }

  .exec(session => {
    session("taskType").asOption[String] match {
      case Some(taskType) => session
      case None => session.set("taskType", "")
    }
  })

    // Loop until the task type matches "checkApplicationC100" *For Cases which selected HWF different steps are required here
    .asLongAs(session => session("taskType").as[String] != "requestCirUpdate") {
      exec(http("XUI_PRL_LA_CreateOrder_100_WaitForEvent")
        .get(BaseURL + "/workallocation/case/task/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$[0].id").optional.saveAs("taskId"))
        .check(jsonPath("$[-1].type").optional.saveAs("taskType")))

        .pause(10) // Wait between retries
    }

  val writeToFile =
    exec { session =>
      val fw = new BufferedWriter(new FileWriter("LAData.csv", true))
      try {
        fw.write(session("orgID").as[String] + "," + session("LAUser").as[String] + "," + session("LAPassword").as[String] + "," + session("LAFirm").as[String] + "," + session("LAFirstName").as[String] + "," + session("LALastName").as[String] + "," + session("LARole").as[String] + "," + session("caseId").as[String] + "," + session("adminUser").as[String] + "," + session("adminPassword").as[String] + "\r\n")
      } finally fw.close()
      session
    }

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