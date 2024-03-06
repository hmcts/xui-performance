package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Solicitor_BailsHearings {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val RequestHearing =

    exec(_.setAll(
      "BailsRandomString" -> (Common.randomString(7)),
      "BailsRandomNumber" -> (Common.randomNumber(7)),
      "BailsDobDay" -> Common.getDay(),
      "BailsDobMonth" -> Common.getMonth(),
      "BailsDobYear" -> Common.getDobYear(),
      "BailsArrivedYear" -> Common.getDobYearChild(),
      "BailsPhoneNumber" -> ("07" + Common.randomNumber(9)),
      "BailsSupporterEmail" -> (Common.randomString(7) + "@gmail.com")))


  /*======================================================================================
  * Select Case
  ======================================================================================*/

  group("XUI_Bails_030_SelectCase") {
    exec(http("XUI_Bails_030_005_SelectCase")
      .get("/data/internal/cases/#{caseId}")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .check(jsonPath("$.state.name").is("Application started")))

  }
    .pause(MinThinkTime, MaxThinkTime)

    /*=============================================================================================
    * Request A Hearing
    ===============================================================================================*/

    .group("XUI_Bails_040_RequestHearing") {

      exec(Common.isAuthenticated)

        .exec(http("XUI_Bails_040_005_RequestHearing")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=caseType&serviceId=BFA1&isChildRequired=Y")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("caseType")))


        .exec(http("XUI_Bails_040_010_RequestHearing")
          .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BFA1")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("flags")))


        .exec(http("XUI_Bails_040_015_RequestHearing")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          //  .check(substring("flags"))
        )

        .exec(Common.userDetails)
        .exec(Common.userDetails)
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Hearing Requirements Continue
    ======================================================================================*/

    .group("XUI_Bails_060_HearingRequirementsContinue") {
      exec(http("XUI_Bails_060_005_HearingRequirementsContinue")
        .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=IA")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/bailHearings/HearingRequirementsContinue.json"))
        .check(substring("hearingChannels")))


        .exec(http("XUI_Bails_060_010_HearingRequirementsContinue")
          .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BFA1")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("flags")))


        .exec(http("XUI_Bails_060_015_HearingRequirementsContinue")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=Facilities&serviceId=BFA1&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          //  .check(substring("flags"))
        )

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Do you require any additional facilities? - No
    ======================================================================================*/

    .group("XUI_Bails_070_AdditionalFacilities") {
      exec(http("XUI_Bails_070_005_AdditionalFacilities")
        .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=BFA1&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingType")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * What stage is this hearing at?
    ======================================================================================*/

    .group("XUI_Bails_080_WhatStage") {
      exec(http("XUI_Bails_080_005_WhatStage")
        .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=BFA1&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Participant attendance
    ======================================================================================*/

    .group("XUI_Bails_090_ParticipantAttendance") {
      exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * What are the hearing venue details? - Birmingham
    ======================================================================================*/

    .group("XUI_Bails_100_HearingVenueDetails") {
      exec(http("XUI_Bails_100_005_HearingVenueDetails")
        .get(BaseURL + "/api/prd/location/getLocations?serviceIds=BFA1&locationType=hearing&searchTerm=birm")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("Birmingham")))

        .exec(http("XUI_Bails_100_010_HearingVenueDetails")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=JudgeType&serviceId=BFA1&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("JudgeType")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Do you want a specific judge? -No
    ======================================================================================*/

    .group("XUI_Bails_110_SpecificJudge") {
      exec(http("XUI_Bails_110_005_SpecificJudge")
        .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingPriority&serviceId=BFA1&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingPriority")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Length, date and priority level of hearing
    ======================================================================================*/

    .group("XUI_Bails_120_LengthOfHearing") {
      exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Enter any additional instructions for the hearing
    ======================================================================================*/

    .group("XUI_Bails_130_AdditionalInstructions") {
      exec(http("XUI_Bails_130_005_AdditionalInstructions")
        .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BFA1")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("flags")))

        .exec(http("XUI_Bails_130_010_AdditionalInstructions")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=BFA1&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))

        .exec(http("XUI_Bails_130_015_AdditionalInstructions")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=BFA1&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))

        .exec(http("XUI_Bails_130_020_AdditionalInstructions")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=231596")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("Birmingham")))

        .exec(http("XUI_Bails_130_025_AdditionalInstructions")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=231596")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("Birmingham")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
    * Check your answers before sending your request
    ======================================================================================*/

    .group("XUI_Bails_140_CheckAnswersHearing") {
      exec(http("XUI_Bails_140_005_CheckAnswersHearing")
        .post(BaseURL + "/api/hearings/submitHearingRequest")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/bailHearings/CheckAnswersHearing.json"))
        .check(substring("HEARING_REQUESTED")))

    }
    .pause(MinThinkTime, MaxThinkTime)


  val ViewHearing =

  /*======================================================================================
    * View Hearing
    ======================================================================================*/

    group("XUI_Bails_150_ViewHearing") {
      exec(http("XUI_Bails_150_005_ViewHearing")
        .get("/api/hearings/getHearing?hearingId=#{hearingId}}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("caseDetails")))

        .exec(Common.isAuthenticated)

        .exec(http("XUI_Bails_150_010_ViewHearing")
          .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BFA1")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("flags")))

        .exec(http("XUI_Bails_150_015_ViewHearing")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=BFA1&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))

        .exec(http("XUI_Bails_150_020_ViewHearing")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=BFA1&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))

        .exec(http("XUI_Bails_150_025_ViewHearing")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=231596")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("Birmingham")))

        .exec(http("XUI_Bails_150_030_ViewHearing")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=231596")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("Birmingham")))

        .exec(Common.userDetails)

        .exec(Common.userDetails)

    }

      .pause(MinThinkTime, MaxThinkTime)


  val AmendHearing =

  /*======================================================================================
  * Select 'How many people will attend the hearing in person?'
  ======================================================================================*/

    group("XUI_Bails_160_HowManyPeopleAmend") {
      exec(http("XUI_Bails_160_005_HowManyPeopleAmend")
        .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=BFA1&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("HearingChannel")))


        .exec(Common.userDetails)

    }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Change the Details
======================================================================================*/

      .group("XUI_Bails_170_ParticipantAttendanceAmend") {
        exec(http("XUI_Bails_170_005_ParticipantAttendanceAmend")
          .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BFA1")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("flags")))

          .exec(http("XUI_Bails_170_010_ParticipantAttendanceAmend")
            .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=BFA1&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingChannel")))


          .exec(http("XUI_Bails_170_015_ParticipantAttendanceAmend")
            .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=HearingSubChannel&serviceId=BFA1&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingSubChannel")))


          .exec(http("XUI_Bails_170_020_ParticipantAttendanceAmend")
            .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=231596")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("Birmingham")))


          .exec(http("XUI_Bails_170_025_ParticipantAttendanceAmend")
            .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=231596")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("Birmingham")))


          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Submit Updates
======================================================================================*/

      .group("XUI_Bails_180_SubmitUpdates") {
        exec(http("XUI_Bails_180_005_SubmitUpdates")
          .get(BaseURL + "/api/prd/lov/getLovRefData?categoryId=ChangeReasons&serviceId=BFA1&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("ChangeReasons")))

          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Provide a reason for changing this hearing - Application for postponement granted
======================================================================================*/

      .group("XUI_Bails_190_ReasonForChanging") {
        exec(http("XUI_Bails_190_005_ReasonForChanging")
          .post(BaseURL + "/api/hearings/updateHearingRequest?hearingId=#{hearingId}}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/bailHearings/ReasonForChanging.json"))
          .check(substring("HEARING_REQUESTED")))

          .exec(Common.userDetails)

      }

      .pause(MinThinkTime, MaxThinkTime)




}
