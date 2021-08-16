package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils._
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.core.pause.PauseType

import scala.concurrent.duration._

class ExUI extends Simulation {

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

	/* TEST TYPE DEFINITION */
	/* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
	/* perftest (default) = performance test against the perftest environment */
	val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

	//set the environment based on the test type
	val environment = testType match{
		case "perftest" => "perftest"
		//TODO: UPDATE PIPELINE TO 'aat' ONCE DATA STRATEGY IS IMPLEMENTED. UNTIL THEN, PIPELINE WILL RUN AGAINST PERFTEST
		case "pipeline" => "perftest"
		case _ => "**INVALID**"
	}
	/* ******************************** */

	/* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
	val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
	val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
	/* ******************************** */

	/* PERFORMANCE TEST CONFIGURATION */
	val probateTargetPerHour = 238
	val iacTargetPerHour = 20
	val fplaTargetPerHour = 7
	val divorceTargetPerHour = 238
	val frTargetPerHour = 98
	val caseworkerTargetPerHour = 900

	val rampUpDurationMins = 20
	val testDurationMins = 60

	val numberOfPipelineUsers = 5
	val pipelinePausesMillis:Long = 3000 //3 seconds

	//Determine the pause pattern to use:
	//Performance test = use the pauses defined in the script
	//Pipeline = override pauses in the script with a fixed value (pipelinePauseMillis)
	//Debug mode = disable all pauses
	val pauseOption:PauseType = debugMode match{
		case "off" if testType == "perftest" => constantPauses
		case "off" if testType == "pipeline" => customPauses(pipelinePausesMillis)
		case _ => disabledPauses
	}

	// below Http Protocol is for the scenario - manage org
  val XUIHttpProtocol = http
    .baseUrl(Environment.manageOrdURL.replace("${env}", s"${env}"))
    .headers(Headers.navigationHeader)

	//Below Http Protocol will be used for all services

  val MChttpProtocol = http
		.baseUrl(Environment.baseURL.replace("${env}", s"${env}"))
		.inferHtmlResources()
		.silentResources

	before{
		exec(_.set("env", s"${env}"))
		println(s"Test Type: ${testType}")
		println(s"Test Environment: ${env}")
		println(s"Debug Mode: ${debugMode}")
	}

	/*===============================================================================================
	* below scenario is for create org, approve org and manage org related business process
	 ==================================================================================================*/

	val EXUIScn = scenario("EXUI").repeat(1)
	 {
		 exitBlockOnFail {
			 exec(_.set("env", s"${env}"))
			 .exec(
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
			 )
			 .repeat(4, "n") {
				 exec(ExUI.sendInvitation)
			 }
			 .exec(
				 ExUI.manageOrganisationLogout
			 )
		 }
	 }
	
	
	/*===============================================================================================
	* XUI Solicitor Probate Scenario
	 ==================================================================================================*/
  val EXUIMCaseProbateScn = scenario("***** Probate Case Journey ******").repeat(1)
  {
		exitBlockOnFail {
			feed(feedUserDataProbate)
				.exec(_.set("service", "Probate")
							.set("env", s"${env}"))
				.exec(EXUIMCLogin.manageCasesHomePage)
				.exec(EXUIMCLogin.manageCaseslogin)
				//	.exec(EXUIMCLogin.termsnconditions)
				.repeat(2) {
					exec(EXUIProbateMC.CreateProbateCase)
					.exec(EXUIProbateMC.AddDeceasedDetails)
					.exec(EXUIProbateMC.AddApplicationDetails)
					.exec(EXUIProbateMC.ReviewAndSubmitApplication)
				}
				.exec(EXUIMCLogin.manageCase_Logout)
		}
  }
	
