package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Environment, Common, Headers}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Solicitor_FPL {

  val IdamUrl = Environment.idamURL
  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val patternDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val patternYear = DateTimeFormatter.ofPattern("yyyy")
  val now = LocalDateTime.now()
  val patternTimeNow = DateTimeFormatter.ofPattern("HH:mm:ss.S")

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
                    "dobYearResp" -> Common.getDobYear(),
                    "currentDate" -> now.format(patternDate)))
 
    /*======================================================================================
    Click On Create Case for FPL
    ======================================================================================*/
  
    .group("XUI_FPL_040_CreateCase") {

      exec(http("XUI_FPL_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("PUBLICLAW")))

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime , MaxThinkTime)
    
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

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime , MaxThinkTime)

    /*======================================================================================
    Enter case name and click Continue
    ======================================================================================*/
    
    .group("XUI_FPL_060_CaseNameContinue") {
      exec(http("XUI_FPL_060_005_CaseNameContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=openCaseprovideCaseName")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLOpenCase.json"))
        .check(substring("caseName")))
    }
    
    .pause(MinThinkTime , MaxThinkTime )

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

      .exec(http("XUI_FPL_070_015_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))
    }
      
    .pause(MinThinkTime , MaxThinkTime )

  val fplOrdersAndDirections =

    /*======================================================================================
    Click on the 'Orders and directions sought' link
    ======================================================================================*/
      
    group("XUI_FPL_080_OrdersAndDirections") {
      exec(http("XUI_FPL_080_005_OrdersAndDirectionsTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/ordersNeeded")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("MCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_080_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_080_015_OrdersAndDirectionsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/ordersNeeded?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("ordersNeeded")))

      .exec(Common.isAuthenticated)
    }
    
    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )
  
    /*======================================================================================
    Select options and click Continue:
    Orders = Care order
    Directions = No
    ======================================================================================*/
    
    .group("XUI_FPL_090_AddOrdersAndDirections") {
      exec(http("XUI_FPL_090_005_AddOrdersAndDirections")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=ordersNeeded1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLOrdersAndDirectionsAdd.json"))
        .check(substring("emergencyProtectionOrderDetails")))
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

      .exec(http("XUI_FPL_100_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("""event_id":"ordersNeeded""")))
    }
      
    .pause(MinThinkTime , MaxThinkTime )

  val fplHearingUrgency =

    /*======================================================================================
    Click on the 'Hearing urgency' link
    ======================================================================================*/

    group("XUI_FPL_110_HearingUrgency") {
      exec(http("XUI_FPL_110_005_HearingUrgencyTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/hearingNeeded")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_110_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_110_015_HearingUrgencyEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/hearingNeeded?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("hearingNeeded")))

      .exec(Common.isAuthenticated)
    }

    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select options and click Continue:
    Urgency = Within 7 days
    Type = Standard
    Without Notice Hearing = No
    Reduced Notice = No
    Respondents Aware = No
    ======================================================================================*/

    .group("XUI_FPL_120_AddHearingUrgency") {
      exec(http("XUI_FPL_120_005_AddHearingUrgency")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=hearingNeeded1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLHearingUrgencyAdd.json"))
        .check(substring("STANDARD")))
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
        .check(jsonPath("$.data.hearing.hearingUrgencyType").is("STANDARD"))
        .check(jsonPath("$.state").is("Open")))

      .exec(http("XUI_FPL_130_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("""event_id":"hearingNeeded""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplGrounds =

    /*======================================================================================
    Click on the 'Hearing urgency' link
    ======================================================================================*/

    group("XUI_FPL_140_GroundsForTheApplication") {
      exec(http("XUI_FPL_140_005_GroundsTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/enterGrounds")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_140_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_140_015_GroundsEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterGrounds?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("enterGrounds")))

      .exec(Common.isAuthenticated)
    }

    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select options and click Continue:
    Beyond parental control
    ======================================================================================*/

    .group("XUI_FPL_150_AddGrounds") {
      exec(http("XUI_FPL_150_005_AddGrounds")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterGrounds1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLGroundsAdd.json"))
        .check(substring("beyondControl")))
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

      .exec(http("XUI_FPL_160_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("""event_id":"enterGrounds""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplLocalAuthority =

    /*======================================================================================
    Click on the 'Local authority's details' link
    ======================================================================================*/

    group("XUI_FPL_170_LocalAuthorityDetails") {
      exec(http("XUI_FPL_170_005_LocalAuthorityTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/enterApplicantDetailsLA")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_170_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_170_015_LocalAuthorityEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterApplicantDetailsLA?ignore-warning=false")
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
        .check(jsonPath("$.id").is("enterApplicantDetailsLA")))

      .exec(Common.isAuthenticated)
    }

    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select options and click Continue:
    Local Authority Name: pre-filled (captured)
    Local Authority Group Email Address: Text
    PBA Number: PBA0066906
    Address: pre-filled (captured)
    Phone Number: 07000000000
    ======================================================================================*/

    .group("XUI_FPL_180_AddLocalAuthorityDetails") {
      exec(http("XUI_FPL_180_005_AddLocalAuthority")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterApplicantDetailsLAApplicantDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLLocalAuthorityAdd.json"))
        .check(substring("#{laName}")))
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
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterApplicantDetailsLAApplicantContact")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLLocalAuthorityColleagueAdd.json"))
        .check(jsonPath("$.data.applicantContact.firstName").is("PerfTest"))
        .check(jsonPath("$.data.applicantContact.lastName").is("ApplicantContact")))
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

      .exec(http("XUI_FPL_200_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("""event_id":"enterApplicantDetailsLA""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplChildDetails =

    /*======================================================================================
    Click on the 'Child's details' link
    ======================================================================================*/

    group("XUI_FPL_210_ChildDetails") {
      exec(http("XUI_FPL_210_005_ChildTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/enterChildren")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_210_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_210_015_ChildEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterChildren?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(Common.savePartyId)
        .check(Common.saveId)
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("enterChildren")))

      .exec(Common.isAuthenticated)
    }

    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select options and click Continue:
    First Name: Text
    Last Name: Text
    DOB: anything under 18
    Gender: Boy
    Living Situation: Living with respondents
    Started staying here: date after DOB and before current date
    Address: Captured from lookup
    Adoption: No
    Mother Name: Text
    Father Name: Text
    Father responsibility: Yes
    Social Worker Phone Number: Any
    Additional Needs: No
    Contact Details Hidden: No
    Ability to Take Part: No
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_FPL_220_AddChildDetails") {
      exec(http("XUI_FPL_220_005_AddChildDetails")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterChildren1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLChildDetailsAdd.json"))
        .check(substring("finalDecisionDate")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Review details and click Save and Continue
     ======================================================================================*/

    .group("XUI_FPL_230_SubmitChildDetails") {
      exec(http("XUI_FPL_230_005_SubmitChildDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLChildDetailsSubmit.json"))
        .check(jsonPath("$.data.children1[0].id").is("#{id}"))
        .check(jsonPath("$.state").is("Open")))

      .exec(http("XUI_FPL_230_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("""event_id":"enterChildren""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplRespondentDetails =

    /*======================================================================================
    Click on the 'Respondents' details' link
    ======================================================================================*/

    group("XUI_FPL_240_RespondentDetails") {
      exec(http("XUI_FPL_240_005_RespondentTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/enterRespondents")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_240_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_240_015_RespondentEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/enterRespondents?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(Common.savePartyId)
        .check(Common.saveId)
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("enterRespondents")))

      .exec(Common.isAuthenticated)

      .exec(http("XUI_FPL_240_020_GetOrgs")
        .get("/api/caseshare/orgs")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(regex(""""name":"(.+?)","organisationIdentifier":"([0-9A-Z]+?)"""").ofType[(String, String)].findRandom.saveAs("respondentOrgs")))
    }

    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select options and click Continue:
    First Name: Text
    Last Name: Text
    DOB: anything over 18
    Address: Captured from lookup
    Telephone Number: any
    Relationship: Free text
    Contact Details Hidden: No
    Ability to Take Part: No
    Legal Representation: No
    ======================================================================================*/

    .exec(Common.postcodeLookup)

    .group("XUI_FPL_250_AddRespondentDetails") {
      exec(http("XUI_FPL_250_005_AddRespondentDetails")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterRespondents1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLRespondentDetailsAdd.json"))
        .check(substring("respondents1")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
     Review details and click Save and Continue
     ======================================================================================*/

    .group("XUI_FPL_260_SubmitRespondentDetails") {
      exec(http("XUI_FPL_260_005_SubmitRespondentDetails")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLRespondentDetailsSubmit.json"))
        .check(jsonPath("$.data.respondents1[0].id").is("#{id}"))
        .check(jsonPath("$.state").is("Open")))

      .exec(http("XUI_FPL_260_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("""event_id":"enterRespondents""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplAllocationProposal =

    /*======================================================================================
    Click on the 'Allocation proposal' link
    ======================================================================================*/

    group("XUI_FPL_270_AllocationProposal") {
      exec(http("XUI_FPL_270_005_AllocationProposalTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/otherProposal")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_270_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_270_015_AllocationProposalEvent")
        .get("/data/internal/cases/#{caseId}/event-triggers/otherProposal?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.id").is("otherProposal")))

      .exec(Common.isAuthenticated)
    }

    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select options and click Continue:
    Allocation Proposal: Circuit Judge
    Reason: Free text
    ======================================================================================*/

    .group("XUI_FPL_280_AddAllocationProposalDetails") {
      exec(http("XUI_FPL_280_005_AddAllocationProposalDetails")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=otherProposal1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLAllocationProposalAdd.json"))
        .check(substring("allocationProposal")))
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
        .check(jsonPath("$.data.allocationProposal.proposalV2").is("Circuit judge"))
        .check(jsonPath("$.state").is("Open")))

      .exec(http("XUI_FPL_290_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("""event_id":"otherProposal""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val fplSubmitApplication =

    /*======================================================================================
    Click on the 'Submit application' link
    ======================================================================================*/

    group("XUI_FPL_300_SubmitApplication") {
      exec(http("XUI_FPL_300_005_SubmitApplicationTrigger")
        .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/#{caseId}/trigger/submitApplication")
        .headers(Headers.navigationHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("HMCTS Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.monitoringTools)

      .exec(http("XUI_FPL_300_010_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))

      .exec(Common.profile)

      .exec(http("XUI_FPL_300_015_SubmitApplicationEvent")
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

      .exec(Common.isAuthenticated)
    }

    .exec(Common.caseActivityGet)

    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    Select options and click Continue:
    Agree with Statement: Tick
    ======================================================================================*/

    .group("XUI_FPL_310_AddSubmitApplicationDetails") {
      exec(http("XUI_FPL_310_005_AddSubmitApplicationDetails")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=submitApplication1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLSubmitApplicationAdd.json"))
        .check(substring("submissionConsent")))
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

  val fplReturnToCase =

    /*======================================================================================
    Click on the 'Close and Return to case details' button
    ======================================================================================*/

    group("XUI_FPL_330_ReturnToCase") {
      exec(http("XUI_FPL_330_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("CCD ID")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val QueryManagement = 

    group("XUI_FPL_340_RaiseNewQuery") {
      exec(http("XUI_FPL_340_005_RaiseNewQuery")
      .get("/query-management/query/#{caseId}")
      .headers(Headers.commonHeader)
      .check(substring("HMCTS Manage cases"))) // No page specific text is returned

      .exec(Common.isAuthenticated)

      .exec(http("XUI_FPL_340_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("CCD ID")))
    }

    .exec(getCookieValue(CookieKey("__userid__").withDomain(BaseURL.replace("https://", "")).saveAs("idamId")))

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_FPL_350_ConfirmQueryDetails") {
      exec(http("XUI_FPL_350_005_ConfirmQueryDetails")
        .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRaiseQuery?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_FPL_360_RaiseNewQuery") {
      exec(http("XUI_FPL_360_005_RaiseNewQuery")
        .get("/query-management/query/#{caseId}raiseAQuery")
        .headers(Headers.commonHeader)
        .check(substring("HMCTS Manage cases")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.setAll("currentTime" -> now.format(patternTimeNow)))

    .group("XUI_FPL_370_SubmitNewQuery") {
      exec(http("XUI_FPL_370_005_SubmitNewQuery")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLRaiseNewQuery.json")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val RespondToQueryManagement = 

    group("XUI_FPL_380_ViewCase") {
      exec(http("XUI_FPL_380_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("CCD ID")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_FPL_390_ViewQuery") {
      exec(Common.isAuthenticated)

      .exec(http("XUI_FPL_390_005_ViewQuery")
        .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRespondQuery?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[?(@.id=='qmCaseQueriesCollectionLASol')].value.caseMessages[0].id").saveAs("raiseQueryParentId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmCaseQueriesCollectionLASol')].value.caseMessages[0].value.id").saveAs("raiseQueryId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmCaseQueriesCollectionLASol')].value.caseMessages[0].value.createdBy").saveAs("queryCreatedBy"))
        .check(jsonPath("$.case_fields[?(@.id=='qmCaseQueriesCollectionLASol')].value.caseMessages[0].value.createdOn").saveAs("queryCreatedOn")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.setAll("currentTime" -> now.format(patternTimeNow)))
    .exec(getCookieValue(CookieKey("__userid__").withDomain(BaseURL.replace("https://", "")).saveAs("idamId")))

    .group("XUI_FPL_400_SubmitQueryResponse") {
      exec(http("XUI_FPL_400_005_SubmitQueryResponse")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLRespondToQuery.json")))
    }

    .pause(MinThinkTime , MaxThinkTime )

}