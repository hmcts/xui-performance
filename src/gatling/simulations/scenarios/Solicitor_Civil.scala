package scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*======================================================================================
* Create a new Private Law FL401 application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_Civil {

  val BaseURL = Environment.baseURL
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val patternTimeNow = DateTimeFormatter.ofPattern("HH:mm:ss.S")
  val patternDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val now = LocalDateTime.now()
  val config: Config = ConfigFactory.load()
  val paybubbleClientSecret = config.getString("auth.paybubbleClientSecret")

  val CreateCivilCase =

    /*======================================================================================
    * Click the Create Case link
    ======================================================================================*/

    group("XUI_Civil_030_CreateCase") {
      exec(http("XUI_Civil_030_005_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("CIVIL")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Civil case type and Specified Claim event type
    ======================================================================================*/

    .group("XUI_Civil_040_SelectCaseType") {
      exec(http("XUI_Civil_040_005_SelectCaseType")
        .get("/data/internal/case-types/CIVIL/event-triggers/CREATE_CLAIM_SPEC?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(substring("Legal representatives: specified civil money claims service"))
        .check(jsonPath("$.event_token").saveAs("event_token")))
    }

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Before you start - Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_050_CreateCaseCheckList") {
      exec(http("XUI_Civil_050_005_CreateCaseCheckList")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECCheckList")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimChecklist.json"))
        .check(substring("pageId=CREATE_CLAIM_SPECCheckList")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Issue civil court proceedings - Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_060_CreateCaseEligibility") {
      exec(http("XUI_Civil_060_005_CreateCaseEligibility")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECEligibility")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimEligibility.json"))
        .check(substring("pageId=CREATE_CLAIM_SPECEligibility")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter Referenecs & Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_070_CreateCaseReferences") {
      exec(http("XUI_Civil_070_005_CreateCaseReferences")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECReferences")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimReferences.json"))
        .check(substring("pageId=CREATE_CLAIM_SPECReferences")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Company, Enter Postcode & click Find Address, click Continue
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_Civil_080_CreateCaseClaimant") {
      exec(http("XUI_Civil_080_005_CreateCaseClaimant")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECClaimant")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimClaimantDetails.json"))
        .check(substring("primaryAddress")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No to additional Claimant & click Continue
    ======================================================================================*/

    .group("XUI_Civil_090_CreateCaseOtherClaimant") {
      exec(http("XUI_Civil_090_005_CreateCaseOtherClaimant")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECAddAnotherClaimant")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimOtherClaimant.json"))
        .check(substring("addApplicant2")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No to use existing email, enter different email & click Continue
    ======================================================================================*/

    .group("XUI_Civil_100_CreateCaseNotifications") {
      exec(http("XUI_Civil_100_005_CreateCaseNotifications")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECNotifications")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimNotifications.json"))
        .check(substring("applicantSolicitor1CheckEmail")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter Solicitor Org details & click Continue
    ======================================================================================*/

    .group("XUI_Civil_110_CreateCaseSolicitorDetails") {
      exec(http("XUI_Civil_110_005_CreateCaseSolicitorDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECClaimantSolicitorOrganisation")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimSolicitorOrg.json"))
        .check(substring("PrepopulateToUsersOrganisation")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No for different address & click Continue
    ======================================================================================*/

    .group("XUI_Civil_120_CreateCaseCorrespondenceAddress") {
      exec(http("XUI_Civil_120_005_CreateCaseCorrespondenceAddress")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECspecCorrespondenceAddress")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimCorrespondenceAddress.json"))
        .check(substring("specApplicantCorrespondenceAddressRequired")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Company type, enter name, search for postcode and select address, click Continue
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_Civil_130_CreateCaseDefendantDetails") {
      exec(http("XUI_Civil_130_005_CreateCaseDefendantDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECDefendant")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimDefendantDetails.json"))
        .check(substring("applicantSolicitor1CheckEmail")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Yes for Defendant Represented & click Continue
    ======================================================================================*/

    .group("XUI_Civil_140_CreateCaseDefendantRepresented") {
      exec(http("XUI_Civil_140_005_CreateCaseDefendantRepresented")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECLegalRepresentation")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimDefendantRepresented.json"))
        .check(substring("specRespondent1Represented")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter Defendant Solicitor details & click Continue
    ======================================================================================*/

    .group("XUI_Civil_150_CreateCaseDefendantSolicitorDetails") {
      exec(http("XUI_Civil_150_005_CreateCaseDefendantSolicitorDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECDefendantSolicitorOrganisation")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimDefendantSolicitor.json"))
        .check(substring("respondent1OrganisationPolicy")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter the Solicitor email address & click Continue
    ======================================================================================*/

    .group("XUI_Civil_160_CreateCaseDefendantSolicitorEmail") {
      exec(http("XUI_Civil_160_005_CreateCaseDefendantSolicitorEmail")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECDefendantSolicitorEmail")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimDefendantSolicitorEmail.json"))
        .check(substring("respondentSolicitor1EmailAddress")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No for Different Address & click Continue
    ======================================================================================*/

    .group("XUI_Civil_170_CreateCaseDefendantCorrespondenceAddress") {
      exec(http("XUI_Civil_170_005_CreateCaseDefendantCorrespondenceAddress")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECspecRespondentCorrespondenceAddress")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimDefendantCorrespondenceAddress.json"))
        .check(substring("respondentSolicitor1EmailAddress")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No for Another Defendant & click Continue
    ======================================================================================*/

    .group("XUI_Civil_180_CreateCaseAddAnotherDefendant") {
      exec(http("XUI_Civil_180_005_CreateCaseAddAnotherDefendant")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECAddAnotherDefendant")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimAddAnotherDefendant.json"))
        .check(substring("addRespondent2")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No for Flight Delay Claim & click Continue
    ======================================================================================*/

    .group("XUI_Civil_190_CreateCaseFlightDelayClaim") {
      exec(http("XUI_Civil_190_005_CreateCaseFlightDelayClaim")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECFlightDelayClaim")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimFlightDelayClaim.json"))
        .check(substring("isFlightDelayClaim")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter Claim description & click Continue
    ======================================================================================*/

    .group("XUI_Civil_200_CreateCaseAddClaimDetails") {
      exec(http("XUI_Civil_200_005_CreateCaseAddClaimDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECDetails")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimAddDetails.json"))
        .check(substring("detailsOfClaim")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Add Manually & click Continue
    ======================================================================================*/

    .group("XUI_Civil_210_CreateCaseAddManualDetails") {
      exec(http("XUI_Civil_210_005_CreateCaseAddManualDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECUploadClaimDocument")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimManualDetails.json"))
        .check(substring("MANUAL")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter Claim Details & click Continue
    ======================================================================================*/

    .group("XUI_Civil_220_CreateCaseEnterClaimDetails") {
      exec(http("XUI_Civil_220_005_CreateCaseEnterClaimDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECClaimTimeline")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimTimelineDetails.json"))
        .check(substring("timelineOfEvents")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select Evidence type, enter Details & click Continue
    ======================================================================================*/

    .group("XUI_Civil_230_CreateCaseEvidenceDetails") {
      exec(http("XUI_Civil_230_005_CreateCaseEvidenceDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECEvidenceList")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimEvidence.json"))
        .check(substring("expertWitnessEvidence")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter Claim Details & Amount, click on Continue
    ======================================================================================*/

    .group("XUI_Civil_240_CreateCaseEnterAmount") {
      exec(http("XUI_Civil_240_005_CreateCaseEnterAmount")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECClaimAmount")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimAmount.json"))
        .check(substring("claimAmountBreakup")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confirm Details & Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_250_CreateCaseConfirmAmountDetails") {
      exec(http("XUI_Civil_250_005_CreateCaseConfirmAmountDetails")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECClaimAmountDetails")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimAmountDetails.json"))
        .check(substring("claimAmountBreakupSummaryObject")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No for Interest Claimed & Click Continue
    ======================================================================================*/

    .group("XUI_Civil_260_CreateCaseClaimInterest") {
      exec(http("XUI_Civil_260_005_CreateCaseClaimInterest")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECClaimInterest")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimInterest.json"))
        .check(substring("calculatedInterest")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confirm Claim Amount Details & Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_270_CreateCaseAmountSummary") {
      exec(http("XUI_Civil_270_005_CreateCaseAmountSummary")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECInterestSummary")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimAmountSummary.json"))
        .check(substring("applicantSolicitor1PbaAccountsIsEmpty")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_280_CreateCaseClaimFeePBA") {
      exec(http("XUI_Civil_280_005_CreateCaseClaimFeePBA")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECPbaNumber")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimFeeAmount.json"))
        .check(substring("calculatedAmountInPence")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Select No for Fixed Costs & Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_290_CreateCaseFixedCosts") {
      exec(http("XUI_Civil_290_005_CreateCaseFixedCosts")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECFixedCommencementCosts")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimFixedCosts.json"))
        .check(substring("claimFixedCosts")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Enter Name & Role & Click on Continue
    ======================================================================================*/

    .group("XUI_Civil_300_CreateCaseStatementOfTruth") {
      exec(http("XUI_Civil_300_005_CreateCaseStatementOfTruth")
        .post("/data/case-types/CIVIL/validate?pageId=CREATE_CLAIM_SPECStatementOfTruth")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/civil/Civil_CreateClaimStatementOfTruth.json"))
        .check(substring("StatementOfTruth")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Confirm all Details & Click on Submit
    ======================================================================================*/

    .group("XUI_Civil_310_CreateCaseSubmit") {
      exec(http("XUI_Civil_310_005_CreateCaseSubmit")
        .post("/data/case-types/CIVIL/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/civil/Civil_CreateClaimSubmit.json"))
        .check(substring("CALLBACK_COMPLETED"))
        .check(jsonPath("$.id").saveAs("caseId")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Click on Close and Return to case details
    ======================================================================================*/

    .group("XUI_Civil_320_ViewCase") {
      exec(http("XUI_Civil_320_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("internal/cases/#{caseId}")))

      .exec(Common.manageLabellingRoleAssignment)
      .exec(Common.waJurisdictions)
    }

    .pause(MinThinkTime, MaxThinkTime)

    //microservice is a string defined in the Simulation and passed into the body below
    def s2s(microservice: String) = {

    exec(http("GetS2SToken")
      .post(Environment.s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(s"""{"microservice":"${microservice}"}"""))
      .check(bodyString.saveAs(s"${microservice}BearerToken")))
      .exitHereIfFailed
    }

  /*======================================================================================
  * Add Payment steps required to get case into required state for raising a Query
  ======================================================================================*/

  val AddPayment =

    exec(http("GetIdamToken")
      .post(Environment.idamAPIURL + "/o/token?grant_type=password&username=pba-perftest-user@mailinator.com&password=Pass19word&client_id=paybubble&client_secret=" + paybubbleClientSecret + "&redirect_uri=" + Environment.refDataApiURL + "/oauth2redirect&scope=openid%20profile%20roles%20search-user")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("access_tokenPayments")))

    .exec(http("PaymentAPI_GetCasePaymentOrders")
      .get("http://payment-api-#{env}.service.core-compute-#{env}.internal/case-payment-orders?case_ids=#{caseId}")
      .header("Authorization", "Bearer #{access_tokenPayments}")
      .header("ServiceAuthorization", "#{xui_webappBearerToken}")
      .header("Content-Type","application/json")
      .header("accept","*/*")
      .check(jsonPath("$.content[0].orderReference").saveAs("caseIdPaymentRef")))

    .pause(MinThinkTime, MaxThinkTime)

    .tryMax(2) {
      exec(http("API_Civil_AddPayment")
        .put("http://civil-service-#{env}.service.core-compute-#{env}.internal/service-request-update-claim-issued")
        .header("Authorization", "Bearer #{access_tokenPayments}")
        .header("ServiceAuthorization", "#{civil_serviceBearerToken}")
        .header("Content-type", "application/json")
        .body(ElFileBody("bodies/civil/AddPayment.json")))
    }
      .pause(MinThinkTime, MaxThinkTime)

  val QueryManagement =

    /*======================================================================================
    * Select the Raies New Query case event from the drop down
    ======================================================================================*/

    group("XUI_Civil_330_RaiseNewQuery") {
      exec(http("XUI_Civil_330_005_RaiseNewQuery")
        .get("/query-management/query/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("HMCTS Manage cases"))) // No page specific text is returned

      .exec(Common.isAuthenticated)

      .exec(http("XUI_Civil_330_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("case_id")))
    }

    .exec(getCookieValue(CookieKey("__userid__").withDomain(BaseURL.replace("https://", "")).saveAs("idamId")))

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    * Enter query details and click Continue
    ======================================================================================*/

    .group("XUI_Civil_340_ConfirmQueryDetails") {
      exec(http("XUI_Civil_340_005_ConfirmQueryDetails")
        .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRaiseQuery?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_Civil_350_RaiseNewQuery") {
      exec(http("XUI_Civil_350_005_RaiseNewQuery")
        .get("/query-management/query/#{caseId}raiseAQuery")
        .headers(Headers.commonHeader)
        .check(substring("HMCTS Manage cases")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.setAll(
      "currentTime" -> now.format(patternTimeNow),
      "currentDate" -> now.format(patternDate)))

    /*======================================================================================
    * Submit query
    ======================================================================================*/

    .group("XUI_Civil_360_SubmitNewQuery") {
      exec(http("XUI_Civil_360_005_SubmitNewQuery")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/civil/CivilRaiseNewQuery.json")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val RespondToQueryManagement =

    /*======================================================================================
    * View the new case as CTSC admin
    ======================================================================================*/

    exec(http("XUI_Civil_370_005_ViewCase")
      .get("/data/internal/cases/#{caseId}")
      .headers(Headers.commonHeader)
      .check(substring("case_id")))

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    * Click on the Tasks tab
    ======================================================================================*/

    .group("XUI_Civil_380_005_ViewTask") {
      exec(http("XUI_Civil_380_005_ViewTask")
        .post("/workallocation/case/task/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"refined":true}"""))
        .check(jsonPath("$[0].id").optional.saveAs("taskId"))
        .check(jsonPath("$[0].type").optional.saveAs("taskType")))

      .exec(http("XUI_Civil_380_010_ViewTask")
        .post("/workallocation/caseworker/getUsersByServiceName")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"services":["CIVIL"]}""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    //Save taskType from response
    .exec(session => {
      // Initialise task type in session if it's not already present, ensure the variable exists before entering Loop
      session("taskType").asOption[String] match {
        case Some(taskType) => session
        case None => session.set("taskType", "")
      }
    })

    // Loop until the task type matches "respondToQueryCTSC"
    .asLongAs(session => session("taskType").as[String] != "respondToQueryCTSC") {
      exec(http("XUI_Civil_380_005_ViewTask")
        .get("/workallocation/case/task/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$[0].id").optional.saveAs("taskId"))
        .check(jsonPath("$[0].type").optional.saveAs("taskType")))

        .pause(5, 10) // Wait between retries
    }

    /*======================================================================================
    * Click on the assign to me task link
    ======================================================================================*/

    .group("XUI_Civil_390_ClaimTask") {
      exec(http("XUI_Civil_390_005_ClaimTask")
        .post("/workallocation/task/#{taskId}/claim")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{}""")))

      .exec(http("XUI_Civil_390_010_ClaimTask")
        .post("/workallocation/case/task/#{caseId}")
        .headers(Headers.commonHeader)
        .header("Accept", "application/json, text/plain, */*")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"refined":true}"""))
        .check(substring("Respond to a query")))
    }

    .pause(MinThinkTime , MaxThinkTime )

      /*======================================================================================
      * Click on the respond to query task
      ======================================================================================*/

    .group("XUI_Civil_400_RespondToQuery") {
      exec(http("XUI_Civil_400_005_RespondToQuery")
        .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRespondQuery?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.case_fields[?(@.id=='qmApplicantSolicitorQueries')].value.caseMessages[0].id").saveAs("raiseQueryParentId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmApplicantSolicitorQueries')].value.caseMessages[0].value.id").saveAs("raiseQueryId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmApplicantSolicitorQueries')].value.caseMessages[0].value.createdBy").saveAs("queryCreatedBy"))
        .check(jsonPath("$.case_fields[?(@.id=='qmApplicantSolicitorQueries')].value.caseMessages[0].value.createdOn").saveAs("queryCreatedOn"))
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(Common.isAuthenticated)

      .exec(http("XUI_Civil_400_010_RespondToQuery")
        .get("/workallocation/case/tasks/#{caseId}/event/queryManagementRespondQuery/caseType/CIVIL/jurisdiction/CIVIL")
        .headers(Headers.commonHeader)
        .check(substring("respondToQueryCTSC")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    * Enter response details and click submit
    ======================================================================================*/

    .group("XUI_Civil_410_SubmitResponse") {
      exec(http("XUI_Civil_410_005_SubmitResponse")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/civil/CivilRespondToQuery.json"))
        .check(substring("AWAITING_RESPONDENT_ACKNOWLEDGEMENT")))

      .exec(http("XUI_Civil_410_010_SubmitResponse")
        .post("/workallocation/task/#{taskId}/complete")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"actionByEvent":true,"eventName":"Respond to a query"}""")))

      .exec(Common.isAuthenticated)
      .exec(Common.waJurisdictions)
      .exec(Common.manageLabellingRoleAssignment)
    }

}