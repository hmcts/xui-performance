package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Environment, ProbateHeader}

object EXUIProbateMC {


  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular

  val MinThinkTime = Environment.minThinkTimePROB
  val MaxThinkTime = Environment.maxThinkTimePROB


  val casedetails = 

    exec(http("XUI${service}_100_005_SearchInputs")
			.get("/data/internal/case-types/GrantOfRepresentation/work-basket-inputs")
			.headers(ProbateHeader.headers_search)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200,304))
    )

      .exec(http("XUI${service}_100_010_SearchAccessJurisdictions")
            .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
            .headers(ProbateHeader.headers_search)
            .header("X-XSRF-TOKEN", "${XSRFToken}")
            .check(status.in(200,304))
      )


    .exec(http("XUI${service}_100_015_SearchResults")
			.post("/data/internal/searchCases?ctid=GrantOfRepresentation&use_case=WORKBASKET&view=WORKBASKET&state=SolAppCreated&page=1")
			.headers(ProbateHeader.headers_searchresults)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(StringBody("{\n  \"size\": 25\n}"))
      .check(status.in(200,304)))
    .pause(MinThinkTime , MaxThinkTime )

        .exec(http("XUI${service}_110_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(ProbateHeader.headers_viewCase)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

//when click on create case tab
  val casecreation=group("XUI${service}_040_CreateCase") {
    exec(http("XUI${service}_040_CreateCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(ProbateHeader.headers_28)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304))).exitHereIfFailed
  }.pause(MinThinkTime, MaxThinkTime)

    //start of case creation
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

      .feed(Feeders.createCaseData)
      .group("XUI${service}_060_CreateApplication") {
        exec(http("XUI${service}_060_005CreateApplication")
          .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage1")
          .headers(ProbateHeader.headers_casedata)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .body(ElFileBody("RecordedSimulationPro1612_0048_request.json")).asJson
          .check(status.in(200, 304)))

    .exec(http("XUI${service}_060_010_ApplicationDraft")
    .post("/data/internal/case-types/GrantOfRepresentation/drafts/")
    .headers(ProbateHeader.headers_draft)
    .header("X-XSRF-TOKEN", "${XSRFToken}")
    .body(ElFileBody("RecordedSimulationPro1612_0054_request.json")).asJson
    .check(status.in(200, 404, 201))
    .check(jsonPath("$.id").optional.saveAs("draftId")))

}
.pause(MinThinkTime, MaxThinkTime)

.group("XUI${service}_070_AddressLookup") {
  exec(http("XUI${service}_070_AddressLookup")
    .get("/api/addresses?postcode=TW33SD")
    .headers(ProbateHeader.headers_28)
    .header("X-XSRF-TOKEN", "${XSRFToken}")
    .check(status.in(200, 304)))
      }
      .pause(MinThinkTime, MaxThinkTime)

.group("XUI${service}_080_CreateApplication2")
  {
    exec(http("XUI${service}_080_005_CreateApplication2")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage2")
      .headers(ProbateHeader.headers_casedata)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("RecordedSimulationPro1612_0126_request.json")).asJson
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_080_010_Draft")
  .put("/data/internal/case-types/GrantOfRepresentation/drafts/${draftId}")
  .headers(ProbateHeader.headers_drfts)
  .header("X-XSRF-TOKEN", "${XSRFToken}")
  .body(ElFileBody("RecordedSimulationProbatecreate_0142_request.json")).asJson
  .check(status.in(400,200,201)))

    .exec(http("XUI${service}_080_015_DraftProfile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.headers_casedataprofile)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304)))

  }.pause(MinThinkTime, MaxThinkTime)

  .group("XUI${service}_090_CaseSubmitted") {
    exec(http("XUI${service}_090_005_CaseSubmitted")
      .post("/data/case-types/GrantOfRepresentation/cases?ignore-warning=false")    .headers(ProbateHeader.headers_solappcreated)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("RecordedSimulationPro1612_0144_request.json")).asJson
      .check(status.in(200, 304, 201))
      .check(jsonPath("$.id").optional.saveAs("caseId")))
      //below is for view the case details page with progression bar
    .exec(http("XUI${service}_090_010_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(ProbateHeader.headers_saveandviewcase)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304))
        .check(regex("Add solicitor details")))
  }
    .pause(MinThinkTime, MaxThinkTime)

}
