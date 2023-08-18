package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Environment, Common, Headers}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Solicitor_FPL {

  val IdamUrl = Environment.idamURL
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val patternDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val patternYear = DateTimeFormatter.ofPattern("yyyy")
  val now = LocalDate.now()

 
  val CreateFPLCase =

    //set session variables
    exec(_.setAll(  "caseName"  -> ("Perf" + Common.randomString(5) + " vs Perf" + Common.randomString(5)),
                    "firstName"  -> ("Perf" + Common.randomString(5)),
                    "childFirstName" -> ("Child" + Common.randomString(5)),
                    "childLastName" -> ("Test" + Common.randomString(5)),
                    "dobDay" -> Common.getDay(),
                    "dobMonth" -> Common.getMonth(),
                    "dobYearChild" -> Common.getDobYearChild(),
                    "livingWithSinceDay" -> Common.getDay(),
                    "livingWithSinceMonth" -> Common.getMonth(),
                    "livingWithSinceYear" -> now.minusYears(1).format(patternYear),
                    "respondentFirstName" -> ("Resp" + Common.randomString(5)),
                    "respondentLastName" -> ("Test" + Common.randomString(5)),
                    "randomString"  -> Common.randomString(5),
                    "dobYearResp" -> Common.getDobYear(),
                    "currentDate" -> now.format(patternDate)))
 

    
    /*======================================================================================
    Select Jurisdiction as Family Law and Casetype as CARE_SUPERVISION_EPO
    ======================================================================================*/

    .group("XUI_FPL_050_005_StartCreateCase") {
      exec(http("XUI_FPL_050_005_StartCreateCase")
        .get("/data/internal/case-types/CARE_SUPERVISION_EPO/event-triggers/openCase?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Start application")))
    }

    .pause(MinThinkTime , MaxThinkTime)

    /*======================================================================================
    Click Save and Continue to create the case
    ======================================================================================*/
    
    .group("XUI_FPL_070_CaseNameSaveContinue") {
      exec(http("XUI_FPL_070_005_CaseNameSaveContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLSaveCase.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(substring("created_on")))
    }
      
    .pause(MinThinkTime , MaxThinkTime )

  val fplOrdersAndDirections =

    /*======================================================================================
    Click on the 'Orders and directions sought' link
    ======================================================================================*/
      
    group("XUI_FPL_080_OrdersAndDirections") {
      exec(http("XUI_FPL_080_015_OrdersAndDirectionsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/ordersNeeded?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("ordersNeeded")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Review details and click Save and Continue
    ======================================================================================*/
    
    .group("XUI_FPL_100_SubmitOrdersAndDirections") {
      exec(http("XUI_FPL_100_005_SubmitOrdersAndDirections")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLOrdersAndDirectionsSubmit.json"))
        .check(jsonPath("$.state").is("Open")))
    }
      
    .pause(MinThinkTime , MaxThinkTime )

  val fplHearingUrgency =

    /*======================================================================================
    Click on the 'Hearing urgency' link
    ======================================================================================*/

    group("XUI_FPL_110_HearingUrgency") {
      exec(http("XUI_FPL_110_015_HearingUrgencyEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/hearingNeeded?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("hearingNeeded")))

    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Review details and click Save and Continue
    ======================================================================================*/

    .group("XUI_FPL_130_SubmitHearingUrgency") {
      exec(http("XUI_FPL_130_005_SubmitHearingUrgency")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLHearingUrgencySubmit.json"))
        .check(jsonPath("$.data.hearing.timeFrame").is("Within 7 days"))
        .check(jsonPath("$.state").is("Open")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplGrounds =

    /*======================================================================================
    Click on the 'Hearing urgency' link
    ======================================================================================*/

    group("XUI_FPL_140_GroundsForTheApplication") {
      exec(http("XUI_FPL_140_015_GroundsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterGrounds?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("enterGrounds")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Review details and click Save and Continue
     ======================================================================================*/

    .group("XUI_FPL_160_SubmitGrounds") {
      exec(http("XUI_FPL_160_005_SubmitGrounds")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLGroundsSubmit.json"))
        .check(jsonPath("$.data.grounds.thresholdReason[0]").is("beyondControl"))
        .check(jsonPath("$.state").is("Open")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplLocalAuthority =

    /*======================================================================================
    Click on the 'Local authority's details' link
    ======================================================================================*/

    group("XUI_FPL_170_LocalAuthorityDetails") {
      exec(http("XUI_FPL_170_015_LocalAuthorityEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterLocalAuthority?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.name").saveAs("laName"))
        .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.id").saveAs("laId"))
        .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.AddressLine1").saveAs("laAddressLine1"))
        .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.AddressLine2").saveAs("laAddressLine2"))
        .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.PostTown").saveAs("laPostTown"))
        .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.PostCode").saveAs("laPostcode"))
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("enterLocalAuthority")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select Add New colleague, select the options and click Continue:
    Role: Solicitor
    Full Name: Text
    Email: Text
    Update Notifications: No
    ======================================================================================*/

    .group("XUI_FPL_190_AddLocalAuthorityColleagues") {
      exec(http("XUI_FPL_190_005_AddLocalAuthorityColleagues")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterLocalAuthorityColleagues")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLLocalAuthorityColleagueAdd.json"))
        .check(jsonPath("$.data.localAuthorityColleagues[0].id").saveAs("colleagueId"))
        .check(substring("localAuthorityColleaguesList")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Review details and click Save and Continue
     ======================================================================================*/

    .group("XUI_FPL_200_SubmitLocalAuthority") {
      exec(http("XUI_FPL_200_005_SubmitLocalAuthority")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLLocalAuthoritySubmit.json"))
        .check(jsonPath("$.data.localAuthorities[0].value.name").is("#{laName}"))
        .check(jsonPath("$.state").is("Open")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplChildDetails =

    /*======================================================================================
    Click on the 'Child's details' link
    ======================================================================================*/

    group("XUI_FPL_210_ChildDetails") {
      exec(http("XUI_FPL_210_015_ChildEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterChildren?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(Common.savePartyId)
        .check(Common.saveId)
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("enterChildren")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Review details and click Save and Continue
     ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_FPL_230_SubmitChildDetails") {
      exec(http("XUI_FPL_230_005_SubmitChildDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLChildDetailsSubmit.json"))
        .check(jsonPath("$.data.children1[0].id").is("#{id}"))
        .check(jsonPath("$.state").is("Open")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplRespondentDetails =

    /*======================================================================================
    Click on the 'Respondents' details' link
    ======================================================================================*/

    group("XUI_FPL_240_RespondentDetails") {
      exec(http("XUI_FPL_240_015_RespondentEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterRespondents?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(Common.savePartyId)
        .check(Common.saveId)
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("enterRespondents")))

      .exec(http("XUI_FPL_240_020_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(regex(""""name":"(.+?)","organisationIdentifier":"([0-9A-Z]+?)"""").ofType[(String, String)].findRandom.saveAs("respondentOrgs")))

    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Review details and click Save and Continue
     ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_FPL_260_SubmitRespondentDetails") {
      exec(http("XUI_FPL_260_005_SubmitRespondentDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLRespondentDetailsSubmit.json"))
        .check(jsonPath("$.data.respondents1[0].id").is("#{id}"))
        .check(jsonPath("$.state").is("Open")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplAllocationProposal =

    /*======================================================================================
    Click on the 'Allocation proposal' link
    ======================================================================================*/

    group("XUI_FPL_270_AllocationProposal") {
      exec(http("XUI_FPL_270_015_AllocationProposalEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/otherProposal?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("otherProposal")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Review details and click Save and Continue
     ======================================================================================*/

    .group("XUI_FPL_290_SubmitAllocationProposalDetails") {
      exec(http("XUI_FPL_290_005_SubmitAllocationProposalDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLAllocationProposalSubmit.json"))
        .check(jsonPath("$.data.allocationProposal.proposal").is("Circuit judge"))
        .check(jsonPath("$.state").is("Open")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplSubmitApplication =

    /*======================================================================================
    Click on the 'Submit application' link
    ======================================================================================*/

    group("XUI_FPL_300_SubmitApplication") {
      exec(http("XUI_FPL_300_015_SubmitApplicationEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/submitApplication?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.case_fields[?(@.id=='draftApplicationDocument')].value.document_filename").saveAs("documentFilename"))
        .check(jsonPath("$.case_fields[?(@.id=='draftApplicationDocument')].value.document_url").saveAs("documentURL"))
        .check(jsonPath("$.case_fields[?(@.id=='submissionConsentLabel')].formatted_value").saveAs("consentText"))
        .check(jsonPath("$.case_fields[?(@.id=='amountToPay')].formatted_value").saveAs("amountToPay"))
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("submitApplication")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Click Submit
     ======================================================================================*/

    .group("XUI_FPL_320_SubmitApplication") {
      exec(http("XUI_FPL_320_005_SubmitApplication")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLSubmitApplicationSubmit.json"))
        .check(jsonPath("$.data.submissionConsent[0]").is("agree"))
        .check(jsonPath("$.state").is("Submitted")))
    }

    .pause(MinThinkTime , MaxThinkTime )

}