package scenarios

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object CCDAPI {

  val RpeAPIURL = Environment.rpeAPIURL
  val IdamAPIURL = Environment.idamAPIURL
  val CcdAPIURL = Environment.ccdAPIURL

  val clientSecret = ConfigFactory.load.getString("auth.clientSecret")

  val Auth =

    exec(http("ET_000_Auth")
      .post(RpeAPIURL + "/testing-support/lease")
      .body(StringBody("""{"microservice":"ccd_data"}""")).asJson
      .check(regex("(.+)").saveAs("authToken")))

    .pause(1)

    .exec(http("ET_000_GetBearerToken")
      .post(IdamAPIURL + "/o/token")
      .formParam("grant_type", "password")
      .formParam("username", "#{username}")
      .formParam("password", "#{password}")
      .formParam("client_id", "ccd_gateway")
      .formParam("client_secret", clientSecret)
      .formParam("scope", "openid profile roles openid roles profile")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(jsonPath("$.access_token").saveAs("bearerToken")))

    .pause(1)

    .exec(http("ET_000_GetIdamID")
      .get(IdamAPIURL + "/details")
      .header("Authorization", "Bearer #{bearerToken}")
      .check(jsonPath("$.id").saveAs("idamId")))

    .pause(1)

  def CreateCase(eventName: String, payloadPath: String) =

    exec(_.set("eventName", eventName))

    .exec(http("ET_000_GetCCDEventToken")
      .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales/event-triggers/#{eventName}/token")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(1)

    .exec(http("ET_000_CCDEvent-#{eventName}")
      .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales/cases")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody(payloadPath))
      .check(jsonPath("$.id").saveAs("caseId")))

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
