package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.LoginHeader
import uk.gov.service.notify.NotificationClient

object EXUIMCLogin {

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val headers_1 = Map(
      "Pragma" -> "no-cache",
      "Sec-Fetch-Dest" -> "empty",
      "Sec-Fetch-Mode" -> "cors",
      "Sec-Fetch-Site" -> "same-origin")

  val headers_4 = Map(
      "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
      "Pragma" -> "no-cache",
      "Sec-Fetch-Dest" -> "document",
      "Sec-Fetch-Mode" -> "navigate",
      "Sec-Fetch-Site" -> "same-origin",
      "Upgrade-Insecure-Requests" -> "1")

  //====================================================================================
  //Business process : Access Home Page by hitting the URL and relavant sub requests
  //below requests are Homepage and relavant sub requests as part of the login submission
  //=====================================================================================

  val manageCasesHomePage =
    tryMax(2) {

      exec(http("XUI${service}_010_005_Homepage")
           .get("/")
           .headers(LoginHeader.headers_0)
           .check(status.in(200,304))).exitHereIfFailed

      //   .exec(http("XUI${service}_010_010_Homepage")
      //         .get("/external/config/ui")
      //     .headers(LoginHeader.headers_0)
      //     .check(status.in(200,304)))

      // .exec(http("XUI${service}_010_015_HomepageTCEnabled")
      //       .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      //       .headers(LoginHeader.headers_hometc)
      //       .check(status.is(200)))

      // .exec(http("XUI${service}_010_020_HompageLoginPage")
      //       .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      //       .headers(LoginHeader.headers_login)
      //       .check(regex("Sign in"))
      //       .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
      //       .check(status.is(200)))

      .exec(http("XUI${service}_010_010_Homepage")
            .get("/assets/config/config.json")
            .headers(headers_1))

      .exec(http("XUI${service}_010_015_HomepageTCEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(headers_1))

      .exec(http("XUI${service}_010_020_HomepageIsAuthenticated")
            .get("/auth/isAuthenticated")
            .headers(headers_1))

      .exec(http("XUI${service}_010_020_Homepage")
            .get("/auth/login")
            .headers(headers_4)
            .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
            .check(regex("manage-user%20create-user&state=(.*)&client").saveAs("state"))
)
    }

    .pause( MinThinkTime, MaxThinkTime )


  val manageCasesHomePageGK =
    tryMax(2) {

      exec(http("XUI${service}_160_005_GKHomepage")
           .get("/")
           .headers(LoginHeader.headers_0)
           .check(status.in(200,304))).exitHereIfFailed

        .exec(http("XUI${service}_160_010_GKHomepage")
              .get("/external/config/ui")
              .headers(LoginHeader.headers_0)
              .check(status.in(200,304)))

      .exec(http("XUI${service}_160_015_GKHomepageTCEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_hometc)
            .check(status.is(200)))

      .exec(http("XUI${service}_160_020_GKHompageLoginPage")
            .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
            .headers(LoginHeader.headers_login)
            .check(regex("Sign in"))
            .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
            .check(status.is(200)))
    }

    .pause( MinThinkTime, MaxThinkTime )

  //==================================================================================
  //Business process : Enter the login details and submit
  //below requests are main login and relavant sub requests as part of the login submission
  //==================================================================================

  val manageCaseslogin =
    tryMax(2) {

      exec(http("XUI${service}_020_005_SignIn")
           //.post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
           .post(IdamUrl + "/login?response_type=code&redirect_uri=" + baseURL + "%2Foauth2%2Fcallback&scope=profile%20openid%20roles%20manage-user%20create-user&state=${state}&client_id=xuiwebapp")
           .formParam("username", "${user}")
           .formParam("password", "Pass19word")
           .formParam("save", "Sign in")
           .formParam("selfRegistrationEnabled", "false")
           .formParam("_csrf", "${csrfToken}")
           .headers(LoginHeader.headers_login_submit)
           .check(status.in(200, 304, 302))).exitHereIfFailed

      .exec(getCookieValue(
        CookieKey("__userid__").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("myUserId")))

      .exec(http("XUI${service}_020_010_Homepage")
            .get("/external/config/ui")
            .headers(LoginHeader.headers_0)
            .check(status.in(200,304)))

        /*.exec(http("XUI${service}_020_010_Homepage")
              .get("/external/config/ui")
              .headers(LoginHeader.headers_0)
              .check(status.in(200,304)))*/

      .exec(http("XUI${service}_020_015_SignInTCEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_38)
            .check(status.in(200, 304)))

     /* .exec(http("XUI${service}_020_020_SignInGetUserId")
            .get("/api/userTermsAndConditions/${myUserId}")
            .headers(LoginHeader.headers_tc))*/

     /* .exec(http("XUI${service}_020_025_SignInAcceptTCGet")
            .get("/accept-terms-and-conditions")
            .headers(LoginHeader.headers_tc_get)
            .check(status.in(200, 304)))*/

     /* .exec(http("XUI${service}_020_030_SignInTCEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_tc))*/

      .repeat(1, "count") {
        exec(http("XUI${service}_020_020_AcceptT&CAccessJurisdictions${count}")
             .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
             .headers(LoginHeader.headers_access_read)
             .check(status.in(200, 304, 302)))
      }

        .exec(http("XUI${service}_020_025_GetWorkBasketInputs")
              .get("/data/internal/case-types/CARE_SUPERVISION_EPO/work-basket-inputs")
              .headers(LoginHeader.headers_17))

        .exec(http("XUI${service}_020_030_GetPaginationMetaData")
              .get("/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata?state=Open")
              .headers(LoginHeader.headers_0))

        .exec(http("XUI${service}_020_035_GetDefaultWorkBasketView")
              .get("/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=WORKBASKET&state=Open&page=1")
              .headers(LoginHeader.headers_0))



    }

    .pause(MinThinkTime , MaxThinkTime)





  //======================================================================================
  //Business process : Click on Terms and Conditions
  //below requests are Terms and Conditions page and relavant sub requests
  // ======================================================================================

  val termsnconditions=
    tryMax(2) {
      exec(http("XUI${service}_030_005_ConfirmT&C")
           .post("/api/userTermsAndConditions")
           .headers(LoginHeader.headers_tc)
           .body(StringBody("{\"userId\":\"${myUserId}\"}"))
           .check(status.in(200, 304, 302))).exitHereIfFailed

      .exec(http("XUI${service}_030_010_AcceptT&CEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_hometc)
            .check(status.in(200, 304, 302)))

      .repeat(1, "count") {
        exec(http("XUI${service}_030_015_AcceptT&CAccessJurisdictions${count}")
             .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
             .headers(LoginHeader.headers_access_read)
             .check(status.in(200, 304, 302)))
      }

      .exec(http("XUI${service}_030_020_GetWorkBasketInputs")
            .get("/data/internal/case-types/CARE_SUPERVISION_EPO/work-basket-inputs")
            .headers(LoginHeader.headers_17))

      .exec(http("XUI${service}_030_025_GetPaginationMetaData")
            .get("/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata?state=Open")
            .headers(LoginHeader.headers_0))

      .exec(http("XUI${service}_030_030_GetDefaultWorkBasketView")
            .get("/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=WORKBASKET&state=Open&page=1")
            .headers(LoginHeader.headers_0))
    }

    .pause(MinThinkTime , MaxThinkTime )


  val managecasesadminlogin =
    tryMax(2) {

      exec(http("XUI${service}_020_005_SignIn")
           .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
           .formParam("username", "fpla.admin@mailinator.com")
           .formParam("password", "Pass19word")
           .formParam("save", "Sign in")
           .formParam("selfRegistrationEnabled", "false")
           .formParam("_csrf", "${csrfToken}")
           .headers(LoginHeader.headers_login_submit)
           .check(status.in(200, 304, 302))).exitHereIfFailed

        .exec(http("XUI${service}_020_010_Homepage")
              .get("/external/config/ui")
              .headers(LoginHeader.headers_0)
              .check(status.in(200,304)))

      .exec(http("XUI${service}_020_015_SignInTCEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_38)
            .check(status.in(200, 304)))

      .repeat(1, "count") {
        exec(http("XUI${service}_020_020_AcceptT&CAccessJurisdictions${count}")
             .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
             .headers(LoginHeader.headers_access_read)
             .check(status.in(200, 304, 302)))
      }

      .exec(http("XUI${service}_020_025_GetWorkBasketInputs")
            .get("/data/internal/case-types/CARE_SUPERVISION_EPO/work-basket-inputs")
            .headers(LoginHeader.headers_17))

      .exec(http("XUI${service}_020_030_GetPaginationMetaData")
            .get("/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata?state=Open")
            .headers(LoginHeader.headers_0))

      .exec(http("XUI${service}_020_035_GetDefaultWorkBasketView")
            .get("/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=WORKBASKET&state=Open&page=1")
            .headers(LoginHeader.headers_0))

    }

    .pause(MinThinkTime , MaxThinkTime)

  val managecasesgatekeeperlogin =
    tryMax(2) {

      exec(http("XUI${service}_170_005_GKSignIn")
           .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
           .formParam("username", "fpla.gatekeeper@mailinator.com")
           .formParam("password", "Pass19word")
           .formParam("save", "Sign in")
           .formParam("selfRegistrationEnabled", "false")
           .formParam("_csrf", "${csrfToken}")
           .headers(LoginHeader.headers_login_submit)
           .check(status.in(200, 304, 302))).exitHereIfFailed

        .exec(http("XUI${service}_170_010_GKSignIn")
              .get("/external/config/ui")
              .headers(LoginHeader.headers_0)
              .check(status.in(200,304)))

      .exec(http("XUI${service}_170_015_GKSignInTCEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_38)
            .check(status.in(200, 304)))

      .repeat(1, "count") {
        exec(http("XUI${service}_170_020_GKAcceptT&CAccessJurisdictions${count}")
             .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
             .headers(LoginHeader.headers_access_read)
             .check(status.in(200, 304, 302)))
      }

      .exec(http("XUI${service}_170_025_GKGetWorkBasketInputs")
            .get("/data/internal/case-types/CARE_SUPERVISION_EPO/work-basket-inputs")
            .headers(LoginHeader.headers_17))

      .exec(http("XUI${service}_170_030_GKGetPaginationMetaData")
            .get("/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata?state=Open")
            .headers(LoginHeader.headers_0))

      .exec(http("XUI${service}_170_035_GKGetDefaultWorkBasketView")
            .get("/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=WORKBASKET&state=Open&page=1")
            .headers(LoginHeader.headers_0))

    }

    .pause(MinThinkTime , MaxThinkTime)

  //======================================================================================
  //Business process : Click on Terms and Conditions
  //below requests are Terms and Conditions page and relavant sub requests
  // ======================================================================================

  val manageCase_Logout =
    tryMax(2) {
      exec(http("XUI${service}_${SignoutNumber}_SignOut")
           .get("/api/logout")
           .headers(LoginHeader.headers_signout)
           .check(status.in(200, 304, 302)))
    }

  val manageCase_LogoutAdmin =
    tryMax(2) {
      exec(http("XUI${service}_${SignoutNumberAdmin}_SignOut")
           .get("/api/logout")
           .headers(LoginHeader.headers_signout)
           .check(status.in(200, 304, 302)))
    }
  pause(105)//to be removed

  val manageCase_LogoutGK =
    tryMax(2) {
      exec(http("XUI${service}_${SignoutNumberGK}_SignOut")
           .get("/api/logout")
           .headers(LoginHeader.headers_signout)
           .check(status.in(200, 304, 302)))
    }
  pause(5)//to be removed
}