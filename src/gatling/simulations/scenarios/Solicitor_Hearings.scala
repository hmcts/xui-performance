package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}
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
  val UserFeederHearingCasesLink = csv("UserDataHearingsCasesLinked.csv").circular
  val UserFeederHearingId = csv("HearingId.csv").circular
  val UserFeederHearingIdCancels = csv("HearingIdCancels.csv").circular
  val UserFeederHearingIdAmend = csv("HearingIdAmend.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90

  val ViewAllHearings =


  feed(randomFeeder)
  .doIfOrElse(session => session("hearings-percentage").as[Int] < hearingPercentage) {
    feed(UserFeederHearingCases)

  }{
    feed(UserFeederHearingCasesLink)
  }

    /*======================================================================================
    * Select the Case you want to view
    ======================================================================================*/

    .group("XUI_Hearing_030_SelectCase") {

      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_Hearing_030_005_SelectCase")
        .get("/api/hearings/getHearings?caseId=${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
        .check(jsonPath("$.caseRef").is("${caseId}"))
        .check(substring("caseHearings")))
    }
    .pause(MinThinkTime, MaxThinkTime)


  val UploadResponse =

    /*======================================================================================
    * Select Upload Response
    ======================================================================================*/

    group("XUI_Hearing_040_SelectUploadResponse") {

      feed(UserFeederHearingCases)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FdwpUploadResponse"))

      .exec(Common.profile)

      .exec(http("XUI_Hearing_040_005_SelectUploadResponse")
        .get("/data/internal/cases/${caseId}/event-triggers/dwpUploadResponse?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[33].value.appealReasons.reasons[0].id").saveAs("appealId"))
        .check(jsonPath("$.id").is("dwpUploadResponse"))
        .check(substring("access_granted").optional.saveAs("accessGranted")))

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

    .group("XUI_Hearing_050_Upload_Response_Document") {

      exec(http("XUI_Hearing_050_005_Upload_Response_Document")
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

    .group("XUI_Hearing_051_AT38_Document") {

      exec(http("XUI_Hearing_051_005_AT38_Document")
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

    .group("XUI_Hearing_052_Evidence_Bundle_Document") {

      exec(http("XUI_Hearing_052_005_Evidence_Bundle_Document")
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

    .group("XUI_Hearing_053_Upload_Response_Documents_Submit") {

      exec(http("XUI_Hearing_053_005_Upload_Response_Documents_Submit")
        .post("/data/case-types/Benefit/validate?pageId=dwpUploadResponse1.0")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsUploadResponse.json"))
        .check(substring("personalIndependencePayment")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FdwpUploadResponse%2Fsubmit"))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Check your answers
    ======================================================================================*/

    .group("XUI_Hearing_060_UploadResponseSubmit") {

      exec(http("XUI_Hearing_060_005_UploadResponseSubmit")
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

    group("XUI_Hearing_070_ClickRequestHearing") {

      exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fhearings%2Frequest%2Fhearing-requirements"))

      .exec(http("XUI_Hearing_070_005_ClickRequestHearing")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("FlagDetails")))

      .exec(http("XUI_Hearing_070_010_ClickRequestHearing")
        .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=372653")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Hearing Requirements
    ======================================================================================*/

    .group("XUI_Hearing_080_Hearing_Requirements") {

      exec(http("XUI_Hearing_080_005_Hearing_Requirements")
        .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=SSCS")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
        .check(substring("hearing-requirements")))

      .exec(http("XUI_Hearing_080_010_Hearing_Requirements")
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

    .group("XUI_Hearing_100_Hearing_Stage") {

      exec(http("XUI_Hearing_100_005_Hearing_Stage")
        .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    How will each participant attend the hearing? - In Person, 1
    ======================================================================================*/

      .group("XUI_Hearing_110_How_Each_Participant") {

        exec(http("XUI_Hearing_110_005_How_Each_Participant")
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
      group("XUI_Hearing_150_Length_Date") {

        exec(http("XUI_Hearing_150_005_Length_Date")
          .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .formParam("jurisdictionId", "SSCS")
          .body(ElFileBody("bodies/hearings/HearingsLength.json"))
          .check(status.is(200)))

      }
      .pause(MinThinkTime, MaxThinkTime)

    }{
      group("XUI_Hearing_155_Length_Link") {

        exec(http("XUI_Hearing_155_005_Length_Link")
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

    .group("XUI_Hearing_160_Additional_Instructions") {

      exec(http("XUI_Hearing_160_005_Additional_Instructions")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("CATGRY")))

      .exec(http("XUI_Hearing_160_010_Additional_Instructions")
        .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .exec(http("XUI_Hearing_160_015_Additional_Instructions")
        .get("/api/prd/location/getLocationById?epimms_id=372653")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    Check your answers
    ======================================================================================*/

    .group("XUI_Hearing_170_Request_Hearing_Submit") {

      exec(http("XUI_Hearing_170_005_Request_Hearing_Submit")
        .post("/api/hearings/submitHearingRequest")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsRequestSubmit.json"))
        .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest")))

      .exec { session =>
        val fw = new BufferedWriter(new FileWriter("HearingId.csv", true))
        try {
          fw.write(session("hearingRequest").as[String] + "\r\n")
        } finally fw.close()
        session
      }

    }
    .pause(MinThinkTime, MaxThinkTime)

  
  val ViewId =

    /*======================================================================================
    * Get a singular case
    ======================================================================================*/

    feed(UserFeederHearingId)

    .group("XUI_Hearing_180_Get_Case") {

      exec(http("XUI_Hearing_180_005_Get_Case")
        .get("/api/hearings/getHearing?hearingId=${hearingRequest}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
        .check(substring("otherReasonableAdjustmentDetails")))

      .exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fhearings%2Frequest%2Fhearing-view-edit-summary"))

    }
    .pause(MinThinkTime, MaxThinkTime)

  val AmendHearing =

    /*======================================================================================
    * Get a singular case
    ======================================================================================*/

    feed(UserFeederHearingIdAmend)

      .group("XUI_Hearing_180_Get_Case") {

        exec(http("XUI_Hearing_180_005_Get_Case")
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

    .group("XUI_Hearing_190_Amend_Hearing") {

      exec(http("XUI_Hearing_190_005_Amend_Hearing")
        .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Change 'How many people will attend the hearing in person?' to 2 and submit
    ======================================================================================*/

    .group("XUI_Hearing_200_Amend_Hearing_Submit") {
      exec(http("XUI_Hearing_200_005_Amend_Hearing_Submit")
        .put("/api/hearings/updateHearingRequest?hearingId=${hearingRequest}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/AmendHearingSubmit.json"))
        .check(substring("UPDATE_REQUESTED")))

    }
    .pause(MinThinkTime, MaxThinkTime)



  val LinkCase =

    /*======================================================================================
    * Get a singular case
    ======================================================================================*/

    group("XUI_Hearing_180_Get_Case") {
      exec(http("XUI_Hearing_150_005_Length_Date")
        .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .formParam("jurisdictionId", "SSCS")
        .body(ElFileBody("bodies/hearings/HearingsLength.json")))

    }
    .pause(MinThinkTime, MaxThinkTime)


  val CancelHearing =

    /*======================================================================================
    * Click on 'Cancel'
    ======================================================================================*/

    group("XUI_Hearing_210_Cancel_Hearing") {

      feed(UserFeederHearingIdCancels)

      .exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fhearings%2Fcancel%2F${hearingRequest}"))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Click on 'Withdrawn' and then submmit
    ======================================================================================*/

    .group("XUI_Hearing_220_Cancel_Hearing_Submit") {

      exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${hearingRequest}%2Fhearings"))

      .exec(http("XUI_Hearing_220_005_Cancel_Hearing_Submit")
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