package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import scala.concurrent.duration._
import scala.util.Random

object EXUIFPLAMC {

  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL
  val loginFeeder = csv("OrgId.csv").circular

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

  private val rng: Random = new Random()

  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString

  val feedUserDataFPL = csv("FPLUserData.csv").circular

  val manageCasesHomePage=	group ("EXUI_ManageCases_Homepage") {

    feed(loginFeeder)
    .exec(http("XUIMC_010_Homepage")
      .get("/")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("XUIMC_020_LoginLandingPage")
      .get(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri="+baseURL+"/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .headers(headers_17)
      .check(regex("Sign in"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }

  val manageCasesLogin = group ("EXUI_ManageCases_Login") {

    feed(feedUserDataFPL)

    .exec(http("XUIMC_030_Login_SubmitLoginpage")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri="+baseURL+"/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
      .formParam("username", "${FPLUserName}")
      .formParam("password", "${FPLUserPassword}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .headers(headers_30))
  }
    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val manageCasesLogout = group ("EXUI_ManageCases_Logout") {
    exec(http("XUIMC_150_Logout")
      .get("/logout")
        .headers(headers_14)
      //  .check(regex("Sign in")))
      .check(status.in(200,304)))
  }

  val shareacase = group("EXUI_Manage_Representative")
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
  }

  val fplacasecreation= group("EXUI_ManageCases_FPLA_Create")
  {
    exec(http("XUIMC_040_005_SolAppCreatedPage1")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(headers_16)
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_040_010_SolAppCreatedPage2")
      .get("/data/internal/case-types/CARE_SUPERVISION_EPO/event-triggers/openCase?ignore-warning=false")
      .headers(headers_68)
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("event_token")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //set the random variables as usable parameters
    .exec(
      _.setAll(
        ("firstName", firstName()),
        ("lastName", lastName())
      ))

    .exec(http("XUIMC_040_015_SolAppCreatedPage3")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=openCase1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"${firstName}\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"caseName\": \"${firstName}\"\n  }\n}"))
      .check(status.is(200)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_040_020_SolAppSubmittedPage")
      .post("/data/case-types/CARE_SUPERVISION_EPO/cases?ignore-warning=false")
      .headers(headers_72)
      .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"${firstName}\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(status.in(200,304))
      .check(jsonPath("$.id").optional.saveAs("caseId")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("XUIMC_080_ViewCase")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //Orders Needed
    .exec(http("XUIMC_050_005_OrdersNeededPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/ordersNeeded?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_050_010_OrdersNeededPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=ordersNeeded1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"ordersNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_050_015_OrdersNeededSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_81)
      .body(StringBody("{\n  \"data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"ordersNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("XUIMC_050_ViewCase")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //hearing needed
    .exec(http("XUIMC_060_005_HearingNeededPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/hearingNeeded?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_060_010_HearingNeededPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=hearingNeeded1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"hearingNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_060_015_HearingNeededSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"hearingNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("XUIMC_170_ViewCase")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //enter children
    .exec(http("XUIMC_070_005_EnterChildrenPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/enterChildren?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_070_010_EnterChildrenPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterChildren1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2010-02-01\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-03-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterChildren\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2010-02-01\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-03-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_070_015_EnterChildrenSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2010-02-01\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-03-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterChildren\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("request_94")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //enter respondants
    .exec(http("XUIMC_080_005_EnterRespondentsPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/enterRespondents?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_080_010_EnterRespondentsGetAddress")
      .get("/addresses?postcode=TW33SD")
      .headers(headers_16)
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_080_015_EnterRespondentsPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterRespondents1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1980-02-01\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterRespondents\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1980-02-01\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_080_020_EnterRespondentsSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1980-02-01\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterRespondents\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("request_110")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // enter applicant
    .exec(http("XUIMC_090_005_EnterApplicantPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/enterApplicant?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_090_010_EnterApplicantGetAddress")
      .get("/addresses?postcode=TW33SD")
      .headers(headers_16)
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_090_015_EnterApplicantPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterApplicant1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"enterApplicant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_090_020_EnterApplicantSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"PBA1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"enterApplicant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("request_115")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // enter grounds
    .exec(http("XUIMC_100_005_EnterGroundsPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/enterGrounds?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_100_010_EnterGroundsPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterGrounds1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"event\": {\n    \"id\": \"enterGrounds\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_100_015_EnterGroundsSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"event\": {\n    \"id\": \"enterGrounds\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("request_119")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //other proposal
    .exec(http("XUIMC_110_005_OtherProposalPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/otherProposal?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_110_010_OtherProposalPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=otherProposal1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"event\": {\n    \"id\": \"otherProposal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_110_015_OtherProposalSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"event\": {\n    \"id\": \"otherProposal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("request_123")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // upload documents
    .exec(http("XUIMC_120_005_UploadDocumentsPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/uploadDocuments?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_120_010_UploadDocumentsUploadProcess")
      .post("/documents")
      .headers(headers_8)
      .bodyPart(RawFileBodyPart("files", "1MB.pdf")
        .fileName("1MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .formParam("classification", "PUBLIC")
      .check(status.is(200))
      .check(regex("""http://(.+)/""").saveAs("DMURL"))
      .check(regex("""internal/documents/(.+?)"},"binary""").saveAs("Document_ID")))

    .exec(http("XUIMC_120_015_UploadDocumentsPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=uploadDocuments1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": \"Attached\",\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"1MB.pdf\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": \"Attached\",\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"1MB.pdf\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_120_020_UploadDocumentsSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"documents_socialWorkChronology_document\": {\n      \"documentStatus\": \"Attached\",\n      \"typeOfDocument\": {\n        \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n        \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n        \"document_filename\": \"1MB.pdf\"\n      }\n    },\n    \"documents_socialWorkStatement_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkAssessment_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkCarePlan_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkEvidenceTemplate_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_threshold_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_checklist_document\": {\n      \"documentStatus\": \"To follow\",\n      \"statusReason\": \"test\"\n    },\n    \"documents_socialWorkOther\": []\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // .exec(http("request_129")
    //   .get("/data/internal/cases/${caseId}")
    //   .headers(headers_74)
    //   .check(status.in(200,304)))

    // .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // submit application
    .exec(http("XUIMC_130_005_SubmitApplicationPage1")
      .get("/data/internal/cases/${caseId}/event-triggers/submitApplication?ignore-warning=false")
      .headers(headers_76)
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_130_010_SubmitApplicationPage2")
      .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=submitApplication1")
      .headers(headers_71)
      .body(StringBody("{\n  \"data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${FPLUserName} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"event\": {\n    \"id\": \"submitApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${FPLUserName} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_130_015_SubmitApplicationSubmittedPage")
      .post("/data/cases/${caseId}/events")
      .headers(headers_80)
      .body(StringBody("{\n  \"data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${FPLUserName} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"event\": {\n    \"id\": \"submitApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("XUIMC_140_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(headers_74)
      .check(status.in(200,304)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)
  }
}
