package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.FR_Header._

object EXUI_FR extends Simulation {

	val login =
		exec(http("XUILogin_000_Login1")
		.post(idamURL + "/login?client_id=xuiwebapp&redirect_uri=http://manage-case.demo.platform.hmcts.net/oauth2/callback&state=5DEQMxTHXkvHWyXJqJftDwNJTZ4eQ-Y4WKaVINdH7Rw&nonce=SPbwFgiEt6VcppYvNHRHyUW1EO8qOCg39qW-PZLPSqk&response_type=code&scope=profile%20openid%20roles%20manage-user%20create-user&prompt=")
		.headers(headers_0)
		.formParam("username", "fr_applicant_sol@sharklasers.com")
		.formParam("password", "Testing1234")
		.formParam("save", "Sign in")
		.formParam("selfRegistrationEnabled", "false")
		.formParam("_csrf", "35097630-0bd7-44f9-bc58-28cf0efaeb14")
			.check(status in (200,304)))

		.exec(http("XUILogin_010_Login2")
			.get("/external/configuration-ui/")
			.headers(headers_5)
			.check(status in (200,304)))

		.exec(http("XUILogin_020_Login3")
			.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
			.headers(headers_6)
			.check(status in (200,304)))

		.exec(http("XUILogin_030_Login4")
			.get("/auth/isAuthenticated")
			.headers(headers_6)
			.check(status in (200,304)))

		.exec(http("XUILogin_040_Login5")
			.get("/api/user/details")
			.headers(headers_6)
			.check(status in (200,304)))

		.exec(http("XUILogin_050_Login6")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(headers_20)
			.check(status in (200,304)))

		.exec(http("XUILogin_060_MonitoringTools1")
			.get("/api/monitoring-tools")
			.headers(headers_6)
			.check(status in (200,304)))

		.exec(http("XUILogin_070_MonitoringTools2")
			.get("/api/monitoring-tools")
			.headers(headers_6)
			.check(status in (200,304)))

		.exec(http("XUILogin_080_MonitoringTools3")
			.get("/api/monitoring-tools")
			.headers(headers_6)
			.check(status in (200,304)))

		.exec(http("XUILogin_090_MonitoringTools4")
			.get("/api/monitoring-tools")
			.headers(headers_6)
			.check(status in (200,304)))
			.pause(minThinkTime, maxThinkTime)

	val createCase =
	exec(http("XUI_000_CreateCase1")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
			.headers(headers_29)
		.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

	.exec(http("XUI_010_CreateCase2")
			.get("/data/internal/case-types/FinancialRemedyMVP2/event-triggers/FR_solicitorCreate?ignore-warning=false")
			.headers(headers_35)
		.check(status in (200,304)))

		.exec(http("XUI_020_CreateCase3")
			.get("/data/internal/case-types/FinancialRemedyMVP2/event-triggers/FR_solicitorCreate?ignore-warning=false")
			.headers(headers_38)
			.check(status in (200,304)))

	.exec(http("XUI_030_CreateCase4")
			.get("/data/internal/profile")
			.headers(headers_40)
		.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_040_CreateSolicitor1")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate1")
			.headers(headers_41)
			.body(RawFileBody("RecordedSimulationFR_0041_request.txt"))
			.check(status in (200,304)))

	.exec(http("XUI_050_GetOrganisations1")
			.get("/api/caseshare/orgs")
			.headers(headers_43)
		.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_060_GetAddress1")
			.get("/api/addresses?postcode=SW1H9AJ")
			.headers(headers_45)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_070_CreateSolicitor2")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate2")
			.headers(headers_48)
			.body(RawFileBody("RecordedSimulationFR_0048_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_080_CreateSolicitor3")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate3")
			.headers(headers_52)
			.body(RawFileBody("RecordedSimulationFR_0052_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_090_CreateSolicitor4")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate4")
			.headers(headers_54)
			.body(RawFileBody("RecordedSimulationFR_0054_request.txt"))
			.check(status in (200,304)))

		.exec(http("XUI_100_GetOrganisations2")
			.get("/api/caseshare/orgs")
			.headers(headers_56)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_110_GetPostcode2")
			.get("/api/addresses?postcode=SW1H9AJ")
			.headers(headers_62)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_120_CreateSolicitor5")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate5")
			.headers(headers_64)
			.body(RawFileBody("RecordedSimulationFR_0064_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_130_CreateSolicitor6")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate6")
			.headers(headers_65)
			.body(RawFileBody("RecordedSimulationFR_0065_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_140_GetDocuments1")
			.post("/documents")
			.headers(headers_72)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_150_CreateSolicitor8")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate8")
			.headers(headers_73)
			.body(RawFileBody("RecordedSimulationFR_0073_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_160_GetDocuments2")
			.post("/documents")
			.headers(headers_75)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_160_CreateSolicitor9")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate9")
			.headers(headers_76)
			.body(RawFileBody("RecordedSimulationFR_0076_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_170_CreateSolicitor11")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate11")
			.headers(headers_78)
			.body(RawFileBody("RecordedSimulationFR_0078_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_180_CreateSolicitor12")
			.post("/data/case-types/FinancialRemedyMVP2/validate?pageId=FR_solicitorCreate12")
			.headers(headers_81)
			.body(RawFileBody("RecordedSimulationFR_0081_request.txt"))
			.check(status in (200,304)))

		.exec(http("XUI_190_CreateCase5")
			.get("/data/internal/profile")
			.headers(headers_83)
			.check(status in (200,304)))

		.exec(http("XUI_200_GetOrganisations2")
			.get("/api/caseshare/orgs")
			.headers(headers_84)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_210_CreateCase6")
			.post("/data/case-types/FinancialRemedyMVP2/cases?ignore-warning=false")
			.headers(headers_87)
			.body(RawFileBody("RecordedSimulationFR_0087_request.txt"))
			.check(status in (200,304)))

		.exec(http("XUI_220_GetCases")
			.get("/data/internal/cases/1603271365614201")
			.headers(headers_89)
			.check(status in (200,304)))

	val logout =
		exec(http("XUI_230_Logout")
			.get("/auth/logout")
			.headers(headers_92)
			.check(status in (200,304)))

}