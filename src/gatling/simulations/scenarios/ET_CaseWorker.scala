package scenarios

import io.gatling.core.Predef.{ElFileBody, csv, exec, jsonPath, substring}
import io.gatling.http.Predef.http
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

object ET_CaseWorker {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime


 // val postcodeFeeder = csv("postcodes.csv").circular

  val MakeAClaim =

    exec(_.setAll(
      "ETRespondRandomString" -> (Common.randomString(7)),
      "caseId" -> ("1674733823532865"),
"CaseAcceptDay" -> (Common.getDay()),
      "CaseAcceptMonth" -> (Common.getCurrentMonth()),
      "CaseAcceptYear" -> (Common.getCurrentYear())
    ))

      /*======================================================================================
    * Find Case
    ======================================================================================*/


      /*  .group("ET_CW_500_FindCase") {
      exec(http("ET_CW_500_005_Find_Case")
        .get(BaseURL + "/data/internal/cases/#{caseId}")
        .headers(NavHeader)
        .check(substring("Submitted")))

        .exec(Common.userDetails)

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

   */

      /*======================================================================================
    * Click on 'ET1 Case Vetting'
    ======================================================================================*/

      .group("ET_CW_510_Case_Vetting") {
        exec(http("ET_CW_510_005_Case_Vetting")
          .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/et1Vetting/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .check(substring("task_required_for_event")))

          .exec(http("ET_CW_510_010_Case_Vetting")
            .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/et1Vetting?ignore-warning=false")
          .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
            .check(jsonPath("$.event_token").saveAs("event_token"))
            .check(jsonPath("$.case_fields[1].formatted_value").saveAs("et1VettingBeforeYouStart"))
            .check(jsonPath("$.case_fields[3].formatted_value").saveAs("et1VettingClaimantDetailsMarkUp"))
            .check(jsonPath("$.case_fields[5].formatted_value").saveAs("et1VettingRespondentDetailsMarkUp"))
            .check(jsonPath("$.case_fields[11].formatted_value").saveAs("et1VettingRespondentAcasDetails1"))
            .check(jsonPath("$.case_fields[48].formatted_value").saveAs("existingJurisdictionCodes"))
            .check(substring("et1Vetting")))

          .exec(Common.userDetails)

      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Before you Start
    ======================================================================================*/

      .group("ET_CW_520_Before_You_Start") {
        exec(http("ET_CW_520_005_Before_You_Start")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting1")
        .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/CaseWorker/EtBeforeYouStart.json"))
          .check(substring("et1VettingBeforeYouStart")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
    * Minimum required information - yes
    ======================================================================================*/

      .group("ET_CW_530_Min_Required_Information") {
        exec(http("ET_CW_530_005_Min_Required_Information")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting2")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/MinRequiredInformation.json"))
          .check(substring("et1VettingCanServeClaimGeneralNote")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
  * Minimum required information - Acas Certificate - yes
  ======================================================================================*/

      .group("ET_CW_540_Acas_Certificate ") {
        exec(http("ET_CW_540_005_Acas_Certificate")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting3")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/AcasCertificate.json"))
          .check(substring("et1VettingAcasCertGeneralNote")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)



      /*======================================================================================
* Possible substantive defects - The tribunal has no jurisdiction to consider - Rule 12(1)(a)
======================================================================================*/

      .group("ET_CW_550_Possible_Substantive_Defects") {
        exec(http("ET_CW_550_005_Possible_Substantive_Defects")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting4")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/PossibleSubstantiveDefects.json"))
          .check(substring("et1SubstantiveDefectsGeneralNotes")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Jurisdiction Codes - yes
======================================================================================*/

      .group("ET_CW_560_Jurisdiction_Codes") {
        exec(http("ET_CW_560_005_Jurisdiction_Codes")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting5")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/JurisdictionCodes.json"))
          .check(substring("et1JurisdictionCodeGeneralNotes")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Track allocation - yes
======================================================================================*/

      .group("ET_CW_570_Track_Allocation") {
        exec(http("ET_CW_570_005_Track_Allocation")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting6")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/TrackAllocation.json"))
          .check(substring("isTrackAllocationCorrect")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Tribunal location - yes
======================================================================================*/

      .group("ET_CW_580_Tribunal_Location") {
        exec(http("ET_CW_580_005_Tribunal_Location")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting7")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/TribunalLocation.json"))
          .check(jsonPath("$.data.et1AddressDetails").saveAs("et1AddressDetails"))
          .check(substring("tribunalCorrespondenceAddress")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Listing Details - no
======================================================================================*/

      .group("ET_CW_590_Listing_Details") {
        exec(http("ET_CW_590_005_Listing_Details")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting8")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/ListingDetails.json"))
          .check(substring("et1HearingVenueGeneralNotes")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Further questions - No, No, Yes
======================================================================================*/

      .group("ET_CW_600_Further_Questions") {
        exec(http("ET_CW_600_005_Further_Questions")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting9")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/FurtherQuestions.json"))
          .check(substring("et1FurtherQuestionsGeneralNotes")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Possible referral to a judge or legal officer - A claim of interim relief
======================================================================================*/

      .group("ET_CW_610_Possible_Referral") {
        exec(http("ET_CW_610_005_Possible_Referral")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting10")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/PossibleReferral.json"))
          .check(substring("aClaimOfInterimReliefTextArea")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Possible referral to Regional Employment Judge or Vice-President - A national security issue
======================================================================================*/

      .group("ET_CW_620_Possible_Referral_Employ_Judge") {
        exec(http("ET_CW_620_005_Possible_Referral_Employ_Judge")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting11")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/PossibleReferralEmployJudge.json"))
          .check(substring("aNationalSecurityIssueTextArea")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Does the claim include any other factors - The whole or any part of the claim is out of time
======================================================================================*/

      .group("ET_CW_630_Other_Factors") {
        exec(http("ET_CW_630_005_Other_Factors")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting12")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/OtherFactors.json"))
          .check(substring("claimOutOfTimeTextArea")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Final Notes
======================================================================================*/

      .group("ET_CW_640_Final_Notes") {
        exec(http("ET_CW_640_005_Final_Notes")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting13")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/FinalNotes.json"))
          .check(substring("et1VettingAdditionalInformationTextArea")))

          .exec(Common.userDetails)
      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Check Your Answers
======================================================================================*/

      .group("ET_CW_650_Et1_Check_Answers") {
        exec(http("ET_CW_650_005_Et1_Check_Answers")
          .post(BaseURL + "/data/cases/#{caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/CaseWorker/VetCheckAnswers.json"))
          .check(substring("Vetted")))

          .exec(Common.userDetails)


      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Accept or Reject Case Link
======================================================================================*/

      .group("ET_CW_660_Accept_Or_Reject") {
        exec(http("ET_CW_660_005_Accept_Or_Reject")
          .get(BaseURL + "/workallocation/case/tasks/#{caseId}/event/preAcceptanceCase/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
       //   .check(substring("Pre-task_required_for_event"))
             )


        .exec(http("ET_CW_660_010_Accept_Or_Reject")
          .get(BaseURL + "/data/internal/cases/#{caseId}/event-triggers/preAcceptanceCase?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(substring("Accept/Reject Case")))


          .exec(Common.userDetails)

      }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Case accepted? - Yes
======================================================================================*/

      .group("ET_CW_670_Accept_Case") {
        exec(http("ET_CW_670_005_Accept_Case")
          .post(BaseURL + "/data/case-types/ET_EnglandWales/validate?pageId=preAcceptanceCase1")
          .headers(Headers.commonHeader)
          .body(ElFileBody("bodies/CaseWorker/AcceptCase.json"))
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("content-type", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          //Date needs to be today or later date after case was made
          .check(regex(""""caseAccepted": "Yes"""))
        )

          .exec(Common.userDetails)


      }
    .pause(MinThinkTime, MaxThinkTime)


}
