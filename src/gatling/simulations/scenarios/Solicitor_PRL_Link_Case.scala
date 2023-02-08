package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

/*======================================================================================
* PRL link cases
======================================================================================*/

object Solicitor_PRL_Link_Case {
  
  val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val PRLcases = csv("cases.csv").circular


  val postcodeFeeder = csv("postcodes.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime



  val LinkCase =


  /*======================================================================================
  * Select case
  ======================================================================================*/

    group("XUI_PRL_Hearings_030_SelectCase") {

      exec(_.setAll(
        "PRLRandomString" -> (Common.randomString(7)),
        "PRLAppDobDay" -> Common.getDay(),
        "PRLAppDobMonth" -> Common.getMonth(),
        "PRLAppDobYear" -> Common.getDobYear()))

        .feed(PRLcases)

        .exec(http("XUI_PRL_Hearings_030_005_SelectCase")
          .get(BaseURL + "/data/internal/cases/${caseId}")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*"))
        //  .check(substring("PRIVATELAW")))

        .exec(Common.userDetails)
    }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
      * Select 'Link Cases'
      ======================================================================================*/

      .group("XUI_PRL_Hearings_040_SelectLinkCases") {
        exec(Common.profile)

        .exec(http("XUI_PRL_Hearings_040_005_SelectLinkCases")
          .get(BaseURL + "/workallocation/case/tasks/${caseId}/event/createCaseLink/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
          .headers(Headers.navigationHeader)
     //     .check(jsonPath("$.event_token").saveAs("event_token"))
     //   .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("local_Court_Id"))
        //  .check(jsonPath("$.id").is("createCaseFlag")))
          .check(substring("task_required_for_event")))

        .exec(http("XUI_PRL_Hearings_040_010_SelectLinkCases")
          .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/createCaseLink?ignore-warning=false")
          .headers(Headers.navigationHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(substring("Create Case Link")))


          .exec(http("XUI_PRL_Hearings_040_015_SelectLinkCases")
            .get(BaseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json")
            .check(substring("PUBLICLAW")))

          .exec(http("XUI_PRL_Hearings_040_020_SelectLinkCases")
            .get(BaseURL + "/refdata/commondata/lov/categories/CaseLinkingReasonCode")
            .headers(Headers.navigationHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(substring("CaseLinkingReasonCode")))


          .exec(http("XUI_PRL_Hearings_040_025_SelectLinkCases")
            .post(BaseURL + "/data/internal/searchCases?ctid=PRLAPPS&use_case=WORKBASKET")
            .headers(Headers.commonHeader)
            .header("accept", "application/json")
            .body(ElFileBody("bodies/hearings/prl/SelectLinkCase.json"))
            .check(substring("results")))


          .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Before you start
      ======================================================================================*/



      /*======================================================================================
      * Select the case you want to link to this case
      ======================================================================================*/

      .group("XUI_PRL_Hearings_050_SelectCaseLink") {
        exec(http("XUI_PRL_Hearings_050_005_SelectCaseLink")
          .post(BaseURL + "/data/case-types/PRLAPPS/validate?pageId=createCaseLinkcreateCaseLink")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/hearings/prl/LinkCaseSelect.json"))
          .check(substring("caseLinksFlag")))

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Create Case Link
======================================================================================*/

      .group("XUI_PRL_Hearings_060_CreateCaseLink") {
        exec(http("XUI_PRL_Hearings_060_005_CreateCaseLink")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/hearings/prl/CreateLinkCase.json"))
          .check(substring("caseLinksFlag")))

      }

      .pause(MinThinkTime, MaxThinkTime)


}