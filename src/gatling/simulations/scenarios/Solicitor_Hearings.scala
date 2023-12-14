package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers,CsrfCheck}
import java.io.{BufferedWriter, FileWriter}
import scala.util.Random

/*======================================================================================
* Create a new Bail application as a professional user (e.g. Legal Rep)
======================================================================================*/

object Solicitor_Hearings {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val UserFeederHearingCases = csv("UserDataHearingsCases.csv").circular
  val UserFeederHearingRequestCases = csv("HearingDetailsRequest.csv").circular
  val UserFeederHearingUploadCases = csv("UserDataHearingsUploadCases.csv").circular
  val UserFeederHearingCasesLink = csv("UserDataHearingsCasesLinked.csv").circular
  val UserFeederHearingId = csv("CivilHearingId.csv").circular
  val UserFeederHearingIdCancels = csv("HearingIdCancels.csv").circular
  val UserFeederHearingIdAmend = csv("CivilHearingIdAmend.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90

  val ViewAllHearings =


  feed(UserFeederHearingCases)

    /*======================================================================================
    * Select the Case you want to view
    ======================================================================================*/

    .group("XUI_GetAllHearings_030_ViewAllHearings") {

      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_GetAllHearings_030_005_ViewAllHearings")
        .get("/api/hearings/getHearings?caseId=${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
        .check(jsonPath("$.caseRef").is("${caseId}"))
        .check(substring("caseHearings")))
    }
    .pause(MinThinkTime, MaxThinkTime)


  val SendToWithFTA =

  /*======================================================================================
  * Select SendtoWithFTA
  ======================================================================================*/


    group("XUI_UploadResponse_031_SelectSendtoWithFTA") {

      feed(UserFeederHearingUploadCases)
        //should I change this so it does the whole case search first with the upload case?

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2FadminSendToWithDwp"))

        .exec(Common.profile)

        .exec(http("XUI_Common_000_SSCS")
          .get("/workallocation2/case/tasks/${caseId}/event/adminSendToWithDwp/caseType/Benefit/jurisdiction/SSCS")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .header("sec-fetch-site", "same-origin")
          .check(status.in(200, 304, 403)))


        .exec(http("XUI_UploadResponse_031_005_SelectSendtoWithFTA")
          .get("/data/internal/cases/${caseId}/event-triggers/adminSendToWithDwp?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(jsonPath("$.id").is("adminSendToWithDwp")))
     //     .check(substring("access_granted").optional.saveAs("STANDARD")))

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    }


      .group("XUI_FTA_032_SubmitResponseFTA") {

        exec(http("XUI_UploadResponse_032_005_SubmitResponseFTA")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
          .body(ElFileBody("bodies/hearings/FTAHearingSubmit.json"))
          .check(substring("responseReceived").optional.saveAs("responseReceived")))

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%23Summary"))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      }
      .pause(MinThinkTime, MaxThinkTime)

  val UploadResponse =

  /*======================================================================================
* Select SendtoWithFTA
======================================================================================*/

/*
    group("XUI_FTA_031_SelectSendtoWithFTA") {

      feed(UserFeederHearingUploadCases)
        //should I change this so it does the whole case search first with the upload case?

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2FadminSendToWithDwp"))

        .exec(Common.profile)

        .exec(http("XUI_Common_000_SSCS")
          .get("/workallocation2/case/tasks/${caseId}/event/adminSendToWithDwp/caseType/Benefit/jurisdiction/SSCS")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .header("sec-fetch-site", "same-origin")
          .check(status.in(200, 304, 403)))


        .exec(http("XUI_FTA_031_005_SelectSendtoWithFTA")
          .get("/data/internal/cases/${caseId}/event-triggers/adminSendToWithDwp?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(jsonPath("$.id").is("adminSendToWithDwp")))
        //     .check(substring("access_granted").optional.saveAs("STANDARD")))

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    }


      .group("XUI_FTA_032_SubmitResponseFTA") {

        exec(http("XUI_FTA_032_005_SubmitResponseFTA")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
          .body(ElFileBody("bodies/hearings/FTAHearingSubmit.json"))
          .check(substring("responseReceived").optional.saveAs("responseReceived")))

          .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%23Summary"))

          .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      }
      .pause(MinThinkTime, MaxThinkTime)
*/
    /*======================================================================================
    * Select Upload Response
    ======================================================================================*/

    group("XUI_UploadResponse_040_SelectUploadResponse") {

      feed(UserFeederHearingUploadCases)
        //should I change this so it does the whole case search first with the upload case?

      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FdwpUploadResponse"))

      .exec(Common.profile)

      .exec(http("XUI_UploadResponse_040_005_SelectUploadResponse")
        .get("/data/internal/cases/${caseId}/event-triggers/dwpUploadResponse?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[33].value.appealReasons.reasons[0].id").saveAs("appealId1"))
        .check(jsonPath("$.case_fields[33].value.appealReasons.reasons[1].id").saveAs("appealId2"))
        .check(jsonPath("$.case_fields[33].value.appealReasons.reasons[2].id").saveAs("appealId3"))
        .check(jsonPath("$.id").is("dwpUploadResponse"))
        .check(substring("description").optional.saveAs("Upload a response")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FdwpUploadResponse%2FdwpUploadResponse1.0"))


      .doIf("${accessGranted.isUndefined()}") {
          exec { session =>
            val fw = new BufferedWriter(new FileWriter("HearingDetailsErrors.csv", true))
            try {
              fw.write(session("caseId").as[String] + "\r\n")
            } finally fw.close()
            session
          }
        }
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*====================================================================
    * Upload Response Document
    =====================================================================*/

    .group("XUI_UploadResponse_050_UploadDocument") {

      exec(http("XUI_UploadResponse_050_005_UploadResponse")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "null")
        .formParam("jurisdictionId", "null")
        .bodyPart(RawFileBodyPart("files", "120KB.pdf")
          .fileName("120KB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .check(jsonPath("$.documents[0].hashToken").saveAs("DocumentHashFTAResponse"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLFTAResponse")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*====================================================================
    * Upload AT38 Document
    =====================================================================*/

    .group("XUI_UploadResponse_051_AT38Document") {

      exec(http("XUI_UploadResponse_051_005_AT38Document")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "null")
        .formParam("jurisdictionId", "null")
        .bodyPart(RawFileBodyPart("files", "120KB.pdf")
          .fileName("120KB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .check(jsonPath("$.documents[0].hashToken").saveAs("DocumentHashAT38"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLAT38")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*====================================================================
    * Upload Evidence Bundle Document
    =====================================================================*/

    .group("XUI_UploadResponse_052_Evidence") {

      exec(http("XUI_UploadResponse_052_005_Evidence")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "null")
        .formParam("jurisdictionId", "null")
        .bodyPart(RawFileBodyPart("files", "120KB.pdf")
          .fileName("120KB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .check(jsonPath("$.documents[0].hashToken").saveAs("DocumentHashFTA"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLFTAEvidence")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*====================================================================
    * Upload Response Documents Submit
    =====================================================================*/

    .group("XUI_UploadResponse_053_DocumentsSubmit") {

      exec(http("XUI_UploadResponse_053_005_DocumentsSubmit")
        .post("/data/case-types/Benefit/validate?pageId=dwpUploadResponse1.0")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsUploadResponse.json"))
        .check(substring("disabilityLivingAllowance")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FdwpUploadResponse%2Fsubmit"))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Check your answers
    ======================================================================================*/

    .group("XUI_UploadResponse_060_SubmitResponse") {

      exec(http("XUI_UploadResponse_060_005_SubmitResponse")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/hearings/HearingsUploadResponseSubmit.json"))
        .check(substring("responseReceived").optional.saveAs("responseReceived")))

        .doIf("${responseReceived.exists()}") {
          exec { session =>
            val fw = new BufferedWriter(new FileWriter("HearingDetails.csv", true))
            try {
              fw.write(session("caseId").as[String] + "\r\n")
            } finally fw.close()
            session
          }
        }

        .doIf("${responseReceived.isUndefined()}") {
          exec { session =>
            val fw = new BufferedWriter(new FileWriter("HearingDetailsErrors.csv", true))
            try {
              fw.write(session("caseId").as[String] + "\r\n")
            } finally fw.close()
            session
          }
        }

    }
    .pause(MinThinkTime, MaxThinkTime)



  val RequestHearing =

    /*======================================================================================
    * Request a hearing
    ======================================================================================*/


    feed(randomFeeder)
      .doIfOrElse(session => session("hearings-percentage").as[Int] < hearingPercentage) {
        feed(UserFeederHearingRequestCases)

      }{
        feed(UserFeederHearingCasesLink)
      }

    .group("XUI_RequestHearing_070_ClickRequestHearing") {

      exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fhearings%2Frequest%2Fhearing-requirements"))

      .exec(http("XUI_RequestHearing_070_005_ClickRequestHearing")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("FlagDetails")))

      .exec(http("XUI_RequestHearing_070_010_ClickRequestHearing")
        .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=372653")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Hearing Requirements
    ======================================================================================*/

    .group("XUI_RequestHearing_080_Requirements") {

      exec(http("XUI_RequestHearing_080_005_Requirements")
        .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=SSCS")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
        .check(substring("hearing-requirements")))

      .exec(http("XUI_RequestHearing_080_010_Requirements")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
        .check(substring("childFlags")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Do you require any additional facilities?
    ======================================================================================*/

    /*======================================================================================
    What stage is this hearing at? - Substantive
    ======================================================================================*/

    .group("XUI_RequestHearing_100_HearingStage") {

      exec(http("XUI_RequestHearing_100_005_HearingStage")
        .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    How will each participant attend the hearing? - In Person, 1
    ======================================================================================*/

      .group("XUI_RequestHearing_110_ParticipantAttend") {

        exec(http("XUI_RequestHearing_110_005_ParticipantAttend")
          .get("/api/prd/location/getLocationById?epimms_id=372653")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("Open")))

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
Hearing Venue Details
======================================================================================*/



    /*======================================================================================
    Does this hearing need to be in Welsh?
    ======================================================================================*/

  //  .group("XUI_Hearing_120_Welsh") {

    //  exec(http("XUI_Hearing_120_005_Welsh")
      //  .get("/api/prd/lov/getLovRefData?category=JudgeType&service=BBA3&isChildRequired=N")
        //.headers(Headers.commonHeader)
        //.header("accept", "application/json, text/plain, */*")
        //.check(substring("JudgeType")))

    //}
    //.pause(MinThinkTime, MaxThinkTime)
//might not need

    /*======================================================================================
    Do you want a specific judge? - no
    ======================================================================================*/

  //  .group("XUI_Hearing_130_Specific_Judge") {

    //  exec(http("XUI_Hearing_130_005_Specific_Judge")
      //  .get("/api/prd/lov/getLovRefData?category=PanelMemberType&service=BBA3&isChildRequired=Y")
        //.headers(Headers.commonHeader)
        //.header("accept", "application/json, text/plain, */*")
        //.formParam("jurisdictionId", "SSCS")
        //.check(substring("PanelMemberType")))

    //}
    //.pause(MinThinkTime, MaxThinkTime)

      //might not need
    /*======================================================================================
    Do you require a panel for this hearing? - no
    ======================================================================================*/

 //   .group("XUI_Hearing_140_Require_Panel") {

  //    exec(http("XUI_Hearing_140_005_Require_Panel")
//        .get("/api/prd/lov/getLovRefData?category=HearingPriority&service=BBA3&isChildRequired=N")
 //       .headers(Headers.commonHeader)
 //       .header("accept", "application/json, text/plain, */*")
 //       .check(substring("HearingPriority")))

 //   }
 //   .pause(MinThinkTime, MaxThinkTime)

      //might not need
    /*======================================================================================
    Select length, date and priority level of hearing - 45 mins,
    ======================================================================================*/

    .doIfOrElse(session => session("hearings-percentage").as[Int] < hearingPercentage) {
      group("XUI_RequestHearing_150_Unlinked") {

        exec(http("XUI_RequestHearing_150_005_Unlinked")
          .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .formParam("jurisdictionId", "SSCS")
          .body(ElFileBody("bodies/hearings/HearingsLength.json"))
          .check(status.is(200)))

      }
      .pause(MinThinkTime, MaxThinkTime)

    }{
      group("XUI_RequestHearing_155_Linked") {

        exec(http("XUI_RequestHearing_155_005_Linked")
          .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .formParam("jurisdictionId", "SSCS")
          .body(ElFileBody("bodies/hearings/HearingsLength.json"))
          .check(status.is(200)))

      }
      .pause(MinThinkTime, MaxThinkTime)
    }


    /*======================================================================================
    Enter any additional instructions for the hearing
    ======================================================================================*/

    .group("XUI_RequestHearing_160_AdditionalInstructions") {

      exec(http("XUI_RequestHearing_160_005_AdditionalInstructions")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("CATGRY")))

      .exec(http("XUI_RequestHearing_160_010_AdditionalInstructions")
        .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .exec(http("XUI_RequestHearing_160_015_AdditionalInstructions")
        .get("/api/prd/location/getLocationById?epimms_id=372653")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    Check your answers
    ======================================================================================*/

    .group("XUI_RequestHearing_170_SubmitRequest") {
      doIfOrElse(session => session("hearings-percentage").as[Int] < hearingPercentage) {
        exec(http("XUI_RequestHearing_170_005_SubmitRequest")
          .post("/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/HearingsRequestSubmit.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest")))

      }{
        exec(http("XUI_RequestHearing_170_005_SubmitRequest")
          .post("/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/HearingsRequestSubmitLinked.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest")))
      }


      .exec { session =>
        val fw = new BufferedWriter(new FileWriter("CivilHearingId.csv", true))
        try {
          fw.write(session("caseId").as[String] + "," + session("hearingRequest").as[String] + "\r\n")
        } finally fw.close()
        session
      }

    }
    .pause(MinThinkTime, MaxThinkTime)

  
  val GetHearing =

    /*======================================================================================
    * Get a singular case
    ======================================================================================*/

    feed(UserFeederHearingId)

    .group("XUI_GetHearing_180_GetHearing") {

      exec(http("XUI_GetHearing_180_005_GetHearing")
        .get("/api/hearings/getHearing?hearingId=${hearingRequest}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
        .check(substring("otherReasonableAdjustmentDetails")))

      .exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fhearings%2Frequest%2Fhearing-view-edit-summary"))

    }
    .pause(MinThinkTime, MaxThinkTime)

  val UpdateHearing =

    /*======================================================================================
    * Get a singular case
    ======================================================================================*/

    feed(UserFeederHearingIdAmend)

      .group("XUI_GetHearing_180_GetHearing") {

        exec(http("XUI_GetHearing_180_005_GetHearing")
          .get("/api/hearings/getHearing?hearingId=${hearingRequest}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
          .check(substring("otherReasonableAdjustmentDetails")))

          .exec(Common.isAuthenticated)

          .exec(Common.healthcheck("%2Fhearings%2Frequest%2Fhearing-view-edit-summary"))

      }
      .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Change 'How many people will attend the hearing in person?'
    ======================================================================================*/

    .group("XUI_UpdateHearing_190_Update") {

      exec(http("XUI_UpdateHearing_190_005_UpdateHearing")
        .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Change 'How many people will attend the hearing in person?' to 2 and submit
    ======================================================================================*/

    .group("XUI_UpdateHearing_200_SubmitUpdate") {
      exec(http("XUI_UpdateHearing_200_005_SubmitUpdate")
        .put("/api/hearings/updateHearingRequest?hearingId=${hearingRequest}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/AmendHearingSubmit.json"))
        .check(substring("UPDATE_REQUESTED")))

    }
    .pause(MinThinkTime, MaxThinkTime)


  val DeleteHearing =

    /*======================================================================================
    * Click on 'Cancel'
    ======================================================================================*/

    group("XUI_DeleteHearing_210_DeleteHearing") {

      feed(UserFeederHearingIdCancels)

      .exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fhearings%2Fcancel%2F${hearingRequest}"))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Click on 'Withdrawn' and then submmit
    ======================================================================================*/

    .group("XUI_DeleteHearing_220_DeleteSubmit") {

      exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${hearingRequest}%2Fhearings"))

      .exec(http("XUI_DeleteHearing_220_005_SubmitDelete")
        .delete("/api/hearings/cancelHearings?hearingId=${hearingRequest}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
        .formParam("jurisdictionId", "SSCS")
        .body(ElFileBody("bodies/hearings/HearingsCancel.json"))
        .check(substring("CANCELLATION_REQUESTED")))

    }
    .pause(MinThinkTime, MaxThinkTime)
}
