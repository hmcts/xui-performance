package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*======================================================================================
* Common Component Notes (CC Notes) - PCS Add Case Note
======================================================================================*/

object CC_Notes {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*====================================================================================
  *Search PCS cases (SEARCH view) by Case ID
  *=====================================================================================*/

  val SearchCase =

    group("XUI_CCNotes_030_SearchCase") {
      exec(http("XUI_CCNotes_030_005_SearchCase")
        .post("/data/internal/searchCases?ctid=#{caseType}&use_case=SEARCH&view=SEARCH&page=1&case_reference=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(status.is(200))
        .check(substring("\"results\""))
        .check(jsonPath("$.results[*].case_id").find.is("#{caseId}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Open case by Case ID from feeder
  *=====================================================================================*/

  val OpenCase =

    group("XUI_CCNotes_040_OpenCase") {
      exec(http("XUI_CCNotes_040_005_OpenCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("experimental", "true")
        .check(status.is(200))
        .check(jsonPath("$.case_id").is("#{caseId}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Get user profile
  *=====================================================================================*/

  val GetProfile =

    group("XUI_CCNotes_050_GetProfile") {
      exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Start Add Case Note event
  *=====================================================================================*/

  val StartAddCaseNote =

    group("XUI_CCNotes_060_StartAddCaseNote") {
      exec(http("XUI_CCNotes_060_005_StartAddCaseNote")
        .get("/data/internal/cases/#{caseId}/event-triggers/addCaseNote?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("experimental", "true")
        .check(status.is(200))
        .check(substring("Add a case note"))
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Validate Add Case Note
  *=====================================================================================*/

  val ValidateAddCaseNote =

    exec(_.set("noteText", "Testing-" + Common.randomString(5) + "-" + System.currentTimeMillis()))

    .group("XUI_CCNotes_070_ValidateAddCaseNote") {
      exec(http("XUI_CCNotes_070_005_ValidateAddCaseNote")
        .post("/data/case-types/PCS/validate?pageId=addCaseNoteaddCaseNote")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/AddCaseNoteValidate.json"))
        .check(status.is(200))
        .check(substring("caseTitleMarkdown")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Submit Add Case Note event
  *=====================================================================================*/

  val SubmitAddCaseNote =

    group("XUI_CCNotes_080_SubmitAddCaseNote") {
      exec(http("XUI_CCNotes_080_005_SubmitAddCaseNote")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/AddCaseNoteSubmit.json"))
        .check(status.is(201))
        .check(substring("\"jurisdiction\":\"PCS\"")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Verify submitted case note text on case
  *=====================================================================================*/

  val VerifyCaseNote =

    group("XUI_CCNotes_090_VerifyCaseNote") {
      exec(http("XUI_CCNotes_090_005_VerifyCaseNote")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("experimental", "true")
        .check(status.is(200))
        .check(substring("#{noteText}")))
    }

    .pause(220)

}
