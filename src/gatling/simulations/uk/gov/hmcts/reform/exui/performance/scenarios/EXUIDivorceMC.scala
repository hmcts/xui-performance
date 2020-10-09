package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{DivorceHeader, Environment, ProbateHeader}

object EXUIDivorceMC {


  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular

  val MinThinkTime = Environment.minThinkTimePROB
  val MaxThinkTime = Environment.maxThinkTimePROB



  val casedetails = 

    exec(http("XUI${service}_100_005_SearchPage")
			.get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata")
			.headers(ProbateHeader.headers_search)
      .check(status.in(200,304))
    )

    .exec(http("XUI${service}_100_010_SearchAccessJurisdictions")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(ProbateHeader.headers_search)
      .check(status.in(200,304))
    )

    .exec(http("XUI${service}_100_015_SearchAccessJurisdictions")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(ProbateHeader.headers_search)
      .check(status.in(200,304))
    )

    .exec(http("XUI${service}_100_020_SearchResults")
			.get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&page=1")
			.headers(ProbateHeader.headers_search)
      .check(status.in(200,304)))
    .pause(MinThinkTime , MaxThinkTime )

        .exec(http("XUI${service}_110_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(ProbateHeader.headers_viewCase)
      .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )


  val casecreation=
    tryMax(2) {
      exec(http("XUI${service}_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(DivorceHeader.headers_accessCreate)
        .check(status.in(200, 304)))
    }
    .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_050_005_StartCreateCase1")
      .get("/data/internal/case-types/DIVORCE_XUI/event-triggers/solicitorCreate?ignore-warning=false")
      .headers(DivorceHeader.headers_27)
      .check(status.in(200,304))
      .check(jsonPath("$.event_token").optional.saveAs("event_token")))

      .exec(http("XUI${service}_050_010_StartCreateCase2")
      .get("/data/internal/case-types/DIVORCE_XUI/event-triggers/solicitorCreate?ignore-warning=false")
      .headers(DivorceHeader.headers_27)
        .check(status.in(200,304)))

    .exec(http("XUI${service}_050_015_CreateCaseProfile")
      .get("/data/internal/profile")
      .headers(DivorceHeader.headers_31)
      .check(status.in(200,304))).exitHereIfFailed

      .exec(http("XUI${service}_050_015_ShareCaseOrg")
            .get("/api/caseshare/orgs")
            .headers(DivorceHeader.headers_shareorgs)
            .check(status.in(200,304)))

      .pause(MinThinkTime, MaxThinkTime)


      .exec(http("XUI${service}_070_CreateApplication")
      .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreatesolicitorCreate")
      .headers(DivorceHeader.headers_soldata)
      .body(ElFileBody("RecordedSimulationSC_0123_request.json")).asJson
      .check(status.in(200,304)))

      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_SolicitorDetails")
      .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolAboutTheSolicitor")
        .headers(DivorceHeader.headers_aboutsol)
      .body(ElFileBody("RecordedSimulationSC_0127_request.json")).asJson
      .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_PetitionerDetails")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolAboutThePetitioner")
            .headers(DivorceHeader.headers_aboutpetitioner)
            .body(ElFileBody("RecordedSimulationSC_0132_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_RespondantDetails")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolAboutTheRespondent")
            .headers(DivorceHeader.headers_headerRespondant)
            .body(ElFileBody("RecordedSimulationSC_0136_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolMarriageCertificate")
            .headers(DivorceHeader.headers_marriagecertificate)
            .body(ElFileBody("RecordedSimulationSC_0141_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolJurisdiction")
            .headers(DivorceHeader.headers_createjurisdiction)
            .body(ElFileBody("RecordedSimulationSC_0145_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolReasonForDivorce")
            .headers(DivorceHeader.headers_reasonfordiv)
            .body(ElFileBody("RecordedSimulationSC_0150_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolSOCBehaviour1")
            .headers(DivorceHeader.headers_behaviour)
            .body(ElFileBody("RecordedSimulationSC_0155_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolExistingCourtCases")
            .headers(DivorceHeader.headers_courtcases)
            .body(ElFileBody("RecordedSimulationSC_0160_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolDividingMoneyAndProperty")
            .headers(DivorceHeader.headers_devideprops)
            .body(ElFileBody("RecordedSimulationSC_0165_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolApplyToClaimCosts")
            .headers(DivorceHeader.headers_claimcosts)
            .body(ElFileBody("RecordedSimulationSC_0170_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreateSolUploadDocs")
            .headers(DivorceHeader.headers_upload)
            .body(ElFileBody("RecordedSimulationSC_0175_request.json")).asJson
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorCreatelangPref")
            .headers(DivorceHeader.headers_langpref)
            .body(ElFileBody("RecordedSimulationSC_0180_request.json")).asJson
            .check(status.is(200)))

      .exec(http("XUI${service}_080_005_InternalProfile")
      .get("/data/internal/profile")
      .headers(DivorceHeader.headers_internalprofile)
        .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("XUI${service}_080_005_ApplicationDraftfinal")
            .post("/data/case-types/DIVORCE_XUI/cases?ignore-warning=false")
            .headers(DivorceHeader.headers_ignorewarning)
            .body(ElFileBody("RecordedSimulationSC_0186_request.json")).asJson
            .check(status.is(200))
      .check(jsonPath("$.id").optional.saveAs("caseId")))

    .exec(http("request_58")
  .get("/data/internal/cases/${caseId}")
  .headers(DivorceHeader.headers_datainternal)
      .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)


    /*.exec(http("request_59")
  .get("/api/monitoring-tools")
  .headers(DivorceHeader.headers_2)
      .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)


      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .get("/data/internal/cases/1600677150585534/event-triggers/solicitorStatementOfTruthPaySubmit?ignore-warning=false")
            .headers(DivorceHeader.headers_65)
            .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("event_token_submit")))

      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .get("/data/internal/profile")
            .headers(DivorceHeader.headers_67)
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("request_69")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorStatementOfTruthPaySubmitSolStatementOfTruth")
            .headers(DivorceHeader.headers_69)
            .body(RawFileBody("RecordedSimulationDiv_0069_request.json"))
        .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("request_73")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorStatementOfTruthPaySubmitSolPayment")
            .headers(DivorceHeader.headers_73)
            .body(RawFileBody("RecordedSimulationDiv_0073_request.json"))
        .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("request_75")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorStatementOfTruthPaySubmitSolPaymentSummary")
            .headers(DivorceHeader.headers_75)
            .body(RawFileBody("RecordedSimulationDiv_0075_request.json"))
        .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("request_78")
            .post("/data/case-types/DIVORCE_XUI/validate?pageId=solicitorStatementOfTruthPaySubmitSolSummary")
            .headers(DivorceHeader.headers_78)
            .body(RawFileBody("RecordedSimulationDiv_0078_request.json"))
        .check(status.is(200)))
      .exec(http("XUI${service}_080_005_ApplicationDraft")
            .get("/data/internal/profile")
            .headers(DivorceHeader.headers_82)
            .check(status.is(200)))
      .pause(MinThinkTime, MaxThinkTime)

      .exec(http("request_83")
            .post("/data/cases/1600677150585534/events")
            .headers(DivorceHeader.headers_83)
            .body(RawFileBody("RecordedSimulationDiv_0083_request.json"))
            .check(status.is(200)))
      .exec(http("XUI${service}_080_005_85")
            .get("/data/internal/cases/1600677150585534")
            .headers(DivorceHeader.headers_85)
            .check(status.is(200)))
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
      .pause(MinThinkTime, MaxThinkTime)*/


  val shareacase =
      exec(http("XUI${service}_040_005_ShareACase")
    .get("/api/caseshare/cases?case_ids=${caseId}")
   .headers(DivorceHeader.headers_shareacasebyid)
    .check(status.in(200,304)))

    .exec(http("XUI${service}_040_010_ShareACase")
  .get("/api/caseshare/users")
  .headers(DivorceHeader.headers_shareacasesuserslist)
      .check(status.in(200,304)))
  .pause(7)

    .exec(http("request_39")
  .post("/api/caseshare/case-assignments")
 .headers(DivorceHeader.headers_shareacaseconfirm)
      .body(ElFileBody("RecordedSimulationSC_0229_request.json")).asJson
      .check(status.in(200,304)))
  .pause(8)




}
