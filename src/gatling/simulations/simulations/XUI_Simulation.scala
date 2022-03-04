package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import utils._
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.core.pause.PauseType

import scala.concurrent.duration._

class XUI_Simulation extends Simulation {

	val CaseworkerUserFeeder = csv("UserDataCaseworkers.csv").circular
	val UserFeederDivorce = csv("UserDataDivorce.csv").circular
	val UserFeederFPL = csv("UserDataFPL.csv").circular
	val UserFeederFR = csv("UserDataFR.csv").circular
	val UserFeederIAC = csv("UserDataIAC.csv").circular
	val UserFeederNFD = csv("UserDataNFD.csv").circular
	val UserFeederProbate = csv("UserDataProbate.csv").circular

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
	val probateTargetPerHour:Double = 238
	val iacTargetPerHour:Double = 20
	val fplTargetPerHour:Double = 7
	val divorceTargetPerHour:Double = 238
	val nfdTargetPerHour:Double = 238
	val frTargetPerHour:Double = 98
	val caseworkerTargetPerHour:Double = 900


	val rampUpDurationMins = 5
	val rampDownDurationMins = 5
	val testDurationMins = 60

	val numberOfPipelineUsers = 5
	val pipelinePausesMillis:Long = 3000 //3 seconds

	//Determine the pause pattern to use:
	//Performance test = use the pauses defined in the scripts
	//Pipeline = override pauses in the script with a fixed value (pipelinePauseMillis)
	//Debug mode = disable all pauses
	val pauseOption:PauseType = debugMode match{
		case "off" if testType == "perftest" => constantPauses
		case "off" if testType == "pipeline" => customPauses(pipelinePausesMillis)
		case _ => disabledPauses
	}

  val httpProtocol = http
		.baseUrl(Environment.baseURL.replace("${env}", s"${env}"))
		.inferHtmlResources()
		.silentResources
		.header("experimental", "true") //used to send through client id, s2s and bearer tokens. Might be temporary

	before{
		println(s"Test Type: ${testType}")
		println(s"Test Environment: ${env}")
		println(s"Debug Mode: ${debugMode}")
	}

	/*===============================================================================================
	* XUI Solicitor Probate Scenario
	 ===============================================================================================*/
  val ProbateSolicitorScenario = scenario("***** Probate Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederProbate)
				.exec(_.set("env", s"${env}")
							.set("caseType", "GrantOfRepresentation"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_Probate.CreateProbateCase)
					.exec(Solicitor_Probate.AddDeceasedDetails)
					.exec(Solicitor_Probate.AddApplicationDetails)
					.exec(Solicitor_Probate.ReviewAndSubmitApplication)
				}
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor IAC Scenario
	 ===============================================================================================*/
	val ImmigrationAndAsylumSolicitorScenario = scenario("***** IAC Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederIAC)
				.exec(_.set("env", s"${env}")
							.set("caseType", "Asylum"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_IAC.CreateIACCase)
					.exec(Solicitor_IAC.shareacase)
				}
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor Divorce Scenario
	 ===============================================================================================*/
	val DivorceSolicitorScenario = scenario("***** Divorce Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederDivorce)
				.exec(_.set("env", s"${env}")
							.set("caseType", "DIVORCE"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_Divorce.CreateDivorceCase)
				}
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor NFD Scenario
	 ===============================================================================================*/
	val NoFaultDivorceSolicitorScenario = scenario("***** NFD Create Case *****")
		.exitBlockOnFail {
			//feed two rows of data - applicant1's solicitor and applicant2's solicitor
			feed(UserFeederNFD, 2)
				.exec(_.set("env", s"${env}")
							.set("caseType", "NFD"))
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.CreateNFDCase)
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor Financial Remedy (FR) Scenario
	 ===============================================================================================*/
	val FinancialRemedySolicitorScenario = scenario("***** FR Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederFR)
				.exec(_.set("env", s"${env}")
							.set("caseType", "FinancialRemedyMVP2"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_FR.CreateFRCase)
				}
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor Family Public Law (FPL) Scenario
	 ===============================================================================================*/
	val FamilyPublicLawSolicitorScenario = scenario("***** FPL Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederFPL)
				.exec(_.set("env", s"${env}")
							.set("caseType", "CARE_SUPERVISION_EPO"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_FPL.CreateFPLCase)
				.exec(Solicitor_FPL.fplOrdersAndDirections)
				.exec(Solicitor_FPL.fplHearingUrgency)
				.exec(Solicitor_FPL.fplGrounds)
				.exec(Solicitor_FPL.fplLocalAuthority)
				.exec(Solicitor_FPL.fplChildDetails)
				.exec(Solicitor_FPL.fplRespondentDetails)
				.exec(Solicitor_FPL.fplAllocationProposal)
				.exec(Solicitor_FPL.fplSubmitApplication)
				.exec(Solicitor_FPL.fplReturnToCase)
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Caseworker - Search & View Case Scenario
	 ===============================================================================================*/
	val CaseworkerScenario = scenario("***** Caseworker Journey ******")
		.exitBlockOnFail {
			feed(CaseworkerUserFeeder)
				//TODO: UPDATE caseType with something more dynamic
				.exec(_.set("env", s"${env}")
					.set("caseType", "NFD"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Caseworker_Navigation.ApplyFilter)
				.exec(Caseworker_Navigation.SortByLastModifiedDate)
				.exec(Caseworker_Navigation.LoadPage2)
				.exec(Caseworker_Navigation.SearchByCaseNumber)
				.exec(Caseworker_Navigation.ViewCase)
				.exec(Caseworker_Navigation.NavigateTabs)
				.exec(Caseworker_Navigation.LoadCaseList)
				.exec(Logout.XUILogout)
		}


	/*===============================================================================================
	* Simulation Configuration
	 ===============================================================================================*/

	def simulationProfile(simulationType: String, userPerHourRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
		val userPerSecRate = userPerHourRate / 3600
		simulationType match {
			case "perftest" =>
				if (debugMode == "off") {
					Seq(
						rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins minutes),
						constantUsersPerSec(userPerSecRate) during (testDurationMins minutes),
						rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins minutes)
					)
				}
				else{
					Seq(atOnceUsers(1))
				}
			case "pipeline" =>
				Seq(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
			case _ =>
				Seq(nothingFor(0))
		}
	}

	setUp(
		ProbateSolicitorScenario.inject(simulationProfile(testType, probateTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		ImmigrationAndAsylumSolicitorScenario.inject(simulationProfile(testType, iacTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		FamilyPublicLawSolicitorScenario.inject(simulationProfile(testType, fplTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		DivorceSolicitorScenario.inject(simulationProfile(testType, divorceTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		NoFaultDivorceSolicitorScenario.inject(simulationProfile(testType, nfdTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		FinancialRemedySolicitorScenario.inject(simulationProfile(testType, frTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		CaseworkerScenario.inject(simulationProfile(testType, caseworkerTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption)
	).protocols(httpProtocol)
		.assertions(forAll.successfulRequests.percent.gte(80))

}
