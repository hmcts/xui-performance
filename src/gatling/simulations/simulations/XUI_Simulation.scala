package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import utils._

import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.core.pause.PauseType

import scala.concurrent.duration._
import scala.util.Random

class XUI_Simulation extends Simulation {

	final def sample[A](distribution: Map[A, Double]): A = {
		val rand = Random.nextDouble * distribution.values.sum
		val counter = distribution.iterator
		var cumulative = 0.0
		while (counter.hasNext) {
			val (item, itemProbability) = counter.next
			cumulative += itemProbability
			if (cumulative >= rand)
				return item
		}
		sys.error(f"Error")
	}

	val totalNumberOfCasesToCreate = 100 //29000
	val numberOfHearingsDistribution = Map(1 -> 10.1, 2 -> 13.4, 3 -> 16.2, 4 -> 15.8, 5 -> 12.4, 6 -> 9.3, 7 -> 6.6, 8 -> 4.8, 9 -> 3.4, 10 -> 2.3,
		11 -> 1.6, 12 -> 1.2, 13 -> 0.8, 14 -> 0.6, 15 -> 0.4, 16 -> 0.3, 17 -> 0.1, 18 -> 0.1, 19 -> 0.1, 20 -> 0.1, 25 -> 0.1, 30 -> 0.1, 35 -> 0.1, 40 -> 0.1)

	//create a feeder to calculate the number of hearings to be added to the case, based on the weighted distribution
	private val hearingsFeeder = Iterator.continually(Map("numberOfHearings" -> sample(numberOfHearingsDistribution)))

	val UserFeederFPL = csv("UserDataFPL.csv").circular

	val FPLMigrationJudgeFeeder = csv("FPLMigrationJudgeData.csv").random

	/* TEST TYPE DEFINITION */
	/* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
	/* perftest (default) = performance test against the perftest environment */
	val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

	//set the environment based on the test type
	val environment = testType match{
		case "perftest" => "perftest"
		case "pipeline" => "perftest"
		case _ => "**INVALID**"
	}

	/* ******************************** */
	/* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
	val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
	val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
	/* ******************************** */

	/* PERFORMANCE TEST CONFIGURATION */

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
		case _ => constantPauses //disabledPauses
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
				.exec(Logout.XUILogout)
				.pause(10)
				//WA MIGRATION
				//Court Admin: Add FamilyMan Case Number
				.exec(CCDAPI.CreateEvent("PublicLawCA", "PUBLICLAW", "CARE_SUPERVISION_EPO", "addFamilyManCaseNumber", "bodies/fpl/addHearings/CAAddCaseNumber.json"))
				//Court Admin: Send to GateKeeper
				.exec(CCDAPI.CreateEvent("PublicLawCA", "PUBLICLAW", "CARE_SUPERVISION_EPO", "sendToGatekeeper", "bodies/fpl/addHearings/CASendToGateKeeper.json"))
				//Court Admin: Judicial Gatekeeping
				.exec(CCDAPI.CreateEvent("PublicLawCA", "PUBLICLAW", "CARE_SUPERVISION_EPO", "addGatekeepingOrder", "bodies/fpl/addHearings/CAAddGatekeepingOrder.json"))

				//determine how many hearings to add to the case
				.feed(hearingsFeeder)

			  //Choose a judge/legal advisor
				.feed(FPLMigrationJudgeFeeder)

				.exec(session => session("judgeTitle").as[String] match {
					case "HIS_HONOUR_JUDGE" => session.set("allocatedJudgeTitle", "His Honour Judge")
					case "HER_HONOUR_JUDGE" => session.set("allocatedJudgeTitle", "Her Honour Judge")
					case "LEGAL_ADVISOR" => session.set("allocatedJudgeTitle", "Legal Advisor")
				})
				//Court Admin: List Gatekeeping Hearing
				.exec(CCDAPI.CreateEvent("PublicLawCA", "PUBLICLAW", "CARE_SUPERVISION_EPO", "listGatekeepingHearing", "bodies/fpl/addHearings/CAListGatekeepingHearing.json"))

				.pause(10)

				.repeat(session => session("numberOfHearings").as[Int] - 1, "count") {

					//Choose a judge/legal advisor
					feed(FPLMigrationJudgeFeeder)
					//Set the hearing date incrementally
					.exec(session => session.set("hearingDate", Common.getNextHearingDate(session("count").as[Int] + 1))
																	.set("judgeNameSuffix", Common.randomString(5)))
					//Court Admin: Manage Hearings (Add a hearing)
					.exec(CCDAPI.CreateEvent("PublicLawCA", "PUBLICLAW", "CARE_SUPERVISION_EPO", "manageHearings", "bodies/fpl/addHearings/CAManageHearings.json"))
				}

				.exec{
					session =>
						println(session)
					session
				}

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
		FamilyPublicLawSolicitorScenario.inject(rampUsers(totalNumberOfCasesToCreate) during rampUpDurationMins.minutes).pauses(pauseOption)
	).protocols(httpProtocol)
		.assertions(forAll.successfulRequests.percent.gte(80))

}
