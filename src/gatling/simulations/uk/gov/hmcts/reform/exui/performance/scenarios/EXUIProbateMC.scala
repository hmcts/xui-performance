package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.EXUIIACMC.{MaxThinkTime, MinThinkTime}
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.ProbateHeader

object EXUIProbateMC {


  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular

 



  val casedetails = 

    exec(http("XUI${service}_100_005_SearchPage")
			.get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata")
			.headers(ProbateHeader.headers_search))

    .exec(http("XUI${service}_100_010_SearchAccessJurisdictions")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(ProbateHeader.headers_search))

    .exec(http("XUI${service}_100_015_SearchAccessJurisdictions")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(ProbateHeader.headers_search))

    .exec(http("XUI${service}_100_020_SearchResults")
			.get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&page=1")
			.headers(ProbateHeader.headers_search)
      .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))

    .pause(MinThinkTime , MaxThinkTime )

    .foreach("${caseNumbers}","caseNumber") {
      exec(http("XUI${service}_110_ViewCase")
        .get("/data/internal/cases/${caseNumber}")
        .headers(ProbateHeader.headers_viewCase))

      .pause(MinThinkTime , MaxThinkTime )
    }

  val casecreation=
    tryMax(2) {

      exec(http("XUI${service}_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(ProbateHeader.headers_28)
        .check(status.in(200, 304))).exitHereIfFailed
    }
    .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_050_005_StartCreateCase1")
      .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
      .headers(ProbateHeader.headers_casecreate)
      .check(status.in(200,304))
      .check(jsonPath("$.event_token").optional.saveAs("event_token_probate")))

      .exec(http("XUI${service}_050_010_StartCreateCase2")
      .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
      .headers(ProbateHeader.headers_casecreate)
        .check(status.in(200,304)))

    .exec(http("XUI${service}_050_015_CreateCaseProfile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.headers_casefilter)
      .check(status.in(200,304))).exitHereIfFailed
      .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI${service}_060_AddressLookup")
      .get("/api/addresses?postcode=TW33SD")
      .headers(ProbateHeader.headers_28)
      .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)

      .feed(Feeders.createCaseData)
      .exec(http("XUI${service}_070_CreateApplication")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage1")
      .headers(ProbateHeader.headers_casedata)
      .body(ElFileBody("RecordedSimulationcasecreate1810_0088_request.json")).asJson
      .check(status.in(200,304)))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
      .post("/data/case-types/GrantOfRepresentation/drafts/")
      .headers(ProbateHeader.headers_draft)
      .body(ElFileBody("RecordedSimulationprobate1205_0018_request.json")).asJson
      .check(status.is(404)))

      .exec(http("XUI${service}_080_010_DraftProfile")
        .get("/data/internal/profile")
        .headers(ProbateHeader.headers_casedataprofile)
        .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_090_005_CaseSubmitted")
        .post("/data/case-types/GrantOfRepresentation/cases?ignore-warning=false")
        .headers(ProbateHeader.headers_solappcreated)
        .body(ElFileBody("RecordedSimulationprobate1205_0025_request.json")).asJson
        .check(status.in(200,304))
      .check(jsonPath("$.id").optional.saveAs("caseId")))

      .exec(http("XUI${service}_090_010_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(ProbateHeader.headers_saveandviewcase)
        .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)


}
