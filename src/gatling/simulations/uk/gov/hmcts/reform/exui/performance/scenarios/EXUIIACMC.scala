package uk.gov.hmcts.reform.exui.performance.scenarios

import java.text.SimpleDateFormat
import java.util.Date

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.service.notify.NotificationClient

import scala.util.Random

object EXUIIACMC {

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular
//  val feedUserDataIAC = csv("IACDataBackground.csv").circular

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
    "origin" -> baseURL,
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
    "origin" -> baseURL,
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
    "origin" -> baseURL,
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_62 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "origin" -> baseURL,
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_63 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

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
    "Origin" -> IdamUrl,
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_new0 = Map(
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_new1 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

  val headers_new3 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

  val headers_new4 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

  val headers_new7 = Map(
    "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "Content-Type" -> "application/json",
    "Origin" -> baseURL,
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "experimental" -> "true")

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

  val headers_42 = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$38732415_350h7vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0")

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

  /*val manageCasesHomePage =group("XUI01_010_Homepage") {

    exec(http("XUI01_010_005_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))
      .pause(MinThinkTime seconds, MaxThinkTime seconds)

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
      .pause(MinThinkTime seconds, MaxThinkTime seconds)
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
      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(getCookieValue(
        CookieKey("__userid__").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("myUserId")))
      /*.exec {
         session =>
           println("current page is ....." + session("myUserId").as[String])

           session
       }*/

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

  val termsandconditions_Get=
    exec(http("tc_get")
      .get("/accept-terms-and-conditions")
      .headers(headers_tc_get)
      .check(status.in(200, 304)))


  val termsnconditions=group("XUI01_030_AcceptT&C") {
    //doIf(session => session.contains("accessToken")) {
      exec(http("XUI01_030_005_AcceptT&C")
        .post("/api/userTermsAndConditions")
        .headers(headers_tc)
        .body(StringBody("{\"userId\":\"${myUserId}\"}"))
        .check(status.in(200, 304, 302))
      )
      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("XUI01_030_010_AcceptT&CEnabled")
    .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
    .headers(headers_hometc)
      .check(status.in(200,304,302))
      )
.repeat(4,"count") {
  exec(http("XUI01_030_015_AcceptT&CAccessJurisdictions${count}")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(headers_access_read)
    .check(status.in(200,304,302)))

     }
.exec(http("XUI01_030_020_AcceptT&C_TCEnabledAppealStartedPage")
    .get("/data/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases/pagination_metadata?state=appealStarted")
    .headers(headers_tc_appeal)
    .check(status.in(200,304,302)))
          .exec(http("XUI01_030_025AcceptT&CEnabledAppealStartedPage1")
    .get("/aggregated/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases?view=WORKBASKET&state=appealStarted&page=1")
    .headers(headers_tc_appeal)
    .check(status.in(200,304,302)))
  }

  val manageCase_Logout =group("XUI01_040_SignOut") {
    exec(http("XUI01_040_005_SignOut")
      .get("/api/logout")
      .headers(headers_34)
      //.check(regex("Sign in")))
      .check(status.in(200, 304, 302)))
  }
*/
  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString

  val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
  val now = new Date()
  val timeStamp = sdfDate.format(now)

