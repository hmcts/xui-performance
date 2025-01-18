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
            //the below section was previously 3 checks instead of 3 matching statements.
            //on one playback, the messages were received in a different order (1, 3, 2) so it failed the second check.
            //Trying to switch them to matching, but not sure if this means the order can be different, so look out to see
            //if this works if the responses are not in the order 1, 2, 3.
            //If this doesn't work, may need to change .is(1) to .in(1, 2, 3) for each check.
            ws.checkTextMessage("PED_030_010_JoinSessionResponse1")
              .matching(jsonPath("$.data.eventName").is("IcpClientJoinedSession")),
            //.check(jsonPath("$.data.eventName").is("IcpClientJoinedSession")),
            ws.checkTextMessage("PED_030_020_JoinSessionResponse2")
              .matching(jsonPath("$.data.eventName").is("IcpParticipantsListUpdated")),
            //.check(jsonPath("$.data.eventName").is("IcpParticipantsListUpdated")),
            ws.checkTextMessage("PED_030_030_JoinSessionResponse3")
              .matching(jsonPath("$.data.eventName").is("IcpNewParticipantJoinedSession"))))
        //.check(jsonPath("$.data.eventName").is("IcpNewParticipantJoinedSession"))))
      )
    )

  val LeaveSession =

    exec(ws("PED_910_LeavePresentation")
      .sendText(ElFileBody("bodies/ped/LeavePresentation.json")))

    .exec(ws("PED_920_LeaveSession")
      .sendText(ElFileBody("bodies/ped/LeaveSession.json")))

    .exec(ws("PED_930_CloseConnection").close)

}


