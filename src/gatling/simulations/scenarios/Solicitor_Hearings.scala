package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}
import java.io.{BufferedWriter, FileWriter}

/*======================================================================================
* Create a new Bail application as a professional user (e.g. Legal Rep)
======================================================================================*/

object Solicitor_Hearings {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val SelectCase =

    /*======================================================================================
    * Select the Case you want to view
    ======================================================================================*/

    group("XUI_Hearing_030_SelectCase") {

      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))


      .exec(http("XUI_Hearing_030_005_SelectCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.case_id").is("${caseId}")))
        //.check(jsonPath("$.metadataFields[1].value").is("${caseId}")))
    }
    .pause(MinThinkTime, MaxThinkTime)


  val UploadResponse =

    /*======================================================================================
    * Select Upload Response
    ======================================================================================*/

    group("XUI_Hearing_040_SelectUploadResponse") {

      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FdwpUploadResponse"))

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
      //  .check((status.is(201)).optional.saveAs("yes"))
        .body(ElFileBody("bodies/hearings/HearingsUploadResponseSubmit.json"))
        .check(substring("responseReceived").optional.saveAs("responseReceived")))
        //       .check(bodyString.saveAs("BODY1"))
        /*       .exec {
          session =>
            println(session("BODY1").as[String])
            session
        }

  */
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


      .exec(http("XUI_Hearing_070_ClickRequestHearing")
        .get("/api/prd/lov/getLovRefData?category=caseType&service=BBA3&isChildRequired=Y")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json"))
      //  .check(jsonPath("$.metadataFields[1].value").is("1655299395545035")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Hearing Requirements
    ======================================================================================*/

    .group("XUI_Hearing_080_Hearing_Requirements") {

      exec(http("XUI_Hearing_080_005_Hearing_Requirements")

    //Need to unhardcode this
   // /api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3
   // /api/prd/location/getLocationById?epimms_id=372653

      .post("/api/hearings/loadServiceHearingValues?jurisdictionId=SSCS")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
      .check(substring("hearing-requirements")))
      //.check(jsonPath("$.screenFlow[0].screenName").is("hearing-requirements")))

      //.get("/api/prd/lov/getLovRefData?category=Facilities&service=BBA3&isChildRequired=N")
      //.headers(Headers.commonHeader)
      //.header("accept", "application/json, text/plain, */*")
      //.body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
      //.check(jsonPath("$.category_key").is("Facilities")))



    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Do you require any additional facilities?
    ======================================================================================*/

  //    .group("XUI_Hearing_090_Additional_Facilities") {

  //      exec(http("XUI_Hearing_090_005_Additional_Facilities")

    //      .get("/api/prd/lov/getLovRefData?category=HearingType&service=BBA3&isChildRequired=N")
      //    .headers(Headers.commonHeader)
       //   .header("accept", "application/json, text/plain, */*")
       //   .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
       //   .check(jsonPath("$.category_key").is("HearingType")))

//      }

  //    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    What stage is this hearing at? - Substantive
    ======================================================================================*/

    .group("XUI_Hearing_100_Hearing_Stage") {

      exec(http("XUI_Hearing_100_005_Hearing_Stage")
        .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
        .check(substring("HearingChannel")))
         // .check(jsonPath("$.[0].category_key").is("HearingChannel")))

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
        //  .check(jsonPath("$.[0].court_status").is("Open")))

      }

      .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    What are the hearing venue details?
    ======================================================================================*/

    /*======================================================================================
    Does this hearing need to be in Welsh?
    ======================================================================================*/

    .group("XUI_Hearing_120_Welsh") {

      exec(http("XUI_Hearing_120_005_Welsh")
        .get("/api/prd/lov/getLovRefData?category=JudgeType&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
       // .check(jsonPath("$.[0].court_status").is("JudgeType")))
        .check(substring("JudgeType")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Do you want a specific judge? - no
    ======================================================================================*/

    .group("XUI_Hearing_130_Specific_Judge") {

      exec(http("XUI_Hearing_130_005_Specific_Judge")
        .get("/api/prd/lov/getLovRefData?category=PanelMemberType&service=BBA3&isChildRequired=Y")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .formParam("jurisdictionId", "SSCS")
        .check(substring("PanelMemberType")))
        //.check(jsonPath("$.[0].category_key").is("PanelMemberType")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Do you require a panel for this hearing? - no
    ======================================================================================*/

    .group("XUI_Hearing_140_Require_Panel") {

      exec(http("XUI_Hearing_140_005_Require_Panel")
        .get("/api/prd/lov/getLovRefData?category=HearingPriority&service=BBA3&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingPriority")))
       // .check(jsonPath("$.[0].category_key").is("HearingPriority")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Select length, date and priority level of hearing - 45 mins,
    ======================================================================================*/

    .group("XUI_Hearing_150_Length_Date") {

      exec(http("XUI_Hearing_150_005_Length_Date")
          .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .formParam("jurisdictionId", "SSCS")
        .body(ElFileBody("bodies/hearings/HearingsLength.json")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Will this hearing need to be linked to other hearings? - no
    ======================================================================================*/

  //    .group("XUI_Hearing_155_Linked_To_Hearings") {

 // exec(http("XUI_Hearing_155_005_Linked_To_Hearings")
  //  .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
  //  .headers(Headers.commonHeader)
  //  .header("accept", "application/json, text/plain, */*")
  //  .body(ElFileBody("bodies/hearings/HearingsLinkToHearingsNo.json")))
 //   .check(substring("HearingChannel")))
  //  .check(jsonPath("$.[0].category_key").is("HearingChannel")))

//}
//.pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Enter any additional instructions for the hearing
    ======================================================================================*/

      .group("XUI_Hearing_160_Additional_Instructions") {

        exec(http("XUI_Hearing_160_005_Additional_Instructions")
          .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))
        //  .check(jsonPath("$.[0].category_key").is("HearingChannel")))

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

      }
      .pause(MinThinkTime, MaxThinkTime)


  val GetCase =

  /*======================================================================================
  * Get a singular case
  ======================================================================================*/

    group("XUI_Hearing_180_Get_Case") {
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
  * Change 'How many people will attend the hearing in person?'
  ======================================================================================*/

    group("XUI_Hearing_190_Amend_Hearing") {
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
}