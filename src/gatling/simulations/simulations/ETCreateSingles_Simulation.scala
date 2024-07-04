package simulations

import io.gatling.core.Predef._
import io.gatling.core.pause.PauseType
import io.gatling.http.Predef._
import scenarios._

import java.io.FileWriter

class ETCreateSingles_Simulation extends Simulation {

	val UserFeederET = csv("UserDataET.csv").random
	val outputFilenameSingles = "casesCreatedSingles.csv"

	/* PERFORMANCE TEST CONFIGURATION */
	val numberOfSinglesToCreatePerUser: Int = 1
	val numberOfUsers: Int = 10

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
		ETCreateSinglesScenario.inject(atOnceUsers(numberOfUsers))
	).protocols(http).assertions(global.successfulRequests.percent.is(100))


}