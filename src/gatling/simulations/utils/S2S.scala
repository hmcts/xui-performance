package scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._

object S2S {

  val config: Config = ConfigFactory.load()
  val s2sUrl = Environment.rpeAPIURL

  //microservice is a string defined in the Simulation and passed into the body below
  // def s2s(microservice: String) = {

  val s2s =

    exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"microservice":"ccd_data"}"""))
      .check(bodyString.saveAs("authToken")))
      .exitHereIfFailed

  // }
}