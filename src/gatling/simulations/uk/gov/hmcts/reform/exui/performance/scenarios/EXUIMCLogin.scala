package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.service.notify.NotificationClient

object EXUIMCLogin {

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  //headers

  val headers_tc_appeal = Map(
    "accept" -> "application/json",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$38734236_77h19vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/accept-terms-and-conditions")

  val headers_tc = Map(
    "Content-Type" -> "application/json",
    "Origin" -> "https://manage-case.perftest.platform.hmcts.net",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_tc_get = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Sec-Fetch-Dest" -> "document",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_0 = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
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
    "Origin" -> IdamUrl,
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")


  val headers_hometc = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$38717637_792h4vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0")

  val headers_38 = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$38732415_350h4vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0")

  val headers_access_read = Map(
    "accept" -> "application/json",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$38734236_77h15vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/accept-terms-and-conditions")

  val manageCasesHomePage =group("XUI01_010_Homepage") {
    // feed(loginFeeder)
    exec(http("XUI01_010_005_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI01_010_010_HomepageTCEnabled")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(headers_hometc)
      .check(status.is(200)))

      .exec(http("XUI01_010_015_HompageLoginPage")
      .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .headers(headers_login)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
      .check(status.is(200)))
      .pause( seconds, MaxThinkTime )
  }

  val manageCaseslogin =group("XUI01_020_SignIn") {

      exec(http("XUI01_020_005_SignIn")
        .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
        .formParam("username", "${user}")
        .formParam("password", "Pass19word")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("_csrf", "${csrfToken}")
        .headers(headers_login_submit)
        .check(status.in(200, 304, 302))
      )
      .pause(MinThinkTime , MaxThinkTime )

      .exec(getCookieValue(
        CookieKey("__userid__").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("myUserId")))
      /*.exec {
         session =>
           println("current page is ....." + session("myUserId").as[String])

           session
       }*/

      .repeat(1,"n") {
      exec(http("XUI01_020_010_SignInTCEnabled${n}")
        .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(headers_38)
        .check(status.in(200, 304)))
        .pause(MinThinkTime , MaxThinkTime )
      /*.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(headers_42)*/
    }
      .exec(http("XUI01_020_015_SignInAcceptTCGet")
        .get("/accept-terms-and-conditions")
        .headers(headers_tc_get)
        .check(status.in(200, 304))
      )
        .pause(MinThinkTime , MaxThinkTime )

      }

  val termsnconditions=group("XUI01_030_AcceptT&C") {
    //doIf(session => session.contains("accessToken")) {
      exec(http("XUI01_030_005_AcceptT&C")
        .post("/api/userTermsAndConditions")
        .headers(headers_tc)
        .body(StringBody("{\"userId\":\"${myUserId}\"}"))
        .check(status.in(200, 304, 302))
      )
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI01_030_010_AcceptT&CEnabled")
    .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
    .headers(headers_hometc)
      .check(status.in(200,304,302))
      )
.repeat(6,"count") {
  exec(http("XUI01_030_015_AcceptT&CAccessJurisdictions${count}")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(headers_access_read)
    .check(status.in(200,304,302)))
    .pause(MinThinkTime , MaxThinkTime )

     }
/*.exec(http("XUI01_030_020_AcceptT&C_TCEnabledAppealStartedPage")
    .get("/data/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases/pagination_metadata?state=appealStarted")
    .headers(headers_tc_appeal)
    .check(status.in(200,304,302)))
          .exec(http("XUI01_030_025AcceptT&CEnabledAppealStartedPage1")
    .get("/aggregated/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases?view=WORKBASKET&state=appealStarted&page=1")
    .headers(headers_tc_appeal)
    .check(status.in(200,304,302)))*/
  }

  val manageCase_Logout =group("XUI01_040_SignOut") {
    exec(http("XUI01_040_005_SignOut")
      .get("/api/logout")
      .headers(headers_34)
      //.check(regex("Sign in")))
      .check(status.in(200, 304, 302)))
  }


}