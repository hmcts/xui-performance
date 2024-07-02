package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._

class ETAddSinglesToMultiple_Simulation extends Simulation {

	val UserFeederETMultiples = csv("UserDataETMultiples.csv")

	/*===============================================================================================
	* ET Add Singles to Multiple
 	===============================================================================================*/
	val ETAddSinglesToMultipleScenario = scenario("ET Add Singles To Multiple")
		.exitBlockOnFail {
			feed(UserFeederETMultiples)
			.exec(
				Authenticate.Auth,
				ETAddSinglesToMultiple.AddSinglesToMultiple
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
		ETAddSinglesToMultipleScenario.inject(atOnceUsers(1))
	).protocols(http).assertions(global.successfulRequests.percent.is(100))


}