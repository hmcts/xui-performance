package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object ETCreateMultiple {

  val CcdAPIURL = Environment.ccdAPIURL

  val CreateMultiple = 

    exec(http("ET_000_GetCCDEventToken")
      .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/event-triggers/createMultiple/token")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(1)

    .exec(http("ET_000_CreateMultipleCase")
      .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/et/createMultiple.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(1)

}
