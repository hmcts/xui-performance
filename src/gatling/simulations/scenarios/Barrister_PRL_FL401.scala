package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.Barrister_PRL_C100.{MaxThinkTime, MinThinkTime}
import utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new Private Law FL401 application as a professional user (e.g. solicitor)
======================================================================================*/

object Barrister_PRL_FL401 {

  val BaseURL = Environment.baseURL
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CreatePrivateLawCase =

    //set session variables
    exec(_.setAll(
      "ApplicantFirstName" -> ("App" + Common.randomString(5)),
      "ApplicantLastName" -> ("Test" + Common.randomString(5)),
      "RespondentFirstName" -> ("Resp" + Common.randomString(5)),
      "RespondentLastName" -> ("Test" + Common.randomString(5)),
      "AppDobDay" -> Common.getDay(),
      "AppDobMonth" -> Common.getMonth(),
      "AppDobYear" -> Common.getDobYear(),
      "RespDobDay" -> Common.getDay(),
      "RespDobMonth" -> Common.getMonth(),
      "RespDobYear" -> Common.getDobYear()))


  val DraftAnOrderFL401 =

    /*======================================================================================
    * Run Draft Order task
    ======================================================================================*/

      group("XUI_PRL_FL401progress_200_010_DraftAnOrderPrivateLaw") {
        exec(http("XUI_PRL_FL401progress_200_010_DraftAnOrderPrivateLaw")
          .get("/workallocation/case/tasks/#{caseId}/event/draftAnOrder/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .check(substring("task_required_for_event"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_FL401progress_200_020_DraftAnOrder_EventTrigger") {
        exec(http("XUI_PRL_FL401progress_200_020_DraftAnOrder_EventTrigger")
            .get("/data/internal/cases/#{caseId}/event-triggers/draftAnOrder?ignore-warning=false")
            .headers(Headers.commonHeader)
            .header("Accept", "application/json, text/plain, */*")
            .check(jsonPath("$.event_token").saveAs("event_token"))
            //.check(jsonPath("$.case_fields[0].value.partyList.list_items[0].code").saveAs("appCode1"))
            //.check(jsonPath("$.case_fields[0].value.partyList.list_items[0].label").saveAs("appLabel1"))
            .check(substring("sdoHearingUrgentCheckList"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Select Draft Order option
        ======================================================================================*/
      .group("XUI_PRL_FL401progress_200_040_DraftAnOrderOptions") {
        exec(http("XUI_PRL_FL401progress_200_040_DraftAnOrderOptions")
            .post("/data/case-types/PRLAPPS/validate?pageId=draftAnOrder1")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/fl401/PRLDraftAnOrderOptions.json"))
            .check(substring("c7ResponseSubmitted"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Select type of Order
        ======================================================================================*/
      .group("XUI_PRL_FL401progress_200_050_DraftAnOrderType") {
        exec(http("XUI_PRL_FL401progress_200_050_DraftAnOrderType")
            .post("/data/case-types/PRLAPPS/validate?pageId=draftAnOrder2")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/fl401/PRLDraftAnOrderType.json"))
            .check(substring("typesOfApplication"))
            .check(status.is(200)))
        }
        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Enter order details
        ======================================================================================*/
        .group("XUI_PRL_FL401progress_200_060_DraftAnOrderDetails") {
            exec(http("XUI_PRL_FL401progress_200_060_DraftAnOrderDetails")
                .post("/data/case-types/PRLAPPS/validate?pageId=draftAnOrder4")
                .headers(Headers.commonHeader)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json, text/plain, */*")
                .body(ElFileBody("bodies/prl/fl401/PRLDraftAnOrderDetails.json"))
                .check(substring("caseNameHmctsInternal"))
                .check(status.is(200)))
        }

        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Enter Hearing outcome
        ======================================================================================*/
        .group("XUI_PRL_FL401progress_200_070_DraftAnOrderDetails") {
          exec(http("XUI_PRL_FL401progress_200_070_DraftAnOrderDetails")
              .post("/data/case-types/PRLAPPS/validate?pageId=draftAnOrder5")
              .headers(Headers.commonHeader)
              .header("Content-Type", "application/json; charset=utf-8")
              .header("Accept", "application/json, text/plain, */*")
              .body(ElFileBody("bodies/prl/fl401/PRLDraftAnOrderHearingOutcome.json"))
              .check(substring("judgeOrMagistratesLastName"))
              .check(status.is(200)))
        }

        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Enter Hearing details
        ======================================================================================*/
        .group("XUI_PRL_FL401progress_200_080_DraftAnOrderDetails") {
          exec(http("XUI_PRL_FL401progress_200_080_DraftAnOrderDetails")
              .post("/data/case-types/PRLAPPS/validate?pageId=draftAnOrder16")
              .headers(Headers.commonHeader)
              .header("Content-Type", "application/json; charset=utf-8")
              .header("Accept", "application/json, text/plain, */*")
              .body(ElFileBody("bodies/prl/fl401/PRLDraftAnOrderHearingDetails.json"))
              .check(jsonPath("$.data.previewOrderDoc.document_hash").saveAs("document_hash"))
              .check(jsonPath("$.data.previewOrderDoc.document_url").saveAs("document_url"))
              .check(jsonPath("$.data.previewOrderDoc.document_filename").saveAs("document_filename"))
              .check(substring("attendToCourt"))
              .check(status.is(200)))
        }

        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Validate order preview is correct
        ======================================================================================*/
        .group("XUI_PRL_FL401progress_200_090_DraftAnOrderPreview") {
            exec(http("XUI_PRL_FL401progress_200_090_DraftAnOrderPreview")
                .post("/data/case-types/PRLAPPS/validate?pageId=draftAnOrder20")
                .headers(Headers.commonHeader)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json, text/plain, */*")
                .body(ElFileBody("bodies/prl/fl401/PRLDraftAnOrderPreview.json"))
                .check(substring("previewOrderDoc"))
                .check(status.is(200)))
        }

        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Run draft an order event
        ======================================================================================*/
        .group("XUI_PRL_FL401progress_200_100_DraftAnOrderEvent") {
          exec(http("XUI_PRL_FL401progress_200_100_DraftAnOrderEvent")
            .post("/data/cases/#{caseId}/events")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/fl401/PRLDraftAnOrderEvent.json"))
            .check(substring("missingAddressWarningText"))
            .check(status.is(201)))
        }

        .pause(MinThinkTime, MaxThinkTime)

}