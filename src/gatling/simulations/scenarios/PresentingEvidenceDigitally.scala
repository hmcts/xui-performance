package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import java.util.concurrent.atomic.AtomicReference

object PresentingEvidenceDigitally {

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  var pedAllUsersJoined = new AtomicReference(false)
  var pedReadyToSendMessages = new AtomicReference(false)
  var pedAllFollowersLeft = new AtomicReference(false)

  val GetSessionInfo =

    exec(http("PED_010_GetSessionInfo")
      .get("/icp/sessions/1712674566646674/7e488855-9fb5-4ee0-9545-5f9b5714a167") // caseId/DocId
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

  val PresenterStartPresenting =

    exec(ws("PED_040_010_StartPresenting")
      .sendText(ElFileBody("bodies/ped/StartPresenting.json"))
      .await(10)(ws.checkTextMessage("PED_040_020_StartPresentingResponse")
        .check(jsonPath("$.data.eventName").is("IcpPresenterUpdated"))
        .check(jsonPath("$.data.data.id").is("#{connectionId}"))))

  val WaitForAllUsersToJoin =

    exec(_.set("allUsersJoined", pedAllUsersJoined))
    //poll every 5 seconds to wait for all followers to join
    .asLongAs(session => !session("allUsersJoined").as[AtomicReference[Boolean]].get()) {
      exec{session =>
        println("Waiting for all users to join...")
        session
      }
      .pause(5)
    }

  val WaitForPresentersToSendMessages =

    exec(_.set("readyToSend", pedReadyToSendMessages))
    //poll every 5 seconds to wait for all followers to join
    .asLongAs(session => !session("readyToSend").as[AtomicReference[Boolean]].get()) {
      exec { session =>
        println("Waiting for presenters to start sending messages...")
        session
      }
      .pause(5)
    }

  val WaitForAllFollowersToLeave =

    exec(_.set("allFollowersLeft", pedAllFollowersLeft))
    //poll every 5 seconds to wait for all followers to join
    .asLongAs(session => !session("allFollowersLeft").as[AtomicReference[Boolean]].get()) {
      exec { session =>
        println("Waiting for all followers to leave...")
        session
      }
      .pause(5)
    }

  def SetEvent(eventName: AtomicReference[Boolean], value: Boolean) = {
    exec { session => eventName.set(value); session} // flag all users as joined
  }

  /*
  val SetAllUsersJoined = exec { session => pedAllUsersJoined.set(true); session} // flag all users as joined
  val SetReadyToSendMessages = exec { session => pedReadyToSendMessages.set(true); session} // flag to start sending messages
  val SetAllFollowersLeft = exec { session => pedAllFollowersLeft.set(true); session} // flag to start sending messages
   */

  val PresenterSendMessages =

    exec(ws("PED_050_010_SendMessage")
      .sendText(ElFileBody("bodies/ped/SendPresenterMessage.json"))
      .await(10)(ws.checkTextMessage("PED_050_020_SendPresenterMessageResponse")
        .check(jsonPath("$.data.eventName").is("IcpScreenUpdated"))))

  val CheckForReceivedMessages = {

    repeat(20, "repeatCounter") {

      exec(ws.processUnmatchedMessages((messages, session) => session.set("messages", messages)))

      .foreach("#{messages}", "message", "messageCounter") {
        exec {
          session =>
            println(session("user").as[String] + " received a message on poll " + (session("repeatCounter").as[Int] + 1).toString
              + ". Message " + (session("messageCounter").as[Int] + 1).toString + ": " + session("message").as[String])
            session
        }
      }
      .pause(1)
    }
  }

  /*
        .exec {
          session =>
            println(session)
            session
        }
   */

  val PresneterStopPresenting =

    exec(ws("PED_900_010_StopPresenting")
      .sendText(ElFileBody("bodies/ped/StopPresenting.json"))
      .await(10)(ws.checkTextMessage("PED_997_020_StopPresentingResponse")
        .check(jsonPath("$.data.eventName").is("IcpPresenterUpdated")))
    )

  val LeaveSession =

    exec(ws("PED_910_LeavePresentation")
      .sendText(ElFileBody("bodies/ped/LeavePresentation.json")))

    .exec(ws("PED_920_LeaveSession")
      .sendText(ElFileBody("bodies/ped/LeaveSession.json")))

    .exec(ws("PED_930_CloseConnection").close)

}


