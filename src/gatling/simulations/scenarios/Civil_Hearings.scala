package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.io.{BufferedWriter, FileWriter}
import scala.util.Random

/*======================================================================================
* Manage Civil Hearings
======================================================================================*/

object Civil_Hearings {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val UserFeederCivilHearingCases = csv("UserDataCivilHearingsCases.csv").circular
  val UserFeederCivilHearingRequestCases = csv("CivilHearingDetailsRequest.csv").circular
  val UserFeederHearingUploadCases = csv("UserDataHearingsUploadCases.csv").circular
  val UserFeederHearingCasesLink = csv("UserDataHearingsCasesLinked.csv").circular
  val UserFeederCivilHearingId = csv("CivilHearingId.csv").circular
  val UserFeederCivilHearingIdCancels = csv("CivilHearingIdCancels.csv").circular
  val UserFeederCivilHearingIdAmend = csv("CivilHearingIdAmend.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90

  val ViewAllHearings =


 // feed(UserFeederCivilHearingCases)

    /*======================================================================================
    * Select the Case you want to view all hearings
    ======================================================================================*/

    group("XUI_GetAllHearings_030_ViewAllHearings") {
  
     // exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      exec(http("XUI_GetAllHearings_030_005_ViewAllHearings")
        .get("/api/hearings/getHearings?caseId=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
       // .header("X-Xsrf-Token", "#{XSRFToken}")
        .check(jsonPath("$.caseRef").is("${caseId}"))
        .check(substring("caseHearings")))
    }
    .pause(MinThinkTime, MaxThinkTime)



  val RequestHearing =

    /*======================================================================================
    * Request a hearing - Civil
    ======================================================================================*/


     //    feed(UserFeederCivilHearingRequestCases)

    group("Civil_RequestHearing_070_ClickRequestHearing") {

      exec(Common.isAuthenticated)
      
      .exec(http("Civil_RequestHearing_070_005_ClickRequestHearing")
        .get("/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=AAA7&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("authority", "manage-case.perftest.platform.hmcts.net")
        .header("path", "/api/prd/lov/getLovRefData?categoryId=caseType&serviceId=AAA7&isChildRequired=Y")
       // .header("X-Xsrf-Token", "#{XSRFToken}")
        .header("accept", "application/json, text/plain, */*")
        
        .check(substring("category_key")))

      .exec(http("Civil_RequestHearing_070_010_ClickRequestHearing")
        .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=739514")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("authority", "manage-case.perftest.platform.hmcts.net")
        .header("path", "/api/prd/location/getLocationById?epimms_id=739514")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Hearing Requirements - Request Hearing -Continue
    ======================================================================================*/

    .group("Civil_RequestHearing_080_Requirements") {

      exec(http("Civil_RequestHearing_080_005_Requirements")
        .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=CIVIL")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/civil/HearingsRequirements.json"))
        .check(substring("hearingLocations")))

      .exec(http("Civil_RequestHearing_080_010_Requirements")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=AAA7")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
      //  .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
        .check(substring("childFlags")))
  
        .exec(http("Civil_RequestHearing_080_010_Requirements")
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
    What stage is this hearing at? - Application Hearings
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
    How will each participant attend the hearing? - Prticipant attendance
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
what are the hearing venue details
======================================================================================*/
           .group("Civil_RequestHearing_110_HearingVenueDetails") {
    
             exec(http("Civil_RequestHearing_110_005_HearingVenueDetails")
               .get("/api/prd/lov/getLovRefData?categoryId=JudgeType&serviceId=AAA7&isChildRequired=N")
               .headers(Headers.commonHeader)
               .header("accept", "application/json, text/plain, */*")
               .check(substring("category_key")))
    
           }
           .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Does this hearing need to be in Welsh?
    ======================================================================================*/

  

    /*======================================================================================
    Do you want a specific judge? - no
    ======================================================================================*/

 

      //might not need
    /*======================================================================================
    Do you require a panel for this hearing? - no
    ======================================================================================*/



      //might not need
    /*======================================================================================
    Select length, date and priority level of hearing - 45 mins,
    ======================================================================================*/

  
      group("Civil_RequestHearing_150_HearingTime") {

        exec(http("Civil_RequestHearing_150_005_HearingTime")
          .get("/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=AAA7&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .formParam("jurisdictionId", "CIVIL")
          .check(status.is(200)))

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
          .body(ElFileBody("bodies/hearings/civil/CivilHearingsRequestSubmit.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest"))
        )
      }


      .exec { session =>
        val fw = new BufferedWriter(new FileWriter("CivilHearingId.csv", true))
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

    feed(UserFeederCivilHearingId)

    .group("Civil_GetHearing_180_GetHearing") {

      exec(http("Civil_GetHearing_180_005_GetHearing")
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

    feed(UserFeederCivilHearingIdAmend)

      .group("Civil_GetHearing_180_GetHearing") {

        exec(http("Civil_GetHearing_180_005_GetHearing")
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
        .body(ElFileBody("bodies/hearings/civil/CivilAmendHearingSubmit.json"))
        .check(substring("UPDATE_REQUESTED")))

    }
    .pause(MinThinkTime, MaxThinkTime)


  val CancelHearing =

    /*======================================================================================
    * Click on 'Cancel'
    ======================================================================================*/

    group("Civil_CancelHearing_210_CancelHearing") {

      feed(UserFeederCivilHearingIdCancels)

      .exec(Common.isAuthenticated)
      
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Click on 'Withdrawn' and then submmit
    ======================================================================================*/

    .group("XUI_CancelHearing_220_CancelSubmit") {

      exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${hearingRequest}%2Fhearings"))

      .exec(http("XUI_CancelHearing_220_005_SubmitCancel")
        .delete("/api/hearings/cancelHearings?hearingId=${hearingRequest}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
        .formParam("jurisdictionId", "SSCS")
        .body(ElFileBody("bodies/hearings/civil/CivilHearingsCancel.json"))
        .check(substring("CANCELLATION_REQUESTED")))

    }
    .pause(MinThinkTime, MaxThinkTime)
}
