package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ApproveOrganisation extends Simulation {

	val httpProtocol = http.proxy(Proxy("proxyout.reform.hmcts.net", 8080))
//		.baseUrl("https://xui-ao-webapp-demo.service.core-compute-demo.internal")
		.inferHtmlResources()
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")

	val headers_0 = Map(
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

    val uri2 = "https://idam-web-public.demo.platform.hmcts.net"
    //val uri3 = "https://www.google-analytics.com"

	val scn = scenario("approveOrganisation")
		.exec(http("firstpage")
			.get("/")
			.headers(headers_0)
			.check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

			/*.resources(http("request_1")
			.get("/styles.101a0d6c45ee2365ce04.css")
			.headers(headers_1),
            http("request_2")
			.get("/runtime.f0055a3a30b3a6886280.js")
			.headers(headers_1),
            http("request_3")
			.get("/polyfills.31fd43fa88a10a9da956.js")
			.headers(headers_1),
            http("request_4")
			.get("/main.0c4b6b9ef8a86902c4c8.js")
			.headers(headers_1),
            http("request_5")
			.get("/assets/images/govuk-crest.png")
			.headers(headers_1),*/
            .exec(http("loginpage")
			.get(uri2 + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri=https://xui-ao-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
			.headers(headers_6))
           /* http("request_7")
			.get(uri2 + "/assets/stylesheets/application.css")
			.headers(headers_1),
            http("request_8")
			.get(uri2 + "/assets/stylesheets/govuk-template.css")
			.headers(headers_1),
            http("request_9")
			.get(uri2 + "/assets/images/gov.uk_logotype_crown_invert_trans.png")
			.headers(headers_1),
            http("request_10")
			.get(uri2 + "/assets/javascripts/details.polyfill.js")
			.headers(headers_1),
            http("request_11")
			.get(uri3 + "/analytics.js")
			.headers(headers_1),
            http("request_12")
			.get(uri2 + "/assets/javascripts/govuk-template.js")
			.headers(headers_1),
            http("request_13")
			.get(uri2 + "/assets/stylesheets/govuk-template-print.css")
			.headers(headers_1),
            http("request_14")
			.get(uri2 + "/assets/javascripts/jquery-3.4.1.min.js")
			.headers(headers_1),
            http("request_15")
			.get(uri3 + "/collect?v=1&_v=j78&a=31993179&t=pageview&_s=1&dl=https%3A%2F%2Fidam-web-public.demo.platform.hmcts.net%2Flogin%3Fresponse_type%3Dcode%26client_id%3Dxuiaowebapp%26redirect_uri%3Dhttps%3A%2F%2Fxui-ao-webapp-demo.service.core-compute-demo.internal%2Foauth2%2Fcallback%26scope%3Dopenid%2520profile%2520roles%2520manage-user%2520create-user&dr=https%3A%2F%2Fxui-ao-webapp-demo.service.core-compute-demo.internal%2F&ul=en-us&de=UTF-8&dt=Sign%20in%20-%20HMCTS%20Access&sd=24-bit&sr=1280x720&vp=709x610&je=0&_u=AACAAEAB~&jid=&gjid=&cid=1106272424.1566207628&tid=UA-122164129-2&_gid=1723029559.1566207628&z=396809671")
			.headers(headers_1),
            http("request_16")
			.get(uri2 + "/assets/stylesheets/fonts.css")
			.headers(headers_1),
            http("request_17")
			.get(uri2 + "/assets/stylesheets/images/gov.uk_logotype_crown.png")
			.headers(headers_1),
            http("request_18")
			.get(uri2 + "/assets/stylesheets/images/open-government-licence.png")
			.headers(headers_1),
            http("request_19")
			.get(uri2 + "/assets/stylesheets/images/govuk-crest.png")
			.headers(headers_1)))
*/		.pause(25)
		.exec(http("login")
			.post(uri2 + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri=https://xui-ao-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
			.headers(headers_20)
			.formParam("username", "vammunadmin@mailnesia.com")
			.formParam("password", "Monday01")
			.formParam("save", "Sign in")
			.formParam("selfRegistrationEnabled", "false")
			.formParam("_csrf", "${csrfToken}"))
			/*.resources(http("request_21")
			.get("/assets/images/govuk-crest.png")
			.headers(headers_1),
            http("request_22")
			.get("/assets/fonts/bold-a2452cb66f-v1.woff2")
			.headers(headers_22),
            http("request_23")
			.get("/assets/fonts/light-f38ad40456-v1.woff2")
			.headers(headers_22),*/
           .exec( http("request_24")
			.get("/api/organisations?status=PENDING")
			.headers(headers_24))
	.exec(http("request_25")
			.get("/api/organisations")
			.headers(headers_24))
		.pause(57)
		.exec(http("approveorganisation")
			.put("/api/organisations/${orgRefCode}")
			.headers(headers_26)
			//.body(RawFileBody("AO.json")))
		.body(ElFileBody("0026.json")).asJson)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}