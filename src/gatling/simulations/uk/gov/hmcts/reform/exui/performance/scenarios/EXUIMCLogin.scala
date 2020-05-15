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

  //headers

  val manageCasesHomePage =

    exec(http("XUI${service}_010_005_Homepage")
      .get("/")
      .headers(LoginHeader.headers_0)
      .check(status.is(200)))

    .exec(http("XUI${service}_010_010_HomepageTCEnabled")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(LoginHeader.headers_hometc)
      .check(status.is(200)))

    .exec(http("XUI${service}_010_015_HompageLoginPage")
      .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .headers(LoginHeader.headers_login)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
      .check(status.is(200)))

    .pause( MinThinkTime, MaxThinkTime )

  val manageCaseslogin =

    exec(http("XUI${service}_020_005_SignIn")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .formParam("username", "${user}")
      .formParam("password", "Pass19word")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(LoginHeader.headers_login_submit)
      .check(status.in(200, 304, 302)))

    .exec(getCookieValue(
      CookieKey("__userid__").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("myUserId")))

    .exec(http("XUI${service}_020_010_SignInTCEnabled")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(LoginHeader.headers_38)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_020_015_SignInGetUserId")
      .get("/api/userTermsAndConditions/${myUserId}")
      .headers(LoginHeader.headers_tc))

    .exec(http("XUI${service}_020_020_SignInAcceptTCGet")
      .get("/accept-terms-and-conditions")
      .headers(LoginHeader.headers_tc_get)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_020_025_SignInTCEnabled")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(LoginHeader.headers_tc))

    .pause(MinThinkTime , MaxThinkTime)

  val termsnconditions=

    exec(http("XUI${service}_030_005_ConfirmT&C")
      .post("/api/userTermsAndConditions")
      .headers(LoginHeader.headers_tc)
      .body(StringBody("{\"userId\":\"${myUserId}\"}"))
      .check(status.in(200, 304, 302)))

    .exec(http("XUI${service}_030_010_AcceptT&CEnabled")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(LoginHeader.headers_hometc)
      .check(status.in(200,304,302)))

    .repeat(6,"count") {
      exec(http("XUI${service}_030_015_AcceptT&CAccessJurisdictions${count}")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(LoginHeader.headers_access_read)
        .check(status.in(200,304,302)))
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

    .pause(MinThinkTime , MaxThinkTime )
  
  val manageCase_Logout =
  
    exec(http("XUI${service}_${SignoutNumber}_SignOut")
      .get("/api/logout")
      .headers(LoginHeader.headers_signout)
      .check(status.in(200, 304, 302)))
}