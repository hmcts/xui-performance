package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.exui.performance.Feeders

object ExUI {

  /*val httpProtocol = http
    .baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
*/

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val url="https://xui-mo-webapp-demo.service.core-compute-demo.internal"

  val headers_co1 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")



  val headers_co2 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors")




  val createOrg=
    exec(http("EXUI_RO_Homepage")
      .get(url+"/register-org/register")
      .headers(headers_co1)
      .check(status.is(200)))
      //.check(regex("Register to manage civil and family law cases")))
      .pause(Environment.minThinkTime,Environment.maxThinkTime)

      .feed(Feeders.createDynamicDataFeeder).exec(http("EXUI_RO_Create")
      .post(url+"/external/register-org/register")
      .headers(headers_co2)
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
    "Origin" -> IdamUrl,
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

  //val uri2 = "https://idam-web-public.demo.platform.hmcts.net"
  val url_approve="https://xui-ao-webapp-demo.service.core-compute-demo.internal"
  //val uri3 = "https://www.google-analytics.com"

  val approveOrgHomePage=	group ("EXUI_AO_Homepage") {

    exec(http("EXUI_AO_005_Homepage")
      .get(url_approve + "/")
      .headers(headers_approve)
      .check(status.is(200)))


      .exec(http("EXUI_AO_010_Homepage")
        .get(IdamUrl + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri=https://xui-ao-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
        .headers(headers_6)
        .check(regex("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

      .exec {
        session =>
          println("this is csrf....." + session("csrfToken").as[String])
          session
      }
  }
    .pause(Environment.minThinkTime)

  val approveOrganisationlogin = group ("EXUI_AO_Login") {

    exec(http("EXUI_AO_005_Login")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri=https://xui-ao-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
      .headers(headers_20)
      .formParam("username", "sourav.bhattacharya@hmcts.net")
      .formParam("password", "ReferenceData2019")
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
    .pause(Environment.minThinkTime)

  val approveOrganisationApprove =
    exec(http("EXUI_AO_Approve")
      .put(url_approve+"/api/organisations/${orgRefCode}")
      .headers(headers_26)
      //.body(RawFileBody("AO.json")))
      .body(ElFileBody("AO.json")).asJson
      .check(status.is(200)))

      .pause(Environment.constantthinkTime)

  val approveOrganisationLogout = group ("EXUI_AO_Logout") {
    exec(http("EXUI_AO_005_Logout")
      .get(url_approve + "/api/logout")
      .headers(headers_approvelogout)
      //  .check(regex("Sign in")))
      .check(status.is(200)))
  }

  // following code is for manage organisation

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
    "Origin" -> IdamUrl,
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

  //val uri22 = "https://idam-web-public.demo.platform.hmcts.net"
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
        .get(IdamUrl + "/?response_type=code&client_id=xuimowebapp&redirect_uri=https://xui-mo-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
        .headers(headers_2)
        .check(regex("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken1"))
      )
  }
    /*http("request_3")
.get(uri3 + "?v=1&_v=j79&a=1361156712&t=pageview&_s=1&dl=https%3A%2F%2Fidam-web-public.demo.platform.hmcts.net%2Flogin%3Fscope%3Dopenid%2Bprofile%2Broles%2Bmanage-user%2Bcreate-user%26response_type%3Dcode%26redirect_uri%3Dhttps%253a%252f%252fxui-mo-webapp-demo.service.core-compute-demo.internal%252foauth2%252fcallback%26client_id%3Dxuimowebapp&dr=https%3A%2F%2Fxui-mo-webapp-demo.service.core-compute-demo.internal%2Fhome&ul=en-us&de=UTF-8&dt=Sign%20in%20-%20HMCTS%20Access&sd=24-bit&sr=1280x720&vp=437x610&je=0&_u=IEBAAEAB~&jid=1616258456&gjid=75239786&cid=272823260.1566920599&tid=UA-122164129-2&_gid=1963206053.1566920599&_r=1&z=304471461")
.headers(headers_3))
.check(status.is(401)))*/
    .pause(Environment.minThinkTime)

  val manageOrganisationLogin = group ("EXUI_MO_Login") {
    exec(http("EXUI_MO_005_Login")
      .post(IdamUrl + "/login?scope=openid+profile+roles+manage-user+create-user&response_type=code&redirect_uri=https%3a%2f%2fxui-mo-webapp-demo.service.core-compute-demo.internal%2foauth2%2fcallback&client_id=xuimowebapp")
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
    .pause(Environment.constantthinkTime)

  val usersPage = group ("EXUI_MO_Userspage") {
    exec(http("EXUI_MO_005_Userspage")
      .get(url_mo + "/api/userList")
      .headers(headers_5)
      .check(status.is(200)))
  }
    .pause(Environment.constantthinkTime)

  val inviteUserPage = group ( "EXUI_MO_InviteUserpage") {
    exec(http("EXUI_MO_005_InviteUserpage")
      .get(url_mo + "/api/jurisdictions")
      .headers(headers_5)
      .check(status.is(200)))
  }
    .pause(Environment.minThinkTime)

  val sendInvitation = group ("EXUI_MO_SendInvitation") {
    feed(Feeders.createDynamicDataFeeder1).exec(http("EXUI_MO_005_SendInvitation")
      .post(url_mo + "/api/inviteUser")
      .headers(headers_12)
      .body(ElFileBody("MO.json")).asJson
      .check(status.is(200)))
  }

    .pause(Environment.constantthinkTime)

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
        .get(IdamUrl + "/?response_type=code&client_id=xuimowebapp&redirect_uri=https://xui-mo-webapp-demo.service.core-compute-demo.internal/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
        .headers(headers_2))
  }


}
