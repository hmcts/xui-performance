package scenarios

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common,Environment}

object ListHearingLA {

  val RpeAPIURL = Environment.rpeAPIURL
  val IdamAPIURL = Environment.idamAPIURL
  val HmcInboundURL = Environment.hmcInboundURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val clientSecret = ConfigFactory.load.getString("auth.clientSecretHmcInbound")

  // must be "prl", "example" or "any"
  def Auth(jurisdiction: String) =

    exec(session => jurisdiction match {
      case "prl" => session.set("username", "prl_pt_ca_swansea@justice.gov.uk").set("password", "Nagoya0102")
      case "example" => session.set("username", "ccdloadtest-la@gmail.com").set("password", "Password12")
      case "any" => session.set("username", session("user").as[String]).set("password", session("password").as[String])
    })

    //set session variables
    .exec(_.setAll(
      "LARandomString" -> Common.randomString(7),
      "LARandomNumber" -> Common.randomNumber(5),
      "futureDate" -> Common.getFutureDate(),
      "todayDate" -> Common.getDate()))

    .exec(http("XUI_000_Auth")
      .post(RpeAPIURL + "/testing-support/lease")
      .body(StringBody("""{"microservice":"api_gw"}""")).asJson
      .check(regex("(.+)").saveAs("authToken")))

    .pause(1)

    .exec(http("XUI_000_GetBearerToken")
      .post(IdamAPIURL + "/o/token")
      .formParam("grant_type", "password")
      .formParam("username", "#{username}")
      .formParam("password", "#{password}")
      .formParam("client_id", "hmc_hmi_inbound_adapter")
      .formParam("client_secret", clientSecret)
      .formParam("scope", "openid profile roles openid roles profile")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(jsonPath("$.access_token").saveAs("bearerToken")))

    .pause(1)

  val ListHearingFL401 =

    exec(Auth("any"))

    .exec(http("LA_000_ListHearing")
      .put(HmcInboundURL + "/listings/#{hearingRequestId}")
      .header("Authorization", "Bearer #{bearerToken}") 
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .header("Accept","*/*")
      .header("Accept-Encoding","gzip, deflate, br")
      .body(ElFileBody("bodies/la/ListHearingFl401.json"))
      .check(status.is(202)))

    .pause(1)

  val ListHearingC100 =

    exec(Auth("any"))

    .exec(http("LA_000_ListHearing")
      .put(HmcInboundURL + "/listings/#{hearingRequestId}")
      .header("Authorization", "Bearer #{bearerToken}") 
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .header("Accept","*/*")
      .header("Accept-Encoding","gzip, deflate, br")
      .body(ElFileBody("bodies/la/ListHearingC100.json"))
      .check(status.is(202)))

    .pause(1)
}