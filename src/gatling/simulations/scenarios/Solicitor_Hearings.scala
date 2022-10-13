package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers,CsrfCheck}
import java.io.{BufferedWriter, FileWriter}
import scala.util.Random

/*======================================================================================
* Create a new Bail application as a professional user (e.g. Legal Rep)
======================================================================================*/

object Solicitor_Hearings {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val UserFeederHearingCases = csv("UserDataHearingsCases.csv").circular
  val UserFeederHearingRequestCases = csv("HearingDetailsRequest.csv").circular
  val TTLcases = csv("UserDataHearingsUploadCases.csv").circular
  val UserFeederHearingCasesLink = csv("UserDataHearingsCasesLinked.csv").circular
  val UserFeederHearingId = csv("HearingId.csv").circular
  val UserFeederHearingIdCancels = csv("HearingIdCancels.csv").circular
  val UserFeederHearingIdAmend = csv("HearingIdAmend.csv").circular
  val randomFeeder = Iterator.continually(Map("hearings-percentage" -> Random.nextInt(100)))
  val hearingPercentage = 90

  val TTLchange =

  /*======================================================================================
  * Select SendtoWithFTA
  ======================================================================================*/


    group("XUI_LAU_031_TTL") {

      feed(TTLcases)

        .exec(http("XUI_UploadResponse_031_005_TTL")
          .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/ManageTTL?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(substring("access_granted")))

        /*    .exec(http("XUI_UploadResponse_032_005_TTL")
          .post(BaseURL + "/data/case-types/MoneyClaimCase/validate?pageId=ManageTTL1")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
          .body(ElFileBody("bodies/hearings/AmendHearingSubmit.json"))
          .check(jsonPath("$.data.TTL.OverrideTTL").is("2022-10-14")))

     */


        .exec(http("XUI_Submit_033_005_TTL")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
          .body(ElFileBody("bodies/hearings/FTAHearingSubmit.json"))
          .check(substring("MoneyClaimCase")))

    }

}
