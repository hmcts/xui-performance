package scenarios

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Environment, Common}
import java.io.{BufferedWriter, FileWriter}

object CCDAPI {

  val RpeAPIURL = Environment.rpeAPIURL
  val IdamAPIURL = Environment.idamAPIURL
  val CcdAPIURL = Environment.ccdAPIURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val clientSecret = ConfigFactory.load.getString("auth.clientSecret")

  //userType must be "Caseworker", "Legal" or "Citizen"
  def Auth(userType: String) =

    exec(session => userType match {
      case "Caseworker" => session.set("emailAddressCCD", "ccdloadtest-cw@gmail.com").set("passwordCCD", "Password12").set("microservice", "ccd_data")
      case "Legal" => session.set("emailAddressCCD", "ccdloadtest-la@gmail.com").set("passwordCCD", "Password12").set("microservice", "ccd_data")
      case "Solicitor" => session.set("emailAddressCCD", session("user").as[String]).set("passwordCCD", session("password").as[String]).set("microservice", "nfdiv_case_api")
    })

    .exec(http("XUI_000_Auth")
      .post(RpeAPIURL + "/testing-support/lease")
      .body(StringBody("""{"microservice":"${microservice}"}""")).asJson
      .check(regex("(.+)").saveAs("authToken")))

    .pause(1)

    .exec(http("XUI_000_GetBearerToken")
      .post(IdamAPIURL + "/o/token")
      .formParam("grant_type", "password")
      .formParam("username", "${emailAddressCCD}")
      .formParam("password", "${passwordCCD}")
      .formParam("client_id", "ccd_gateway")
      .formParam("client_secret", clientSecret)
      .formParam("scope", "openid profile roles openid roles profile")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(jsonPath("$.access_token").saveAs("bearerToken")))

    .pause(1)

    .exec(http("XUI_000_GetIdamID")
      .get(IdamAPIURL + "/details")
      .header("Authorization", "Bearer ${bearerToken}")
      .check(jsonPath("$.id").saveAs("idamId")))

    .pause(1)

  val AssignCase =

    exec(Auth("Solicitor"))

    .exec(http("XUI_000_AssignCase")
      .post(CcdAPIURL + "/case-users")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("ServiceAuthorization", "${authToken}")
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
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/${caseId}/event-triggers/${eventName}/token")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("ServiceAuthorization", "${authToken}")
      .header("Content-Type", "application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(1)

    .exec(http("XUI_000_CCDEvent-${eventName}")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/${caseId}/events")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("ServiceAuthorization", "${authToken}")
      .header("Content-Type", "application/json")
      .body(ElFileBody(payloadPath))
      .check(jsonPath("$.id")))

    .pause(1)

  val prlCreateCase =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/event-triggers/solicitorCreate/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_CreateCase")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlCreateCase.json"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .exec {
      session =>
        val fw = new BufferedWriter(new FileWriter("PRLCreatedCaseIds.csv", true))
        try {
          fw.write(session("caseId").as[String] + "\r\n")
        }
        finally fw.close()
        session
    }

  val prlApplicationType =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/fl401TypeOfApplication/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_ApplicationType")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlApplicationType.json")))

    .pause(Environment.maxThinkTime)

  val prlWithoutNotice =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/withoutNoticeOrderDetails/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_WithoutNotice")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlWithoutNotice.json")))

    .pause(Environment.maxThinkTime)

  val prlApplicantDetails =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/applicantsDetails/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_ApplicantDetails")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlApplicantDetails.json")))

    .pause(Environment.maxThinkTime)

  val prlRespondentDetails =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/respondentsDetails/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_RespondentDetails")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlRespondentDetails.json")))

    .pause(Environment.maxThinkTime)

  val prlFamilyDetails =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/fl401ApplicantFamilyDetails/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_FamilyDetails")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlFamilyDetails.json")))

    .pause(Environment.maxThinkTime)

  val prlRelationship =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/respondentRelationship/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_Relationship")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlRelationship.json")))

    .pause(Environment.maxThinkTime)

  val prlBehaviour =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/respondentBehaviour/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_Behaviour")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlBehaviour.json")))

    .pause(Environment.maxThinkTime)

  val prlHome =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/fl401Home/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_PRL_Home")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlHome.json")))

    .pause(Environment.maxThinkTime)

  val prlSubmit =

    exec(http("API_PRL_GetEventToken")
      .get(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/event-triggers/fl401StatementOfTruthAndSubmit/token")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(_.set("currentDate", Common.getDate()))

    .exec(http("API_PRL_Submit")
      .post(CcdAPIURL + "/caseworkers/${idamId}/jurisdictions/PRIVATELAW/case-types/PRLAPPS/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${authToken}")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("Content-Type","application/json")
      .body(ElFileBody("bodies/prl/api/prlSubmit.json")))

    .pause(Environment.maxThinkTime)


}
