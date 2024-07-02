package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._

import java.io.FileWriter

class ETCreateMultiple_Simulation extends Simulation {

	val UserFeederET = csv("UserDataET.csv").random
	val UserFeederETMultiples = csv("UserDataETMultiples.csv")
	val outputFilenameMultiples = "casesCreatedMultiples.csv"

	/*===============================================================================================
	* ET Create Singles Cases
 	===============================================================================================*/
	val ETCreateSinglesScenario = scenario("ET Create Singles")
		.exitBlockOnFail {
			feed(UserFeederET)
			.exec(
				Authenticate.Auth,
				ETCreateSingles.CreateSingle,
				ETCreateSingles.CreateEvent("et1Vetting", "bodies/et/et1Vetting.json"),
				ETCreateSingles.CreateEvent("preAcceptanceCase", "bodies/et/preAcceptanceCase.json")
			)
		}

	/*===============================================================================================
	* ET Create Multiple Case
 	===============================================================================================*/
	val ETCreateMultipleScenario = scenario("ET Create Multiple")
		.exitBlockOnFail {
			exec(ETCreateSinglesScenario) //create the lead case
			.feed(UserFeederETMultiples)
			.exec(
				Authenticate.Auth,
				ETCreateMultiple.CreateMultiple
			)
			.exec(session => {
				val fw = new FileWriter(outputFilenameMultiples, true)
				fw.write(session("caseId").as[String] + "\n")
				fw.close()
				session
			})
		}

	/*===============================================================================================
	* Simulation Configuration
	 ===============================================================================================*/

	setUp(
		ETCreateMultipleScenario.inject(atOnceUsers(1))
	).protocols(http).assertions(global.successfulRequests.percent.is(100))


}