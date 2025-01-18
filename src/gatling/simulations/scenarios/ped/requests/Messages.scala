package scenarios.ped.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Messages {

  val PresenterSendMessage =

    exec(ws("PED_050_010_SendMessage")
      .sendText(ElFileBody("bodies/ped/SendPresenterMessage.json"))
      .await(10)(ws.checkTextMessage("PED_050_020_SendPresenterMessageResponse")
        .check(jsonPath("$.data.eventName").is("IcpScreenUpdated"))))

  val CheckForReceivedMessages = {

    exec(ws.processUnmatchedMessages((messages, session) => session.set("messages", messages)))

    .foreach("#{messages}", "message", "messageCounter") {
      exec {
        session =>
          println(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
            + (session("messageCounter").as[Int] + 1).toString + "," + session("message").as[String])
          session
      }
    }
  }

}


