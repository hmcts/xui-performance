package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.exui.performance.scenarios._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils._
import scala.concurrent.duration._

class ManageOrg extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    //.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    //.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
    .doNotTrackHeader("1")

  val ManageOrgScn = scenario("MOS").repeat(1)
  {
    exec(
      ManageOrganisation.MOHome,
      ManageOrganisation.MOLogin,
      ManageOrganisation.MOAPI,
      //ManageOrganisation.MOLogout
    )
  }

  setUp(
    ManageOrgScn.inject(rampUsers(1) during (1 minutes)))
    .protocols(httpProtocol)

}