  val iaccasecreation= group("IAC Create") {

    //set the current date as a usable parameter
    exec(session => session.set("currentDate", timeStamp))
    
    //set the random variables as usable parameters
    .exec(
      _.setAll(
        ("firstName", firstName()),
        ("lastName", lastName())
      ))

    .exec(http("XUIIAC_020_005_StartAppealCreatedPage1")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(headers_2)
      .check(status.in(200, 304)))
      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_010_StartAppealCreatedPage2")
      .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
      .headers(headers_4)
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("event_token")))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_015_StartAppealChecklist")
      .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    }\n  }\n}"))
      .check(status.is(200)))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_020_StartAppealHomeOfficeDecision")
      .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecision")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  }\n}"))
      .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_025_StartAppealBasicDetails")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appellantTitle\": \"sasasa\",\n    \"appellantGivenNames\": \"asasas\",\n    \"appellantFamilyName\": \"fgfgfgfg\",\n    \"appellantDateOfBirth\": \"1975-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"GB\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/008\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"sasasa\",\n    \"appellantGivenNames\": \"asasas\",\n    \"appellantFamilyName\": \"fgfgfgfg\",\n    \"appellantDateOfBirth\": \"1975-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"GB\"\n        }\n      }\n    ]\n  }\n}"))
      .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_030_StartAppealBasicDetailsAddressSearch")
      .get("/api/addresses?postcode=TW33SD")
      .headers(headers_2))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_035_StartAppealAppellantAddress")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  }\n}"))
      .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_040_StartAppealAppealType")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappealType")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appealType\": \"revocationOfProtection\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\"\n  }\n}"))
      .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_045_StartAppealGroundsRevocation")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsRevocation")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    }\n  }\n}"))
      .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_050_StartAppealNewMatters")
      .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"hasNewMatters\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_055_StartAppealHasOtherAppeals")
      .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"hasOtherAppeals\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_060_StartAppealLegalRepresentative")
      .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_065_StartAppealCaseSave")
      .post("/data/case-types/Asylum/cases?ignore-warning=false")
      .headers(headers_32)
      .body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(status.in(200, 304))
      .check(jsonPath("$.id").optional.saveAs("caseId")))

    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_070_SubmitAppealPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/submitAppeal?ignore-warning=false")
      .headers(headers_new1)
      .check(jsonPath("$.event_token").optional.saveAs("event_token")))

    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_020_075_SubmitAppealAppealDeclaration")
      .post("/data/case-types/Asylum/validate?pageId=submitAppealdeclaration")
      .headers(headers_new4)
      .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

    .exec(http("XUIIAC_020_080_SubmitAppealAppealDeclaration")
      .post("/data/cases/${caseId}/events")
      .headers(headers_new7)
      .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false\n}")))
  }


  val headers_sharecase = Map(
    "accept" -> "application/json, text/plain, */*",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h28vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0")

  val headers_sharecase1 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h29vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0")

  val headers_sharecase2 = Map(
    "accept" -> "application/json, text/plain, */*",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h30vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0")

  val headers_shareacasesubmit = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "origin" -> "https://manage-case.perftest.platform.hmcts.net",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h32vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0")

  val headers_sharecase9 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h34vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}/trigger/shareACase/shareACaseshareACase")

  val headers_shareacase12 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "origin" -> "https://manage-case.perftest.platform.hmcts.net",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h35vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0")

  val headers_shareacase14 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h37vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0")
  val headers_sc_data_internal = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$61761456_902h31vGMGKQDHJNJUKWRAKMQJOGGKKAFNQFRAC-0",
    "x-dtreferer" -> "https://manage-case.perftest.platform.hmcts.net/cases/case-details/${caseId}")

  val StringBodyVJ="{\n  \"data\": {\n    \"orgListOfUsers\": {\n      \"value\": {\n        \"code\": \"${code}\",\n        \"label\": \"${label}\"\n      },\n      \"list_items\": [\n        {\"code\":\"${code1}\",\"label\":\"${label1}\"},\n        {\"code\":\"${code2}\",\"labe1\":\"${label2}\"},\n        {\"code\":\"${code3}\",\"label\":\"${label3}\"},\n        {\"code\":\"${code4}\",\"label\":\"${label4}\"},\n        {\"code\":\"${code5}\",\"label\":\"${label5}\"},\n        {\"code\":\"${code6}\",\"label\":\"${label6}\"}\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"shareACase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token_sharecase}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"orgListOfUsers\": \"${code1}\"\n  },\n  \"case_reference\": \"${caseId}\"\n}"

  val StringBodyVJSubmit="{\n  \"data\": {\n    \"orgListOfUsers\": {\n      \"value\": {\n        \"code\": \"${code}\",\n        \"label\": \"${label}\"\n      },\n      \"list_items\": [\n        {\"code\":\"${code1}\",\"label\":\"${label1}\"},\n        {\"code\":\"${code2}\",\"label\":\"${label2}\"},\n        {\"code\":\"${code3}\",\"label\":\"${label3}\"},\n        {\"code\":\"${code4}\",\"label\":\"${label4}\"},\n        {\"code\":\"${code5}\",\"label\":\"${label5}\"},\n        {\"code\":\"${code6}\",\"label\":\"${label6}\"}\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"shareACase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token_sharecase}\",\n  \"ignore_warning\": false\n}"



  val shareacase = group("EXUIShareCase")
  {
    exec(http("XUIIAC_030_005_ShareACase")
      .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FshareACase")
      .headers(headers_sharecase)
      .check(status.in(200,304,302)))
      .pause(MinThinkTime , MaxThinkTime )

    exec(http("XUIIAC_030_010_EventTrigger")
      .get("/data/internal/cases/${caseId}/event-triggers/shareACase?ignore-warning=false")
      .headers(headers_sharecase1)
      .check(status.in(200,304,302))
      .check(jsonPath("$..list_items").optional.saveAs("userlist"))
      .check(jsonPath("$..list_items[0].code").optional.saveAs("code1"))
      .check(jsonPath("$..list_items[0].label").optional.saveAs("label1"))
      .check(jsonPath("$..list_items[1].code").optional.saveAs("code2"))
      .check(jsonPath("$..list_items[1].label").optional.saveAs("label2"))
      .check(jsonPath("$..list_items[2].code").optional.saveAs("code3"))
      .check(jsonPath("$..list_items[2].label").optional.saveAs("label3"))
      .check(jsonPath("$..list_items[3].code").optional.saveAs("code4"))
      .check(jsonPath("$..list_items[3].label").optional.saveAs("label4"))
      .check(jsonPath("$..list_items[4].code").optional.saveAs("code5"))
      .check(jsonPath("$..list_items[4].label").optional.saveAs("label5"))
      .check(jsonPath("$..list_items[5].code").optional.saveAs("code6"))
      .check(jsonPath("$..list_items[5].label").optional.saveAs("label6"))
      .check(jsonPath("$..code").find(0).optional.saveAs("code"))
      .check(jsonPath("$..label").find(2).optional.saveAs("label"))
      .check(jsonPath("$.event_token").optional.saveAs("event_token_sharecase"))
    )
      .pause(MinThinkTime , MaxThinkTime )

      /*exec {
         session =>
           println("userlist ....." + session("userlist").as[String])
           println("code ....." + session("code1").as[String])
           println("label ....." + session("label").as[String])

           session
       }*/


    .exec(http("XUIIAC_030_015_ShareACaseTrigger")
      .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FshareACase%2FshareACaseshareACase")
      .headers(headers_sharecase2)
      .check(status.in(200,304,302)))

        .exec(http("XUIIAC_030_020_ShareACaseInternalProfile")
          .get("/data/internal/profile")
          .headers(headers_sc_data_internal)
      .check(status.in(200,304,302))
        )
      .pause(MinThinkTime , MaxThinkTime )


     /* .exec(http("XUIIAC_030_025_ShareACaseValidate")
        .post("/data/case-types/Asylum/validate?pageId=shareACaseshareACase")
        .headers(headers_shareacasesubmit)
       // .body(RawFileBody("RecordedSimulationshareacaseiac_0006_request.txt"))
        .body(StringBody(StringBodyVJ))
       // .body(ElFileBody("RecordedSimulationshareacaseiac_0006_request.json")).asJson
        //.body(StringBody("{\n  \"data\": {\n    \"orgListOfUsers\": {\n      \"value\": {\n   \"code\":,\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304,302)))*/

      .exec(http("XUIIAC_030_025_ShareaCaseInternal")
        .get("/data/internal/profile")
        .headers(headers_sharecase9)
      .check(status.in(200,304,302)))
    .pause(MinThinkTime , MaxThinkTime)
  }


    .exec(http("XUIIAC_030_030_ShareACaseEvents")
      .post("/data/cases/${caseId}/events")
      .headers(headers_shareacase12)
      .body(StringBody(StringBodyVJSubmit))
    //.body(ElFileBody("RecordedSimulationshareacaseiac_0012_request.json")).asJson
      .check(status.in(200,304,302)))
    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_030_035_ShareACaseData")
    .get("/data/internal/cases/${caseId}")
    .headers(headers_shareacase14)
  .check(status.in(200,304,302)))
    .pause(MinThinkTime , MaxThinkTime )

  val headers_37 = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$424950534_38h25vPMBLUVMCRTSPKMRPFVPPBRCOTEDTMABH-0")

  val headers_viewtabs = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$424950534_38h32vPMBLUVMCRTSPKMRPFVPPBRCOTEDTMABH-0")

  val headers_data_internal_cases = Map(
    "accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-dtpc" -> "3$424950534_38h29vPMBLUVMCRTSPKMRPFVPPBRCOTEDTMABH-0")

  val findandviewcase =

    exec(http("XUI01_040_005_SearchPage")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(headers_search))

    .exec(http("XUI01_040_010_SearchPaginationMetaData")
			.get("/data/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases/pagination_metadata?state=appealSubmitted&case.searchPostcode=TW3%203SD")
			.headers(headers_search))

    .exec(http("XUI01_040_015_SearchResults")
			.get("/aggregated/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases?view=WORKBASKET&state=appealSubmitted&page=1&case.searchPostcode=TW3%203SD")
			.headers(headers_search)
      .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))

    .exec(http("XUI01_040_020_SearchAccessJurisdictions")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(headers_search))

    .pause(MinThinkTime,MaxThinkTime)

    .foreach("${caseNumbers}","caseNumber") {
      exec(http("XUIIAC_050_CaseDetails")
        .get("/data/internal/cases/${caseNumber}")
        .headers(headers_data_internal_cases)
        .check(regex("""internal/documents/(.+?)","document_filename""").find(0).saveAs("Document_ID"))
        .check(status.is(200)))

    .pause(MinThinkTime , MaxThinkTime )

    .exec(http("XUIIAC_060_005_ViewCaseDocumentUI")
      .get("/external/config/ui")
      .headers(headers_documents))

    .exec(http("XUIIAC_060_010_ViewCaseDocumentT&C")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(headers_documents))

    .exec(http("XUIIAC_060_015_ViewCaseDocumentAnnotations")
      .get("/em-anno/annotation-sets/filter?documentId=${Document_ID}")
      .headers(headers_documents)
      .check(status.in(200, 404)))

    .exec(http("XUIIAC_060_020_ViewCaseDocumentBinary")
      .get("/documents/${Document_ID}/binary")
      .headers(headers_documents))
  }
}