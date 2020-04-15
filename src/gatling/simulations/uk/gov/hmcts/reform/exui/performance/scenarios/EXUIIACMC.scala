package uk.gov.hmcts.reform.exui.performance.scenarios

import java.text.SimpleDateFormat
import java.util.Date
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import scala.util.Random

object EXUIIACMC {

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  val loginFeeder = csv("OrgId.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

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

  val manageCasesHomePage =	group ("EXUI_ManageCases_Homepage") {

    feed(loginFeeder)
    .exec(http("XUIMC_010_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("XUIMC02_020_Login_LandingPage")
      .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .headers(headers_login)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }
  val feedUserDataIAC = csv("IACUserData.csv").circular

  val manageCaseslogin = group ("EXUI_ManageCases_Login") {

    feed(feedUserDataIAC)

    .exec(http("XUIMC_030_Login_SubmitLoginPage")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .formParam("username", "${IACUserName}")
      .formParam("password", "${IACUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_login_submit))
  }
    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val manageCase_Logout = group ("EXUI_IAC_ManageCases_Logout") {
    exec(http("XUIMC_140_Logout")
      .get("/api/logout")
      .headers(headers_34)
      //.check(regex("Sign in")))
      .check(status.in(200,304,302)))
  }

  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString

  val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
  val now = new Date()
  val timeStamp = sdfDate.format(now)

  val iaccasecreation= group("EXUI_ManageCases_IAC_Create") {

    //set the current date as a usable parameter
    exec(session => session.set("currentDate", timeStamp))
    
    //set the random variables as usable parameters
    .exec(
      _.setAll(
        ("firstName", firstName()),
        ("lastName", lastName())
      ))

    .exec(http("XUIMC_040_005_StartAppealCreatedPage1")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(headers_2)
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_040_010_StartAppealCreatedPage2")
      .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
      .headers(headers_4)
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_050_StartAppealChecklist")
      .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    }\n  }\n}"))
      .check(status.is(200)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_060_StartAppealHomeOfficeDecision")
      .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecision")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_070_005_StartAppealBasicDetails")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appellantTitle\": \"sasasa\",\n    \"appellantGivenNames\": \"asasas\",\n    \"appellantFamilyName\": \"fgfgfgfg\",\n    \"appellantDateOfBirth\": \"1975-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"GB\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/008\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"sasasa\",\n    \"appellantGivenNames\": \"asasas\",\n    \"appellantFamilyName\": \"fgfgfgfg\",\n    \"appellantDateOfBirth\": \"1975-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"GB\"\n        }\n      }\n    ]\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_070_010_StartAppealBasicDetailsAddressSearch")
      .get("/api/addresses?postcode=TW33SD")
      .headers(headers_2))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_080_StartAppealAppellantAddress")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_090_StartAppealAppealType")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappealType")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appealType\": \"revocationOfProtection\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_100_StartAppealGroundsRevocation")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsRevocation")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    }\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_110_StartAppealNewMatters")
      .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"hasNewMatters\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_120_StartAppealHasOtherAppeals")
      .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"hasOtherAppeals\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_130_StartAppealLegalRepresentative")
      .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
      .headers(headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  }\n}"))
      .check(status.in(200, 304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_140_StartAppealCaseSave")
      .post("/data/case-types/Asylum/cases?ignore-warning=false")
      .headers(headers_32)
      .body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(status.in(200, 304))
      .check(jsonPath("$.id").optional.saveAs("caseId")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_150_SubmitAppealPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/submitAppeal?ignore-warning=false")
      .headers(headers_new1)
      .check(jsonPath("$.event_token").optional.saveAs("event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_160_SubmitAppealAppealDeclaration")
      .post("/data/case-types/Asylum/validate?pageId=submitAppealdeclaration")
      .headers(headers_new4)
      .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

    .exec(http("XUIMC_170_SubmitAppealAppealDeclaration")
      .post("/data/cases/${caseId}/events")
      .headers(headers_new7)
      .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false\n}")))
  }

  val shareacase = group("EXUI_filter")
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
      .body(StringBody("{\n  \"data\": {\n    \"orgListOfUsers\": {\n      \"value\": {\n        \"code\": \"26002d02-6496-4639-b432-4985d9c5c52b\",\n        \"label\": \"ia.legalrep.a.xui@gmail.com\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"26002d02-6496-4639-b432-4985d9c5c52b\",\n          \"label\": \"ia.legalrep.a.xui@gmail.com\"\n        },\n        {\n          \"code\": \"65ada726-e9df-4cae-8969-ed5339ef1862\",\n          \"label\": \"ia.legalrep.bbb.xui@protonmail.com\"\n        },\n        {\n          \"code\": \"835c71bd-1f73-47c1-a00d-453f954d0d56\",\n          \"label\": \"ia.legalrep.orgcreator@gmail.com\"\n        },\n        {\n          \"code\": \"cbb37832-1ca8-4bf6-b234-779262359c63\",\n          \"label\": \"ia.legalrep.c.xui@gmail.com\"\n        },\n        {\n          \"code\": \"e9a1d081-49ba-48a2-bdb2-ea4635edd976\",\n          \"label\": \"ia.legalrep.bb.xui@gmail.com\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"shareACase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"orgListOfUsers\": \"26002d02-6496-4639-b432-4985d9c5c52b\"\n  },\n  \"case_reference\": \"1580904712599223\"\n}")))
  
    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  /*val filtercaselist= group("EXUI_filter")
  {
    /*exec(http("EXUI_AO_005_Caselist")
      .get("/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata?state=AwaitingPayment")
      .headers(headers_0)
      .check(status.is(200)))*/

    .exec(http("request_69")
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
}