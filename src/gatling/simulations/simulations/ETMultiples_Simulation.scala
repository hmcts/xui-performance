package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._

import io.gatling.core.pause.PauseType

import scala.concurrent.duration._
import scala.util.Random

class ETMultiples_Simulation extends Simulation {

	val UserFeederET = csv("UserDataET.csv").random

	/* ******************************** */
	/* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
	val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
	/* ******************************** */

	/* PERFORMANCE TEST CONFIGURATION */
	val numberOfCasesToCreate: Double = 1
	val multipleCaseReference = 1234567890

	//Determine the pause pattern to use:
	//Debug mode = disable all pauses
	val pauseOption: PauseType = debugMode match {
		case "off" => constantPauses
		case _ => disabledPauses
	}

	val httpProtocol = http

	before {
		println(s"Debug Mode: ${debugMode}")
	}

	/*===============================================================================================
	* ET Case Creation
 	===============================================================================================*/
	val ETCaseCreation = scenario("ET Case Creation")
		.exitBlockOnFail {
			feed(UserFeederET)
			.exec(
				CCDAPI.Auth,
				CCDAPI.CreateCase("initiateCase", "bodies/et/initiateCase.json"),
				CCDAPI.CreateEvent("et1Vetting", "bodies/et/et1Vetting.json"),
				CCDAPI.CreateEvent("preAcceptanceCase", "bodies/et/preAcceptanceCase.json")
			)
		}

		.exec {
			session =>
				println(session)
				session
		}

	/*===============================================================================================
	* Simulation Configuration
	 ===============================================================================================*/

	setUp(
    ETCaseCreation.inject(atOnceUsers(1)).pauses(pauseOption)
	).protocols(httpProtocol)


}