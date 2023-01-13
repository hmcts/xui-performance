package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.io.{BufferedWriter, FileWriter}

/*======================================================================================
* Create a new Private Law application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_PRL_CreateFlag {
  
  val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val PRLcases = csv("cases.csv").circular


  val postcodeFeeder = csv("postcodes.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime



  val CreateAFlag =


  /*======================================================================================
  * Select case
  ======================================================================================*/

    group("XUI_PRL_030_SelectCase") {

      exec(_.setAll(
        "PRLRandomString" -> (Common.randomString(7)),
        "PRLAppDobDay" -> Common.getDay(),
        "PRLAppDobMonth" -> Common.getMonth(),
        "PRLAppDobYear" -> Common.getDobYear()))

        .feed(PRLcases)

        .exec(http("XUI_PRL_030_005_SelectCase")
          .post(BaseURL + "/data/internal/cases/api/role-access/roles/manageLabellingRoleAssignment/${caseId}")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*"))
        //  .check(substring("PRIVATELAW")))

        .exec(Common.userDetails)
    }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
      * Select 'Create Case Flag'
      ======================================================================================*/

      .group("XUI_PRL_040_CreateCaseFlag") {
        exec(Common.profile)

        .exec(http("XUI_PRL_040_005_SelectIssue")
          .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/createCaseFlag?ignore-warning=false")
          .headers(Headers.navigationHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          //   .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("local_Court_Id"))
          .check(jsonPath("$.id").is("createCaseFlag")))


          .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      *Where should this flag be added? - Case Level
      ======================================================================================*/

      .group("XUI_PRL_050_WhereFlagAdded") {
        exec(http("XUI_PRL_050_005_WhereFlagAdded")
          .get(BaseURL + "/refdata/location/orgServices?ccdServiceNames=PRIVATELAW")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("service_short_description")))


        exec(http("XUI_PRL_050_010_WhereFlagAdded")
          .get(BaseURL + "/refdata/commondata/caseflags/service-id=ABA5?flag-type=CASE")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("FlagDetails")))
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Add comments for this flag
      ======================================================================================*/

      .group("XUI_PRL_060_AddComments") {
        exec(http("XUI_PRL_060_005_AddComments")
          .get(BaseURL + "/api/user/details")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("canShareCases")))

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
      * Review flag details
      ======================================================================================*/

      .group("XUI_PRL_070_ReviewFlagDetails") {
        exec(http("XUI_PRL_070_005_ReviewFlagDetails")
          .get(BaseURL + "/data/internal/cases/${caseId}")
          .headers(Headers.navigationHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .check(substring("Case Flag Issue Testing")))

          .exec(Common.userDetails)
      }

      .pause(MinThinkTime, MaxThinkTime)








}