	/*===============================================================================================
	* XUI Solicitor IAC Scenario
	 ==================================================================================================*/
	val EXUIMCaseCreationIACScn = scenario("***** IAC Create Case *****").repeat(1)
	{
		exitBlockOnFail {
			feed(feedUserDataIACCreate)
				.exec(_.set("service", "IAC")
							.set("env", s"${env}"))
				.exec(EXUIMCLogin.manageCasesHomePage)
				.exec(EXUIMCLogin.manageCaseslogin)
				//	.exec(EXUIMCLogin.termsnconditions)
				.repeat(2) {
					exec(EXUIIACMC.iaccasecreation)
						.exec(EXUIIACMC.shareacase)
				}
				.exec(EXUIMCLogin.manageCase_Logout)
		}
	}
	
	/*===============================================================================================
	* below scenario is for Divorce Business Process related scenario
	 ==================================================================================================*/
	val EXUIMCaseCreationDivorceScn = scenario("***** Div Create Case *****").repeat(1)
	{
		exitBlockOnFail {
			feed(feedUserDataDivorce).feed(Feeders.DivDataFeeder)
			.exec(_.set("env", s"${env}"))
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
			//	.exec(EXUIMCLogin.termsnconditions)
			.repeat(2) {
				exec(EXUIDivorceMC.casecreation)
					.exec(EXUIDivorceMC.shareacase)
			}
			.exec(EXUIMCLogin.manageCase_Logout)
		}
	}
	
	/*===============================================================================================
	* below scenario is for FPLA Business Process related scenario
	 ==================================================================================================*/
	val EXUIMCaseCreationFPLAScn = scenario("***** FPLA Create Case ***** ").repeat(1)
	{
		exitBlockOnFail {
			feed(feedUserDataFPLCreate)
				.feed(Feeders.FPLCreateDataFeeder)
				.exec(_.set("env", s"${env}"))
				.exec(EXUIMCLogin.manageCasesHomePage)
				.exec(EXUIMCLogin.manageCaseslogin)
				.repeat(2) {
					exec(EXUIFPLAMC.fplcasecreation)
						.exec(EXUIFPLAMC.fplOrdersNeeded)
						.exec(EXUIFPLAMC.fplHearingNeeded)
						.exec(EXUIFPLAMC.fplChildDetails)
						.exec(EXUIFPLAMC.fplEnterRespondents)
						.exec(EXUIFPLAMC.fplEnterApplicant)
						.exec(EXUIFPLAMC.fplEnterGrounds)
						.exec(EXUIFPLAMC.fplAllocationProposal)
						.exec(EXUIFPLAMC.fplUploadDocuments)
						.exec(EXUIFPLAMC.fplSubmitApplication)
				}
				.exec(EXUIMCLogin.manageCase_Logout)
		}
	}

	
	/*===============================================================================================
	* below scenario is for search and view case as a case worker
	 ==================================================================================================*/

