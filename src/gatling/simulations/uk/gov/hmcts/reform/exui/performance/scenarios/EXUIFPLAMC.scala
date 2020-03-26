package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment

object EXUIFPLAMC {

  /*val httpProtocol = http
    .baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
*/

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseFPLAURL
  val loginFeeder = csv("OrgId.csv").circular





  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_3 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_5 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_7 = Map(
    "accept" -> "text/css,*/*;q=0.1",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "cross-site")

  val headers_9 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_10 = Map(
    "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "cross-site")

  val headers_12 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_13 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Access-Control-Request-Headers" -> "content-type,experimental",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_14 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Access-Control-Request-Headers" -> "content-type",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_15 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_16 = Map(
    "Accept" -> "application/json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_17 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "cross-site",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_20 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_30 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://idam-web-public.aat.platform.hmcts.net",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_57 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-banners.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_59 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_68 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_69 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Access-Control-Request-Headers" -> "content-type,experimental",
    "Access-Control-Request-Method" -> "POST",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_70 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_72 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_74 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_76 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_80 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_139 = Map(
    "Accept" -> "",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundaryucjE7WrGyxbtCSQN",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_140 = Map(
    "Accept" -> "",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundary9n7AAqp1SNXC8LRR",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_160 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> "https://ccd-case-management-web-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_179 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "cross-site",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val manageCasesHomePage=	group ("TX03_EXUI_ManageCases_Homepage") {

    feed(loginFeeder)
      .exec(http("XUIMC02_OB010_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

      .exec(http("XUIMC02_020_Login_LandingPage")
        .get(IdamUrl + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=https%3A%2F%2Fccd-case-management-web-aat.service.core-compute-aat.internal%2Foauth2redirect")
        .headers(headers_17)
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
      .post(IdamUrl + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=https%3A%2F%2Fccd-case-management-web-aat.service.core-compute-aat.internal%2Foauth2redirect")
      .formParam("username", "james@swansea.gov.uk")
      .formParam("password", "Password12")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_30))
  }
    .pause(10)




  val manageCase_Logout = group ("TX01_EXUI_IAC_ManageCases_Logout") {
    exec(http("XUIMC01_140__Logout")
      .get("/logout")
        .headers(headers_14)
      //  .check(regex("Sign in")))
      .check(status.in(200,304)))


  }

  val shareacase= group("EXUI_Manage_Representative")
  {
    exec(http("XUIMC02_OB010_Homepage")
    .get("/")
    .headers(headers_0)
    .check(status.is(200)))

    .exec(http("XUIMC02_020_Login_LandingPage")
      .get(IdamUrl + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=https%3A%2F%2Fccd-case-management-web-aat.service.core-compute-aat.internal%2Foauth2redirect")
      .headers(headers_17)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))
    .pause(10)

      .exec(http("XUIMC01_030_Login_SubmitLoginpage")
      .post(IdamUrl + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=https%3A%2F%2Fccd-case-management-web-aat.service.core-compute-aat.internal%2Foauth2redirect")
      .formParam("username", "hmcts-admin@example.com")
      .formParam("password", "Password12")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_30))
    .pause(10)
    
    



        }

  val fplacasecreation= group("TX01_EXUI_ManageCases_FPLA_Create")
  {
      exec(http("XUIMC01_050_SolAppCreatedPage1")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(headers_16)
        .check(status.in(200,304)))
    .pause(5)

      .exec(http("XUIMC01_070_Access-Create")
        .get("/data/internal/case-types/CARE_SUPERVISION_EPO/event-triggers/openCase?ignore-warning=false")
        .headers(headers_68)
        .check(status.is(200)))
       // .check(jsonPath("$.event_token").optional.saveAs("event_token")))
        .pause(4)

      /* .exec {
             session =>
               println("this is event token ....." + session("event_token").as[String])
               session
           }*/

        /*.exec(http("request_9")
          .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=openCase1")
          .headers(headers_70)
          .body(ElFileBody("RecordedSimulationFPLA_0070_request.json")).asJson
          .check(status.is(200))
        )
          .pause(3)
     .exec(http("request_14")
       .post("/data/case-types/CARE_SUPERVISION_EPO/cases?ignore-warning=false")
       .headers(headers_72)
       .body(ElFileBody("RecordedSimulationFPLA_0072_request.json")).asJson
   .check(status.in(200,304)))
      //  .check(jsonPath("$.id").optional.saveAs("caseId")))
     .pause(5)
      /*  .exec {
          session =>
            println("this is caseId ....." + session("caseId").as[String])
            session
        }*/
      .exec(http("request_74")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
        .check(status.in(200,304)))
        .pause(5)
        .exec(http("request_75")
        .get("/data/internal/cases/${caseId}/event-triggers/ordersNeeded?ignore-warning=false")
        .headers(headers_76)
        .check(status.in(200,304)))
    .pause(5)
        .exec(http("request_76")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=ordersNeeded1")
        .headers(headers_70)
        .body(ElFileBody("RecordedSimulationFPLA_0078_request.json")).asJson
          .check(status.in(200,304)))
    .pause(1)
        .exec(http("request_76")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(ElFileBody("RecordedSimulationFPLA_0080_request.json")).asJson
          .check(status.in(200,304)))
        .pause(1)

      .exec(http("request_82")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
    .check(status.in(200,304)))
    .pause(1)

    //hearing needed
    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/hearingNeeded?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
    .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=hearingNeeded1")
    .headers(headers_70)
    .body(ElFileBody("RecordedSimulationFPLA_0086_request.json")).asJson
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
    .post("/data/cases/${caseId}/events")
    .headers(headers_80)
    .body(ElFileBody("RecordedSimulationFPLA_0088_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

      .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
    .check(status.in(200,304)))
    .pause(1)

    //enter children

    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/enterChildren?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterChildren1")
      .headers(headers_70)
      .body(ElFileBody("RecordedSimulationFPLA_0094_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(ElFileBody("RecordedSimulationFPLA_0096_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))
    .pause(1)

    //enter respondants
    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/enterRespondents?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_101")
      .get("/addresses?postcode=TW33SD")
        .headers(headers_16)
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterRespondents1")
      .headers(headers_70)
      .body(ElFileBody("RecordedSimulationFPLA_0104_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_105")
             .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterRespondents1")
        .headers(headers_70)
        .body(ElFileBody("RecordedSimulationFPLA_0106_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(ElFileBody("RecordedSimulationFPLA_0108_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))
    .pause(1)

// enter applicant
    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/enterApplicant?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_101")
      .get("/addresses?postcode=TW33SD")
      .headers(headers_16)
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterApplicant1")
      .headers(headers_70)
      .body(ElFileBody("RecordedSimulationFPLA_0116_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(ElFileBody("RecordedSimulationFPLA_0118_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))
    .pause(1)

// enter grounds
    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/enterGrounds?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterGrounds1")
      .headers(headers_70)
      .body(ElFileBody("RecordedSimulationFPLA_0124_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(ElFileBody("RecordedSimulationFPLA_0126_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))
    .pause(1)
//other proposal

    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/otherProposal?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=otherProposal1")
      .headers(headers_70)
      .body(ElFileBody("RecordedSimulationFPLA_0132_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(ElFileBody("RecordedSimulationFPLA_0134_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))
    .pause(1)
// upload documents

    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/uploadDocuments?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_139")
      .post("/documents")
      .headers(headers_139)
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_140")
      .post("/documents")
      .headers(headers_140)
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=uploadDocuments1")
      .headers(headers_70)
      .body(ElFileBody("RecordedSimulationFPLA_0142_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(ElFileBody("RecordedSimulationFPLA_0144_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))
    .pause(1)

    // submit application


    .exec(http("request_82")
    .get("/data/internal/cases/${caseId}/event-triggers/submitApplication?ignore-warning=false")
    .headers(headers_76)
    .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_82")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=submitApplication1")
      .headers(headers_70)
      .body(ElFileBody("RecordedSimulationFPLA_0150_request.json")).asJson
      .check(status.in(200,304)))
    .pause(1)


    .exec(http("request_82")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(ElFileBody("RecordedSimulationFPLA_0152_request.tx"))
      .check(status.in(200,304)))
    .pause(1)

    .exec(http("request_90")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))
    .pause(1)*/





  }

}
