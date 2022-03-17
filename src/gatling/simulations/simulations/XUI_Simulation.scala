package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import utils._
import scala.io.Source
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

	//Read in text labels required for each NFD case type - sole and joint case labels are different, so are fed directly into the JSON payload bodies
	val nfdSoleLabelsInitialised = Source.fromResource("bodies/nfd/labels/soleLabelsInitialised.txt").mkString
	val nfdSoleLabelsPopulated = Source.fromResource("bodies/nfd/labels/soleLabelsPopulated.txt").mkString
	val nfdJointLabelsInitialised = Source.fromResource("bodies/nfd/labels/jointLabelsInitialised.txt").mkString
	val nfdJointLabelsPopulated = Source.fromResource("bodies/nfd/labels/jointLabelsPopulated.txt").mkString

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
	val nfdSoleTargetPerHour:Double = 119
	val nfdJointTargetPerHour:Double = 119
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
	* XUI Solicitor NFD Scenario (Sole Application)
	 ===============================================================================================*/
	val NoFaultDivorceSolicitorSoleScenario = scenario("***** NFD Create Case (Sole) *****")
		.exitBlockOnFail {
			//feed two rows of data - applicant1's solicitor and applicant2's solicitor
			feed(UserFeederNFD, 2)
				.exec(_.set("env", s"${env}")
							.set("caseType", "NFD")
							.set("nfdCaseType", "sole")
							.set("NFDLabelsInitialised", nfdSoleLabelsInitialised) //sets the initialised labels for JSON bodies
							.set("NFDLabelsPopulated", nfdSoleLabelsPopulated)) //sets the populated labels for JSON bodies
				//Solicitor 1 - Divorce Application
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.CreateNFDCase)
				.exec(Solicitor_NFD.SignAndSubmitSole)
				.exec(Logout.XUILogout)
				//Caseworker - Issue Application
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-issue-application", "bodies/nfd/CWIssueApplication.json"))
				//set 'user'/'password' to the second one (applicant2's solicitor) for assigning the case and login
				.exec(session => session.set("user", session("user2").as[String]).set("password", session("password2").as[String]))
				//Update the case in CCD to assign it to the second solicitor
				.exec(CCDAPI.AssignCase)
				//Solicitor 2 - Respond to Divorce Application
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.RespondToNFDCase)
				.exec(Logout.XUILogout)
				//Caseworker - Mark the Case as Awaiting Conditional Order (to bypass 20-week holding)
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-progress-held-case", "bodies/nfd/CWAwaitingConditionalOrder.json"))
				//Solicitor 1 - Apply for Conditional Order
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.ApplyForCO)
				.exec(Logout.XUILogout)
				//Legal Advisor - Grant Conditional Order
				.exec(CCDAPI.CreateEvent("Legal", "DIVORCE", "NFD", "legal-advisor-make-decision", "bodies/nfd/LAMakeDecision.json"))
				//Caseworker - Make Eligible for Final Order
				.exec(
					//link with bulk case
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-link-with-bulk-case", "bodies/nfd/CWLinkWithBulkCase.json"),
					//set case hearing and decision dates to a date in the past
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-update-case-court-hearing", "bodies/nfd/CWUpdateCaseWithCourtHearing.json"),
					//set judge details, CO granted and issued dates in the past
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-amend-case", "bodies/nfd/CWSetCODetails.json"),
					//pronounce case
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-pronounce-case", "bodies/nfd/CWPronounceCase.json"),
					//set final order eligibility dates
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-amend-case", "bodies/nfd/CWSetFOEligibilityDates.json"),
					//set case as awaiting final order
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-progress-case-awaiting-final-order", "bodies/nfd/CWAwaitingFinalOrder.json"))
			//TODO: ADD FINAL ORDER HERE ONCE DEVELOPED
		}

	/*===============================================================================================
	* XUI Solicitor NFD Scenario (Joint Application)
	 ===============================================================================================*/
	val NoFaultDivorceSolicitorJointScenario = scenario("***** NFD Create Case (Joint) *****")
		.exitBlockOnFail {
			//feed two rows of data - applicant1's solicitor and applicant2's solicitor
			feed(UserFeederNFD, 2)
				.exec(_.set("env", s"${env}")
							.set("caseType", "NFD")
							.set("nfdCaseType", "joint")
							.set("NFDLabelsInitialised", nfdJointLabelsInitialised) //sets the initialised labels for JSON bodies
							.set("NFDLabelsPopulated", nfdJointLabelsPopulated)) //sets the populated labels for JSON bodies
				//Solicitor 1 - Divorce Application
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.CreateNFDCase)
				.exec(Solicitor_NFD.JointInviteApplicant2)
				.exec(Logout.XUILogout)
				//set 'user'/'password' to the second one (applicant2's solicitor) for assigning the case and login
				.exec(session => session.set("user", session("user2").as[String]).set("password", session("password2").as[String]))
				//Update the case in CCD to assign it to the second solicitor
				.exec(CCDAPI.AssignCase)
				//Solicitor 2 - Confirm Divorce Application
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.SubmitJointApplication)
				.exec(Logout.XUILogout)
				//Solicitor 1 - Submit Application
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.SignAndSubmitJoint)
				.exec(Logout.XUILogout)
				//Caseworker - Issue Application
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-issue-application", "bodies/nfd/CWIssueApplication.json"))
				//Caseworker - Mark the Case as Awaiting Conditional Order (to bypass 20-week holding)
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-progress-held-case", "bodies/nfd/CWAwaitingConditionalOrder.json"))
			//TODO: ADD CONDITIONAL ORDER HERE ONCE DEVELOPED
			//TODO: ADD FINAL ORDER HERE ONCE DEVELOPED
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
				//Only continue with the case activities if results were returned
				.doIf(session => session("numberOfResults").as[Int] > 0) {
					exec(Caseworker_Navigation.SearchByCaseNumber)
					.exec(Caseworker_Navigation.ViewCase)
					.exec(Caseworker_Navigation.NavigateTabs)
				}
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
		NoFaultDivorceSolicitorSoleScenario.inject(simulationProfile(testType, nfdSoleTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		NoFaultDivorceSolicitorJointScenario.inject(simulationProfile(testType, nfdJointTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		FinancialRemedySolicitorScenario.inject(simulationProfile(testType, frTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		CaseworkerScenario.inject(simulationProfile(testType, caseworkerTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption)
	).protocols(httpProtocol)
		.assertions(forAll.successfulRequests.percent.gte(80))

}
