package scenarios

import utils.{Environment, Headers}

import java.io.{BufferedWriter, FileWriter}

/*======================================================================================
* Create a new Bail application as a professional user (e.g. Legal Rep)
======================================================================================*/

object HearingManagementCivil {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val UserFeederHearingCases = csv("UserDataHearingsCases.csv").circular
  val UserFeederHearingRequestCases = csv("HearingDetailsRequest.csv").circular
  val UserFeederHearingUploadCases = csv("UserDataHearingsUploadCases.csv").circular
  val UserFeederHearingCasesLink = csv("UserDataHearingsCasesLinked.csv").circular
  val UserFeederHearingId = csv("HearingId.csv").circular
  val UserFeederHearingIdCancels = csv("HearingIdCancels.csv").circular
  val UserFeederHearingIdAmend = csv("HearingIdAmend.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90

  val ViewAllHearings =


  feed(UserFeederHearingCases)

    /*======================================================================================
    * Select the Case you want to view all hearings
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



  val RequestHearing =

    /*======================================================================================
    * Request a hearing for Civil
    ======================================================================================*/


    feed(UserFeederHearingRequestCases)
    .group("Civil_RequestHearing_040_ClickRequestHearing") {
      exec(Common.isAuthenticated)
      
      .exec(http("Civil_RequestHearing_040_005_ClickRequestHearing")
        .get(BaseURL + "api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=AAA7&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("Application Hearings")))

      .exec(http("Civil_RequestHearing_040_010_ClickRequestHearing")
        .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=739514")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Hearing Requirements - Continue
    ======================================================================================*/

    .group("Civil_RequestHearing_050_Requirements") {

      exec(http("Civil_RequestHearing_050_005_Requirements")
        .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=CIVIL")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
        .check(substring("hearingLocations")))

      .exec(http("Civil_RequestHearing_050_010_Requirements")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=AAA7")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
      //  .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
        .check(substring("childFlags")))
  
        .exec(http("Civil_RequestHearing_050_010_Requirements")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=Facilities&serviceId=AAA7&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          //  .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
          .check(substring("category_key")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Do you require any additional facilities?No
    ======================================================================================*/

    /*======================================================================================
    What stage is this hearing at? - Substantive
    ======================================================================================*/

    .group("Civil_RequestHearing_100_HearingStage") {

      exec(http("Civil_RequestHearing_100_005_HearingStage")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=AAA7&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    How will each participant attend the hearing? - In Person, 1
    ======================================================================================*/

      .group("Civil_RequestHearing_110_ParticipantAttend") {

        exec(http("Civil_RequestHearing_110_005_ParticipantAttend")
          .get("/api/prd/location/getLocationById?epimms_id=739514")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("site_name")))

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
Hearing Venue Details
======================================================================================*/
           .group("Civil_RequestHearing_110_ParticipantAttend") {
    
             exec(http("Civil_RequestHearing_110_005_ParticipantAttend")
               .get("/api/prd/lov/getLovRefData?categoryId=JudgeType&serviceId=AAA7&isChildRequired=N")
               .headers(Headers.commonHeader)
               .header("accept", "application/json, text/plain, */*")
               .check(substring("category_key")))
    
           }
           .pause(MinThinkTime, MaxThinkTime)


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
      //  .get("/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=AAA7&isChildRequired=N")
        //.headers(Headers.commonHeader)
        //.header("accept", "application/json, text/plain, */*")
        //.formParam("jurisdictionId", "SSCS")
        //.check(substring("category_key")))

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
      group("Civil_RequestHearing_150_Unlinked") {

        exec(http("Civil_RequestHearing_150_005_Unlinked")
          .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .formParam("jurisdictionId", "SSCS")
          .body(ElFileBody("bodies/hearings/HearingsLength.json"))
          .check(status.is(200)))

      }
      .pause(MinThinkTime, MaxThinkTime)

    }{
      group("Civil_RequestHearing_155_Linked") {

        exec(http("Civil_RequestHearing_155_005_Linked")
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

    .group("Civil_RequestHearing_160_AdditionalInstructions") {

      exec(http("Civil_RequestHearing_160_005_AdditionalInstructions")
        .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=AAA7")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("hearingRelevant")))

      .exec(http("Civil_RequestHearing_160_010_AdditionalInstructions")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=AAA7&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .exec(http("Civil_RequestHearing_160_015_AdditionalInstructions")
        .get("/api/prd/location/getLocationById?epimms_id=739514")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    Check your answers
    ======================================================================================*/

    .group("Civil_RequestHearing_170_SubmitRequest") {
      
        exec(http("Civil_RequestHearing_170_005_SubmitRequest")
          .post("/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/HearingsRequestSubmit.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest"))
        )
      .exec(http("Civil_RequestHearing_170_005_SubmitRequest")
          .post("/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/HearingsRequestSubmitLinked.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest")))
      }


      .exec { session =>
        val fw = new BufferedWriter(new FileWriter("HearingId.csv", true))
        try {
          fw.write(session("caseId").as[String] + "," + session("hearingRequest").as[String] + "\r\n")
        } finally fw.close()
        session
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
