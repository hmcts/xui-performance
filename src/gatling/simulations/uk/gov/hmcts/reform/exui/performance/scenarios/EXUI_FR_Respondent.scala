package uk.gov.hmcts.reform.exui.performance.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.FR_Respondent_Header._

object EXUI_FR_Respondent extends Simulation {

	val shareCase = scenario("ShareCase")
		.exec(http("XUI${service}_030_UnassignedCases1")
			.post("/api/unassignedCaseTypes")
			.headers(headers_37)
		.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_040_UnassignedCases2")
			.post("/api/unassignedcases?caseTypeId=FinancialRemedyConsentedRespondent")
			.headers(headers_38)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_050_ShareCase1")
			.get("/api/caseshare/cases?case_ids=${caseId}")
			.headers(headers_9)
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_060_ShareCase2")
			.get("/api/caseshare/users")
			.headers(headers_9)
			.check(jsonPath("$..email").saveAs("email"))
			.check(jsonPath("$..firstName").saveAs("firstName"))
			.check(jsonPath("$..idamId").saveAs("idamId"))
			.check(jsonPath("$..lastName").saveAs("lastName"))
			.check(status in (200,304)))
		.pause(minThinkTime, maxThinkTime)

		.exec(http("XUI${service}_070_ShareCase3")
			.post("/api/caseshare/case-assignments")
			.headers(headers_54)
			.body(StringBody("""{
					 |    "sharedCases":
					 |    [
					 |        {"caseId":"${caseId}",
					 |        "caseTitle":"Baker Vs Parker",
					 |        "caseTypeId":"FinancialRemedyConsentedRespondent",
					 |        "pendingShares":
					 |        [
					 |            {"email":"${email}",
					 |            "firstName":"${firstName}",
					 |            "idamId":"${idamId}",
					 |            "lastName":"${lastName}"}
					 |        ],
					 |        "pendingUnshares":[]}
					 |    ]
					 |}""".stripMargin))
			.check(status in (201,304)))
		.pause(minThinkTime, maxThinkTime)

}