	val EXUIMCaseCaseworkerScn = scenario("***** Caseworker Journey ******").repeat(1)
  {
		exitBlockOnFail {
			feed(feedUserDataCaseworker).feed(Feeders.CwDataFeeder)
			.exec(_.set("env", s"${env}"))
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.caseworkerLogin)
			.repeat(4) {
				exec(EXUICaseWorker.ApplyFilters)
					.exec(EXUICaseWorker.ApplySort)
					.exec(EXUICaseWorker.ClickFindCase)
					.exec(EXUICaseWorker.ViewCase)
			}
			.exec(EXUIMCLogin.manageCase_Logout)
		}
  }
	
	/*===============================================================================================
	* below scenario is for Financial Remedy Business Process related scenario
	 ==================================================================================================*/

	val EXUIFinancialRemedyScn = scenario("Scenario FR").repeat(1)
	{
		exitBlockOnFail {
			feed(feedUserDataFR)
			.feed(Feeders.FRApplicantDataFeeder)
			.exec(_.set("env", s"${env}"))
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
			.repeat(2) {
				exec(EXUI_FR_Applicant.createCase)
			}
			.exec(EXUIMCLogin.manageCase_Logout)
			/*.pause(20)
			.feed(Feeders.FRRespondentDataFeeder)
				.feed(feedUserDataRJ)
			.exec(EXUIMCLogin.manageOrgHomePage)
			.exec(EXUIMCLogin.manageOrglogin)
			.exec(EXUI_FR_Respondent.shareCase)
			.exec(EXUIMCLogin.manageOrg_Logout)*/
		}

	}
	
	/*===============================================================================================
	* Below setup is to do the smoke test to make sure all the scripts are working for one user
	 ==================================================================================================*/

	/*setUp(
		 EXUIMCaseProbateScn.inject(atOnceUsers(1)).disablePauses,
		 EXUIMCaseCreationIACScn.inject(atOnceUsers(1)).disablePauses,
		 EXUIMCaseCreationFPLAScn.inject(atOnceUsers(1)).disablePauses,
		 EXUIMCaseCaseworkerScn.inject(atOnceUsers(1)).disablePauses,
		 EXUIMCaseCreationDivorceScn.inject(atOnceUsers(1)).disablePauses,
		 EXUIFinancialRemedyScn.inject(atOnceUsers(1)).disablePauses
	)
			.protocols(MChttpProtocol)*/
	

	
	
	/*===============================================================================================
	* Below setup  is to do the smoke test to make sure manage org is working, we can uncomment it when we use it
	 ==================================================================================================*/
	/*setUp(
		EXUIScn.inject(atOnceUsers(1)).disablePauses
			.protocols(XUIHttpProtocol)
	)*/
	
	
	/*===============================================================================================
	* Below setup  is to do the smoke test to make sure one particular scenario  is working as part of sanity test
	 ==================================================================================================*/
	 /*setUp(
     EXUIMCaseCreationFPLAScn.inject(atOnceUsers(1)).disablePauses
	  // EXUIMCaseCaseworkerScn.inject(nothingFor(20),rampUsers(1) during (1))
	 )
      .protocols(MChttpProtocol)


	  */

/*
	 setUp(
		 EXUIMCaseProbateScn.inject(nothingFor(5),rampUsers(238) during (1200)),
		 EXUIMCaseCreationIACScn.inject(nothingFor(15),rampUsers(20) during (1200)),
		 EXUIMCaseCreationFPLAScn.inject(nothingFor(35),rampUsers(7) during (1200)),
		 EXUIMCaseCaseworkerScn.inject(nothingFor(55),rampUsers(900) during (1200)),
		 EXUIMCaseCreationDivorceScn.inject(nothingFor(65),rampUsers(238) during (1200)),
		 EXUIFinancialRemedyScn.inject(nothingFor(75),rampUsers(98) during (1200))
        ).protocols(MChttpProtocol)
 */

	def simulationProfile(simulationType: String, users: Int): Seq[OpenInjectionStep] = {
		simulationType match {
			case "perftest" =>
				if (debugMode == "off") {
					Seq(
						rampUsers(users) during (rampUpDurationMins minutes)
					)
				}
				else{
					Seq(atOnceUsers(1))
				}
			case "pipeline" =>
				Seq(rampUsers(numberOfPipelineUsers) during (2 minutes))
			case _ =>
				Seq(nothingFor(0))
		}
	}

	setUp(
		EXUIMCaseProbateScn.inject(simulationProfile(testType, probateTargetPerHour)).pauses(pauseOption),
		EXUIMCaseCreationIACScn.inject(simulationProfile(testType, iacTargetPerHour)).pauses(pauseOption),
		EXUIMCaseCreationFPLAScn.inject(simulationProfile(testType, fplaTargetPerHour)).pauses(pauseOption),
		EXUIMCaseCreationDivorceScn.inject(simulationProfile(testType, divorceTargetPerHour)).pauses(pauseOption),
		EXUIFinancialRemedyScn.inject(simulationProfile(testType, frTargetPerHour)).pauses(pauseOption),
		EXUIMCaseCaseworkerScn.inject(simulationProfile(testType, caseworkerTargetPerHour)).pauses(pauseOption)
	).protocols(MChttpProtocol)
		.assertions(forAll.successfulRequests.percent.gte(80))

 



}
