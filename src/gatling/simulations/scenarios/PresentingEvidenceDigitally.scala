package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object PresentingEvidenceDigitally {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*====================================================================================
  *Apply a filter from the dropdowns (State = Submitted)
  *=====================================================================================*/

  val Presenter =

    exec(http("PED_010_GetSessionInfo")
      .get("/icp/sessions/1712674566646674/7e488855-9fb5-4ee0-9545-5f9b5714a167") // caseId/DocId
      .header("x-xsrf-token", "#{XSRFToken}")
      .check(jsonPath("$.username").saveAs("session_username"))
      .check(jsonPath("$.session.presenterName").saveAs("session_presenterName"))
      .check(jsonPath("$.session.participants").saveAs("session_participants"))
      .check(jsonPath("$.session.dateOfHearing").saveAs("session_dateOfHearing"))
      .check(jsonPath("$.session.presenterId").saveAs("session_presenterId"))
      .check(jsonPath("$.session.sessionId").saveAs("session_sessionId"))
      .check(jsonPath("$.session.connectionUrl").saveAs("session_connectionUrl")))

    .exec(ws("PED_020_OpenSocketConnection")
      //wss://em-icp-webpubsub-#{env}.webpubsub.azure.com/client/hubs/Hub?access_token=#{bearerToken}
      .connect("#{session_connectionUrl}")
      .subprotocol("json.webpubsub.azure.v1")
      .await(10)(ws.checkTextMessage("PED_030_OpenSocketConnectionResponse")
        .check(
          jsonPath("$.event").is("connected"),
          jsonPath("$.connectionId").saveAs("connectionId")
        ))
      .onConnected(
        exec(ws("PED_040_JoinSession")
          .sendText(ElFileBody("bodies/ped/JoinSession.json"))
          .await(10)(ws.checkTextMessage("PED_050_JoinSessionResponse")
          .check(jsonPath("$.data.eventName").is("IcpClientJoinedSession"))))
      )
    )

      .pause(1)

      .exec(ws.processUnmatchedMessages((messages, session) => session.set("messages", messages)))

      .foreach("#{messages}", "message"){
        exec {
          session =>
            println("RECEIVED MESSAGE: " + session("message").as[String])
            session
        }
      }
/*
      .exec {
        session =>
          println(session)
          session
      }
 */

    .exec(ws("PED_060_CloseConnection").close)

    .pause(MinThinkTime, MaxThinkTime)

}


