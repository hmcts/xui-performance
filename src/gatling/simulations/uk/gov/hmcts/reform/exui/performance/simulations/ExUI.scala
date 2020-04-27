package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef.Proxy
import uk.gov.hmcts.reform.exui.performance.scenarios._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils._

import scala.concurrent.duration._

class ExUI extends Simulation {

	val BaseURL = Environment.baseURL
	val httpProtocol = Environment.HttpProtocol
		.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
	//.baseUrl("https://xui-webapp-aat.service.core-compute-aat.internal")
		.baseUrl("https://ccd-case-management-web-perftest.service.core-compute-perftest.internal")

  val XUIHttpProtocol = Environment.HttpProtocol
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    //.baseUrl("https://xui-webapp-aat.service.core-compute-aat.internal")
    //.baseUrl("https://ccd-case-management-web-perftest.service.core-compute-perftest.internal")
    .headers(Environment.commonHeader)


  val IAChttpProtocol = Environment.HttpProtocol
		//.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
		.baseUrl(BaseURL)
		//.baseUrl("https://xui-webapp-perftest.service.core-compute-perftest.internal")
		//.baseUrl("https://ccd-case-management-web-perftest.service.core-compute-perftest.internal")

   // .inferHtmlResources()
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")

	val EXUIScn = scenario("EXUI").repeat(1)
	 {
		exec(
			ExUI.createOrg,
			ExUI.approveOrgHomePage,
			ExUI.approveOrganisationlogin,
			ExUI.approveOrganisationApprove,
			ExUI.approveOrganisationLogout,
			ExUI.manageOrgHomePage,
			ExUI.manageOrganisationLogin,
			ExUI.usersPage,
			ExUI.inviteUserPage
			.repeat(5) {
				exec(ExUI.sendInvitation)
				},
			ExUI.manageOrganisationLogout
			)
	 }

	val EXUIMCaseScn = scenario("EXUI Manage Case").repeat(1)
	{
		exec(
			EXUIManageCase.manageCasesHomePage,
			EXUIManageCase.manageCaseslogin,
			EXUIManageCase.filtercaselist,
			EXUIManageCase.casedetails,
			EXUIManageCase.caseFind,
			EXUIManageCase.manageCase_Logout
		)
	}

  val EXUIMCaseCreationScn = scenario("EXUI Manage Case").repeat(1)
  {
		exec(EXUIManageCaseCreation.manageCasesHomePage)
		.exec(EXUIManageCaseCreation.manageCaseslogin)
		.repeat(1) {
			//EXUIManageCaseCreation.filtercaselist,
			exec(EXUIManageCaseCreation.casecreation)
			.exec(EXUIManageCase.caseFind)
			}
		.exec(EXUIManageCaseCreation.manageCase_Logout)
  }

	val EXUIMCaseCreationIACScn = scenario("EXUI Manage Case IAC").repeat(1)
	{
		exec(EXUIIACMC.manageCasesHomePage)
			.exec(EXUIIACMC.manageCaseslogin)
		//	.exec(EXUIIACMC.termsandconditions_Get)
			.exec(EXUIIACMC.termsnconditions)
      .exec(EXUIIACMC.manageCase_Logout)
		/*.exec(EXUIIACMC.iaccasecreation)
		.exec(EXUIIACMC.manageCase_Logout)*/

	}

	val EXUIMCaseCreationFPLAScn = scenario("EXUI Manage Case FPLA").repeat(1)
	{
		exec(EXUIFPLAMC.manageCasesHomePage)
		.exec(EXUIFPLAMC.manageCasesLogin)
		.exec(EXUIFPLAMC.fplacasecreation)
		.exec(EXUIFPLAMC.manageCasesLogout)
	}

  /*setUp(
		EXUIMCaseCreationFPLAScn.inject(rampUsers(1) during (1 seconds)))
		.protocols(IAChttpProtocol)*/

	setUp(
		EXUIMCaseCreationIACScn.inject(rampUsers(2) during (10 seconds)))
		.protocols(IAChttpProtocol)

  /*setUp(
    EXUIScn.inject(rampUsers(2) during (1 seconds)))
    .protocols(XUIHttpProtocol)*/

}