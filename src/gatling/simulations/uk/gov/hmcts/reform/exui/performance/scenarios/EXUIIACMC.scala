package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment

object EXUIIACMC {

  /*val httpProtocol = http
    .baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
*/

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  val loginFeeder = csv("OrgId.csv").circular

  //headers



  val headers_0 = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_1 = Map(
    "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "cross-site")

  val headers_2 = Map(
    "accept" -> "application/json",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_4 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_8 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_9 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "origin" -> "https://xui-webapp-aat.service.core-compute-aat.internal",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_11 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_32 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "origin" -> "https://xui-webapp-aat.service.core-compute-aat.internal",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_34 = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val headers_35 = Map(
    "accept" -> "*/*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "same-origin")

  val headers_36 = Map(
    "accept" -> "text/css,*/*;q=0.1",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "same-origin")

  val headers_44 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_45 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_48 = Map(
    "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "same-origin")

  val headers_50 = Map(
    "accept" -> "*/*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "origin" -> "https://xui-webapp-aat.service.core-compute-aat.internal",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_62 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "origin" -> "https://xui-webapp-aat.service.core-compute-aat.internal",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_73 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_120 = Map(
    "accept" -> "*/*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "if-modified-since" -> "Fri, 24 Jan 2020 01:10:36 GMT",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "cross-site")

  val headers_122 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "cross-site",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_123 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_125 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_126 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_login = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "cross-site",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_login_submit = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://idam-web-public.aat.platform.hmcts.net",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val manageCasesHomePage=	group ("TX01_EXUI_ManageCases_Homepage") {

    feed(loginFeeder)
      .exec(http("XUIMC02_OB010_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

      .exec(http("XUIMC02_020_Login_LandingPage")
        .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-aat.service.core-compute-aat.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
        .headers(headers_login)
        .check(regex("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))
      .pause(10)



     /* .exec {
        session =>
          println("this is csrf....." + session("csrfToken").as[String])
          session
      }*/
  }


  val manageCaseslogin = group ("TX01_EXUI_ManageCases_Login") {

    exec(http("XUIMC01_030_Login_SubmitLoginpage")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-aat.service.core-compute-aat.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .formParam("username", "ia.legalrep.bbb.xui@protonmail.com")
      .formParam("password", "Aldg@teT0wer")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_login_submit))
  }
    .pause(10)


  val manageCase_Logout = group ("TX01_EXUI_IAC_ManageCases_Logout") {
    exec(http("XUIMC01_140__Logout")
      .get("/api/logout")
        .headers(headers_34)
      //  .check(regex("Sign in")))
      .check(status.in(200,304)))
  }

  val shareacase= group("EXUI_filter")
  {
    exec(http("request_100")
    .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F1580904712599223%2Ftrigger%2FshareACase")
    .headers(headers_0))
      .exec(http("request_102")
      .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F1580904712599223%2Ftrigger%2FshareACase%2FshareACaseshareACase")
      .headers(headers_0))
      .exec(http("request_105")
        .post("/data/case-types/Asylum/validate?pageId=shareACaseshareACase")
        .headers(headers_9)
        .body(RawFileBody("RecordedSimulationiacexui_0105_request.json")))



  }

  /*val filtercaselist= group("EXUI_filter")
  {
    /*exec(http("EXUI_AO_005_Caselist")
    .get("/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata?state=AwaitingPayment")
      .headers(headers_0)
      .check(status.is(200)))*/

      exec(http("request_69")
      .get("/data/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases/pagination_metadata")
      .headers(headers_28)
        .check(status.is(200)))
      .exec(http("request_70")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(headers_28)
        .check(status.in(200,304)))
      .exec(http("request_72")
      .get("/data/internal/case-types/Asylum/search-inputs")
      .headers(headers_72)
        .check(status.is(200)))
        .exec(http("request_70")
        .get("/data/internal/cases/1580904712599223")
        .headers(headers_44))
        .exec(http("request_74")
          .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F1580904712599223")
          .headers(headers_0)
        .exec(http("request_92")
          .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F1580904712599223%23appeal")
          .headers(headers_0))

          .exec(http("request_94")
            .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F1580904712599223%23caseDetails")
            .headers(headers_0))

          .exec(http("request_96")
            .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F1580904712599223%23documents")
            .headers(headers_0))

          .exec(http("request_98")
            .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F1580904712599223%23directions")
            .headers(headers_0)
  }
*/
  val iaccasecreation= group("TX01_EXUI_ManageCases_IAC_Create")
  {

      exec(http("XUIMC01_050_SolAppCreatedPage1")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(headers_2)
        .check(status.in(200,304)))
    .pause(5)

      .exec(http("XUIMC01_070_Access-Create")
        .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
        .headers(headers_4)
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
        .pause(4)

       .exec {
             session =>
               println("this is event token ....." + session("event_token").as[String])
               session
           }

        .exec(http("request_9")
        .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
        .headers(headers_9)
          .body(ElFileBody("RecordedSimulationiacexui_0009_request.json")).asJson
          .check(status.is(200))
        )
          .pause(3)
     .exec(http("request_14")
       .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecision")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0014_request.json")).asJson
   .check(status.in(200,304)))
     .pause(5)

     .exec(http("request_16")
       .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0016_request.json")).asJson
       .check(status.in(200,304)))
     .pause(5)

     .exec(http("request_18")
     .get("/api/addresses?postcode=TW33SD")
     .headers(headers_2))
     .pause(5)
     .exec(http("request_19")
       .post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0019_request.json")).asJson
       .check(status.in(200,304)))
     .pause(5)

     .exec(http("request_21")
       .post("/data/case-types/Asylum/validate?pageId=startAppealappealType")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0021_request.json")).asJson
       .check(status.in(200,304)))
     .pause(5)


     .exec(http("request_23")
       .post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsRevocation")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0023_request.json")).asJson
       .check(status.in(200,304)))
     .pause(5)

     .exec(http("request_25")
       .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0025_request.json")).asJson
       .check(status.in(200,304)))
     .pause(5)

     .exec(http("request_27")
       .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0027_request.json")).asJson
       .check(status.in(200,304)))
     .pause(5)

     .exec(http("request_29")
       .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
       .headers(headers_9)
       .body(ElFileBody("RecordedSimulationiacexui_0029_request.json")).asJson
       .check(status.in(200,304)))
     .pause(5)

   .exec(http("request_32")
       .post("/data/case-types/Asylum/cases?ignore-warning=false")
       .headers(headers_32)
       .body(ElFileBody("RecordedSimulationiacexui_0032_request.json")).asJson
       .check(status.in(200,304))
     .check(jsonPath("$.id").optional.saveAs("caseId"))
   )
     .pause(5)

        .exec {
          session =>
            println("this is caseId ....." + session("caseId").as[String])
            session
        }

        .exec(http("request_34")
        .get("/case/IA/Asylum/${caseId}/trigger/submitAppeal")
        .check(status.in(200,304)))
        .pause(5)

          .exec(http("request_44")
        .get("/data/internal/cases/${caseId}")
            .headers(headers_44)
            .check(status.in(200,304)))
        .pause(5)

          .exec(http("request_45")
          .get("/data/internal/cases/${caseId}/event-triggers/submitAppeal?ignore-warning=false")
          .headers(headers_45)
        .check(status.in(200,304)))
    .pause(5)


                        .exec(http("request_55")
                          .post("/data/case-types/Asylum/validate?pageId=submitAppealsubmissionOutOfTimePage")
                          .headers(headers_9)
                          .body(ElFileBody("RecordedSimulationiacexui_0055_request.json")).asJson
                          .check(status.in(200,304)))
                        .pause(5)

    .exec(http("request_58")
      .post("/data/case-types/Asylum/validate?pageId=submitAppealdeclaration")
      .headers(headers_9)
      .body(ElFileBody("RecordedSimulationiacexui_0058_request.json")).asJson
      .check(status.in(200,304)))
    .pause(5)

                                .exec(http("request_61")
                              .get("/data/internal/profile")
                              .headers(headers_8)
        .check(status.in(200,304)))
    .pause(5)
                                .exec(http("request_62")
                                .post("/data/cases/${caseId}/events")
                                .headers(headers_62)
                                  .body(ElFileBody("RecordedSimulationiacexui_0062_request.json"))
        .check(status.in(200,304)))
    .pause(5)

      .exec(http("request_63")
      .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FsubmitAppeal%2Fconfirm")
      .headers(headers_0)
        .check(status.in(200,304)))
        .pause(5)


  }

}
