package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers, LoginHeader}

object Login {

  val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  
  val baseDomain=Environment.baseDomain
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*====================================================================================
  *Manage Case Login
  *=====================================================================================*/

  val XUILogin =

    group("XUI_020_Login") {
      exec(http("XUI_020_005_Login")
        .post(IdamUrl + "/login?client_id=xuiwebapp&redirect_uri=" + BaseURL + "/oauth2/callback&state=${state}&nonce=${nonce}&response_type=code&scope=profile%20openid%20roles%20manage-user%20create-user%20search-user&prompt=")
        .formParam("username", "${user}")
        .formParam("password", "${password}")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("_csrf", "${csrf}")
        .headers(Headers.navigationHeader)
        .headers(Headers.postHeader)
        .check(regex("Manage cases")))

      .exec(Common.configurationui)

      .exec(Common.configJson)

      .exec(Common.TsAndCs)

      .exec(Common.configUI)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(Common.healthcheck("%2Fcases"))

      .exec(http("XUI_020_010_Jurisdictions")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("id")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

      .exec(Common.orgDetails)

      .exec(Common.userDetails)

      .exec(Common.userDetails)

      .exec(Common.userDetails)

      .exec(Common.monitoringTools)

      .exec(Common.userDetails)

      //if there is no in-flight case, set the case to 0 for the activity calls
      .doIf("${caseId.isUndefined()}") {
        exec(_.set("caseId", "0"))
      }

      .exec(Common.caseActivityGet)

      .exec(http("XUI_020_015_WorkBasketInputs")
        .get("/data/internal/case-types/${caseType}/work-basket-inputs")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8")
        .check(regex("workbasketInputs|Not Found"))
        .check(status.in(200, 404)))

      .exec(http("XUI_020_020_SearchCases")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(substring("columns")))

    }
    .pause(MinThinkTime , MaxThinkTime)
  
  val LoginAsJudge =
    group("CivilDamages_020_005_SignInJudge") {
      exec(flushHttpCache).exec(http("CivilHM_020_005_SignIn")
        /*.post(IdamUrl + "/login?response_type=code&redirect_uri=" + baseURL + "%2Foauth2%2Fcallback&scope=profile%20openid%20roles%20manage-user%20create-user&state=${state}&client_id=xuiwebapp")*/
        .post(IdamUrl + "/login?client_id=xuiwebapp&redirect_uri=" + BaseURL + "/oauth2/callback&state=#{state}&nonce=#{nonce}&response_type=code&scope=profile%20openid%20roles%20manage-user%20create-user&prompt=")
        .formParam("username", "#{judgeuser}")
        .formParam("password", "#{judgepassword}")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("azureLoginEnabled", "true")
        .formParam("mojLoginEnabled", "true")
        .formParam("_csrf", "#{csrf}")
        
        .headers(LoginHeader.headers_login_submit)
        .check(status.in(200, 304, 302))).exitHereIfFailed
        //.check(regex("Manage Cases"))).exitHereIfFailed
        
        //following is the other way of getting cookies
        // .check(headerRegex("Set-Cookie","__auth-token=(.*)").saveAs("authToken"))
        
        .exec(http("CivilDamages_020_010_configUI")
          .get("/external/config/ui")
          .headers(LoginHeader.headers_0)
          .check(status.in(200, 304)))
        
        .exec(http("CivilDamages_020_015_Config")
          .get("/assets/config/config.json")
          .headers(LoginHeader.headers_0)
          .check(status.in(200, 304)))
        
        .exec(http("CivilDamages_020_020_SignInTCEnabled")
          .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
          .headers(LoginHeader.headers_38)
          .check(status.in(200, 304)))
        
        .exec(http("CivilDamages_020_025_SignInGetUserId")
          .get("/api/user/details")
          .headers(LoginHeader.headers_0)
          .check(status.in(200, 304)))
        
       /* .repeat(1, "count") {
          exec(http("CivilDamages_020_030_AcceptT&CAccessJurisdictions#{count}")
            .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
            .headers(LoginHeader.headers_access_read)
            .check(status.in(200, 304, 302)))
        }*/
        /* .exec(http("CivilDamages_020_035_GetWorkBasketInputs")
               .get("/data/internal/case-types/FinancialRemedyMVP2/work-basket-inputs")
               .headers(LoginHeader.headers_17)
               .check(status.in(200, 304, 302)))*/
        
        .exec(http("CivilDamages_020_040_HomepageIsAuthenticated")
          .get("/auth/isAuthenticated")
          .headers(LoginHeader.headers_0))
        
        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(baseDomain).saveAs("XSRFToken")))
      
    }
      .pause(MinThinkTime, MaxThinkTime)

}