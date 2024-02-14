package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.Login.baseDomain
import utils.{Common, Environment, Headers}

import java.io.{BufferedWriter, FileWriter}
import scala.util.Random

object PRL_Hearings {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val UserFeederPRLHearingCases = csv("UserDataPRLHearingsCases.csv").circular
  val FeederPRLHearingCases = csv("PRLHearingsCases.csv").circular
  val UserFeederPRLHearingRequestCases = csv("CivilHearingDetailsRequest.csv").circular
  val UserFeederHearingUploadCases = csv("UserDataHearingsUploadCases.csv").circular
  val UserFeederHearingCasesLink = csv("UserDataHearingsCasesLinked.csv").circular
  val UserFeederPRLHearingId = csv("PRLHearingId.csv").circular
  val UserFeederPRLHearingIdCancels = csv("PrlHearingIdCancels.csv").circular
  val UserFeederPRLHearingIdAmend = csv("PrlHearingIdAmend.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90

  val ViewAllHearings =


    feed(UserFeederPRLHearingCases)

      /*======================================================================================
    * Select the Case you want to view all hearings
    ======================================================================================*/

      .group("PRL_GetAllHearings_030_ViewAllHearings") {
          exec(http("PRL_GetAllHearings_030_005_ViewAllHearings")
            .get("/api/hearings/getHearings?caseId=#{caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
            .check(jsonPath("$.caseRef").is("#{caseId}"))
            .check(substring("caseHearings")))
      }
      .pause(MinThinkTime, MaxThinkTime)


  val RequestHearing =

  /*======================================================================================
    * Request a hearing - PRL
    ======================================================================================*/

    feed(FeederPRLHearingCases)
    .group("PRL_RequestHearing_040_ClickRequestHearing") {
      exec(_.setAll(
        "PRLRandomString" -> (Common.randomString(7))))
      //  "caseId" -> "1704898151394750"))
        .exec(Common.isAuthenticated)

        .exec(http("PRL_RequestHearing_040_005_ClickRequestHearing")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=caseType&serviceId=ABA5&isChildRequired=Y")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("PRIVATE LAW")))


        .exec(http("PRL_RequestHearing_040_010_ClickRequestHearing")
          .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("flags")))

        .exec(http("PRL_RequestHearing_040_015_ClickRequestHearing")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=29656")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("court_name")))

    }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Hearing Requirements - Continue
    ======================================================================================*/

      .group("PRL_RequestHearing_050_Requirements") {

        exec(http("PRL_RequestHearing_050_005_Requirements")
          .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=PRIVATELAW")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/prl/HearingsRequirements.json"))
          .check(substring("hearingLocations")))

          .exec(http("PRL_RequestHearing_050_010_Requirements")
            .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            //  .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
            .check(substring("childFlags")))

          .exec(http("PRL_RequestHearing_050_015_Requirements")
            .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=Facilities&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            //  .body(ElFileBody("bodies/hearings/HearingsRequirements.json"))
            .check(substring("category_key")))

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    Do you require any additional facilities?No
    ======================================================================================*/

      .group("PRL_RequestHearing_060_AdditionalFacilities") {

        exec(http("PRL_RequestHearing_060_005_AdditionalFacilities")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingType")))

          .exec(http("PRL_RequestHearing_060_010_AdditionalFacilities")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    What stage is this hearing at? - Substantive
    ======================================================================================*/

      .group("PRL_RequestHearing_70_HearingStage") {

        exec(http("PRL_RequestHearing_70_005_HearingStage")
          .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))

          .exec(http("PRL_RequestHearing_70_010_HearingStage")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    How will each participant attend the hearing? - In Person, 1
    ======================================================================================*/

      .group("PRL_RequestHearing_80_ParticipantAttend") {

        exec(http("PRL_RequestHearing_80_005_ParticipantAttend")
          .get("/api/prd/location/getLocationById?epimms_id=29656")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("site_name")))


          .exec(http("PRL_RequestHearing_80_010_ParticipantAttend")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
Hearing Venue Details
======================================================================================*/
      .group("PRL_RequestHearing_90_HearingVenueDetails") {

        exec(http("PRL_RequestHearing_90_005_HearingVenueDetails")
          .get("/api/prd/lov/getLovRefData?categoryId=JudgeType&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("category_key")))


          .exec(http("PRL_RequestHearing_90_010_HearingVenueDetails")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)



      /*======================================================================================
    Do you want a specific judge? - no
    ======================================================================================*/
      .group("PRL_RequestHearing_100_SpecificJudge") {

        exec(http("PRL_RequestHearing_100_005_SpecificJudge")
          .get("/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("category_key")))


          .exec(http("PRL_RequestHearing_100_010_SpecificJudge")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)



      //might not need
      /*======================================================================================
    Select length, date and priority level of hearing - 45 mins,
    ======================================================================================*/


      .group("PRL_RequestHearing_110_HearingTime") {

        exec(http("PRL_RequestHearing_110_005_HearingTime")
          .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=PRIVATELAW")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/prl/PrlHearingsLength.json")))


          .exec(http("PRL_RequestHearing_110_010_HearingTime")
            .get("/refdata/commondata/lov/categories/CaseLinkingReasonCode")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("CaseLinkingReasonCode")))


          .exec(http("PRL_RequestHearing_110_015_HearingTime")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))
        // .check(substring("hearingRelevant")))


      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
    Enter any additional instructions for the hearing
    ======================================================================================*/

      .group("PRL_RequestHearing_120_AdditionalInstructions") {

        exec(http("PRL_RequestHearing_120_005_AdditionalInstructions")
          .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("hearingRelevant")))

          .exec(http("PRL_RequestHearing_120_010_AdditionalInstructions")
            .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingChannel")))

          .exec(http("PRL_RequestHearing_120_015_AdditionalInstructions")
            .get("/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingSubChannel")))

          .exec(http("PRL_RequestHearing_120_020_AdditionalInstructions")
            .get("/api/prd/location/getLocationById?epimms_id=29656")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))

          .exec(http("PRL_RequestHearing_120_025_AdditionalInstructions")
            .get("/api/prd/location/getLocationById?epimms_id=29656")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))


          .exec(http("PRL_RequestHearing_120_030_AdditionalInstructions")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
    Check your answers
    ======================================================================================*/

      .group("PRL_RequestHearing_130_SubmitRequest") {

        exec(http("PRL_RequestHearing_130_005_SubmitRequest")
          .post("/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/prl/PrlHearingsRequestSubmit.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest"))
        )


          .exec { session =>
            val fw = new BufferedWriter(new FileWriter("PRLHearingData.csv", true))
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

    feed(UserFeederPRLHearingId)

      .group("PRL_GetHearing_140_GetHearing") {

        exec(_.setAll(
          "PRLRandomString" -> (Common.randomString(7))))

          .exec(http("PRL_GetHearing_140_005_GetHearing")
            .get("/api/hearings/getHearing?hearingId=#{prlHearingRequestId}")
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
   
    feed(UserFeederPRLHearingIdAmend)

      .group("PRL_UpdateHearing_150_GetHearing") {

        exec(http("PRL_UpdateHearing_150_005_GetHearing")
          .get("/api/hearings/getHearing?hearingId=#{prlUpdateHearingRequestId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
          .check(substring("otherReasonableAdjustmentDetails")))

          .exec(Common.isAuthenticated)

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
    * Change 'How many people will attend the hearing in person?'
    ======================================================================================*/

      .group("PRL_UpdateHearing_160_Update") {

        exec(http("PRL_UpdateHearing_160_005_UpdateHearing")
          .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))

          .exec(http("PRL_UpdateHearing_160_010_UpdateHearing")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
 Change 'How many people will attend the hearing in person?'
======================================================================================*/

      .group("PRL_UpdateHearing_170_UpdateChange") {

        exec(http("PRL_UpdateHearing_170_005_UpdateChange")
          .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=ABA5")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("hearingRelevant")))

          .exec(http("PRL_UpdateHearing_170_010_UpdateChange")
            .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingChannel")))

          .exec(http("PRL_UpdateHearing_170_015_UpdateChange")
            .get("/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=ABA5&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingSubChannel")))

          .exec(http("PRL_UpdateHearing_170_020_UpdateChange")
            .get("/api/prd/location/getLocationById?epimms_id=29656")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))

          .exec(http("PRL_UpdateHearing_170_025_UpdateChange")
            .get("/api/prd/location/getLocationById?epimms_id=29656")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))


          .exec(http("PRL_UpdateHearing_170_030_UpdateChange")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Submit Update Continue
    ======================================================================================*/

      .group("PRL_UpdateHearing_180_SubmitUpdateContinue") {
        exec(http("PRL_UpdateHearing_180_005_SubmitUpdateContinue")
          .get("/api/prd/lov/getLovRefData?categoryId=ChangeReasons&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("ChangeReasons")))

          .exec(http("PRL_UpdateHearing_180_010_SubmitUpdateContinue")
            .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("roleAssignmentInfo")))

      }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Submit Update
    ======================================================================================*/

      .group("XUI_UpdateHearing_190_SubmitUpdate") {
        exec(_.setAll(
          "PRLRandomString" -> (Common.randomString(7))))
        .exec(http("PRL_UpdateHearing_190_005_SubmitUpdate")
          .put("/api/hearings/updateHearingRequest?hearingId=#{prlUpdateHearingRequestId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/prl/PRLAmendHearingSubmit.json"))
          .check(substring("UPDATE_REQUESTED")))

      }
      .pause(MinThinkTime, MaxThinkTime)


  val CancelHearing =

  /*======================================================================================
    * Click on 'Cancel'
    ======================================================================================*/

    group("PRL_CancelHearing_200_CancelHearing") {

      feed(UserFeederPRLHearingIdCancels)

        .exec(Common.isAuthenticated)

        .exec(http("PRL_CancelHearing_200_005_CancelHearing")
          .get("/api/prd/lov/getLovRefData?categoryId=CaseManagementCancellationReasons&serviceId=ABA5&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
         // .check(substring("HearingChannel"))
        )

        .exec(http("PRL_CancelHearing_200_010_CancelHearing")
          .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("roleAssignmentInfo")))

        .exec(http("PRL_CancelHearing_200_015_CancelHearing")
          .get(BaseURL + "/api/user/details?refreshRoleAssignments=undefined")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("roleAssignmentInfo")))

    }
      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Are you sure you want to cancel this hearing? - Withdrawn
    ======================================================================================*/

      .group("PRL_CancelHearing_210_CancelSubmit") {

        exec(Common.isAuthenticated)


          .exec(http("PRL_CancelHearing_210_005_SubmitCancel")
            .delete("/api/hearings/cancelHearings?hearingId=#{prlCancelHearingRequestId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
           // .formParam("jurisdictionId", "SSCS")
            .body(ElFileBody("bodies/hearings/prl/PrlHearingsCancel.json"))
            .check(substring("CANCELLATION_REQUESTED")))
      }
      .pause(MinThinkTime, MaxThinkTime)
}
