package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object ETCreateSingles {

  val CcdAPIURL = Environment.ccdAPIURL

  val CreateSingle =

    exec(http("ET_000_GetCCDEventToken")
      .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales/event-triggers/initiateCase/token")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(1)

    .exec(http("ET_000_CreateSinglesCase")
      .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales/cases")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/et/initiateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId"))
      .check(jsonPath("$.case_data.ethosCaseReference").saveAs("ethosCaseRef")))

    .pause(1)

  def CreateEvent(eventName: String, payloadPath: String) =

    exec(_.set("eventName", eventName))

    .exec(http("ET_000_GetCCDEventToken")
      .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales/cases/#{caseId}/event-triggers/#{eventName}/token")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(1)

    .exec(http("ET_000_CCDEvent-#{eventName}")
      .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales/cases/#{caseId}/events")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody(payloadPath))
      .check(jsonPath("$.id")))

    .pause(1)

}
