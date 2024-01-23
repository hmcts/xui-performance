package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios.Login.baseDomain
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
  val UserFeederCivilHearingCases = csv("CivilHearingsCasesForAllHearings.csv").circular
  val UserFeederCivilHearingId = csv("CivilHearingIdForGetHearings.csv").circular
  val UserFeederCivilHearingIdCancels = csv("CivilHearingIdCancels.csv").circular
  val UserFeederCivilHearingIdAmend = csv("CivilHearingIdAmend.csv").circular
  val UserFeederCivilHearingRequestCases=csv("CivilHearingsCasesForRequestHearing.csv").circular
  
  val hearingPercentage = 90
  
  val ViewAllHearings =
  
  
   feed(UserFeederCivilHearingCases)
  
  /*======================================================================================
  * Select the Case you want to view all hearings
  ======================================================================================*/
    
    .group("XUI_GetAllHearings_030_ViewAllHearings") {
      
      // exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))
      
      exec(http("XUI_GetAllHearings_030_005_ViewAllHearings")
        .get("/api/hearings/getHearings?caseId=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$.caseRef").is("${caseId}"))
        .check(substring("caseHearings")))
    }
      .pause(MinThinkTime, MaxThinkTime)
  
  
  
  val RequestHearing =
  
  /*======================================================================================
  * Request a hearing - Civil
  ======================================================================================*/
  
  
      feed(UserFeederCivilHearingRequestCases)
    
    .group("Civil_RequestHearing_040_ClickRequestHearing") {
      
      exec(Common.isAuthenticated)
        
        .exec(http("Civil_RequestHearing_040_005_ClickRequestHearing")
          .get("/api/prd/lov/getLovRefData?categoryId=HearingType&serviceId=AAA7&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("category_key")))
        
        .exec(http("Civil_RequestHearing_040_010_ClickRequestHearing")
          .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=739514")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("court_address")))
      
    }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      * Hearing Requirements - Request Hearing -Continue
      ======================================================================================*/
      
      .group("Civil_RequestHearing_050_Requirements") {
        
        exec(http("Civil_RequestHearing_080_005_Requirements")
          .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=CIVIL")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("Content-Type", "application/json")
         // .header("X-Xsrf-Token", "#{XSRFToken}")
          .body(ElFileBody("bodies/hearings/civil/HearingsRequirements.json"))
          .check(substring("hearingLocations"))
          /*
          following values  to parameterise in submit request
           */
          .check(jsonPath("$..hmctsInternalCaseName").saveAs("internalCaseName"))
          .check(jsonPath("$..publicCaseName").saveAs("publicCaseName"))
          .check(jsonPath("$..caseDeepLink").saveAs("caseDeepLink"))
          .check(jsonPath("$..caseManagementLocationCode").saveAs("locationCode"))
          .check(jsonPath("$..caseSLAStartDate").saveAs("SLAStartDate"))
          .check(jsonPath("$..locationId").saveAs("locationId"))
          .check(jsonPath("$..locationType").saveAs("locationType"))
          //extract claimant details
          .check(jsonPath("$.parties[0].partyID").saveAs("claimantPartyId"))
          .check(jsonPath("$.parties[0].partyType").saveAs("claimantPartyType"))
          .check(jsonPath("$.parties[0].partyName").saveAs("claimantPartyName"))
          .check(jsonPath("$.parties[0].partyRole").saveAs("claimantPartyRole"))
          .check(jsonPath("$.parties[0].organisationDetails.name").saveAs("claimantOrgName"))
          .check(jsonPath("$.parties[0].organisationDetails.organisationType").saveAs("claimantOrgType"))
          //extract claimant legal representative details
          .check(jsonPath("$.parties[1].partyID").saveAs("claimantLRPartyId"))
          .check(jsonPath("$.parties[1].partyType").saveAs("claimantLRPartyType"))
          .check(jsonPath("$.parties[1].partyName").saveAs("claimantLRPartyName"))
          .check(jsonPath("$.parties[1].partyRole").saveAs("claimantLRPartyRole"))
          .check(jsonPath("$.parties[1].organisationDetails.name").saveAs("claimantLROrgName"))
          .check(jsonPath("$.parties[1].organisationDetails.organisationType").saveAs("claimantLROrgType"))
          //extract defendent details
          .check(jsonPath("$.parties[2].partyID").saveAs("defPartyId"))
          .check(jsonPath("$.parties[2].partyType").saveAs("defPartyType"))
          .check(jsonPath("$.parties[2].partyName").saveAs("defPartyName"))
          .check(jsonPath("$.parties[2].partyRole").saveAs("defPartyRole"))
          .check(jsonPath("$.parties[2].organisationDetails.name").saveAs("defOrgName"))
          .check(jsonPath("$.parties[2].organisationDetails.organisationType").saveAs("defOrgType"))
          //extract def LR details
          .check(jsonPath("$.parties[3].partyID").saveAs("defLRPartyId"))
          .check(jsonPath("$.parties[3].partyType").saveAs("defLRPartyType"))
          .check(jsonPath("$.parties[3].partyName").saveAs("defLRPartyName"))
          .check(jsonPath("$.parties[3].partyRole").saveAs("defLRPartyRole"))
          .check(jsonPath("$.parties[3].organisationDetails.name").saveAs("defLROrgName"))
          .check(jsonPath("$.parties[3].organisationDetails.organisationType").saveAs("defLROrgType"))
        )
          
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
      What stage is this hearing at? - Application Hearings
      ======================================================================================*/
      
      .group("Civil_RequestHearing_060_HearingStage") {
        
        exec(http("Civil_RequestHearing_060_005_HearingStage")
          .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=AAA7&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      How will each participant attend the hearing? - Prticipant attendance
      ======================================================================================*/
      
      .group("Civil_RequestHearing_070_ParticipantAttend") {
        
        exec(http("Civil_RequestHearing_070_005_ParticipantAttend")
          .get("/api/prd/location/getLocationById?epimms_id=739514")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("site_name")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
what are the hearing venue details
======================================================================================*/
      .group("Civil_RequestHearing_080_HearingVenueDetails") {
        
        exec(http("Civil_RequestHearing_080_005_HearingVenueDetails")
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
      
      
      
     
      
      
      /*======================================================================================
      Enter any additional instructions for the hearing
      ======================================================================================*/
      
      .group("Civil_RequestHearing_090_AdditionalInstructions") {
        
        exec(http("Civil_RequestHearing_090_005_AdditionalInstructions")
          .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=AAA7")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("hearingRelevant")))
          
          .exec(http("Civil_RequestHearing_090_010_AdditionalInstructions")
            .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=AAA7&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingChannel")))
          
          .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(baseDomain).saveAs("XSRFToken")))
          
          
          .exec(http("Civil_RequestHearing_090_015_AdditionalInstructions")
            .get("/api/prd/location/getLocationById?epimms_id=739514")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
      Check your answers
      ======================================================================================*/
      
      .group("Civil_RequestHearing_100_SubmitRequest") {
        
        exec(http("Civil_RequestHearing_100_005_SubmitRequest")
          .post("/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("Content-Type", "application/json")
          .header("X-Xsrf-Token","#{XSRFToken}")
          .body(ElFileBody("bodies/hearings/civil/CivilHearingsRequestSubmit.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest"))
        )
      }
      
      
      .exec { session =>
        val fw = new BufferedWriter(new FileWriter("CivilHearingData.csv", true))
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
      
      .group("Civil_GetHearing_110_GetHearing") {
        
        exec(http("Civil_GetHearing_110_005_GetHearing")
          .get("/api/hearings/getHearing?hearingId=#{civilhearingRequestId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
          .check(substring("requestDetails")))
          
          .exec(Common.isAuthenticated)
        
      }
      .pause(MinThinkTime, MaxThinkTime)
  
  val UpdateHearing =
  
  /*======================================================================================
  * Update Hearing for a single hearing Id
  ======================================================================================*/
   
      feed(UserFeederCivilHearingIdAmend)
      
      .group("Civil_UpdateHearing_120_ClickUpdateHearing") {
        exec(http("Civil_UpdateHearing_120_005_ClickUpdateHearing")
           .get("/api/hearings/getHearing?hearingId=${updateCivilHearingRequestId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
          .check(jsonPath("$..hmctsInternalCaseName").saveAs("internalCaseName"))
          .check(jsonPath("$..publicCaseName").saveAs("publicCaseName"))
          .check(jsonPath("$..caseDeepLink").saveAs("caseDeepLink"))
          .check(jsonPath("$..caseManagementLocationCode").saveAs("locationCode"))
          .check(jsonPath("$..caseSLAStartDate").saveAs("SLAStartDate"))
          .check(jsonPath("$..locationId").saveAs("locationId"))
          .check(jsonPath("$..locationType").saveAs("locationType"))
          //extract claimant details
          .check(jsonPath("$.partyDetails[0].partyID").saveAs("claimantPartyId"))
          .check(jsonPath("$.partyDetails[0].partyType").saveAs("claimantPartyType"))
          .check(jsonPath("$.partyDetails[0].partyRole").saveAs("claimantPartyRole"))
          .check(jsonPath("$.partyDetails[0].organisationDetails.name").saveAs("claimantOrgName"))
          .check(jsonPath("$.partyDetails[0].organisationDetails.organisationType").saveAs("claimantOrgType"))
          //extract claimant legal representative details
          .check(jsonPath("$.partyDetails[1].partyID").saveAs("claimantLRPartyId"))
          .check(jsonPath("$.partyDetails[1].partyType").saveAs("claimantLRPartyType"))
          .check(jsonPath("$.partyDetails[1].partyRole").saveAs("claimantLRPartyRole"))
          .check(jsonPath("$.partyDetails[1].organisationDetails.name").saveAs("claimantLROrgName"))
          .check(jsonPath("$.partyDetails[1].organisationDetails.organisationType").saveAs("claimantLROrgType"))
          //extract defendent details
          .check(jsonPath("$.partyDetails[2].partyID").saveAs("defPartyId"))
          .check(jsonPath("$.partyDetails[2].partyType").saveAs("defPartyType"))
          .check(jsonPath("$.partyDetails[2].partyRole").saveAs("defPartyRole"))
          .check(jsonPath("$.partyDetails[2].organisationDetails.name").saveAs("defOrgName"))
          .check(jsonPath("$.partyDetails[2].organisationDetails.organisationType").saveAs("defOrgType"))
          //extract def LR details
          .check(jsonPath("$.partyDetails[3].partyID").saveAs("defLRPartyId"))
          .check(jsonPath("$.partyDetails[3].partyType").saveAs("defLRPartyType"))
          .check(jsonPath("$.partyDetails[3].partyRole").saveAs("defLRPartyRole"))
          .check(jsonPath("$.partyDetails[3].organisationDetails.name").saveAs("defLROrgName"))
          .check(jsonPath("$.partyDetails[3].organisationDetails.organisationType").saveAs("defLROrgType"))
          
          .check(substring("hearingDetails")))
          
          .exec(Common.isAuthenticated)
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      /*======================================================================================
      * Change 'How many people will attend the hearing in person?'
      ======================================================================================*/
      
      .group("XUI_UpdateHearing_130_UpdateHearing") {
        
        exec(http("XUI_UpdateHearing_130_005_UpdateHearing")
          .get("/api/prd/lov/getLovRefData?categoryId=ChangeReasons&serviceId=AAA7&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("ChangeReasons")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      * Change 'How many people will attend the hearing in person?' to 3 and submit
      ======================================================================================*/
      
      .group("XUI_UpdateHearing_140_SubmitUpdate") {
        exec(http("XUI_UpdateHearing_140_005_SubmitUpdate")
           .put("/api/hearings/updateHearingRequest?hearingId=${updateCivilHearingRequestId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("Content-Type", "application/json")
          .body(ElFileBody("bodies/hearings/civil/CivilAmendHearingSubmit.json"))
          .check(substring("UPDATE_REQUESTED")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
  
  
  val CancelHearing =
    feed(UserFeederCivilHearingIdCancels)
  /*======================================================================================
  * Click on 'Cancel'
  ======================================================================================*/
    
    .group("Civil_CancelHearing_150_CancelHearing") {
      exec(http("XUI_CancelHearing_150_005_ClickCancel")
        .get("/api/prd/lov/getLovRefData?categoryId=CaseManagementCancellationReasons&serviceId=AAA7&isChildRequired=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("CaseManagementCancellationReasons")))
      
    }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      * Click on 'Withdrawn' and then submmit
      ======================================================================================*/
      
      .group("XUI_CancelHearing_160_SubmitCancelHearing") {
        
        exec(Common.isAuthenticated)
          .exec(http("XUI_CancelHearing_160_005_SubmitCancel")
            .delete("/api/hearings/cancelHearings?hearingId=${cancelCivilHearingRequestId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("Content-Type", "application/json")
            .body(ElFileBody("bodies/hearings/civil/CivilHearingsCancel.json"))
            .check(substring("CANCELLATION_REQUESTED")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
}
