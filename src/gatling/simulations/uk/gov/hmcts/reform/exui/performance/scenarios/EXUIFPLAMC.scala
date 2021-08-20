package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Environment, Common, Headers}

import java.text.SimpleDateFormat
import java.util.Date
import scala.util.Random

object EXUIFPLAMC {

  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL
  val loginFeeder = csv("FPLUserData.csv").circular

  val MinThinkTime = Environment.minThinkTimeFPLC
  val MaxThinkTime = Environment.maxThinkTimeFPLC
  
  val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
  val now = new Date()
  val timeStamp = sdfDate.format(now)
 
  val fplcasecreation =

    //set session variables
    exec(_.setAll(  "firstName"  -> ("Perf" + Common.randomString(5)),
                    "childFirstName" -> ("Child" + Common.randomString(5)),
                    "childLastName" -> ("Test" + Common.randomString(5)),
                    "dobDay" -> Common.getDay(),
                    "dobMonth" -> Common.getMonth(),
                    "dobYearChild" -> Common.getDobYearChild(),
                    "dobYearRes" -> Common.getDobYear(),
                    "respondentFirstName" -> ("Res" + Common.randomString(5)),
                    "respondentLastName" -> ("Test" + Common.randomString(5)),
                    "currentDate" -> timeStamp))
 
    /*======================================================================================
    *Business process : Click On Create Case for FPL
    ======================================================================================*/
  
    .group("XUI_FPL_040_CreateCase") {
      exec(http("XUI_FPL_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(substring("Create Case")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-create%2FPUBLICLAW%2FCARE_SUPERVISION_EPO%2FopenCase"))
    }

    .pause(MinThinkTime , MaxThinkTime)
    
    /*======================================================================================
    *Business process : Select Jurisdiction as Family Law and Casetype as CARE_SUPERVISION_EPO
    ======================================================================================*/

    .group("XUI_FPL_050_005_StartCreateCase") {
      exec(http("XUI_FPL_050_005_StartCreateCase")
        .get("/data/internal/case-types/CARE_SUPERVISION_EPO/event-triggers/openCase?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Start application")))

      .exec(Common.profile)

