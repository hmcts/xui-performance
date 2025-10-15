package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*===============================================================================================================
* Court Admin C100 case progression. Send to local court --> Send to Gatekeeper --> Add an order --> Serve 
================================================================================================================*/

object Barrister_PRL_C100 {

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

  val AddDocumentC100Applicant =

    /*======================================================================================
    * Run Manage Document task
    ======================================================================================*/

    group("XUI_PRL_C100progress_200_010_ApplicantAddDocument") {
      exec(http("XUI_PRL_C100progress_200_010_ApplicantManageDocsPrivateLaw")
        .get("/workallocation/case/tasks/#{caseId}/event/manageDocumentsNew/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(substring("task_required_for_event"))
        .check(status.is(200)))

    }

    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100progress_200_020_ApplicantManageDocs_EventTrigger") {
      exec(http("XUI_PRL_C100progress_200_020_ApplicantManageDocs_EventTrigger")
        .get("/data/internal/cases/#{caseId}/event-triggers/manageDocumentsNew?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[0].value[0].id").saveAs("id_value"))
        .check(substring("Confirm the document is related to this case"))
        .check(status.is(200)))
    }

    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_PRL_C100progress_200_030_ApplicantManageDocTask_Upload") {
      exec(http("XUI_PRL_C100progress_200_030_ApplicantManageDocTask_Upload")
          .post("/documents")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("content-type", "multipart/form-data")
          .bodyPart(RawFileBodyPart("files", "BarristerDocument.pdf")
            .fileName("BarristerDocument.pdf")
            .transferEncoding("binary"))
          .asMultipartForm
          .formParam("classification", "PUBLIC")
          .formParam("caseTypeId", "PRLAPPS")
          .formParam("jurisdictionId", "PRIVATELAW")
          .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("document_url"))
          .check(jsonPath("$._embedded.documents[0]._links.binary.href").saveAs("document_binary_url"))
          .check(substring("originalDocumentName"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_C100progress_200_040_ApplicantManageDocTask_Validate") {
        exec(http("XUI_PRL_C100progress_200_040_ApplicantManageDocTask_Validate")
            .post("/data/case-types/PRLAPPS/validate?pageId=manageDocumentsNew1")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/c100/PRLAddDocument.json"))
            .check(substring("isCurrentAddressKnown"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_C100progress_200_050_ApplicantManageDocTask_Event") {
        exec(http("XUI_PRL_C100progress_200_050_ApplicantManageDocTask_Event")
            .post("/data/cases/#{caseId}/events")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/c100/PRLAddDocumentEvent.json"))
            .check(substring("newAllegationsOfHarmDomesticAbuseYesNo"))
            .check(status.is(201)))
      }

      .pause(MinThinkTime, MaxThinkTime)

  val AddDocumentC100Respondent =

    /*======================================================================================
    * Run Manage Document task
    ======================================================================================*/

      group("XUI_PRL_C100progress_200_010_RespondentManageDocsPrivateLaw") {
        exec(http("XUI_PRL_C100progress_200_010_RespondentManageDocsPrivateLaw")
          .get("/workallocation/case/tasks/#{caseId}/event/manageDocumentsNew/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .check(substring("task_required_for_event"))
          .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_C100progress_200_020_RespondentManageDocs_EventTrigger") {
        exec(http("XUI_PRL_C100progress_200_020_RespondentManageDocs_EventTrigger")
            .get("/data/internal/cases/#{caseId}/event-triggers/manageDocumentsNew?ignore-warning=false")
            .headers(Headers.commonHeader)
            .header("Accept", "application/json, text/plain, */*")
            .check(jsonPath("$.event_token").saveAs("event_token"))
            .check(jsonPath("$.case_fields[0].value[0].id").saveAs("id_value"))
            .check(substring("Confirm the document is related to this case"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_C100progress_200_030_RespondentManageDocTask_Upload") {
        exec(http("XUI_PRL_C100progress_200_030_RespondentManageDocTask_Upload")
            .post("/documents")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("content-type", "multipart/form-data")
            .bodyPart(RawFileBodyPart("files", "BarristerDocument.pdf")
              .fileName("BarristerDocument.pdf")
              .transferEncoding("binary"))
            .asMultipartForm
            .formParam("classification", "PUBLIC")
            .formParam("caseTypeId", "PRLAPPS")
            .formParam("jurisdictionId", "PRIVATELAW")
            .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("document_url"))
            .check(jsonPath("$._embedded.documents[0]._links.binary.href").saveAs("document_binary_url"))
            .check(substring("originalDocumentName"))
            .check(status.is(200)))
      }

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_C100progress_200_040_RespondentManageDocTask_Validate") {
        exec(http("XUI_PRL_C100progress_200_040_RespondentManageDocTask_Validate")
            .post("/data/case-types/PRLAPPS/validate?pageId=manageDocumentsNew1")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/c100/PRLAddDocumentRespondent.json"))
            .check(substring("isCurrentAddressKnown"))
            .check(status.is(200)))
      }
      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_PRL_C100progress_200_050_RespondentManageDocTask_Event") {
        exec(http("XUI_PRL_C100progress_200_050_RespondentManageDocTask_Event")
            .post("/data/cases/#{caseId}/events")
            .headers(Headers.commonHeader)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/prl/c100/PRLAddDocumentEventRespondent.json"))
            .check(substring("newAllegationsOfHarmDomesticAbuseYesNo"))
            .check(status.is(201)))
      }

      .pause(MinThinkTime, MaxThinkTime)

}