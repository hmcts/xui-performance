package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new Probate Intestacy application as a professional user (e.g. solicitor).
* This file only contains pages that differ from the standard Probate journey
======================================================================================*/

object Solicitor_Probate_Intestacy {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val BranchToIntestacyJourney =

    /*======================================================================================
    * Has a will been left?
    ======================================================================================*/

    group("XUI_Probate_Intestacy_010_SubmitWillLeft") {
      exec(http("XUI_Probate_Intestacy_010_005_SubmitWillLeft")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage7")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/probate/intestacy/ProbateIntestacyWillLeft.json"))
        .check(substring("solsWillType")))

      .exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confirm deceased details - check your answers
    ======================================================================================*/

    .group("XUI_Probate_Intestacy_020_ConfirmDeceasedDetails") {
      exec(http("XUI_Probate_Intestacy_020_005_ConfirmDeceasedDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/probate/intestacy/ProbateIntestacyConfirmDeceasedDetails.json"))
        .check(jsonPath("$.state").is("SolIntestacyCreated")))

      .exec(Common.activity)

      .exec(http("XUI_Probate_000_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.state.name").is("Intestacy grant created")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Part 3 - Click 'Add application details' link
  ======================================================================================*/

  val AddApplicationDetails =

    group("XUI_Probate_Intestacy_030_GrantOfProbateIntestacyDetails") {
      exec(http("XUI_Probate_Intestacy_030_005_GrantOfProbateIntestacyDetails")
        .get("/cases/case-details/#{caseId}/trigger/solicitorUpdateIntestacy/solicitorUpdateIntestacysolicitorUpdateIntestacyPage1")
        .headers(Headers.navigationHeader)
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.userDetails)

      .exec(Common.configUI)

      .exec(Common.isAuthenticated)

      .exec(http("XUI_Probate_000_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.state.name").is("Intestacy grant created")))

      .exec(http("XUI_Probate_Intestacy_030_010_GrantOfProbateIntestacyDetails")
        .get("/data/internal/cases/#{caseId}/event-triggers/solicitorUpdateIntestacy?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token_probate"))
        .check(substring("solicitorUpdateIntestacy")))

      .exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Postcode Lookup for Applicant
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit Executor Details
    ======================================================================================*/

    .group("XUI_Probate_Intestacy_040_SubmitExecutorDetails") {
      exec(http("XUI_Probate_Intestacy_040_005_SubmitExecutorDetails")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateIntestacysolicitorUpdateIntestacyPage1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/probate/intestacy/ProbateIntestacyApplicantDetails.json"))
        .check(substring("primaryApplicantForenames")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit Executor Details
    ======================================================================================*/

    .group("XUI_Probate_Intestacy_050_SubmitApplicantRelationshipDetails") {
      exec(http("XUI_Probate_Intestacy_050_005_SubmitApplicantRelationshipDetails")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateIntestacysolicitorUpdateIntestacyPage2")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/probate/intestacy/ProbateIntestacyApplicantRelationshipDetails.json"))
        .check(jsonPath("$.data.solsIntestacyExecutorList[0].value.solsApplicantFamilyDetails.applicantRelationshipToDeceased").is("Child"))
        .check(jsonPath("$.data.solsIntestacyExecutorList[0].id").saveAs("newExecId")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Postcode Lookup for new executor
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit Extra Executor Details
    ======================================================================================*/

    .group("XUI_Probate_Intestacy_060_SubmitExtraExecDetails") {
      exec(http("XUI_Probate_Intestacy_060_005_SubmitExtraExecDetails")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateIntestacysolicitorUpdateIntestacyPage3")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/probate/intestacy/ProbateIntestacyExtraExecDetails.json"))
        .check(substring("solsApplicantFamilyDetails")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Submit Extra Information
    ======================================================================================*/

    .group("XUI_Probate_Intestacy_070_SubmitExtraInfo") {
      exec(http("XUI_Probate_Intestacy_070_005_SubmitExtraInfo")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateIntestacysolicitorUpdateIntestacyPage4")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/probate/intestacy/ProbateIntestacyExtraInfo.json"))
        .check(substring("solsAdditionalInfo")))

        .exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confirm Intestacy details - check your answers
    ======================================================================================*/

    .group("XUI_Probate_Intestacy_080_ConfirmIntestacyDetails") {
      exec(http("XUI_Probate_Intestacy_080_005_ConfirmIntestacyDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/probate/intestacy/ProbateIntestacyConfirmIntestacyDetails.json"))
        .check(jsonPath("$.state").is("SolAppUpdated")))

      .exec(http("XUI_Probate_000_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.state.name").is("Application updated")))

      .exec(Common.activity)
    }

    .pause(MinThinkTime, MaxThinkTime)

}