      .exec(Common.healthcheck("%2Fcases%2Fcase-create%2FPUBLICLAW%2FCARE_SUPERVISION_EPO%2FopenCase%2FopenCaseprovideCaseName"))
    }

    .pause(MinThinkTime , MaxThinkTime)

    /*======================================================================================
    *Business process : Enter case name and click Continue
    ======================================================================================*/
    
    .group("XUI_FPL_060_CaseNameContinue") {
      exec(http("XUI_FPL_060_005_CaseNameContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=openCaseprovideCaseName")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLOpenCase.json"))
        .check(substring("caseName")))

      .exec(Common.profile)
    }
    
    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    *Business process : Click Save and Continue to create the case
    ======================================================================================*/
    
    .group("XUI_FPL_070_CaseNameSaveContinue") {
      exec(http("XUI_FPL_070_005_CaseNameSaveContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLSaveCase.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(substring("created_on")))

      .exec(http("XUI_FPL_070_010_WorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"openCase","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
        .check(status.in(200, 400))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_FPL_070_015_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "${XSRFToken}")
        .check(substring("CCD ID")))
    }
      
    .exec(Common.caseActivityGet)
    .pause(2)
    .exec(Common.caseActivityPost)
      
    .pause(MinThinkTime , MaxThinkTime )

  val fplOrdersNeeded =

    /*======================================================================================
    *Business process : Select Orders and Directions sought case event from the dropdown
    ======================================================================================*/
      
    group("XUI_FPL_080_OrdersDirectionNeededGo") {
      exec(http("XUI_FPL_080_005_OrdersDirectionNeededGo")
        .get("/data/internal/cases/${caseId}/event-triggers/ordersNeeded?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Orders and directions sought")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FordersNeeded"))

      .exec(Common.profile)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FordersNeeded%2FordersNeeded1"))
    }
    
    .exec(Common.caseActivityGet)
    .pause(2)
    .exec(Common.caseActivityPost)

    .pause(MinThinkTime , MaxThinkTime )
  
    /*======================================================================================
    *Business process : Select the top option and click Continue
    ======================================================================================*/
    
    .group("XUI_FPL_090_OrdersDirectionNeededContinue") {
      exec(http("XUI_FPL_090_005_OrdersDirectionNeededContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=ordersNeeded1")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLOrdersNeededAdd.json"))
        .check(substring("emergencyProtectionOrderDetails")))

      .exec(Common.profile)

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FordersNeeded%2Fsubmit"))
    }

    .exec(Common.caseActivityGet)
    .pause(2)
    .exec(Common.caseActivityPost)
    
    .pause(MinThinkTime , MaxThinkTime )

    /*======================================================================================
    *Business process : Review details and click Save and Continue
    ======================================================================================*/
    
    .group("XUI_FPL_100_OrdersDirectionNeededSaveContinue") {
      exec(http("XUI_FPL_100_005_OrdersDirectionNeededSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/fpl/FPLOrdersNeededSubmit.json"))
        .check(substring("last_modified_on")))

      .exec(http("XUI_FPL_100_010_WorkAllocation")
        .post("/workallocation/searchForCompletable")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"ordersNeeded","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
        .check(status.in(200, 400))
        .check(substring("tasks")))

      .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_FPL_100_015_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "${XSRFToken}")
        .check(substring("""event_id":"ordersNeeded""")))
    }

    .exec(Common.caseActivityGet)
    .pause(2)
    .exec(Common.caseActivityPost)
      
    .pause(MinThinkTime , MaxThinkTime )

  val fplHearingNeeded =

  /*======================================================================================
  *Business process : Select Hearing urgency from the dropdown
  ======================================================================================*/
    
  group("XUI_FPL_110_HearingNeededGo") {
    exec(http("XUI_FPL_110_005_HearingNeededGo")
      .get("/data/internal/cases/${caseId}/event-triggers/hearingNeeded?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(substring("Hearing urgency")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FhearingNeeded"))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FhearingNeeded%2FhearingNeeded1"))

    .exec(Common.profile)
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Select desired check boxes and click on Continue
  ======================================================================================*/
    
  .group("XUI_FPL_120_HearingNeededContinue") {
    exec(http("XUI_FPL_120_005_HearingNeededContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=hearingNeeded1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLHearingNeededAdd.json"))
      .check(substring("Within 18 days")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FhearingNeeded%2Fsubmit"))

    .exec(Common.profile)
  }

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review details and click Save and Continue
  ======================================================================================*/
    
  .group("XUI_FPL_130_HearingNeededSaveContinue") {
    exec(http("XUI_FPL_130_005_HearingNeededSaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLHearingNeededSubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_130_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"hearingNeeded","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_130_010_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"hearingNeeded""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

val fplChildDetails =

  /*======================================================================================
  *Business process : Select Child details from the dropdown
  ======================================================================================*/
  
  group("XUI_FPL_140_ChildrenGo") {
    exec(http("XUI_FPL_140_005_ChildrenGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterChildren?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(Common.savePartyId)
      .check(Common.saveId)
      .check(substring("Entering the children for the case")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterChildren%2FenterChildren1"))

    .exec(Common.profile)
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Enter Children details and click on Continue
  ======================================================================================*/

  .group("XUI_FPL_150_ChildrenContinue") {
    exec(http("XUI_FPL_150_005_ChildrenContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterChildren1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLChildDetailsAdd.json"))
      .check(substring("finalOrderIssuedType")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterChildren%2Fsubmit"))

    .exec(Common.profile)
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review details and click on Save and Continue
  ======================================================================================*/
    
  .group("XUI_FPL_160_ChildrenSaveContinue") {
    exec(http("XUI_FPL_160_005_ChildrenSaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLChildDetailsSubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_160_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"enterChildren","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_160_015_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"enterChildren""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

val fplEnterRespondents = 

  /*======================================================================================
  *Business process : Select Enter Respondents from the dropdown
  ======================================================================================*/
    
  group("XUI_FPL_170_RespondentsGo") {
    exec(http("XUI_FPL_170_005_RespondentsGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterRespondents?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(Common.savePartyId)
      .check(Common.saveId)
      .check(substring("Entering the respondents for the case")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterRespondents"))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterRespondents%2FenterRespondents1"))

    .exec(Common.caseShareOrgs)
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
      
  .pause(MinThinkTime , MaxThinkTime)

  /*======================================================================================
  *Business process : Enter required details and click Continue
  ======================================================================================*/

  .exec(Common.postcodeLookup)

  .pause(MinThinkTime , MaxThinkTime)
  
  .group("XUI_FPL_180_RespondentsContinue") {
    exec(http("XUI_FPL_180_005_RespondentsContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterRespondents1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLEnterRespondentsAdd.json"))
      .check(substring("livingSituation")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterRespondents%2Fsubmit"))

    .exec(Common.profile)
    
  }
  
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review details and click Save and Continue
  ======================================================================================*/
  
  .group("XUI_FPL_190_RespondentsSaveContinue") {
    exec(http("XUI_FPL_190_005_RespondentsSaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLEnterRespondentsSubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_190_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"enterRespondents","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_190_015_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"enterRespondents""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

val fplEnterApplicant =

  /*======================================================================================
  *Business process : Select Applicant Details from the dropdownh
  ======================================================================================*/
    
  group("XUI_FPL_200_ApplicantGo") {
    exec(http("XUI_FPL_200_005_ApplicantGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterApplicant?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(Common.savePartyId)
      .check(Common.saveId)
      .check(substring("Entering the applicant for the case")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterApplicant"))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterApplicant%2FenterApplicant1"))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
  
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Enter postcode and search
  ======================================================================================*/
    
  .exec(Common.postcodeLookup)

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : As part of the FPL Case Creation there are different steps
  ======================================================================================*/
    
  .group("XUI_FPL_210_ApplicantContinue") {
    exec(http("XUI_FPL_210_005_ApplicantContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterApplicant1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLApplicantDetailsAdd.json"))
      .check(substring("additionalNeeds")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterApplicant%2Fsubmit"))

    .exec(Common.profile)
  }

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review details and click Save and Continue
  ======================================================================================*/
    
  .group("XUI_FPL_220_ApplicantSaveContinue") {
    exec(http("XUI_FPL_220_005_ApplicantSaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLApplicantDetailsSubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_220_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"enterApplicants","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_220_015_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"enterApplicant""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

val fplEnterGrounds =

  /*======================================================================================
  *Business process : Select Enter Grounds from the dropdown
  ======================================================================================*/
    
  group("XUI_FPL_230_GroundApplicationGo") {
    exec(http("XUI_FPL_230_005_GroundApplicationGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterGrounds?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(substring("Grounds for the application")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterGrounds"))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterGrounds%2FenterGrounds1"))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
  
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Select the desired reason and click on Continue
  ======================================================================================*/

  .group("XUI_FPL_240_GroundApplicationContinue") {
    exec(http("XUI_FPL_240_005_GroundApplicationContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterGrounds1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLEnterGroundsAdd.json"))
      .check(substring("thresholdReason")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterGrounds%2Fsubmit"))

    .exec(Common.profile)
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review details and click Save and Continue
  ======================================================================================*/

  .group("XUI_FPL_250_GroundApplicationSaveContinue") {
    exec(http("XUI_FPL_250_005_GroundApplicationSaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLEnterGroundsSubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_250_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"enterGrounds","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_250_015_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"enterGrounds""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

val fplAllocationProposal =

  /*======================================================================================
  *Business process : Select Allocation Proposal from the dropdown
  ======================================================================================*/
  
  group("XUI_FPL_260_AllocationProposalGo") {
    exec(http("XUI_FPL_260_005_AllocationProposalGo")
      .get("/data/internal/cases/${caseId}/event-triggers/otherProposal?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(substring("Allocation proposal")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FotherProposal"))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FotherProposal%2FotherProposal1"))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Select relevant checkboxes and click Continue
  ======================================================================================*/
    
  .group("XUI_FPL_270_AllocationProposalContinue") {
    exec(http("XUI_FPL_270_005_AllocationProposalContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=otherProposal1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLAllocationProposalAdd.json"))
      .check(substring("District judge")))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FotherProposal%2Fsubmit"))
  }

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review page details and click Save and Continue
  ======================================================================================*/
    
  .group("XUI_FPL_280_AllocationProposalSaveContinue") {
    exec(http("XUI_FPL_280_005_AllocationProposalSaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLAllocationProposalSubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_280_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"otherProposal","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_280_015_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"otherProposal""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

val fplUploadDocuments =

  /*======================================================================================
  *Business process : Select Application Documents from the dropdown
  ======================================================================================*/

  group("XUI_FPL_290_010_DocumentsUploadPage") {
    exec(http("XUI_FPL_290_010_DocumentsUploadPage")
      .get("/data/internal/cases/${caseId}/event-triggers/uploadDocuments?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(substring("Application documents")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FuploadDocuments"))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FuploadDocuments%2FuploadDocumentsaddApplicationDocuments"))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
  
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Click Add New and upload the Document
  ======================================================================================*/

  .group("XUI_FPL_300_UploadFile") {
    exec(http("XUI_FPL_300_UploadFile")
      .post("/documentsv2")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .header("content-type", "multipart/form-data")
      .header("x-xsrf-token", "${XSRFToken}")
      .bodyPart(RawFileBodyPart("files", "3MB.pdf")
      .fileName("3MB.pdf")
      .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "CARE_SUPERVISION_EPO")
      .formParam("jurisdictionId", "PUBLICLAW")
      .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("documentHash")))
  }

  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Enter a description and click Continue
  ======================================================================================*/

  .group("XUI_FPL_310_DocumentsContinue") {
    exec(http("XUI_FPL_310_005_DocumentsContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=uploadDocumentsaddApplicationDocuments")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLUploadDocumentAdd.json"))
      .check(substring("applicationDocumentsToFollowReason")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FuploadDocuments%2Fsubmit"))

    .exec(Common.profile)
  }
    
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review details and click Save and Continue
  ======================================================================================*/
    
  .group("XUI_FPL_320_DocumentsSaveContinue") {
    exec(http("XUI_FPL_320_005_DocumentsSaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLUploadDocumentSubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_320_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"uploadDocuments","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_320_015_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"uploadDocuments""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
  
  .pause(MinThinkTime , MaxThinkTime )

val fplLocalAuthority = 

  /*======================================================================================
  *Business process : Select Local Authority from the dropdown
  ======================================================================================*/

  group("XUI_FPL_330_LocalAuthorityPage") {
    exec(http("XUI_FPL_330_LocalAuthorityPage")
      .get("/data/internal/cases/${caseId}/event-triggers/enterLocalAuthority?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.name").ofType[Any].saveAs("laName"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.id").ofType[Any].saveAs("laId"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.AddressLine1").saveAs("laAddressLine1"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.AddressLine2").saveAs("laAddressLine2"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.AddressLine3").saveAs("laAddressLine3"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.PostTown").saveAs("laPostTown"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.County").saveAs("laCounty"))
      .check(jsonPath("$.case_fields[?(@.id=='localAuthority')].value.address.PostCode").saveAs("laPostcode"))
      .check(substring("Local authority's details"))
      // .check(bodyString.saveAs("BODY"))
      )

    // .exec(session => {
    //   val response = session("BODY").as[String]
    //   println(s"Response body: \n$response")
    //   session
    // })

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterLocalAuthority"))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterLocalAuthority%2FenterLocalAuthorityDetails"))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
  
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Enter a description and click Continue
  ======================================================================================*/

  .group("XUI_FPL_340_LocalAuthorityContinue") {
    exec(http("XUI_FPL_340_LocalAuthorityContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterLocalAuthorityDetails")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLLocalAuthorityAdd.json"))
      .check(substring("colleagues")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterLocalAuthority%2FenterLocalAuthorityColleagues"))

    .exec(Common.profile)
  }
    
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Add Colleague details and click Continue
  ======================================================================================*/

  .group("XUI_FPL_350_LocalAuthorityAddColleague") {
    exec(http("XUI_FPL_350_LocalAuthorityAddColleague")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterLocalAuthorityColleagues")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLLocalAuthorityColleagueAdd.json"))
      .check(jsonPath("$.data.localAuthorityColleagues[0].id").saveAs("laColleagueId"))
      .check(substring("localAuthority")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FenterLocalAuthority%2Fsubmit"))

    .exec(Common.profile)
  }
    
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Review details and click Save and Continue
  ======================================================================================*/
    
  .group("XUI_FPL_360_LocalAuthoritySaveContinue") {
    exec(http("XUI_FPL_360_LocalAuthoritySaveContinue")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLLocalAuthoritySubmit.json"))
      .check(substring("${caseId}"))
      .check(substring("last_modified_on")))

    .exec(http("XUI_FPL_360_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"uploadDocuments","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

    .exec(http("XUI_FPL_360_015_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"uploadDocuments""")))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
  
  .pause(MinThinkTime , MaxThinkTime )

val fplSubmitApplication =

  /*======================================================================================
  *Business process : Select Submit Application from the dropdown
  ======================================================================================*/
    
  group("XUI_FPL_370_SubmitApplicationGo") {
    exec(http("XUI_FPL_370_005_SubmitApplicationGo")
      .get("/data/internal/cases/${caseId}/event-triggers/submitApplication?ignore-warning=false")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(regex("""document_filename":"(.+?).pdf""").saveAs("DocumentName"))
      .check(regex("""document_url":"(.*?)","document_filename""").saveAs("DocumentURL"))
      .check(regex("""formatted_value":"I, (.+?), believe that""").saveAs("applicantName"))
      .check(substring("Submit application")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitApplication"))

    .exec(Common.profile)

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitApplication%2FsubmitApplication1"))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)
  
  .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : Confirm details and click Continue
  ======================================================================================*/
    
  .group("XUI_FPL_380_SubmitApplicationContinue") {
    exec(http("XUI_FPL_380_005_SubmitApplicationContinue")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=submitApplication1")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLSubmitApplicationAdd.json"))
      .check(substring("submissionConsent")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitApplication%2Fsubmit"))

    .exec(Common.profile)
  }

  .pause(MinThinkTime , MaxThinkTime )

/*======================================================================================
Business process : Submit the Application
======================================================================================*/
    
  .group("XUI_FPL_390_ApplicationSubmitted") {
    exec(http("XUI_FPL_390_005_ApplicationSubmitted")
      .post("/data/cases/${caseId}/events")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/fpl/FPLSubmitApplicationSubmit.json"))
      .check(substring("""state":"Submitted"""")))

    .exec(http("XUI_FPL_390_010_WorkAllocation")
      .post("/workallocation/searchForCompletable")
      .headers(Headers.commonHeader)
      .header("accept", "application/json")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(StringBody("""{"searchRequest":{"ccdId":"${caseId}","eventId":"submitApplication","jurisdiction":"PUBLICLAW","caseTypeId":"CASE_SUPERVISION_EPO"}}"""))
      .check(status.in(200, 400))
      .check(substring("tasks")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitApplication%2Fconfirm"))
  }

  .pause(MinThinkTime , MaxThinkTime )

/*======================================================================================
Business process : Click on the Close and Return to case button
======================================================================================*/

  .group("XUI_FPL_400_ViewCase") {
    exec(http("XUI_FPL_400_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .check(substring("""event_id":"submitApplication""")))

    .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))
  }

  .exec(Common.caseActivityGet)
  .pause(2)
  .exec(Common.caseActivityPost)

  .pause(MinThinkTime , MaxThinkTime )

}