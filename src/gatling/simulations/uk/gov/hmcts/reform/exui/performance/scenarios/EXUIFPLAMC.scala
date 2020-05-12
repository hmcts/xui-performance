package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment

import scala.util.Random

object EXUIFPLAMC {

  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL
  val loginFeeder = csv("FPLUserData.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

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

  val headers_8 = Map(
    "Accept" -> "",
    "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundary0NoZ3fbG8de034Bj",
    "Origin" -> baseURL,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_9 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> baseURL,
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
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_14 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Access-Control-Request-Headers" -> "content-type",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_15 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_16 = Map(
    "Accept" -> "application/json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
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
    "Origin" -> IdamUrl,
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_57 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-banners.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_59 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_68 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_69 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Access-Control-Request-Headers" -> "content-type,experimental",
    "Access-Control-Request-Method" -> "POST",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_70 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_71 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

  val headers_72 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_74 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_76 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site",
    "experimental" -> "true")

  val headers_80 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

  val headers_81 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

  val headers_139 = Map(
    "Accept" -> "",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundaryucjE7WrGyxbtCSQN",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_140 = Map(
    "Accept" -> "",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "multipart/form-data; boundary=----WebKitFormBoundary9n7AAqp1SNXC8LRR",
    "Origin" -> baseURL,
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_160 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
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

  val headers_search = Map(
    "Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

  val headers_documents = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Upgrade-Insecure-Requests" -> "1")

  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString

  /*val manageCasesHomePage=	group("EXUI_ManageCases_Homepage") {

    exec(http("XUIMC_010_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

      .exec(http("XUI01_010_010_HomepageTCEnabled")
        .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(headers_hometc)
        .check(status.is(200)))

    .exec(http("XUIMC_020_LoginLandingPage")
      .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri="+baseURL+"/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .headers(headers_17)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val manageCasesLogin = group ("EXUI_ManageCases_Login") {

    exec(http("XUIMC_030_Login_SubmitLoginPage")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri="+baseURL+"/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .formParam("username", "${user}")
      .formParam("password", "Pass19word")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_30)
      .check(status.in(200, 304, 302))
    )
      .pause(MinThinkTime seconds, MaxThinkTime seconds)
      .exec(getCookieValue(
        CookieKey("__userid__").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("myUserIdFPL")))
      .repeat(2,"n") {
        exec(http("XUI01_020_010_SignInTCEnabled${n}")
          .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
          .headers(headers_38)
          .check(status.in(200, 304)))
        /*.get("/api/configuration?configurationKey=termsAndConditionsEnabled")
          .headers(headers_42)*/
      }
      .exec(http("XUI01_020_015_SignInAcceptTCGet")
        .get("/accept-terms-and-conditions")
        .headers(headers_tc_get)
        .check(status.in(200, 304))
      )
  }
    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val termsnconditions=group("XUI01_030_AcceptT&C") {
    //doIf(session => session.contains("accessToken")) {
    exec(http("XUI01_030_005_AcceptT&C")
      .post("/api/userTermsAndConditions")
      .headers(headers_tc)
      .body(StringBody("{\"userId\":\"${myUserIdFPL}\"}"))
      .check(status.in(200, 304, 302))
    )
      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("XUI01_030_010_AcceptT&CEnabled")
        .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(headers_hometc)
        .check(status.in(200,304,302))
      )
      .repeat(2,"count") {
        exec(http("XUI01_030_015_AcceptT&CAccessJurisdictions${count}")
          .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
          .headers(headers_access_read)
          .check(status.in(200,304,302)))

      }

  }

  val manageCasesLogout = group ("EXUI_ManageCases_Logout") {
    exec(http("XUIMC_150_Logout")
      .get("/logout")
        .headers(headers_14)
      //  .check(regex("Sign in")))
      .check(status.in(200,304)))
  }*/

