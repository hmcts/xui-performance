package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._

class ETAddDocumentsToMultiple_Simulation extends Simulation {

	val UserFeederETMultiples = csv("UserDataETMultiples.csv")

	/*===============================================================================================
	* ET Documents to Multiple
 	===============================================================================================*/
	val ETAddDocumentsToMultipleScenario = scenario("ET Add Documents To Multiple")
		.exitBlockOnFail {
			feed(UserFeederETMultiples)
			.exec(
				Authenticate.Auth,
				ETAddDocumentsToMultiple.AddDocumentsToMultipleUpload
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
		ETAddDocumentsToMultipleScenario.inject(atOnceUsers(1))
	).protocols(http).assertions(global.successfulRequests.percent.is(100))


}