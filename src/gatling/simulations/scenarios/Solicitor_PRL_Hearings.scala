package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.io.{BufferedWriter, FileWriter}
import scala.util.Random

/*======================================================================================
* Create a new Bail application as a professional user (e.g. Legal Rep)
======================================================================================*/

object Solicitor_PRL_Hearings {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val UserFeederHearingCases = csv("caseReady.csv").circular
  val UserFeederHearingId = csv("hearingRequests.csv").circular
  val UserFeederHearingIdCancel = csv("hearingRequestsDelete.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90

  val ViewAllHearings =


  feed(UserFeederHearingCases)

    /*======================================================================================
    * Select the Case you want to view
    ======================================================================================*/

    .group("XUI_GetAllHearings_030_ViewAllHearings") {

      exec(_.setAll(
        "PRLRandomString" -> ("App" + Common.randomString(7)),
        "PRLHearingDay" -> Common.getDay(),
        "PRLHearingMonth" -> Common.getMonth(),
        "PRLHearingYear" -> Common.getDobYear()))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

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
    * Request a hearing
    ======================================================================================*/


    feed(UserFeederHearingCases)

  .exec(_.setAll(
    "PRLRandomString" -> ("App" + Common.randomString(7)),
    "PRLHearingDay" -> Common.getDay(),
    "PRLHearingMonth" -> Common.getMonth(),
    "PRLHearingYear" -> Common.getDobYear()))
  /*    .doIfOrElse(session => session("hearings-percentage").as[Int] < hearingPercentage) {
        feed(UserFeederHearingRequestCases)

      }{
        feed(UserFeederHearingCasesLink)
      }

   */

    .group("XUI_RequestHearingPRL_040_ClickRequestHearing") {

      exec(Common.isAuthenticated)


      .exec(http("XUI_RequestHearingPRL_040_005_ClickRequestHearing")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("FlagDetails")))

      .exec(http("XUI_RequestHearingPRL_040_010_ClickRequestHearing")
        .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=36791")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Hearing Requirements
    ======================================================================================*/

    .group("XUI_RequestHearingPRL_050_Requirements") {

      exec(http("XUI_RequestHearingPRL_050_005_Requirements")
        .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/hearings/prl/HearingsRequirements.json"))
        .check(jsonPath("$.parties[0].partyID").saveAs("partyIdApp"))
        .check(jsonPath("$.parties[1].partyID").saveAs("partyIdRes")))

      .exec(http("XUI_RequestHearingPRL_050_010_Requirements")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("childFlags")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Do you require any additional facilities?
    ======================================================================================*/

     // .group("XUI_RequestHearingPRL_060_AdditionalFacilities") {

       // exec(http("XUI_RequestHearingPRL_060_005_AdditionalFacilities")
         // .post(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=ABA5&isChildRequired=N")
         // .headers(Headers.commonHeader)
        //  .header("accept", "application/json, text/plain, */*")
        //  .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
         // .check(substring("HearingType")))

    /*======================================================================================
    What stage is this hearing at? - Breach
    ======================================================================================*/

    .group("XUI_RequestHearingPRL_070_HearingStage") {

      exec(http("XUI_RequestHearingPRL_070_005_HearingStage")
        .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    How will each participant attend the hearing? - In Person, 1
    ======================================================================================*/

      .group("XUI_RequestHearingPRL_080_ParticipantAttend") {

        exec(http("XUI_RequestHearing_110_005_ParticipantAttend")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=36791")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("Open")))

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
Hearing Venue Details
======================================================================================*/
          .group("XUI_RequestHearingPRL_090_HearingVenue") {

            exec(http("XUI_RequestHearingPRL_090_005_HearingVenue")
              .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=JudgeType&serviceId=ABA5&isChildRequired=N")
              .headers(Headers.commonHeader)
              .header("accept", "application/json, text/plain, */*")
              .check(substring("JudgeType")))

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

    .group("XUI_HearingPRL_100_Specific_Judge") {

      exec(http("XUI_Hearing_100_005_Specific_Judge")
        .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingPriority")))

    }
    .pause(MinThinkTime, MaxThinkTime)

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

      .group("XUI_RequestHearingPRL_110_SelectLength") {

        exec(http("XUI_RequestHearingPRL_110_005_SelectLength")
          .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=PRIVATELAW")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .formParam("jurisdictionId", "PRIVATELAW")
          .body(ElFileBody("bodies/hearings/prl/HearingsLength.json"))
          .check(status.is(200)))

      }
      .pause(MinThinkTime, MaxThinkTime)



      /*======================================================================================
Will this hearing need to be linked to other hearings?
======================================================================================*/

  //   .group("XUI_RequestHearingPRL_120_LinkedHearings") {

    //    exec(http("XUI_RequestHearingPRL_120_005_LinkedHearings")
      //    .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=PRIVATELAW")
       //   .headers(Headers.commonHeader)
       //   .header("accept", "application/json, text/plain, */*")
       //   .formParam("jurisdictionId", "SSCS")
        //  .body(ElFileBody("bodies/hearings/HearingsLength.json"))
       //   .check(status.is(200)))

     // }
     // .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    Enter any additional instructions for the hearing
    ======================================================================================*/

    .group("XUI_RequestHearingPRL_130_AdditionalInstructions") {

      exec(http("XUI_RequestHearingPRL_130_005_AdditionalInstructions")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("CATGRY")))

      .exec(http("XUI_RequestHearingPRL_130_010_AdditionalInstructions")
        .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

      .exec(http("XUI_RequestHearing_160_015_AdditionalInstructions")
        .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=36791")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("court_address")))

      .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    Check your answers
    ======================================================================================*/

    .group("XUI_RequestHearing_170_SubmitRequest") {

        exec(http("XUI_RequestHearing_170_005_SubmitRequest")
          .post(BaseURL + "/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/prl/HearingsRequestSubmit.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest")))

          .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

      .exec { session =>
        val fw = new BufferedWriter(new FileWriter("hearingRequestsNew.csv", true))
        try {
          fw.write(session("hearingRequest").as[String] +","+ fw.write(session("caseId").as[String] + "\r\n"))
        } finally fw.close()
        session
      }

  val GetHearing =

  /*======================================================================================
  * Get a singular case
  ======================================================================================*/

    feed(UserFeederHearingId)

      .group("XUI_GetHearingPRL_180_GetHearing") {

        exec(http("XUI_GetHearing_180_005_GetHearing")
          .get(BaseURL + "/api/hearings/getHearing?hearingId=${hearingRequest}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(jsonPath("$.partyDetails[0].partyID").saveAs("partyId"))
          .check(jsonPath("$.partyDetails[1].partyID").saveAs("partyIdApp"))
          .check(jsonPath("$.partyDetails[2].partyID").saveAs("partyIdResp"))
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber")))

          .exec(Common.isAuthenticated)

          .exec(http("XUI_GetHearing_180_010_GetHearing")
            .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("FlagDetails")))


          .exec(http("XUI_GetHearing_180_015_GetHearing")
            .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingChannel")))


          .exec(http("XUI_GetHearing_180_020_GetHearing")
            .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=20262")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))

      }
      .pause(MinThinkTime, MaxThinkTime)


  val UpdateHearing =

  /*======================================================================================
  * Get a singular case
  ======================================================================================*/

    feed(UserFeederHearingId)

      .group("XUI_GetHearingPRL_180_GetHearing") {

        exec(http("XUI_GetHearing_180_005_GetHearing")
          .get(BaseURL + "/api/hearings/getHearing?hearingId=${hearingRequest}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(jsonPath("$.partyDetails[0].partyID").saveAs("partyId"))
          .check(jsonPath("$.partyDetails[1].partyID").saveAs("partyIdApp"))
          .check(jsonPath("$.partyDetails[2].partyID").saveAs("partyIdResp"))
          .check(jsonPath("$.requestDetails.timestamp").saveAs("timestamp"))
          .check(jsonPath("$.partyDetails[0].individualDetails.firstName").saveAs("partyFirstName"))
          .check(jsonPath("$.partyDetails[0].individualDetails.lastName").saveAs("partyLastName"))
          .check(jsonPath("$.partyDetails[0].individualDetails.hearingChannelEmail[0]").saveAs("partyEmail"))
          .check(jsonPath("$.partyDetails[1].individualDetails.firstName").saveAs("appFirstName"))
          .check(jsonPath("$.partyDetails[1].individualDetails.lastName").saveAs("appLastName"))
          .check(jsonPath("$.partyDetails[1].individualDetails.hearingChannelEmail[0]").saveAs("appEmail"))
          .check(jsonPath("$.partyDetails[2].individualDetails.firstName").saveAs("respFirstName"))
          .check(jsonPath("$.partyDetails[2].individualDetails.lastName").saveAs("respLastName"))
          .check(jsonPath("$.partyDetails[2].individualDetails.hearingChannelEmail[0]").saveAs("respEmail"))
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber")))

          .exec(Common.isAuthenticated)

          .exec(http("XUI_GetHearing_180_010_GetHearing")
            .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("FlagDetails")))


          .exec(http("XUI_GetHearing_180_015_GetHearing")
            .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingChannel")))


          .exec(http("XUI_GetHearing_180_020_GetHearing")
            .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=20262")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Change 'Does the hearing need to take place on a specific date?'
      ======================================================================================*/



      /*======================================================================================
      * Change 'Does the hearing need to take place on a specific date?' to No
      ======================================================================================*/

      .group("XUI_UpdateHearingPRL_200_SubmitUpdate") {
        exec(http("XUI_UpdateHearingPRL_200_005_SubmitUpdate")
          .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("FlagDetails")))


          .exec(http("XUI_UpdateHearingPRL_200_015_SubmitUpdate")
            .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=20262")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))


        .exec(http("XUI_UpdateHearingPRL_200_010_SubmitUpdate")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))


          .exec(http("XUI_UpdateHearingPRL_200_020_SubmitUpdate")
            .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingSubChannel")))


          .exec(Common.userDetails)


      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Submit Updated Request
    ======================================================================================*/


          .exec(Common.userDetails)



      /*======================================================================================
* Provide a reason for changing this hearing - Judge Requested Change
======================================================================================*/

      .group("XUI_UpdateHearingPRL_220_ReasonForChange") {
        exec(http("XUI_UpdateHearingPRL_220_005_ReasonForChange")
          .put(BaseURL + "/api/hearings/updateHearingRequest?hearingId=${hearingRequest}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/prl/HearingsUpdateSubmit.json"))
          .check(substring("timeStamp")))


          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)



  val CancelHearing =

  /*======================================================================================
  * Click on 'Cancel'
  ======================================================================================*/

    group("XUI_CancelHearingPRL_230_CancelHearing") {

      feed(UserFeederHearingIdCancel)

        .exec(Common.isAuthenticated)

        .exec(http("XUI_CancelHearingPRL_230_005_CancelHearing")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=CaseManagementCancellationReasons&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
          .check(substring("CaseManagementCancellationReasons")))

        .exec(Common.healthcheck("%2Fhearings%2Fcancel%2F${hearingRequest}"))

    }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
      * Are you sure you want to cancel this hearing? - Withdrawn
      ======================================================================================*/

      .group("XUI_DeleteHearingPRL_240_WhyCancelled") {

        exec(http("XUI_DeleteHearingPRL_240_005_WhyCancelled")
            .delete(BaseURL + "/api/hearings/cancelHearings?hearingId=${hearingRequest}")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
            .formParam("hearingId", "${hearingRequest}")
            .body(ElFileBody("bodies/hearings/prl/HearingsCancel.json"))
            .check(substring("CANCELLATION_REQUESTED")))

        .exec(Common.isAuthenticated)

          .exec(http("XUI_DeleteHearingPRL_240_010_WhyCancelled")
            .get(BaseURL + "/api/hearings/getHearings?caseId=${caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("caseHearings")))


          .exec(http("XUI_DeleteHearingPRL_240_015_WhyCancelled")
            .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .body(ElFileBody("bodies/hearings/prl/HearingsWhyCancelled.json"))
            .check(substring("hearingChannelEmail")))


          .exec(http("XUI_DeleteHearingPRL_240_020_WhyCancelled")
            .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingType")))

          .exec(Common.userDetails)

      }
      .pause(MinThinkTime, MaxThinkTime)

}