 /* val shareacase = group("EXUI_Manage_Representative")
  {
    exec(http("XUIMC_O10_Homepage")
      .get("/")
      .headers(headers_0)
    .check(status.is(200)))

    .exec(http("XUIMC_020_LoginLandingPage")
      .get(IdamUrl + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=https%3A%2F%2Fccd-case-management-web-aat.service.core-compute-aat.internal%2Foauth2redirect")
      .headers(headers_17)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_030_LoginSubmitLoginpage")
      .post(IdamUrl + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=https%3A%2F%2Fccd-case-management-web-aat.service.core-compute-aat.internal%2Foauth2redirect")
      .formParam("username", "hmcts-admin@example.com")
      .formParam("password", "Password12")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_30))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }*/

  val headers_createprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h38vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-filter")

  val headers_opencaseprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h40vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-create/PUBLICLAW/CARE_SUPERVISION_EPO/openCase/openCase1")

  val headers_casesprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h43vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0")

  val headers_orddersprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.Y7WW",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h48vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}")

  val headers_ordersneed1profile= Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.xtL1X",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h51vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/ordersNeeded/ordersNeeded1")

  val headers_hearingneededprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.foKPP",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h61vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/hearingNeeded/hearingNeeded1")
  val headers_childrenprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.c7MKw",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h72vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterChildren/enterChildren1")
  val headers_respondantprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.qOiFU",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h83vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterRespondents/enterRespondents1")

  val headers_applicantprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.SiNFL",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h101vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterApplicant/enterApplicant1")

  val headers_groundsprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.eINt",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h111vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/enterGrounds/enterGrounds1")

  val headers_otherprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.fYX/R",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h121vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/otherProposal/otherProposal1")

  val headers_uploaddocprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.l5g3b",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h132vPKUSVOSAPKKCNGHKEFHLNFMFNJDCKBGI-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/uploadDocuments/uploadDocuments1")

  val headers_submitprofile = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "request-context" -> "appId=cid-v1:32f2f79b-4f35-43d3-a880-a5a2e9f89979",
    "request-id" -> "|l2yDf.bYLSk",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$407919787_877h171vNMOKVINRMMCFSCDPWHJRAGBTITOCLLHM-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/submitApplication/submitApplication1")

  val fplacasecreation =
    exec(http("XUIFPL_020_005_CreateCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(headers_16)
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_010__StartCreateCase")
      .get("/data/internal/case-types/CARE_SUPERVISION_EPO/event-triggers/openCase?ignore-warning=false")
      .headers(headers_68)
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("event_token")))

      .exec(http("XUIFPL_020_015__CreateCaseProfile")
        .get("/data/internal/profile")
        .headers(headers_createprofile)
        .check(status.in(200,304)))
      .pause(MinThinkTime , MaxThinkTime )

      //set the random variables as usable parameters
      .exec(
      _.setAll(
        ("firstName", firstName()),
        ("lastName", lastName())
      ))

      .exec(http("XUIFPL_020_020_CaseNameContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=openCase1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"${firstName}\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"caseName\": \"${firstName}\"\n  }\n}"))
        .check(status.is(200)))


      .exec(http("XUIFPL_020_025_CaseNameProfile")
      .get("/data/internal/profile")
      .headers(headers_opencaseprofile)
      .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_030_CaseNameSaveContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/cases?ignore-warning=false")
        .headers(headers_72)
        .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"${firstName}\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        .check(status.in(200,304))
        .check(jsonPath("$.id").optional.saveAs("caseId")))


      .exec(http("XUIFPL_020_035_CaseNameViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))
      .pause(MinThinkTime , MaxThinkTime )

      //Orders Needed
      .exec(http("XUIFPL_020_040_OrdersDirectionNeededGo")
      .get("/data/internal/cases/${caseId}/event-triggers/ordersNeeded?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_045_OrdersDirectionProfile")
        .get("/data/internal/profile")
        .headers(headers_orddersprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_050_OrdersDirectionNeededContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=ordersNeeded1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"ordersNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_055_OrdersDirectionContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_ordersneed1profile)
        .check(status.in(200,304)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_060_OrdersDirectionNeededSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_81)
        .body(StringBody("{\n  \"data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"ordersNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_065_OrdersDirectionViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))
      .pause(MinThinkTime , MaxThinkTime )

      //hearing needed
      .exec(http("XUIFPL_020_070_HearingNeededGo")
      .get("/data/internal/cases/${caseId}/event-triggers/hearingNeeded?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_075_HearingNeededGoProfile")
        .get("/data/internal/profile")
        .headers(headers_orddersprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_080_HearingNeededContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=hearingNeeded1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"hearingNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_085_HearingNeededContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_hearingneededprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_090_HearingNeededSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"hearingNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_095_HearingNeededSaveViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      //enter children
      .exec(http("XUIFPL_020_100_ChildrenGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterChildren?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_105_ChildrenGoProfile")
        .get("/data/internal/profile")
        .headers(headers_ordersneed1profile)
        .check(status.in(200,304)))


      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_110_ChildrenContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterChildren1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2010-02-01\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-03-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterChildren\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2010-02-01\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-03-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_115_ChildrenContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_childrenprofile)
        .check(status.in(200,304)))


      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_120_ChildrenSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2010-02-01\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-03-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterChildren\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))



      .exec(http("XUIFPL_020_125_ChildrenSaveViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      //enter respondants
      .exec(http("XUIFPL_020_130_RespondentsGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterRespondents?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_135_RespondentsGoProfile")
        .get("/data/internal/profile")
        .headers(headers_ordersneed1profile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_140_RespondentsGetAddress")
        .get("/addresses?postcode=TW33SD")
        .headers(headers_16)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_145_RespondentsContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterRespondents1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1980-02-01\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterRespondents\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1980-02-01\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_150_RespondentsContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_respondantprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_155_RespondentsSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1980-02-01\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterRespondents\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))


      .exec(http("XUIFPL_020_160_RespondentsSaveViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      // enter applicant
      .exec(http("XUIFPL_020_165_ApplicantGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterApplicant?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_170_ApplicantGoProfile")
        .get("/data/internal/profile")
        .headers(headers_respondantprofile)
        .check(status.in(200,304)))


      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_175_ApplicantGetAddress")
        .get("/addresses?postcode=TW33SD")
        .headers(headers_16)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_180_ApplicantContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterApplicant1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"enterApplicant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_190_ApplicantGoProfile")
        .get("/data/internal/profile")
        .headers(headers_applicantprofile)
        .check(status.in(200,304)))


      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_195_ApplicantSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"PBA1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"enterApplicant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))



      .exec(http("XUIFPL_020_200_ApplicantSaveViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))
      .pause(MinThinkTime , MaxThinkTime )

      // enter grounds
      .exec(http("XUIFPL_020_205__GroundApplicationGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterGrounds?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_210__GroundApplicationGoProfile")
        .get("/data/internal/profile")
        .headers(headers_ordersneed1profile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_215_GroundApplicationContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterGrounds1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"event\": {\n    \"id\": \"enterGrounds\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_220_GroundApplicationContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_groundsprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_225_GroundApplicationSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"event\": {\n    \"id\": \"enterGrounds\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))



      .exec(http("XUIFPL_020_230_GroundApplicationSaveViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      //other proposal
      .exec(http("XUIFPL_020_235_AllocationProposalGo")
      .get("/data/internal/cases/${caseId}/event-triggers/otherProposal?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_240_AllocationProposalGoProfile")
        .get("/data/internal/profile")
        .headers(headers_ordersneed1profile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_245_AllocationProposalContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=otherProposal1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"event\": {\n    \"id\": \"otherProposal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_250_AllocationProposalContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_otherprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_255_AllocationProposalSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"event\": {\n    \"id\": \"otherProposal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))



      .exec(http("XUIFPL_020_260_AllocationProposalSaveViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      // upload documents
      .exec(http("XUIFPL_020_265_DocumentsGo")
      .get("/data/internal/cases/${caseId}/event-triggers/uploadDocuments?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_270_DocumentsGoProfile")
        .get("/data/internal/profile")
        .headers(headers_ordersneed1profile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_275_UploadFile")
        .post("/documents")
        .headers(headers_8)
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .check(status.is(200))
        .check(regex("""http://(.+)/""").saveAs("DMURL"))
        .check(regex("""internal/documents/(.+?)/binary""").saveAs("Document_ID")))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_280_DocumentsContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=uploadDocuments1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": \"Attached\",\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"3MB.pdf\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": \"Attached\",\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"3MB.pdf\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_285_DocumentsContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_uploaddocprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_290_DocumentsSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": \"Attached\",\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"3MB.pdf\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))


      .exec(http("XUIFPL_020_295_DocumentsSaveViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      // submit application
      .exec(http("XUIFPL_020_300_SubmitApplicationGo")
      .get("/data/internal/cases/${caseId}/event-triggers/submitApplication?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

      .exec(http("XUIFPL_020_305_SubmitApplicationGoProfile")
        .get("/data/internal/profile")
        .headers(headers_ordersneed1profile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_310_SubmitApplicationContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=submitApplication1")
        .headers(headers_71)
        .body(StringBody("{\n  \"data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${user} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"event\": {\n    \"id\": \"submitApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${user} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304)))

      .exec(http("XUIFPL_020_315_SubmitApplicationContinueProfile")
        .get("/data/internal/profile")
        .headers(headers_submitprofile)
        .check(status.in(200,304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUIFPL_020_320_ApplicationSubmitted")
        .post("/data/cases/${caseId}/events")
        .headers(headers_80)
        .body(StringBody("{\n  \"data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${user} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"event\": {\n    \"id\": \"submitApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200,304)))


      .exec(http("XUIFPL_020_325_ApplicationSubmittedViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(headers_casesprofile)
        .check(status.in(200,304)))
      .pause(MinThinkTime , MaxThinkTime )

  val headers_viewtab = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$430739744_795h37vRRPOHSUEPACJDAFRRDTWMOFWLCMTVMIB-0")

  val headers_searchinputs = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$430739744_795h28vRRPOHSUEPACJDAFRRDTWMOFWLCMTVMIB-0")

  val findandviewcasefpl= 

    exec(http("XUI01_040_005_SearchPage")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(headers_search))

    .exec(http("XUI01_040_010_SearchPaginationMetaData")
			.get("/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata")
			.headers(headers_search))

    .exec(http("XUI01_040_015_SearchResults")
			.get("/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=WORKBASKET&page=1")
			.headers(headers_search)
      .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbersFPL")))

    .exec(http("XUI01_040_020_SearchAccessJurisdictions")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(headers_search))

      // .exec(http("XUIFPL_030_010_SearchParams")
      //   .get("/data/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases/pagination_metadata?state=Submitted")
      //   .headers(headers_viewtab)
      //   .check(status.is(200)))
      // .pause(MinThinkTime , MaxThinkTime )

      // .exec(http("XUIFPL_030_015_SearchResults")
      //   .get("/aggregated/caseworkers/:uid/jurisdictions/PUBLICLAW/case-types/CARE_SUPERVISION_EPO/cases?view=SEARCH&page=1&state=Submitted")
      //   .headers(headers_viewtab)
      //   .check(status.is(200))
      //   .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbersFPL"))
      // )

    .pause(MinThinkTime , MaxThinkTime )

    .foreach("${caseNumbersFPL}","caseNumberFPL") {
      exec(http("XUIFPL_050_ViewCase")
        .get("/data/internal/cases/${caseNumberFPL}")
        .headers(headers_searchinputs)
        .check(regex("""internal/documents/(.+?)","document_filename""").find(0).saveAs("Document_ID"))
        .check(status.is(200)))


    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIFPL_060_005_ViewCaseDocumentUI")
      .get("/external/config/ui")
      .headers(headers_documents))

    .exec(http("XUIFPL_060_010_ViewCaseDocumentT&C")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(headers_documents))

    .exec(http("XUIFPL_060_015_ViewCaseDocumentAnnotations")
      .get("/em-anno/annotation-sets/filter?documentId=${Document_ID}")
      .headers(headers_documents)
      .check(status.in(200, 404)))

    .exec(http("XUIFPL_060_020_ViewCaseDocumentBinary")
      .get("/documents/${Document_ID}/binary")
      .headers(headers_documents))

    .pause(MinThinkTime , MaxThinkTime )
    }
}