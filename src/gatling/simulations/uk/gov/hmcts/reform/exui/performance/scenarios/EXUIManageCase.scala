package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment

object EXUIManageCase {

  /*val httpProtocol = http
    .baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
*/

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL

  //headers

  val headers_69 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_13 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "cross-site",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_26 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://idam-web-public.perftest.platform.hmcts.net",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_41 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors")

  val headers_45 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_51 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_59 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_61 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_66 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_169 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_10 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")
  val headers_40 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors")

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

  val headers_37 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
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

  val headers_480 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_500 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")


  val manageCasesHomePage=	group ("TX01_EXUI_ManageCases_Homepage") {

    exec(http("EXUI_AO_005_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

      .exec(http("EXUI_AO_010_Homepage")
        .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-perftest.service.core-compute-perftest.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
        .headers(headers_13)
        .check(regex("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

     /* .exec {
        session =>
          println("this is csrf....." + session("csrfToken").as[String])
          session
      }*/
  }
    .pause(Environment.minThinkTime)

  val manageCaseslogin = group ("EXUI_AO_Login") {

    exec(http("EXUI_AO_005_Login")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-perftest.service.core-compute-perftest.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .headers(headers_26)
      .formParam("username", "lukexuisuperuser@mailnesia.com")
      .formParam("password", "Monday01")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}"))
  }
    .pause(Environment.minThinkTime)


  val manageCase_Logout = group ("EXUI_MC_Logout") {
    exec(http("request_69")
      .get("/api/logout")
      .headers(headers_69)
      //  .check(regex("Sign in")))
      .check(status.is(200)))
  }

  val filtercaselist= group("EXUI_filter")
  {
    exec(http("EXUI_AO_005_Caselist")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata")
      .headers(headers_40)
      .check(status.is(200)))

    exec(http("EXUI_AO_006_Caselist")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(headers_40)
      .check(status.is(200)))

    exec(http("EXUI_AO_006_Caselist")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?state=SolAppCreated")
      .headers(headers_40)
      .check(status.is(200)))

    exec(http("EXUI_AO_006_Caselist")
      .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=SolAppCreated&page=1")
      .headers(headers_40)
      .check(status.is(200)))

    exec(http("EXUI_AO_006_Caselist")
      .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=SolAppCreated&page=1")
      .headers(headers_40)
      .check(status.is(200)))



  }

  val casedetails= group("EXUI_caseDetails")
  {

    exec(http("request_50")
      .get("/api/healthCheck?path=%2Fcases%2Fcase-search")
      .headers(headers_5))
      .exec(http("request_50")
        .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=SEARCH&page=1&case_reference=1571416945472483")
    .headers(headers_5))
      .exec(http("request_51")
        .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?case_reference=1571416945472483")
      .headers(headers_51))
    .pause(19)
    .exec(http("request_52")
      .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=SEARCH&page=1&case_reference=1571416945472483")
      .headers(headers_5))
    .pause(3)
      .exec(http("request_52")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(headers_5))
      .pause(3)
      .exec(http("request_52")
        .get("/data/internal/case-types/GrantOfRepresentation/search-inputs")
        .headers(headers_5))
      .pause(3)




  }

  val caseFind= group("TX01_EXUI_ManageCases_Details")
  {

   /* exec(http("EXUI_ManageCases_11_CaseSearch")
    .get("/api/healthCheck?path=%2Fcases%2Fcase-search")
    .headers(headers_1)*/
    exec(http("XUIMC01_110_Findcase")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?case_reference=${caseId}")
      .headers(headers_37)
      .check(status.in(200,304)))
      .pause(20)

        .exec(http("XUIMC01_120_Findcase_Page1")
        .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=SEARCH&page=1&case_reference=${caseId}")
        .headers(headers_37)
          .check(status.in(200,304)))
        .pause(30)
       /* .exec(http("EXUI_ManageCases_014_AccessRead")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(headers_37)
          .check(status.is(200)))
        .exec(http("EXUI_ManageCases_015_Search-Inputs")
        .get("/data/internal/case-types/GrantOfRepresentation/search-inputs")
        .headers(headers_480)
          .check(status.is(200)))
      .pause(5)*/

      .exec(http("XUIMC01_130_CaseDetails")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_500)
      .check(status.in(200,304)))
      .pause(100)
  }

  // following code is for manage organisation


  //val uri22 = "https://idam-web-public.demo.platform.hmcts.net"




}
