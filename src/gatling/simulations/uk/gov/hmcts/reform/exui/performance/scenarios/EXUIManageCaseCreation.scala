package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment

object EXUIManageCaseCreation {

  /*val httpProtocol = http
    .baseUrl("https://xui-mo-webapp-demo.service.core-compute-demo.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
*/

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgIdAAT.csv").circular

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




  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")


  val headers_28 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors")

  val headers_72 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")


  val headers_78 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_81 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_83 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Content-Type" -> "application/json",
    "Origin" -> "https://xui-webapp-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_84 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-draft-create.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://xui-webapp-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_88 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Origin" -> "https://xui-webapp-aat.service.core-compute-aat.internal",
    "Request-Id" -> "|aPld4.8/RnX",
    "Sec-Fetch-Mode" -> "cors")








  val headers_59 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://idam-web-public.aat.platform.hmcts.net",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val manageCasesHomePage=	group ("TX01_EXUI_ManageCases_Homepage") {

    //feed(loginFeeder)
      exec(http("EXUI_ManageCases_001_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

      .exec(http("EXUI_ManageCases_002_Landingpage")
       // .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-aat.service.core-compute-aat.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
        .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-aat.service.core-compute-aat.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
        .headers(headers_13)
        .check(regex("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))
      .pause(20)



     /* .exec {
        session =>
          println("this is csrf....." + session("csrfToken").as[String])
          session
      }*/
  }


  val manageCaseslogin = group ("TX01_EXUI_ManageCases_Login") {

    exec(http("EXUI_ManageCases_003_SubmitLoginpage")
      //.post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-aat.service.core-compute-aat.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=https://xui-webapp-aat.service.core-compute-aat.internal/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .formParam("username", "${generatedEmail}")
      .formParam("password", "Pass19word")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_59))
  }
    .pause(40)


  val manageCase_Logout = group ("TX01_EXUI_ManageCases_Logout") {
    exec(http("EXUI_ManageCases_014_Logout")
      .get("/api/logout")
      .headers(headers_69)
      //  .check(regex("Sign in")))
      .check(status.in(200,304)))
  }

  val filtercaselist= group("EXUI_filter")
  {
    /*exec(http("EXUI_AO_005_Caselist")
    .get("/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata?state=AwaitingPayment")
      .headers(headers_0)
      .check(status.is(200)))*/

      exec(http("request_69")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata")
      .headers(headers_28)
        .check(status.is(200)))
      .exec(http("request_70")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(headers_28)
        .check(status.in(200,304)))
      .exec(http("request_72")
      .get("/data/internal/case-types/GrantOfRepresentation/work-basket-inputs")
      .headers(headers_72)
        .check(status.is(200)))
  }

  val casecreation= group("TX01_EXUI_ManageCases_Create")
  {


    exec(http("EXUI_ManageCases_004_SolAppCreated")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?state=SolAppCreated")
      .headers(headers_28)
      .check(status.in(200,304)))
      .pause(20)
      .exec(http("EXUI_ManageCases_005_SolAppCreatedPage1")
      .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=SolAppCreated&page=1")
      .headers(headers_28)
        .check(status.in(200,304)))
    .pause(40)
  /*  .exec(http("request_75")
      .get("/api/healthCheck?path=/cases/case-filter")
      .headers(headers_5)*/
      .exec(http("EXUI_ManageCases_006_Access-Create")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(headers_28)
      .check(status.in(200,304)))
    .pause(40)
    /*.exec(http("request_77")
      .get("/api/healthCheck?path=/cases/case-create/PROBATE/GrantOfRepresentation/solicitorCreateApplication")
      .headers(headers_5))*/
      .exec(http("EXUI_ManageCases_007_Sol-Create-Application")
        .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
        .headers(headers_78)
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
        .pause(40)

      /* .exec {
             session =>
               println("this is event token ....." + session("event_token").as[String])
               session
           }*/

          /*.exec(http("request_79")
          .get("/api/healthCheck?path=/cases/case-create/PROBATE/GrantOfRepresentation/solicitorCreateApplication/solicitorCreateApplicationsolicitorCreateApplicationPage1")
          .headers(headers_5))*/
          .exec(http("EXUI_ManageCases_008_CreateCasePage1")
          .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
          .headers(headers_78)
      .check(status.in(200,304)))
        .pause(50)
     /* .exec(http("EXUI_ManageCases_009_InternalProfile")
          .get("/data/internal/profile")
          .headers(headers_81)
      .check(status.is(200)))
    .pause(5)*/
    .exec(http("EXUI_ManageCases_09_AddressLookup")
      .get("/api/addresses?postcode=TW33SD")
      .headers(headers_28))
    .pause(30)
    /*.exec(http("request_83")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage1")
      .headers(headers_83)
      .body(ElFileBody("RecordedSimulationcasecreate1810_0083_request.json")).asJson
      .check(status.is(200)))
      .exec(http("request_86")
        .post("/data/case-types/GrantOfRepresentation/drafts/")
        .headers(headers_84)
        .body(ElFileBody("RecordedSimulationcasecreate1810_0084_request.json")).asJson
        .check(status.is(404)))
    .pause(3)*/
      .feed(Feeders.createCaseData) .exec(http("EXUI_ManageCases_10_CaseCreateFinal")
      .post("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?ignore-warning=false")
      .headers(headers_88)
      .body(ElFileBody("RecordedSimulationcasecreate1810_0088_request.json")).asJson
      .check(status.in(200,304))
      .check(jsonPath("$.id").optional.saveAs("caseId")))
      .pause(30)

      /*.exec {
        session =>
          println("this is caseid  ....." + session("caseId").as[String])
          session
      }*/
  }








}
