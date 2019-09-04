package uk.gov.hmcts.reform.exui.performance


import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders

class ManageOrganisationOriginal extends Simulation {

	val httpProtocol = http
		//.baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
		.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
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
		"Sec-Fetch-Mode" -> "cors")

	val headers_2 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "cross-site",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_3 = Map("Sec-Fetch-Mode" -> "no-cors")

	val headers_4 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Origin" -> "https://idam-web-public.demo.platform.hmcts.net",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_5 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Sec-Fetch-Mode" -> "cors",
		"authorization" -> "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiS0N4QmRlaHNIVUY2OTc4U2l6dklTRXhjWDBFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJleHVpZ2R6MmV1QG1haWxpbmF0b3IuY29tIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiY2M3Nzc5MDgtNzM3Yi00OWVkLWIxNzItYjMwMmJlODBjYzA3IiwiaXNzIjoiaHR0cHM6Ly9mb3JnZXJvY2stYW0uc2VydmljZS5jb3JlLWNvbXB1dGUtaWRhbS1kZW1vLmludGVybmFsOjg0NDMvb3BlbmFtL29hdXRoMi9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6IjNlMTZjNzg3LTc1YjAtNDFlZi04MGY1LTJhMzRmOGVkNDEzNCIsImF1ZCI6Inh1aW1vd2ViYXBwIiwibmJmIjoxNTY2OTIwNjI2LCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciJdLCJhdXRoX3RpbWUiOjE1NjY5MjA2MjYwMDAsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNTY2OTQ5NDI2LCJpYXQiOjE1NjY5MjA2MjYsImV4cGlyZXNfaW4iOjI4ODAwLCJqdGkiOiIyMTEwOWZiYy1iZDNkLTRiNzctOWRkMC0yZTFlZGM2NzliYmQifQ.SUlg2BIsSCs2vRf_klWRlPq24x0bXYJM7yLl6Vg6p52Yvc5fnyZjangogGd_Zim0AkiH-9enOTUIvjvKAomWn6lw7JirHAXTOxkrnAc4j4dtXXWj588NuhuTYJxSirgyybF3o5hWjMvcLPpZiDuIHlxNruYFy_fH_I3Y3FGjKs825c60xUq8OkyIF5KQTvoaRddc8D_i9YWpZj1XpUSDC1ozE5dme4BnQ5vyzQNXv4A9_ZU8LV4fnQ1G4oEeKD5viviWfbXhR6ePD6XkBRVidbyeD8jg9R5OwVsWKVY6c5w_YttTWpu2Q8ZQKFMa5VdHRjnoawpINHW4YAgDSscUCA")

	val headers_12 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Content-Type" -> "application/json",
		"Sec-Fetch-Mode" -> "cors",
		"authorization" -> "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiS0N4QmRlaHNIVUY2OTc4U2l6dklTRXhjWDBFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJleHVpZ2R6MmV1QG1haWxpbmF0b3IuY29tIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiY2M3Nzc5MDgtNzM3Yi00OWVkLWIxNzItYjMwMmJlODBjYzA3IiwiaXNzIjoiaHR0cHM6Ly9mb3JnZXJvY2stYW0uc2VydmljZS5jb3JlLWNvbXB1dGUtaWRhbS1kZW1vLmludGVybmFsOjg0NDMvb3BlbmFtL29hdXRoMi9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6IjNlMTZjNzg3LTc1YjAtNDFlZi04MGY1LTJhMzRmOGVkNDEzNCIsImF1ZCI6Inh1aW1vd2ViYXBwIiwibmJmIjoxNTY2OTIwNjI2LCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciJdLCJhdXRoX3RpbWUiOjE1NjY5MjA2MjYwMDAsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNTY2OTQ5NDI2LCJpYXQiOjE1NjY5MjA2MjYsImV4cGlyZXNfaW4iOjI4ODAwLCJqdGkiOiIyMTEwOWZiYy1iZDNkLTRiNzctOWRkMC0yZTFlZGM2NzliYmQifQ.SUlg2BIsSCs2vRf_klWRlPq24x0bXYJM7yLl6Vg6p52Yvc5fnyZjangogGd_Zim0AkiH-9enOTUIvjvKAomWn6lw7JirHAXTOxkrnAc4j4dtXXWj588NuhuTYJxSirgyybF3o5hWjMvcLPpZiDuIHlxNruYFy_fH_I3Y3FGjKs825c60xUq8OkyIF5KQTvoaRddc8D_i9YWpZj1XpUSDC1ozE5dme4BnQ5vyzQNXv4A9_ZU8LV4fnQ1G4oEeKD5viviWfbXhR6ePD6XkBRVidbyeD8jg9R5OwVsWKVY6c5w_YttTWpu2Q8ZQKFMa5VdHRjnoawpINHW4YAgDSscUCA")

	val headers_14 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Accept-Language" -> "en-US,en;q=0.9",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

    val uri22 = "https://idam-web-public.demo.platform.hmcts.net"
	  val url_mo = "https://xui-mo-webapp-demo.service.core-compute-demo.internal"

			val manageOrgHomePage = group ( "EXUI_MO_Homepage") {

				exec(http("EXUI_MO_005_Homepage")
					.get(url_mo + "/")
					.headers(headers_0)
					.check(status.is(200)))

					.exec(http("EXUI_MO_010_Homepage")
						.get(url_mo + "/api/user/details")
						.headers(headers_1)
						.check(status.is(401)))

					.exec(http("EXUI_MO_015_Homepage")
						.get(uri22 + "/?response_type=code&client_id=xuimowebapp&redirect_uri=https://xui-mo-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
						.headers(headers_2)
						.check(regex("Sign in"))
						.check(css("input[name='_csrf']", "value").saveAs("csrfToken1"))
					)
			}
			/*http("request_3")
.get(uri3 + "?v=1&_v=j79&a=1361156712&t=pageview&_s=1&dl=https%3A%2F%2Fidam-web-public.demo.platform.hmcts.net%2Flogin%3Fscope%3Dopenid%2Bprofile%2Broles%2Bmanage-user%2Bcreate-user%26response_type%3Dcode%26redirect_uri%3Dhttps%253a%252f%252fxui-mo-webapp-demo.service.core-compute-demo.internal%252foauth2%252fcallback%26client_id%3Dxuimowebapp&dr=https%3A%2F%2Fxui-mo-webapp-demo.service.core-compute-demo.internal%2Fhome&ul=en-us&de=UTF-8&dt=Sign%20in%20-%20HMCTS%20Access&sd=24-bit&sr=1280x720&vp=437x610&je=0&_u=IEBAAEAB~&jid=1616258456&gjid=75239786&cid=272823260.1566920599&tid=UA-122164129-2&_gid=1963206053.1566920599&_r=1&z=304471461")
.headers(headers_3))
.check(status.is(401)))*/
			.pause(10)

				val manageOrganisationLogin = group ("EXUI_MO_Login") {
					exec(http("EXUI_MO_005_Login")
						.post(uri22 + "/login?scope=openid+profile+roles+manage-user+create-user&response_type=code&redirect_uri=https%3a%2f%2fxui-mo-webapp-demo.service.core-compute-demo.internal%2foauth2%2fcallback&client_id=xuimowebapp")
						.headers(headers_4)
						//.disableFollowRedirect
						.formParam("username", "exuigdz2eu@mailinator.com")
						.formParam("password", "Compaq123!")
						.formParam("save", "Sign in")
						.formParam("selfRegistrationEnabled", "false")
						//.formParam("_csrf", "c0a9f3cb-971d-47bc-a5a1-696eae6056ef"))
						.formParam("_csrf", "${csrfToken1}")
						.check(status.in(200, 302)))
						//.check(headerRegex("Set-Cookie", ".*(.+?); Path=").saveAs("AuthHeader3")))
						//.check(headerRegex("Set-Cookie", ".\\*(.*); Path=").saveAs("AuthHeader3")))


						/*.exec {
							session =>
								println("this is a authentication token....." + session("AuthHeader3").as[String])

								session
						}*/





						.exec(http("EXUI_MO_010_Login")
							.get(url_mo + "/api/user/details")
							.headers(headers_5))

						.exec(http("EXUI_MO_015_Login")
							.get(url_mo + "/api/organisation")
							.headers(headers_5))

						.exec(http("EXUI_MO_020_Login")
							.get(url_mo + "/api/organisation/"))
				}
			.pause(5)

					val usersPage = group ("EXUI_MO_Userspage") {
						exec(http("EXUI_MO_005_Userspage")
							.get(url_mo + "/api/userList")
							.headers(headers_5)
							.check(status.is(200)))
					}
			.pause(5)

			val inviteUserPage = group ( "EXUI_MO_InviteUserpage") {
				exec(http("EXUI_MO_005_InviteUserpage")
					.get(url_mo + "/api/jurisdictions")
					.headers(headers_5)
					.check(status.is(200)))
			}
			.pause(10)

				val sendInvitation = group ("EXUI_MO_SendInvitation") {
					feed(Feeders.createDynamicDataFeeder1).exec(http("EXUI_MO_005_SendInvitation")
						.post(url_mo + "/api/inviteUser")
						.headers(headers_12)
						.body(ElFileBody("MO.json")).asJson
						.check(status.is(200)))
				}

			.pause(5)

	     val manageOrganisationLogout = group ("EXUI_MO_Logout") {
				 exec(http("EXUI_MO_005_Logout")
					 .get(url_mo + "/api/logout")
					 .headers(headers_14)
					 .check(status.is(200)))

					 .exec(http("EXUI_MO_010_Logout")
						 .get(url_mo + "/api/user/details")
						 .headers(headers_1)
						 .check(status.is(401)))

					 .exec(http("EXUI_MO_015_Logout")
						 .get(uri22 + "/?response_type=code&client_id=xuimowebapp&redirect_uri=https://xui-mo-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
						 .headers(headers_2))
			 }

	val scn = scenario("ManageOrganisationOriginal")
  		.exec (manageOrgHomePage)
  		.exec(manageOrganisationLogin)
  		.exec(usersPage)
  		.exec(inviteUserPage)
  		.exec (sendInvitation)
  		.exec (manageOrganisationLogout)

		.exec {
			session =>
				println("this is a email id1 ....." + session("generatedEmail1").as[String])
				// println("this is a user json ....." + session("addUser").as[String])
				session
		}

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}