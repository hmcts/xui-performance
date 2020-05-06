package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.EXUIIACMC.{MaxThinkTime, MinThinkTime}
import uk.gov.hmcts.reform.exui.performance.scenarios.EXUIManageCase.headers_51
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment

object EXUIManageCaseCreation {


  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular

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
    "Origin" -> "Origin" -> "https://manage-case.perftest.platform.hmcts.net/",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_84 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-draft-create.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> "https://manage-case.perftest.platform.hmcts.net/",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")

  val headers_88 = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Origin" -> "https://manage-case.perftest.platform.hmcts.net/",
    "Request-Id" -> "|aPld4.8/RnX",
    "Sec-Fetch-Mode" -> "cors")



  val headers_59 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://idam-web-public.perftest.platform.hmcts.net",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_prosearch = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Mode" -> "cors",
    "experimental" -> "true")


  /*val manageCasesHomePage=	group ("TX01_EXUI_ManageCases_Homepage") {

   // feed(loginFeeder).
      exec(http("XUIMC01_OB010_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

      .exec(http("XUIMC01_020_Login_LandingPage")
        .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
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

    exec(http("XUIMC01_030_Login_SubmitLoginpage")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .formParam("username", "fplorg-a00rw_user1@mailinator.com")
      .formParam("password", "Pass19word")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_59))
  }
    .pause(60)


  val manageCase_Logout = group ("TX01_EXUI_ManageCases_Logout") {
    exec(http("XUIMC01_140__Logout")
      .get("/api/logout")
      .headers(headers_69)
      //  .check(regex("Sign in")))
      .check(status.in(200,304)))
  }*/

  val casedetails= group("EXUI View Case")
  {


      exec(http("XUIPROB_020_005_Pagination")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata")
      .headers(headers_28)
        .check(status.is(200)))
      .exec(http("XUIPROB_020_010_AccessRead")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(headers_28)
        .check(status.in(200,304)))
      .exec(http("XUIPROB_020_015_WorkBasket")
      .get("/data/internal/case-types/GrantOfRepresentation/work-basket-inputs")
      .headers(headers_prosearch)
        .check(status.is(200)))

        .exec(http("XUIPROB_020_020_CaseReference1")
          .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?case_reference=${caseId}")
          .headers(headers_prosearch)
        .check(status.is(200)))
        .pause(10)

        .exec(http("XUIPROB_020_025_CaseReference1")
          .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?case_reference=${caseId}")
          .headers(headers_51))
        .pause(10)
  }

  val casecreation= group("Probate Create")
  {


    exec(http("XUIPROB_010_005_SolAppCreated")
      .get("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata?state=SolAppCreated")
      .headers(headers_28)
      .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)
      .exec(http("XUIPROB_010_010SolAppCreatedPage1")
      .get("/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&state=SolAppCreated&page=1")
      .headers(headers_28)
        .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)
  /*  .exec(http("request_75")
      .get("/api/healthCheck?path=/cases/case-filter")
      .headers(headers_5)*/
      .exec(http("XUIPROB_010_015_AccessCreate")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(headers_28)
      .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)
    /*.exec(http("request_77")
      .get("/api/healthCheck?path=/cases/case-create/PROBATE/GrantOfRepresentation/solicitorCreateApplication")
      .headers(headers_5))*/
      .exec(http("XUIPROB_010_020_AccessCreate")
        .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
        .headers(headers_78)
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
      .pause(MinThinkTime, MaxThinkTime)

       .exec(http("XUIPROB_010_025_CreateCasePage1")
          .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
          .headers(headers_78)
      .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUIPROB_010_030_AddressLookup")
      .get("/api/addresses?postcode=TW33SD")
      .headers(headers_28)
      .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)

      .feed(Feeders.createCaseData)
      .exec(http("XUIPROB_010_035_AddressLookupCaseCreateFinal")
      .post("/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?ignore-warning=false")
      .headers(headers_88)
      .body(ElFileBody("RecordedSimulationcasecreate1810_0088_request.json")).asJson
      .check(status.in(200,304))
      .check(jsonPath("$.id").optional.saveAs("caseId")))
      .pause(50)

  }








}
