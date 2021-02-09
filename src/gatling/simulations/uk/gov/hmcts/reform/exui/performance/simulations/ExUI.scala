package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
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
	val feedUserDataFR = csv("FRSolicitorData.csv").circular
	val feedUserDataRJ = csv("FRRespondentData.csv").circular
	val feedUserDataCaseworker = csv("Caseworkers.csv").circular
	val feedUserDataDivorce = csv("DivorceUserData.csv").circular
	val feedUserDataFPLCases = csv("FPLCases.csv").circular
	val caseFeederProbate = csv("CaseworkerSearchesProbate.csv").circular
	val caseFeederDivorce = csv("CaseworkerSearchesDivorce.csv").circular
	val caseFeederFR = csv("CaseworkerSearchesFR.csv").circular
	val caseFeederIAC = csv("CaseworkerSearchesIAC.csv").circular
	val caseFeederFPL = csv("CaseworkerSearchesFPL.csv").circular

	// below Http Protocol is for the scenario - manage org
  val XUIHttpProtocol = Environment.HttpProtocol
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .baseUrl(orgurl)
    .headers(Environment.commonHeader)

	//Below Http Protocol will be used for all services

  val MChttpProtocol = Environment.HttpProtocol
		//.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
		.baseUrl(BaseURL).inferHtmlResources().silentResources
		//.baseUrl("https://xui-webapp-perftest.service.core-compute-perftest.internal")
		//.baseUrl("https://ccd-case-management-web-perftest.service.core-compute-perftest.internal")

   // .inferHtmlResources()
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
	
	
	/*===============================================================================================
	* below scenario is for create org, approve org and manage org related business process
	 ==================================================================================================*/

	val EXUIScn = scenario("EXUI").repeat(1)
	 {
		exec(
		S2SHelper.S2SAuthToken,
		ExUI.createSuperUser,
		ExUI.createOrg,
      ExUI.approveOrgHomePage,
		ExUI.approveOrganisationlogin,
			ExUI.approveOrganisationApprove,
			ExUI.approveOrganisationLogout,
			ExUI.manageOrgHomePage,
			ExUI.manageOrganisationLogin,
			ExUI.usersPage,
			ExUI.inviteUserPage
			.repeat(4,"n") {
				exec(ExUI.sendInvitation)
				},
			ExUI.manageOrganisationLogout
			)
	 }
	
	
	/*===============================================================================================
	* below scenario is for Probate Business Process related scenario
	 ==================================================================================================*/
  val EXUIMCaseProbateScn = scenario("***** Probate Case Journey ******").repeat(1)
  {
		feed(feedUserDataProbate).feed(Feeders.ProDataFeeder)
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
		//	.exec(EXUIMCLogin.termsnconditions)
		.repeat(2) {
			exec(EXUIProbateMC.casecreation)
			}
		.exec(EXUIMCLogin.manageCase_Logout)
  }
	
	/*===============================================================================================
	* below scenario is for IAC  Business Process related scenario
	 ==================================================================================================*/
	val EXUIMCaseCreationIACScn = scenario("***** IAC Create Case *****").repeat(1)
	{
	  	feed(feedUserDataIACCreate).feed(Feeders.IACCreateDataFeeder)
	  	.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
		//	.exec(EXUIMCLogin.termsnconditions)
		  	.repeat(2) {
					exec(EXUIIACMC.iaccasecreation)
						.exec(EXUIIACMC.shareacase)
				}

		.exec(EXUIMCLogin.manageCase_Logout)
	}
	
	/*===============================================================================================
	* below scenario is for Divorce Business Process related scenario
	 ==================================================================================================*/
	val EXUIMCaseCreationDivorceScn = scenario("***** Div Create Case *****").repeat(1)
	{
		feed(feedUserDataDivorce).feed(Feeders.DivDataFeeder)
		.exec(EXUIMCLogin.manageCasesHomePage)
		.exec(EXUIMCLogin.manageCaseslogin)
		//	.exec(EXUIMCLogin.termsnconditions)
		.repeat(2) {
      exec(EXUIDivorceMC.casecreation)
      	.exec(EXUIDivorceMC.shareacase)
    }
		.exec(EXUIMCLogin.manageCase_Logout)
	}
	
	/*===============================================================================================
	* below scenario is for FPLA Business Process related scenario
	 ==================================================================================================*/
	val EXUIMCaseCreationFPLAScn = scenario("***** FPLA Create Case ***** ").repeat(1)
	{
		feed(feedUserDataFPLCreate).feed(Feeders.FPLCreateDataFeeder)
	  	.exec(EXUIMCLogin.manageCasesHomePage)
		.exec(EXUIMCLogin.manageCaseslogin)
			//.exec(EXUIMCLogin.termsnconditions)
		  	.repeat(2) {
					exec(EXUIFPLAMC.fplacasecreation)
				}
		.exec(EXUIMCLogin.manageCase_Logout)
	}

	
	/*===============================================================================================
	* below scenario is for search and view case as a case worker
	 ==================================================================================================*/

	val EXUIMCaseCaseworkerScn = scenario("***** Caseworker Journey ******").repeat(1)
  {
		feed(feedUserDataCaseworker).feed(Feeders.CwDataFeeder)
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.caseworkerLogin)
		.repeat(4) {
				randomSwitch(
					3d -> exec(feed(caseFeederIAC)),//iac
					39d -> exec(feed(caseFeederProbate)),//probate
					39d -> exec(feed(caseFeederDivorce)),//divorce
					1d -> exec(feed(caseFeederFPL)),//fpl
					18d -> exec(feed(caseFeederFR))//FR
				)
			exec(EXUICaseWorker.ApplyFilters)
		  	.exec(EXUICaseWorker.ApplySort)
				.exec(EXUICaseWorker.ClickFindCase)
			.exec(EXUICaseWorker.ViewCase)
			}
		.exec(EXUIMCLogin.manageCase_Logout)
  }
	
	/*===============================================================================================
	* below scenario is for Financial Remedy Business Process related scenario
	 ==================================================================================================*/

	val EXUIFinancialRemedyScn = scenario("Scenario FR").repeat(2)
	{	 feed(feedUserDataFR)
		.feed(Feeders.FRApplicantDataFeeder)
		.exec(EXUIMCLogin.manageCasesHomePage)
		.exec(EXUIMCLogin.manageCaseslogin)
		.exec(EXUI_FR_Applicant.createCase)
		.exec(EXUIMCLogin.manageCase_Logout)
	  	/*.pause(20)
		.feed(Feeders.FRRespondentDataFeeder)
	  	.feed(feedUserDataRJ)
		.exec(EXUIMCLogin.manageOrgHomePage)
		.exec(EXUIMCLogin.manageOrglogin)
		.exec(EXUI_FR_Respondent.shareCase)
		.exec(EXUIMCLogin.manageOrg_Logout)*/
		

	}
	
	/*===============================================================================================
	* Below setup is to do the smoke test to make sure all the scripts are working for one user
	 ==================================================================================================*/

	/*setUp(
		EXUIMCaseCreationDivorceScn.inject(nothingFor(5),rampUsers(1) during (3)),
		EXUIMCaseCaseworkerScn.inject(rampUsers(1) during 1),
		EXUIMCaseProbateScn.inject(nothingFor(5),rampUsers(1) during (3)),
		EXUIMCaseCreationIACScn.inject(nothingFor(15),rampUsers(1) during (3)),
		EXUIMCaseCreationFPLAScn.inject(nothingFor(35),rampUsers(1) during (2)),
		EXUIFinancialRemedyScn.inject(atOnceUsers(1)).protocols(MChttpProtocol))
}
*/
	
	
	/*===============================================================================================
	* Below setup  is to do the smoke test to make sure manage org is working, we can uncomment it when we use it
	 ==================================================================================================*/
	/*setUp(
		EXUIScn.inject(rampUsers(209) during (3600))
			.protocols(XUIHttpProtocol)
	)*/
	
	
	/*===============================================================================================
	* Below setup  is to do the smoke test to make sure one particular scenario  is working as part of sanity test
	 ==================================================================================================*/
	 setUp(
		 EXUIMCaseCaseworkerScn.inject(rampUsers(1) during (3)),
	  // EXUIMCaseCaseworkerScn.inject(nothingFor(20),rampUsers(1) during (1))
	 )
      .protocols(MChttpProtocol)
	
	
	/*===============================================================================================
  * Below setup  is for actual test to be run on VM and for reporting, below numbers needs changing as per the agreed load model  nd also need adjust the think times accordingly
   ==================================================================================================*/
	
	/* setUp(
		 EXUIMCaseProbateScn.inject(nothingFor(5),rampUsers(238) during (1200)),
		 EXUIMCaseCreationIACScn.inject(nothingFor(15),rampUsers(20) during (1200)),
		 EXUIMCaseCreationFPLAScn.inject(nothingFor(35),rampUsers(7) during (1200)),
		 EXUIMCaseCaseworkerScn.inject(nothingFor(55),rampUsers(900) during (1200)),
		 EXUIMCaseCreationDivorceScn.inject(nothingFor(65),rampUsers(238) during (1200)),
		 EXUIFinancialRemedyScn.inject(nothingFor(75),rampUsers(98) during (1200))
        ).protocols(MChttpProtocol)*/


	

 



}
