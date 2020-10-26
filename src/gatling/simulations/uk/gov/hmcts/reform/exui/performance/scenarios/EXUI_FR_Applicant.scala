package uk.gov.hmcts.reform.exui.performance.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.FR_Applicant_Header._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment._

object EXUI_FR_Applicant extends Simulation {

	val createCase = scenario("CreateCase")
		.exec(http("XUI${service}_030_CreateCase1")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
			.headers(headers_2)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_040_CreateCase2")
			.get("/data/internal/case-types/FinancialRemedyConsentedRespondent/event-triggers/FR_solicitorCreate?ignore-warning=false")
			.headers(headers_6)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_050_CreateCase3")
			.get("/data/internal/case-types/FinancialRemedyConsentedRespondent/event-triggers/FR_solicitorCreate?ignore-warning=false")
			.headers(headers_8)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_060_CreateCase4")
			.get("/data/internal/profile")
			.headers(headers_9)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_070_CreateSolicitor1")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate1")
			.headers(headers_10)
			.body(RawFileBody("RecordedSimulationFRApplicant_0010_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_080_GetAddress1")
			.get("/api/addresses?postcode=SW1H9AJ")
			.headers(headers_15)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_090_CreateSolicitor2")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate2")
			.headers(headers_18)
			.body(RawFileBody("RecordedSimulationFRApplicant_0018_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_100_CreateSolicitor3")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate3")
			.headers(headers_22)
			.body(RawFileBody("RecordedSimulationFRApplicant_0022_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_110_CreateSolicitor4")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate4")
			.headers(headers_26)
			.body(RawFileBody("RecordedSimulationFRApplicant_0026_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_120_GetOrganisations")
			.get("/api/caseshare/orgs")
			.headers(headers_27)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_130_GetAddress2")
			.get("/api/addresses?postcode=SW1H9AJ")
			.headers(headers_31)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_140_CreateSolicitor5")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate5")
			.headers(headers_34)
			.body(RawFileBody("RecordedSimulationFRApplicant_0034_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_150_CreateSolicitor6")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate6")
			.headers(headers_37)
			.body(RawFileBody("RecordedSimulationFRApplicant_0037_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_160_GetDocuments1")
			.post("/documents")
			.headers(headers_41)
			.body(RawFileBody("RecordedSimulationFRApplicant_0041_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_170_CreateSolicitor8")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate8")
			.headers(headers_44)
			.body(RawFileBody("RecordedSimulationFRApplicant_0044_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_180_GetDocuments2")
			.post("/documents")
			.headers(headers_48)
			.body(RawFileBody("RecordedSimulationFRApplicant_0048_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_190_CreateSolicitor9")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate9")
			.headers(headers_51)
			.body(RawFileBody("RecordedSimulationFRApplicant_0051_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_200_CreateSolicitor11")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate11")
			.headers(headers_55)
			.body(RawFileBody("RecordedSimulationFRApplicant_0055_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_210_CreateSolicitor12")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/validate?pageId=FR_solicitorCreate12")
			.headers(headers_59)
			.body(RawFileBody("RecordedSimulationFRApplicant_0059_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_220_CreateCase5")
			.get("/data/internal/profile")
			.headers(headers_61)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_230_CreateCase6")
			.post("/data/case-types/FinancialRemedyConsentedRespondent/cases?ignore-warning=false")
			.headers(headers_64)
			.body(RawFileBody("RecordedSimulationFRApplicant_0064_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_240_GetCases")
			.get("/data/internal/cases/1603460169541294")
			.headers(headers_66)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

}