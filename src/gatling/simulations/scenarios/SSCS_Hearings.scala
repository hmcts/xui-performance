package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers,CsrfCheck}
import java.io.{BufferedWriter, FileWriter}
import scala.util.Random

/*======================================================================================
* Create a new Bail application as a professional user (e.g. Legal Rep)
======================================================================================*/

object SSCS_Hearings {
  
  val BaseURL = Environment.baseURL
  
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val SSCSUserFeederHearingRequestCases = csv("SSCSUserDataHearingsCasesViewHearings.csv").circular
  val UserFeederHearingId = csv("SSCSHearingIds.csv").circular
  val UserFeederHearingIdCancels = csv("SSCSHearingIdCancels.csv").circular
  val SSCSUserFeederHearingIdAmend = csv("SSCSHearingIdAmend.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90
  
  /*======================================================================================
       Below is the View All Hearings
       ======================================================================================*/
  
  val ViewAllHearings =
    
    feed(SSCSUserFeederHearingRequestCases)
      
      /*======================================================================================
      * Select the Case you want to view
      ======================================================================================*/
      
      .group("SSCS_GetAllHearings_030_ViewAllHearings") {
        
          exec(http("SSCS_GetAllHearings_030_005_ViewAllHearings")
            .get("/api/hearings/getHearings?caseId=${caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
            .check(jsonPath("$.caseRef").is("${caseId}"))
            .check(substring("caseHearings")))
      }
      .pause(MinThinkTime, MaxThinkTime)
  
  
  /*======================================================================================
   * Request a hearing
   ======================================================================================*/
  
  val RequestHearing =
  
    
        feed(SSCSUserFeederHearingRequestCases)
          /*======================================================================================
           * Click On Request a hearing
           ======================================================================================*/
      .group("SSCS_RequestHearing_040_ClickRequestHearing") {
        exec(Common.isAuthenticated)
          
          .exec(http("SSCS_RequestHearing_040_005_ClickRequestHearing")
            .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("FlagDetails")))
          
          .exec(http("SSCS_RequestHearing_040_010_ClickRequestHearing")
            .get(BaseURL + "/api/prd/location/getLocationById?epimms_id=372653")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      * Hearing Requirements - Continue
      ======================================================================================*/
      
      .group("SSCS_RequestHearing_050_Requirements") {
        
        exec(http("SSCS_RequestHearing_050_005_Requirements")
          .post(BaseURL + "/api/hearings/loadServiceHearingValues?jurisdictionId=SSCS")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/sscs/HearingsRequirements.json"))
          .check(substring("hearing-requirements")))
          
          .exec(http("SSCS_RequestHearing_050_010_Requirements")
            .get(BaseURL + "/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("childFlags")))
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      Do you require any additional facilities?
      ======================================================================================*/
      
      /*======================================================================================
      What stage is this hearing at? - Substantive
      ======================================================================================*/
      
      .group("SSCS_RequestHearing_60_HearingStage") {
        
        exec(http("SSCS_RequestHearing_60_005_HearingStage")
          //   .get("/api/prd/lov/getLovRefData?category=HearingChannel&service=BBA3&isChildRequired=N")
          .get("/api/prd/lov/getLovRefData?categoryId=Facilities&serviceId=BBA3&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("category_key")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      How will each participant attend the hearing? - In Person, 1
      ======================================================================================*/
      
      .group("SSCS_RequestHearing_70_ParticipantAttend") {
        
        exec(http("SSCS_RequestHearing_70_005_ParticipantAttend")
          .get("/api/prd/location/getLocationById?epimms_id=372653")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("Open")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      Select length, date and priority level of hearing - 45 mins,
      ======================================================================================*/
      
     
        .group("SSCS_RequestHearing_80_Linked") {
          
          exec(http("SSCS_RequestHearing_80_005_Linked")
            .post("/api/hearings/loadServiceLinkedCases?jurisdictionId=SSCS")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .formParam("jurisdictionId", "SSCS")
            .body(ElFileBody("bodies/hearings/sscs/HearingsLength.json"))
            .check(status.is(200)))
          
        }
          .pause(MinThinkTime, MaxThinkTime)
      


/*======================================================================================
Enter any additional instructions for the hearing
======================================================================================*/
  
      /*======================================================================================
         Enter any additional instructions for the hearing
         ======================================================================================*/
  
      .group("SSCS_RequestHearing_90_AdditionalInstructions") {
    
        exec(http("SSCS_RequestHearing_90_005_AdditionalInstructions")
          .get("/api/prd/caseFlag/getCaseFlagRefData?serviceId=BBA3")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("CATGRY")))
      
          .exec(http("SSCS_RequestHearing_90_010_AdditionalInstructions")
            .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=BBA3&isChildRequired=N")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("HearingChannel")))
      
          .exec(http("SSCS_RequestHearing_900_015_AdditionalInstructions")
            .get("/api/prd/location/getLocationById?epimms_id=372653")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("court_address")))
    
      }
      .pause(MinThinkTime, MaxThinkTime)
  
      /*======================================================================================
      Check your answers
      ======================================================================================*/
  
      .group("SSCS_RequestHearing_100_SubmitRequest") {
        //   doIfOrElse(session => session("hearings-percentage").as[Int] < hearingPercentage) {
        exec(http("SSCS_RequestHearing_100_005_SubmitRequest")
          .post("/api/hearings/submitHearingRequest")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .header("Content-Type", "application/json")
          .header("X-Xsrf-Token", "#{XSRFToken}")
          .body(ElFileBody("bodies/hearings/sscs/SSCSHearingsRequestSubmit.json"))
          .check(jsonPath("$.hearingRequestID").saveAs("hearingRequest")))
          
          .exec { session =>
            val fw = new BufferedWriter(new FileWriter("SSCSHearingData.csv", true))
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
    
    feed(UserFeederHearingId)
      
      .group("SSCS_GetHearing_110_GetHearing") {
        
        exec(http("SSCS_GetHearing_110_005_GetHearing")
          .get("/api/hearings/getHearing?hearingId=#{hearingRequestId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(jsonPath("$.requestDetails.versionNumber").saveAs("versionNumber"))
          .check(substring("otherReasonableAdjustmentDetails")))
          
          .exec(Common.isAuthenticated)
      }
      .pause(MinThinkTime, MaxThinkTime)
  
  val UpdateHearing =
  
  /*======================================================================================
  * Get a singular case
  ======================================================================================*/
    
    feed(SSCSUserFeederHearingIdAmend)
      
      .group("SSCS_UpdateHearing_120_GetHearing") {
        
        exec(http("SSCS_UpdateHearing_120_005_GetHearing")
          .get("/api/hearings/getHearing?hearingId=${updateSSCSHearingRequestId}")
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
      
      .group("SSCS_UpdateHearing_130_UpdateHearing") {
        
        exec(http("SSCS_UpdateHearing_130_005_UpdateHearing")
          .get("/api/prd/lov/getLovRefData?categoryId=HearingChannel&serviceId=BBA3&isChildRequired=N")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("HearingChannel")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      * Change 'How many people will attend the hearing in person?' to 2 and submit
      ======================================================================================*/
      
      .group("SSCS_UpdateHearing_140_SubmitUpdateHearing") {
        exec(http("SSCS_UpdateHearing_140_005_SubmitUpdate")
          .put("/api/hearings/updateHearingRequest?hearingId=${updateSSCSHearingRequestId}")
          .headers(Headers.commonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/hearings/sscs/AmendHearingSubmit.json"))
          .check(substring("UPDATE_REQUESTED")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
  
  
  val cancelHearing =
  
  /*======================================================================================
  * Click on 'Cancel'
  ======================================================================================*/
    
    group("SSCS_DeleteHearing_150_DeleteHearing") {
      
      feed(UserFeederHearingIdCancels)
        
        .exec(Common.isAuthenticated)
      
    }
      .pause(MinThinkTime, MaxThinkTime)
      
      
      /*======================================================================================
      * Click on 'Withdrawn' and then submmit
      ======================================================================================*/
      
      .group("SSCS_DeleteHearing_160_DeleteHearingSubmit") {
        
        exec(Common.isAuthenticated)
          
          .exec(http("SSCS_DeleteHearing_160_005_SubmitDeleteHearing")
            .delete("/api/hearings/cancelHearings?hearingId=${cancelSSCSHearingRequestId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
            .formParam("jurisdictionId", "SSCS")
            .body(ElFileBody("bodies/hearings/sscs/SSCSHearingsCancel.json"))
            .check(substring("CANCELLATION_REQUESTED")))
        
      }
      .pause(MinThinkTime, MaxThinkTime)
}