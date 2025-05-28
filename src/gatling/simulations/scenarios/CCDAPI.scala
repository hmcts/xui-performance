package scenarios

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object CCDAPI {

  val RpeAPIURL = Environment.rpeAPIURL
  val IdamAPIURL = Environment.idamAPIURL
  val CcdAPIURL = Environment.ccdAPIURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val clientSecret = ConfigFactory.load.getString("auth.clientSecret")
  val ccdScope = "openid profile authorities acr roles openid profile roles"

  //userType must be "Caseworker", "Legal" or "Citizen"
  def Auth(userType: String) =

    exec(session => userType match {
      case "Caseworker" => session.set("emailAddressCCD", "ccdloadtest-cw@gmail.com").set("passwordCCD", "Password12").set("microservice", "ccd_data")
      case "Legal" => session.set("emailAddressCCD", "ccdloadtest-la@gmail.com").set("passwordCCD", "Password12").set("microservice", "ccd_data")
      case "Solicitor" => session.set("emailAddressCCD", session("user").as[String]).set("passwordCCD", session("password").as[String]).set("microservice", "nfdiv_case_api")
    })

    .exec(http("XUI_000_Auth")
      .post(RpeAPIURL + "/testing-support/lease")
      .body(StringBody("""{"microservice":"#{microservice}"}""")).asJson
      .check(regex("(.+)").saveAs("authToken")))

    .pause(1)

    .exec(http("XUI_000_GetBearerToken")
      .post(IdamAPIURL + "/o/token")
      .formParam("grant_type", "password")
      .formParam("username", "#{emailAddressCCD}")
      .formParam("password", "#{passwordCCD}")
      .formParam("client_id", "ccd_gateway")
      .formParam("client_secret", clientSecret)
      .formParam("scope", "openid profile roles openid roles profile")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(jsonPath("$.access_token").saveAs("bearerToken")))

    .pause(1)

    .exec(http("XUI_000_GetIdamID")
      .get(IdamAPIURL + "/details")
      .header("Authorization", "Bearer #{bearerToken}")
      .check(jsonPath("$.id").saveAs("idamId")))

    .pause(1)

  val AssignCase =

    exec(Auth("Solicitor"))

    .exec(http("XUI_000_AssignCase")
      .post(CcdAPIURL + "/case-users")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody("bodies/nfd/AssignCase.json"))
      .check(jsonPath("$.status_message").is("Case-User-Role assignments created successfully")))

    .pause(1)

  // allows the event to be used where the userType = "Caseworker" or "Legal"
  def CreateEvent(userType: String, jurisdiction: String, caseType: String, eventName: String, payloadPath: String) =

    exec(_.set("eventName", eventName)
          .set("jurisdiction", jurisdiction)
          .set("caseType", caseType))

    .exec(Auth(userType))

    .exec(http("XUI_000_GetCCDEventToken")
      .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/#{jurisdiction}/case-types/#{caseType}/cases/#{caseId}/event-triggers/#{eventName}/token")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(1)

    .exec(http("XUI_000_CCDEvent-#{eventName}")
      .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/#{jurisdiction}/case-types/#{caseType}/cases/#{caseId}/events")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody(payloadPath))
      .check(jsonPath("$.id")))

    .pause(1)

  val S2SLogin =

    exec(http("GetS2SToken")
      .post(RpeAPIURL + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"probate_backend\"}")) //probate_backend
      .check(bodyString.saveAs("bearerToken"))) //docUploadBearerToken
      .exitHereIfFailed

  val idamLogin =

    exec(http("GetIdamToken")
      .post(IdamAPIURL + "/o/token?client_id=ccd_gateway&client_secret=" + clientSecret + "&grant_type=password&scope=" + ccdScope + "&username=#{user}&password=#{password}")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("accessToken")))

  val CreateCase =

    exec(http("API_Probate_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/applyForGrant/token")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Probate_CreateCase")
      .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"#{eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("API_Probate_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/event-triggers/paymentSuccessApp/token")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken2")))

    .exec(http("API_Probate_PaymentSuccessful")
      .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/#{caseId}/events")
      .header("ServiceAuthorization", "Bearer #{bearerToken}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n    \"data\": {\n      \"applicationSubmittedDate\": \"2025-05-03\"\n    },\n    \"event\": {\n      \"id\": \"paymentSuccessApp\",\n      \"summary\": \"\",\n      \"description\": \"\"\n    },\n    \"event_token\": \"#{eventToken2}\",\n    \"ignore_warning\": false\n  }")))

    .pause(MinThinkTime, MaxThinkTime)

}