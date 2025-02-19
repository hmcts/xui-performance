package scenarios.ped.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Session {

  val GetSessionInfo =

    exec(http("PED_010_GetSessionInfo")
      .get("/icp/sessions/#{caseId}/#{docId}")
      .header("x-xsrf-token", "#{XSRFToken}")
      .check(jsonPath("$.username").saveAs("session_username"))
      .check(jsonPath("$.session.presenterName").saveAs("session_presenterName"))
      .check(jsonPath("$.session.participants").transform(x => x.replace(""""""", """\"""")).saveAs("session_participants"))
      .check(jsonPath("$.session.dateOfHearing").saveAs("session_dateOfHearing"))
      .check(jsonPath("$.session.presenterId").saveAs("session_presenterId"))
      .check(jsonPath("$.session.sessionId").saveAs("session_sessionId"))
      .check(jsonPath("$.session.connectionUrl").saveAs("session_connectionUrl")))

  val JoinSession =

    exec(ws("PED_020_010_OpenSocketConnection")
      //wss://em-icp-webpubsub-#{env}.webpubsub.azure.com/client/hubs/Hub?access_token=#{bearerToken}
      .connect("#{session_connectionUrl}")
      .subprotocol("json.webpubsub.azure.v1")
      .await(10)(ws.checkTextMessage("PED_020_020_OpenSocketConnectionResponse")
        .check(
          jsonPath("$.event").is("connected"),
          jsonPath("$.connectionId").saveAs("connectionId")
        ))
      .onConnected(
        exec(ws("PED_030_010_JoinSession")
          .sendText(ElFileBody("bodies/ped/JoinSession.json"))
          .await(10)(
            //The following 3 messages are sometimes received in a different order (e.g. 1, 3, 2) so allowing any option on each check.
            //Saving the third one to ensure all three responses were received
            ws.checkTextMessage("PED_030_010_JoinSessionResponse1")
              .check(jsonPath("$.data.eventName").in("IcpClientJoinedSession", "IcpParticipantsListUpdated", "IcpNewParticipantJoinedSession")),
            ws.checkTextMessage("PED_030_020_JoinSessionResponse2")
              .check(jsonPath("$.data.eventName").in("IcpClientJoinedSession", "IcpParticipantsListUpdated", "IcpNewParticipantJoinedSession")),
            ws.checkTextMessage("PED_030_030_JoinSessionResponse3")
              .check(jsonPath("$.data.eventName").in("IcpClientJoinedSession", "IcpParticipantsListUpdated", "IcpNewParticipantJoinedSession").saveAs("JoinedSessionEvent"))))
      )
    )

  val LeaveSession =

    exec(ws("PED_910_LeavePresentation")
      .sendText(ElFileBody("bodies/ped/LeavePresentation.json")))

    .exec(ws("PED_920_LeaveSession")
      .sendText(ElFileBody("bodies/ped/LeaveSession.json")))

    .exec(ws("PED_930_CloseConnection").close)

}


