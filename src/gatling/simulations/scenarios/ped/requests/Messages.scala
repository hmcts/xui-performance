package scenarios.ped.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import utils.{Common, Config}
import java.io.{BufferedWriter, FileWriter}
import java.nio.file.{Files, Paths}

object Messages {

  Files.createDirectories(Paths.get(Config.LOG_OUTPUT_PATH))

  val PresenterSendMessage =

    exec(_.setAll("message_page" -> Common.randomNumberBetween(1, 10),
                  "message_top" -> Common.randomNumberBetween(1, 1000),
                  "message_left" -> (Common.randomNumberBetween(1, 1000) - 1000)))
    /*
    .exec {
      session =>
        println(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
          + (session("counter").as[Int] + 1).toString + ",sending page:" + session("message_page").as[String]
          + " top:" + session("message_top").as[String] + " left:" + session("message_left").as[String] + " at " + System.currentTimeMillis())
        session
    }
     */

    .exec { session =>
      val logFileFullPath = Config.LOG_OUTPUT_PATH + "/" + session("session_sessionId").as[String] + "-user" + session.userId + "-" + session("type").as[String] + ".log"
      val fw = new BufferedWriter(new FileWriter(logFileFullPath, true))
      try {
        fw.write(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
          + (session("counter").as[Int] + 1).toString + ",sending page:" + session("message_page").as[String]
          + " top:" + session("message_top").as[String] + " left:" + session("message_left").as[String] + " at " + System.currentTimeMillis() + "\n")
      } finally fw.close()
      session
    }

    .exec(ws("PED_050_010_SendMessage")
      .sendText(ElFileBody("bodies/ped/SendPresenterMessage.json"))
      .await(10)(ws.checkTextMessage("PED_050_020_SendPresenterMessageResponse")
        .check(jsonPath("$.data.eventName").is("IcpScreenUpdated"))))

  val CheckForReceivedMessages = {

    exec(ws.processUnmatchedMessages((messages, session) => session.set("messages", messages)))

    .foreach("#{messages}", "message", "messageCounter") {
      /*
      exec {
        session =>
          println(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
            + (session("messageCounter").as[Int] + 1).toString + "," + session("message").as[String])
          session
      }
       */
      .exec { session =>
        val logFileFullPath = Config.LOG_OUTPUT_PATH + "/" + session("session_sessionId").as[String] + "-user" + session.userId + "-" + session("type").as[String] + ".log"
        val fw = new BufferedWriter(new FileWriter(logFileFullPath, true))
        try {
          fw.write(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
            + (session("messageCounter").as[Int] + 1).toString + "," + session("message").as[String] + "\n")
        } finally fw.close()
        session
      }
    }
  }

}


