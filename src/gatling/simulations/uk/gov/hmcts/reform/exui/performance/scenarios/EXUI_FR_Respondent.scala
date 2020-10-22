package uk.gov.hmcts.reform.exui.performance.scenarios


import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.FR_Respondent_Header._

object EXUI_FR_Respondent extends Simulation {

	val login =
		exec(http("XUILogin_000_Login1")
			.post(idamURL + "/login?response_type=code&redirect_uri=http%3A%2F%2Fmanage-org.demo.platform.hmcts.net%2Foauth2%2Fcallback&scope=profile%20openid%20roles%20manage-user%20create-user%20manage-roles&state=3NKjEUXE46HLb-71fsQ_DepmQUqKeepn94fFRc8hIkU&client_id=xuimowebapp")
			.headers(headers_0)
			.formParam("username", "udaydemo1@mailinator.com")
			.formParam("password", "Welcome01")
			.formParam("save", "Sign in")
			.formParam("selfRegistrationEnabled", "false")
			.formParam("_csrf", "35097630-0bd7-44f9-bc58-28cf0efaeb14")
			.check(status in (200,304)))

			.exec(http("XUILogin_010_Login2")
			.get("/external/configuration-ui/")
			.headers(headers_5)
				.check(status in (200,304)))

            .exec(http("XUILogin_020_Login3")
			.get("/api/user/details")
			.headers(headers_9)
							.check(status in (200,304)))

            .exec(http("XUILogin_030_Login4")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
							.check(status in (200,304)))

            .exec(http("XUILogin_040_Login5")
			.get("/api/user/details")
			.headers(headers_9)
							.check(status in (200,304)))

            .exec(http("XUILogin_050_Login6")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
							.check(status in (200,304)))

            .exec(http("XUILogin_060_Login7")
			.get("/external/configuration?configurationKey=feature.termsAndConditionsEnabled")
			.headers(headers_9)
							.check(status in (200,304)))

            .exec(http("XUILogin_070_GetOrganisations")
			.get("/api/organisation")
			.headers(headers_9)
							.check(status in (200,304)))
			.pause(minThinkTime, maxThinkTime)

	val shareCase =
	exec(http("XUI_000_Authentication1")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
		.check(status in (200,304)))

			.exec(http("XUI_010_Authentication2")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
				.check(status in (200,304)))

	.exec(http("XUI_020_UnassignedCases1")
			.post("/api/unassignedCaseTypes")
			.headers(headers_37)
		.check(status in (200,304)))


		.exec(http("XUI_030_UnassignedCases2")
			.post("/api/unassignedcases?caseTypeId=FinancialRemedyConsentedRespondent")
			.headers(headers_38)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

			.exec(http("XUI_040_UnassignedCases3")
			.post("/api/unassignedcases?caseTypeId=FinancialRemedyMVP2")
			.headers(headers_40)
				.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

			.exec(http("XUI_050_Authentication3")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
				.check(status in (200,304)))


		.exec(http("XUI_060_ShareCase1")
			.get("/api/caseshare/cases?case_ids=1603271365614201")
			.headers(headers_9)
			.check(status in (200,304)))

		.exec(http("XUI_070_ShareCase2")
			.get("/api/caseshare/users")
			.headers(headers_9)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_080_Authentication4")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI_090_Authentication5")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
			.check(status in (200,304)))

		.exec(http("XUI_100_ShareCase3")
			.post("/api/caseshare/case-assignments")
			.headers(headers_54)
			.body(RawFileBody("RecordedSimulationFRRespondent_0054_request.txt"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

	val logout =
		exec(http("XUI_110_Authentication6")
			.get("/auth/isAuthenticated")
			.headers(headers_9)
			.check(status in (200,304)))

			.exec(http("XUI_1200_Logout")
			.get("/auth/logout")
			.headers(headers_59)
				.check(status in (200,304)))

}