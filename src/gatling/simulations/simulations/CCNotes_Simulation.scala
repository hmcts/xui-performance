package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import utils._

import scala.concurrent.duration._

class CCNotes_Simulation extends Simulation {

	val env = System.getProperty("env", "perftest")
	val debugMode = System.getProperty("debug", "off")

	val CSV_Data_Set_Config = csv("ccnoteuserdetails.csv").circular

	val httpProtocol = http
		.baseUrl(Environment.baseURL.replace("#{env}", env))
		.inferHtmlResources()
		.silentResources
		.header("experimental", "true")
		.header("Upgrade-Insecure-Requests", "1")

	val scn = scenario("CCNotes")
		.exitBlockOnFail {
			feed(CSV_Data_Set_Config)
			.exec(_.set("env", env))
			.repeat(5) {
				exec(CCNotes.Flow)
			}
		}

	setUp(
		scn.inject(rampUsers(5).during(200.seconds))
	).protocols(httpProtocol)
}
