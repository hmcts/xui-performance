package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import java.io.FileWriter

class ETAddDocumentsToMultiple_Simulation extends Simulation {

	val totalMultiCasesToUpdate = csv("multipleCaseId.csv").recordsCount
 	val multipleCaseIdFeeder = csv("multipleCaseId.csv").circular
	val UserFeederETMultiples = csv("UserDataETMultiples.csv")
	val outputFilenameMultiples = "docsAddedToMultiples.csv"
	val microservice = "xui_webapp"

	/*===============================================================================================
	* ET Documents to Multiple
 	===============================================================================================*/

	val ETAddDocumentsToMultipleScenario = scenario("ET Add Documents To Multiple")
	.exitBlockOnFail {
		feed(UserFeederETMultiples)
		.repeat(totalMultiCasesToUpdate) {
		exec(
			feed(multipleCaseIdFeeder)
			.exec(Authenticate.Auth)
			.exec(ETAddDocumentsToMultiple.AddDocumentsToMultipleUpload)
			.exec(session => {
				val fw = new FileWriter(outputFilenameMultiples, true)
				fw.write(session("caseId").as[String] + "\n")
				fw.close()
				session
				})
		) // end of exec
		} // end of repeat loop
		} // end of exitBlockOnFail

	/*===============================================================================================
	* Simulation Configuration
	 ===============================================================================================*/

	setUp(
		ETAddDocumentsToMultipleScenario.inject(atOnceUsers(1))
	).protocols(http).assertions(global.successfulRequests.percent.is(100))


}