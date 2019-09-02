package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders

class ExUI extends Simulation {

	val url="https://xui-mo-webapp-demo.service.core-compute-demo.internal"
	val httpProtocol = http.proxy(Proxy("proxyout.reform.hmcts.net", 8080))
	//	.baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
		//.inferHtmlResources()
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")



	val headers_1 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Content-Type" -> "application/json",
		"Sec-Fetch-Mode" -> "cors")




	val createOrg=
	exec(http("EXUI_RO_Homepage")
			.get(url+"/register-org/register")
			.headers(headers_0)
		.check(status.is(200)))
		//.check(regex("Register to manage civil and family law cases")))
		.pause(10,30)

	.feed(Feeders.createDynamicDataFeeder).exec(http("EXUI_RO_Create")
			.post(url+"/external/register-org/register")
			.headers(headers_1)
  			//.body(StringBody("${addUser}")).asJson
		.body(ElFileBody("CR.json")).asJson

		    .check(status.is(200))
		   .check(jsonPath("$.organisationIdentifier").optional.saveAs("orgRefCode")))
		//.pause(10)
	.exec {
		session =>
			println("this is a org ref. code....." + session("orgRefCode").as[String])
			session
	}

	// end of create organisation



	// following code is for approve organisation

	val headers_approve= Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Upgrade-Insecure-Requests" -> "1")

	//	val headers_1 = Map("Sec-Fetch-Mode" -> "no-cors")

	val headers_6 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "cross-site",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_20 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Origin" -> "https://idam-web-public.demo.platform.hmcts.net",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

	/*	val headers_22 = Map(
      "Origin" -> "https://xui-ao-webapp-demo.service.core-compute-demo.internal",
      "Sec-Fetch-Mode" -> "cors")*/

	val headers_24 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Sec-Fetch-Mode" -> "cors")

	val headers_26 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Content-Type" -> "application/json",
		"Sec-Fetch-Mode" -> "cors")

	val headers_approvelogout = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

	val uri2 = "https://idam-web-public.demo.platform.hmcts.net"
	val url_approve="https://xui-ao-webapp-demo.service.core-compute-demo.internal"
	//val uri3 = "https://www.google-analytics.com"

	val approveOrgHomePage=	group ("EXUI_AO_Homepage") {

		exec(http("EXUI_AO_005_Homepage")
			.get(url_approve + "/")
			.headers(headers_approve)
			.check(status.is(200)))


			.exec(http("EXUI_AO_010_Homepage")
				.get(uri2 + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri=https://xui-ao-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
				.headers(headers_6)
				.check(regex("Sign in"))
				.check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

			.exec {
				session =>
					println("this is csrf....." + session("csrfToken").as[String])
					session
			}
	}
		.pause(10)

		val approveOrganisationlogin = group ("EXUI_AO_Login") {

			exec(http("EXUI_AO_005_Login")
				.post(uri2 + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri=https://xui-ao-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
				.headers(headers_20)
				.formParam("username", "vammunadmin@mailnesia.com")
				.formParam("password", "Monday01")
				.formParam("save", "Sign in")
				.formParam("selfRegistrationEnabled", "false")
				.formParam("_csrf", "${csrfToken}"))

				.exec(http("EXUI_AO_010_Login")
					.get(url_approve + "/api/organisations?status=PENDING")
					.headers(headers_24)
					.check(status.is(200)))
				.exec(http("EXUI_AO_015_Login")
					.get(url_approve + "/api/organisations")
					.headers(headers_24))

		}
  	.pause(5,10)

			val approveOrganisationApprove =
		 exec(http("EXUI_AO_Approve")
			.put(url_approve+"/api/organisations/${orgRefCode}")
			.headers(headers_26)
			//.body(RawFileBody("AO.json")))
			.body(ElFileBody("AO.json")).asJson
		.check(status.is(200)))

			 .pause(5)

			val approveOrganisationLogout = group ("EXUI_AO_Logout") {
				exec(http("EXUI_AO_005_Logout")
					.get(url_approve + "/api/logout")
					.headers(headers_approvelogout)
					//  .check(regex("Sign in")))
					.check(status.is(200)))
			}
	val scn = scenario("EXUI")
  		.exec(createOrg)
		.exec(approveOrgHomePage)
  	.exec(approveOrganisationlogin)
  	.exec(approveOrganisationApprove)
  	.exec(approveOrganisationLogout)

	 .exec {
      session =>
         println("this is a email id ....." + session("generatedEmail").as[String])
        // println("this is a user json ....." + session("addUser").as[String])
        session
     }


	setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}