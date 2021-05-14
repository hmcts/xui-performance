package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Environment, ProbateHeader}

object EXUIProbateMC {


  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL

  val MinThinkTime = Environment.minThinkTimePROB
  val MaxThinkTime = Environment.maxThinkTimePROB


/*======================================================================================
*Business process : Following business process is for Probate Case Creation
======================================================================================*/

/*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are when click on create case on MC
======================================================================================*/

val casecreation=
  
  group("XUI${service}_040_CreateCase") {
    exec(http("XUI${service}_040_CreateCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(ProbateHeader.headers_28)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304))).exitHereIfFailed
  }

  .pause(MinThinkTime, MaxThinkTime)
  
  /*======================================================================================
  *Business process : Following business process is for Probate Case Creation
  * Below group contains all the requests are when satrt create case for probate by selecting jurisdiction as probate and case type as GrantOfRepresentation
    ======================================================================================*/

  .group("XUI${service}_050_StartCreateCase1") {
    exec(http("XUI${service}_050_005_StartCreateCase1")
      .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
      .headers(ProbateHeader.headers_casecreate)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304))
      .check(jsonPath("$.event_token").optional.saveAs("event_token_probate")))

    .exec(http("XUI${service}_050_010_StartCreateCase2")
      .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
      .headers(ProbateHeader.headers_casecreate)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_050_015_CreateCaseProfile")
      .get("/data/internal/profile")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .headers(ProbateHeader.headers_casefilter)
      .check(status.in(200, 304)))
      .exitHereIfFailed
  }

  .pause(MinThinkTime, MaxThinkTime)

 /*======================================================================================
   *Business process : Following business process is for Probate Case Creation
   * Below group contains all the requests are when create application
     ======================================================================================*/      
     
  .feed(Feeders.createCaseData)

  .group("XUI${service}_060_CreateApplication") {
    exec(http("XUI${service}_060_005_CreateApplication")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage1")
      .headers(ProbateHeader.headers_casedata)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateCreateApplication.json")).asJson
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_060_010_CreateApplication")
      .post("/data/internal/case-types/GrantOfRepresentation/drafts/")
      .headers(ProbateHeader.headers_draft)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateCreateApplicationDraft.json")).asJson
      .check(status.in(200, 404, 201))
      .check(jsonPath("$.id").optional.saveAs("draftId")))
  }

.pause(MinThinkTime, MaxThinkTime)

 /*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are address look up for applicant
  ======================================================================================*/ 
  
  .group("XUI${service}_070_AddressLookup") {
    exec(http("XUI${service}_070_AddressLookup")
      .get("/api/addresses?postcode=TW33SD")
      .headers(ProbateHeader.headers_28)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304)))
    }
    
  .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are address look up for applicant
======================================================================================*/

  .group("XUI${service}_080_CreateApplication2") {
    exec(http("XUI${service}_080_005_CreateApplication2")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage2")
      .headers(ProbateHeader.headers_casedata)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateCreateApplication2.json")).asJson
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_080_010_Draft")
      .put("/data/internal/case-types/GrantOfRepresentation/drafts/${draftId}")
      .headers(ProbateHeader.headers_drfts)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateApplication2Draft.json")).asJson
      .check(status.in(400,200,201)))

    .exec(http("XUI${service}_080_015_DraftProfile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.headers_casedataprofile)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304)))
  }
  .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains adetails of case submission
======================================================================================*/

  .group("XUI${service}_090_CaseSubmitted") {
    exec(http("XUI${service}_090_005_CaseSubmitted")
      .post("/data/case-types/GrantOfRepresentation/cases?ignore-warning=false")
      .headers(ProbateHeader.headers_solappcreated)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateCaseSubmitted.json")).asJson
      .check(status.in(200, 304, 201))
      .check(jsonPath("$.id").optional.saveAs("caseId")))

/*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are view case and progression bar tab
======================================================================================*/

  .exec(http("XUI${service}_090_010_ViewCase")
    .get("/data/internal/cases/${caseId}")
    .headers(ProbateHeader.headers_saveandviewcase)
    .header("X-XSRF-TOKEN", "${XSRFToken}")
    .check(status.in(200, 304))
    //.check(regex("Add solicitor details"))
    )
  }
    
  .pause(MinThinkTime, MaxThinkTime)

}
