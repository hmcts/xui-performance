package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils._
import scala.concurrent.duration._
import uk.gov.hmcts.reform.exui.performance.Feeders

class ExUI extends Simulation {


	val httpProtocol = Environment.HttpProtocol
		.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
	//	.baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
		//.inferHtmlResources()
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")


	val EXUIScn = scenario("EXUI").repeat(1)
	 {
		exec(
			ExUI.createOrg,
			ExUI.approveOrgHomePage,
			ExUI.approveOrganisationlogin,
			ExUI.approveOrganisationApprove,
			ExUI.approveOrganisationLogout,
			ExUI.manageOrgHomePage,
			ExUI.manageOrganisationLogin,
			ExUI.usersPage,
			ExUI.inviteUserPage,
			ExUI.sendInvitation,
			ExUI.manageOrganisationLogout
		)
	   }
	 .exec {
      session =>
         println("this is a email id ....." + session("generatedEmail").as[String])
				println("this is a email id1 ....." + session("generatedEmail1").as[String])
        // println("this is a user json ....." + session("addUser").as[String])
        session
     }

	setUp(
		EXUIScn.inject(rampUsers(10) during (1 minutes)))
		.protocols(httpProtocol)

}