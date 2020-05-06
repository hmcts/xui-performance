package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef.Proxy
import uk.gov.hmcts.reform.exui.performance.scenarios._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils._

class ExUI extends Simulation {

	val BaseURL = Environment.baseURL
	val orgurl=Environment.manageOrdURL
	val feedUserDataIACView = csv("IACDataView.csv").circular
	val feedUserDataFPLView = csv("FPLDataView.csv").circular
	val feedUserDataIACCreate = csv("IACDataCreate.csv").circular
	val feedUserDataFPLCreate = csv("FPLDataCreate.csv").circular
	val feedUserDataProbate = csv("ProbateUserData.csv").circular

	val httpProtocol = Environment.HttpProtocol
		.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
	//.baseUrl("https://xui-webapp-aat.service.core-compute-aat.internal")
		.baseUrl("https://ccd-case-management-web-perftest.service.core-compute-perftest.internal")

  val XUIHttpProtocol = Environment.HttpProtocol
   // .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .baseUrl(orgurl)
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
			.repeat(2) {
				exec(ExUI.sendInvitation)
				},
			ExUI.manageOrganisationLogout
			)
	 }


  val EXUIMCaseProbateScn = scenario("EXUI Manage Case").repeat(1)
  {
		feed(feedUserDataProbate)
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
			.exec(EXUIMCLogin.termsnconditions)
		.repeat(1) {
			//EXUIManageCaseCreation.filtercaselist,
			exec(EXUIManageCaseCreation.casecreation)
			.exec(EXUIManageCaseCreation.casedetails)
			}
		.exec(EXUIMCLogin.manageCase_Logout)
  }

	val EXUIMCaseCreationIACScn = scenario("IAC Create Case").repeat(1)
	{
	  	feed(feedUserDataIACCreate)
	  	.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
			.exec(EXUIMCLogin.termsnconditions)
		  	.repeat(2) {
					exec(EXUIIACMC.iaccasecreation)
						.exec(EXUIIACMC.shareacase)
				}

		.exec(EXUIMCLogin.manageCase_Logout)
	}

	val EXUIMCaseViewIACScn = scenario("IAC View Case").repeat(1)
	{
		feed(feedUserDataIACView)
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
			.exec(EXUIMCLogin.termsnconditions)
			.exec(EXUIIACMC.findandviewcase)
			.exec(EXUIMCLogin.manageCase_Logout)
	}

	val EXUIMCaseCreationFPLAScn = scenario("FPLA Create Case").repeat(1)
	{
		feed(feedUserDataFPLCreate)
	  	.exec(EXUIMCLogin.manageCasesHomePage)
		.exec(EXUIMCLogin.manageCaseslogin)
			.exec(EXUIMCLogin.termsnconditions)
		  	.repeat(2) {
					exec(EXUIFPLAMC.fplacasecreation)
				}
		.exec(EXUIMCLogin.manageCase_Logout)
	}

	val EXUIMCaseViewFPLAScn = scenario("FPLA View Case").repeat(1)
	{
		feed(feedUserDataFPLView)
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
			.exec(EXUIMCLogin.termsnconditions)
			.exec(EXUIFPLAMC.findandviewcasefpl)
			.exec(EXUIMCLogin.manageCase_Logout)
	}

	/*setUp(
		EXUIScn.inject(rampUsers(75) during (1800))
			.protocols(XUIHttpProtocol)
	)*/
	 /*setUp(
		 EXUIMCaseProbateScn.inject(rampUsers(1) during (1)))
      .protocols(IAChttpProtocol)*/

  /*setUp(
		EXUIMCaseCreationFPLAScn.inject(rampUsers(1) during (3)))
		.protocols(IAChttpProtocol)*/
	/*setUp(
		EXUIMCaseViewIACScn.inject(rampUsers(1) during (1)))
		.protocols(IAChttpProtocol)*/


	/*setUp(
		EXUIMCaseCreationIACScn.inject(rampUsers(1) during (6)))
		.protocols(IAChttpProtocol)*/
  setUp(
		nothingFor(5),
    EXUIScn.inject(rampUsers(131) during (3000 seconds)),
	nothingFor(15),
		EXUIMCaseCreationIACScn.inject(rampUsers(82) during (3000 seconds)),
	nothingFor(25),
		EXUIMCaseViewIACScn.inject(rampUsers(74) during (3000 seconds)),
	nothingFor(35),
		EXUIMCaseCreationFPLAScn.inject(rampUsers(38) during (3000 seconds)),
	nothingFor(45),
		EXUIMCaseViewFPLAScn.inject(rampUsers(19) during (3000 seconds))
	).protocols(IAChttpProtocol)
}
