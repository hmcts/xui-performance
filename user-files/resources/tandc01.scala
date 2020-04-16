
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class tandc01 extends Simulation {

	val httpProtocol = http
		.baseUrl("https://manage-case.perftest.platform.hmcts.net")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.9")
		.doNotTrackHeader("1")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"Origin" -> "https://idam-web-public.perftest.platform.hmcts.net",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

	val headers_6 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_16 = Map(
		"Content-Type" -> "application/json",
		"Origin" -> "https://manage-case.perftest.platform.hmcts.net",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

	val headers_18 = Map(
		"Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
		"Sec-Fetch-Dest" -> "image",
		"Sec-Fetch-Mode" -> "no-cors",
		"Sec-Fetch-Site" -> "same-origin")

    val uri2 = "https://idam-web-public.perftest.platform.hmcts.net/login"

	val scn = scenario("tandc01")
		.exec(http("request_0")
			.post(uri2 + "?response_type=code&client_id=xuiwebapp&redirect_uri=https://manage-case.perftest.platform.hmcts.net/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
			.headers(headers_0)
			.formParam("username", "exui.userss1@mailinator.com")
			.formParam("password", "Pass19word")
			.formParam("save", "Sign in")
			.formParam("selfRegistrationEnabled", "false")
			.formParam("_csrf", "005bc916-e229-4e7c-be22-27c120212e42")
			.resources(http("request_1")
			.get("/external/config/ui")
			.headers(headers_1),
            http("request_2")
			.get("/assets/config/config.json")
			.headers(headers_1),
            http("request_3")
			.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
			.headers(headers_1),
            http("request_4")
			.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
			.headers(headers_1),
            http("request_5")
			.get("/api/userTermsAndConditions/46e8c396-d7c7-4354-b772-055a47e355b9")
			.headers(headers_1),
            http("request_6")
			.get("/accept-terms-and-conditions")
			.headers(headers_6),
            http("request_7")
			.get("/assets/config/config.json")
			.headers(headers_1),
            http("request_8")
			.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
			.headers(headers_1),
            http("request_9")
			.get("/external/config/ui")
			.headers(headers_1)))
		.pause(712)
		.exec(http("request_10")
			.get("/accept-terms-and-conditions")
			.headers(headers_6)
			.resources(http("request_11")
			.get("/assets/config/config.json")
			.headers(headers_1),
            http("request_12")
			.get("/external/config/ui")
			.headers(headers_1),
            http("request_13")
			.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
			.headers(headers_1),
            http("request_14")
			.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
			.headers(headers_1),
            http("request_15")
			.get("/api/userTermsAndConditions/46e8c396-d7c7-4354-b772-055a47e355b9")
			.headers(headers_1)))
		.pause(3)
		.exec(http("request_16")
			.post("/api/userTermsAndConditions")
			.headers(headers_16)
			.body(StringBody("{"userId":"46e8c396-d7c7-4354-b772-055a47e355b9"}"))
			.resources(http("request_17")
			.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
			.headers(headers_1),
            http("request_18")
			.get("/assets/images/icon-search-white.svg")
			.headers(headers_18),
            http("request_19")
			.get("/api/healthCheck?path=%2Fcases")
			.headers(headers_1)))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}