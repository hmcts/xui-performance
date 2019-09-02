package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment

object ManageOrganisation {

  /*val httpProtocol = http
    .baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
*/

  val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL

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
    "authorization" -> "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiS0N4QmRlaHNIVUY2OTc4U2l6dklTRXhjWDBFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJleHVpZ2R6MmV1QG1haWxpbmF0b3IuY29tIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiOTQ2ZWMwMDgtYzc4Ni00NDVjLWE2MTctNDMyYjgwYTMyMGI3IiwiaXNzIjoiaHR0cHM6Ly9mb3JnZXJvY2stYW0uc2VydmljZS5jb3JlLWNvbXB1dGUtaWRhbS1kZW1vLmludGVybmFsOjg0NDMvb3BlbmFtL29hdXRoMi9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6IjU5ZTI0MDk4LTc3NDgtNDJhNi04MDE4LTA1NjZkZTMwYTkwMCIsImF1ZCI6Inh1aW1vd2ViYXBwIiwibmJmIjoxNTY3NDIyOTYwLCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciJdLCJhdXRoX3RpbWUiOjE1Njc0MjI5NjAwMDAsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNTY3NDUxNzYwLCJpYXQiOjE1Njc0MjI5NjAsImV4cGlyZXNfaW4iOjI4ODAwLCJqdGkiOiIxMTBmNTJlYS0wMWY3LTRmYWMtOTBlYS1hZDhjZjQ3ZDFjYTIifQ.oUufd-JuA50yVNQjI7MJ_XN1rNVSnfNcIFZBOmDyiAzy2J0JE5GmwyVNVRdxTXynhsW9VQtaftoPTUj1yi6He9lkVaESf5GQILEZZJpKZiDcB1n4p6qP8c8KZQ-57tx0RD6no0MgtPJhnN8cASnfeoYKft_wimimvAINPKFptKbB1Lsrof5earbxH5VAca4cFb3RFRX0GrUyD_8Jxbvy1apHfK7KpEYI9bbW3lf3kFtxzJq7Kx4LU3BtFcMZ7uyVilFuKeUpG3jMlGeMCgffHLBnjdiNuOTh56Uygrh9v-l2bj2hyU8NFWJvQPU4fzVf0bYXkHHwK6tbhQfGA7N6lw"
  )

  //    "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiS0N4QmRlaHNIVUY2OTc4U2l6dklTRXhjWDBFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJleHVpZ2R6MmV1QG1haWxpbmF0b3IuY29tIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiY2M3Nzc5MDgtNzM3Yi00OWVkLWIxNzItYjMwMmJlODBjYzA3IiwiaXNzIjoiaHR0cHM6Ly9mb3JnZXJvY2stYW0uc2VydmljZS5jb3JlLWNvbXB1dGUtaWRhbS1kZW1vLmludGVybmFsOjg0NDMvb3BlbmFtL29hdXRoMi9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6IjNlMTZjNzg3LTc1YjAtNDFlZi04MGY1LTJhMzRmOGVkNDEzNCIsImF1ZCI6Inh1aW1vd2ViYXBwIiwibmJmIjoxNTY2OTIwNjI2LCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciJdLCJhdXRoX3RpbWUiOjE1NjY5MjA2MjYwMDAsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNTY2OTQ5NDI2LCJpYXQiOjE1NjY5MjA2MjYsImV4cGlyZXNfaW4iOjI4ODAwLCJqdGkiOiIyMTEwOWZiYy1iZDNkLTRiNzctOWRkMC0yZTFlZGM2NzliYmQifQ.SUlg2BIsSCs2vRf_klWRlPq24x0bXYJM7yLl6Vg6p52Yvc5fnyZjangogGd_Zim0AkiH-9enOTUIvjvKAomWn6lw7JirHAXTOxkrnAc4j4dtXXWj588NuhuTYJxSirgyybF3o5hWjMvcLPpZiDuIHlxNruYFy_fH_I3Y3FGjKs825c60xUq8OkyIF5KQTvoaRddc8D_i9YWpZj1XpUSDC1ozE5dme4BnQ5vyzQNXv4A9_ZU8LV4fnQ1G4oEeKD5viviWfbXhR6ePD6XkBRVidbyeD8jg9R5OwVsWKVY6c5w_YttTWpu2Q8ZQKFMa5VdHRjnoawpINHW4YAgDSscUCA")

  val headers_12 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "authorization" -> "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiS0N4QmRlaHNIVUY2OTc4U2l6dklTRXhjWDBFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJleHVpZ2R6MmV1QG1haWxpbmF0b3IuY29tIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiOTQ2ZWMwMDgtYzc4Ni00NDVjLWE2MTctNDMyYjgwYTMyMGI3IiwiaXNzIjoiaHR0cHM6Ly9mb3JnZXJvY2stYW0uc2VydmljZS5jb3JlLWNvbXB1dGUtaWRhbS1kZW1vLmludGVybmFsOjg0NDMvb3BlbmFtL29hdXRoMi9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6IjU5ZTI0MDk4LTc3NDgtNDJhNi04MDE4LTA1NjZkZTMwYTkwMCIsImF1ZCI6Inh1aW1vd2ViYXBwIiwibmJmIjoxNTY3NDIyOTYwLCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciJdLCJhdXRoX3RpbWUiOjE1Njc0MjI5NjAwMDAsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNTY3NDUxNzYwLCJpYXQiOjE1Njc0MjI5NjAsImV4cGlyZXNfaW4iOjI4ODAwLCJqdGkiOiIxMTBmNTJlYS0wMWY3LTRmYWMtOTBlYS1hZDhjZjQ3ZDFjYTIifQ.oUufd-JuA50yVNQjI7MJ_XN1rNVSnfNcIFZBOmDyiAzy2J0JE5GmwyVNVRdxTXynhsW9VQtaftoPTUj1yi6He9lkVaESf5GQILEZZJpKZiDcB1n4p6qP8c8KZQ-57tx0RD6no0MgtPJhnN8cASnfeoYKft_wimimvAINPKFptKbB1Lsrof5earbxH5VAca4cFb3RFRX0GrUyD_8Jxbvy1apHfK7KpEYI9bbW3lf3kFtxzJq7Kx4LU3BtFcMZ7uyVilFuKeUpG3jMlGeMCgffHLBnjdiNuOTh56Uygrh9v-l2bj2hyU8NFWJvQPU4fzVf0bYXkHHwK6tbhQfGA7N6lw"
  )
      //"eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiS0N4QmRlaHNIVUY2OTc4U2l6dklTRXhjWDBFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJleHVpZ2R6MmV1QG1haWxpbmF0b3IuY29tIiwiYXV0aF9sZXZlbCI6MCwiYXVkaXRUcmFja2luZ0lkIjoiY2M3Nzc5MDgtNzM3Yi00OWVkLWIxNzItYjMwMmJlODBjYzA3IiwiaXNzIjoiaHR0cHM6Ly9mb3JnZXJvY2stYW0uc2VydmljZS5jb3JlLWNvbXB1dGUtaWRhbS1kZW1vLmludGVybmFsOjg0NDMvb3BlbmFtL29hdXRoMi9obWN0cyIsInRva2VuTmFtZSI6ImFjY2Vzc190b2tlbiIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJhdXRoR3JhbnRJZCI6IjNlMTZjNzg3LTc1YjAtNDFlZi04MGY1LTJhMzRmOGVkNDEzNCIsImF1ZCI6Inh1aW1vd2ViYXBwIiwibmJmIjoxNTY2OTIwNjI2LCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciJdLCJhdXRoX3RpbWUiOjE1NjY5MjA2MjYwMDAsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNTY2OTQ5NDI2LCJpYXQiOjE1NjY5MjA2MjYsImV4cGlyZXNfaW4iOjI4ODAwLCJqdGkiOiIyMTEwOWZiYy1iZDNkLTRiNzctOWRkMC0yZTFlZGM2NzliYmQifQ.SUlg2BIsSCs2vRf_klWRlPq24x0bXYJM7yLl6Vg6p52Yvc5fnyZjangogGd_Zim0AkiH-9enOTUIvjvKAomWn6lw7JirHAXTOxkrnAc4j4dtXXWj588NuhuTYJxSirgyybF3o5hWjMvcLPpZiDuIHlxNruYFy_fH_I3Y3FGjKs825c60xUq8OkyIF5KQTvoaRddc8D_i9YWpZj1XpUSDC1ozE5dme4BnQ5vyzQNXv4A9_ZU8LV4fnQ1G4oEeKD5viviWfbXhR6ePD6XkBRVidbyeD8jg9R5OwVsWKVY6c5w_YttTWpu2Q8ZQKFMa5VdHRjnoawpINHW4YAgDSscUCA")

  val headers_14 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  //val uri2 = "https://idam-web-public.demo.platform.hmcts.net"

  //val scn = scenario("ManageOrganisationOriginal")

  val MOHome = exec(http("manageorganisation_homepage")
    .get("/")
    .headers(headers_0))

    .exec(http("request_1")
      .get("/api/user/details")
      .headers(headers_1)
      .check(status.is(401)))

    .exec(http("idam_loginpage")
      .get(IdamUrl + "/?response_type=code&client_id=xuimowebapp&redirect_uri=" + BaseURL + "/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
      .headers(headers_2)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken1"))
    )

    .pause(5)

  val MOLogin = exec(http("manageorganisation_login")
    .post(IdamUrl + "/login?scope=openid+profile+roles+manage-user+create-user&response_type=code&redirect_uri=" + BaseURL + "%2foauth2%2fcallback&client_id=xuimowebapp")
    .headers(headers_4)
    .disableFollowRedirect
    .formParam("username", "exuigdz2eu@mailinator.com")
    .formParam("password", "Compaq123!")
    .formParam("save", "Sign in")
    .formParam("selfRegistrationEnabled", "false")
    //.formParam("_csrf", "c0a9f3cb-971d-47bc-a5a1-696eae6056ef"))
    .formParam("_csrf", "${csrfToken1}")
    //.check(headerRegex("Set-Cookie", "__auth__=(.*); Path").saveAs("AuthHeader"))
    .check(headerRegex("Set-Cookie", "Idam.Session=*AAJTSQACMDEAAlMxAAA.*(.*)").saveAs("AuthHeader"))
  )
    .pause(2)

  val MOAPI = exec(http("request_5")
    .get("/api/user/details")
    .headers(headers_5))

    .exec(http("request_6")
      .get("/api/organisation")
      .headers(headers_5))

    .exec(http("request_7")
      .get("/api/organisation/"))

    .pause(5)

    .exec(http("request_9")
      .get("/api/userList")
      .headers(headers_5))

    .pause(5)

    .exec(http("request_11")
      .get("/api/jurisdictions")
      .headers(headers_5))

    .pause(5)

    .exec(http("request_12")
      .post("/api/inviteUser")
      .headers(headers_12)
      .body(RawFileBody("0012_request.json")))


    .pause(8)

  val MOLogout = exec(http("request_14")
      .get("/api/logout")
      .headers(headers_14))

    .exec(http("request_15")
      .get("/api/user/details")
      .headers(headers_1)
      .check(status.is(401)))

    .exec(http("request_16")
      .get(IdamUrl + "/?response_type=code&client_id=xuimowebapp&redirect_uri=" + BaseURL + "/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
      .headers(headers_2))
}
