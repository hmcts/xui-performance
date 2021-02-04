package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.FR_Respondent_Header._

object EXUI_FR_Respondent {

	val minThinkTime = Environment.minThinkTimeFR
	val maxThinkTime = Environment.maxThinkTimeFR

	val manageOrgURL=manageOrdURL
	
	/*======================================================================================
*Business process : As part of the create FR respondent application share a case
* Below group contains all the share the unassigned case
======================================================================================*/
	val shareCase = scenario("ShareCase")
		.group("XUI${service}_030_UnassignedCases") {
			exec(http("XUI${service}_030_UnassignedCases")
				.post(manageOrgURL + "/api/unassignedCaseTypes")
				.headers(headers_37)
				.check(status in(200, 304)))
		}
		.pause(minThinkTime, maxThinkTime)
	
/*======================================================================================
*Business process : As part of the create FR respondent application share a case
* Below group contains all the share the unassigned case -FR Consented
======================================================================================*/
		
		.group("XUI${service}_040_FRConsented") {
			exec(http("XUI${service}_040_FRConsented")
				.post(manageOrgURL + "/api/unassignedcases?caseTypeId=FinancialRemedyConsentedRespondent")
				.headers(headers_38)
				.check(status in(200, 304)))
		}
		.pause(minThinkTime, maxThinkTime)/*======================================================================================
*Business process : As part of the create FR Respondent application share a case
* Below group contains all the share the unassigned case by caseId
======================================================================================*/
		
		
		.group("XUI${service}_050_ShareCaseByCaseId") {
			exec(http("XUI${service}_050_ShareCaseByCaseId")
				.get(manageOrgURL + "/api/caseshare/cases?case_ids=${caseId}")
				.headers(headers_9)
				.check(status in(200, 304)))
		}
		.pause(minThinkTime, maxThinkTime)
	
/*======================================================================================
*Business process : As part of the create FR Respondent application share a case
* Below group contains all the share the unassigned case- share case users
======================================================================================*/
		.group("XUI${service}_060_ShareCaseUsers") {
			exec(http("XUI${service}_060_ShareCaseUsers")
				.get(manageOrgURL + "/api/caseshare/users")
				.headers(headers_9)
				.check(jsonPath("$..email").saveAs("email"))
				.check(jsonPath("$..firstName").saveAs("firstName"))
				.check(jsonPath("$..idamId").saveAs("idamId"))
				.check(jsonPath("$..lastName").saveAs("lastName"))
				.check(status in(200, 304)))
		}
		.pause(minThinkTime, maxThinkTime)
	
/*======================================================================================
*Business process : As part of the create FR Respondent application share a case
* Below group contains all the share the unassigned case-final assignment of share a case
======================================================================================*/
		
		.group("XUI${service}_070_ShareCaseAssignments") {
			exec(http("XUI${service}_070_ShareCaseAssignments")
				.post(manageOrgURL + "/api/caseshare/case-assignments")
				.headers(headers_54)
				.body(StringBody(
					"""{
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
				.check(status in(201, 304)))
		}
		.pause(minThinkTime, maxThinkTime)

}
