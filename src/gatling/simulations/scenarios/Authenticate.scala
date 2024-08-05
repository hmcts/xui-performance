package scenarios

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object Authenticate {

  val RpeAPIURL = Environment.rpeAPIURL
  val IdamAPIURL = Environment.idamAPIURL

  val clientSecret = ConfigFactory.load.getString("auth.clientSecret")

  val Auth =

    exec(http("ET_000_Auth")
      .post(RpeAPIURL + "/testing-support/lease")
      .body(StringBody("""{"microservice":"xui_webapp"}""")).asJson   // ccd_data
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

}
