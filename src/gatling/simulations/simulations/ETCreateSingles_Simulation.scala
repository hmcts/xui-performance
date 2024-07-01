package simulations

import io.gatling.core.Predef._
import io.gatling.core.pause.PauseType
import io.gatling.http.Predef._
import scenarios._

import java.io.FileWriter

class ETCreateSingles_Simulation extends Simulation {

	val UserFeederET = csv("UserDataET.csv").random
	val outputFilenameSingles = "casesCreatedSingles.csv"

	/* ******************************** */
	/* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
	val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
	/* ******************************** */

	/* PERFORMANCE TEST CONFIGURATION */
	val numberOfSinglesToCreatePerUser: Int = 4
	val numberOfUsers: Int = 3

	//Determine the pause pattern to use:
	//Debug mode = disable all pauses
	val pauseOption: PauseType = debugMode match {
		case "off" => constantPauses
		case _ => disabledPauses
	}

	before {
		println(s"Debug Mode: ${debugMode}")
	}

	/*===============================================================================================
	* ET Create Singles Cases
 	===============================================================================================*/
	val ETCreateSinglesScenario = scenario("ET Create Singles")
		.exitBlockOnFail {
			repeat(numberOfSinglesToCreatePerUser) {
				feed(UserFeederET)
				.exec(
					Authenticate.Auth,
					ETCreateSingles.CreateSingle,
					ETCreateSingles.CreateEvent("et1Vetting", "bodies/et/et1Vetting.json"),
					ETCreateSingles.CreateEvent("preAcceptanceCase", "bodies/et/preAcceptanceCase.json")
				)
				.exec(session => {
					val fw = new FileWriter(outputFilenameSingles, true)
						fw.write(session("ethosCaseRef").as[String] + "\n")
						fw.close()
					session
				})
			}
		}

	/*===============================================================================================
	* Simulation Configuration
	 ===============================================================================================*/

	setUp(
		ETCreateSinglesScenario.inject(atOnceUsers(numberOfUsers)).pauses(pauseOption)
	).protocols(http)


}