package scenarios.ped.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import utils.Common
import java.io.{BufferedWriter, FileWriter}
import java.nio.file.{Files, Paths}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Messages {

  val now = LocalDate.now()
  val patternDate = DateTimeFormatter.ofPattern("yyyyMMdd")
  val outputPath = "logs/" + now.format(patternDate)
  Files.createDirectories(Paths.get(outputPath))

  val PresenterSendMessage =

    exec(_.setAll("message_page" -> Common.randomNumberBetweenXandY(1, 10),
                  "message_top" -> Common.randomNumberBetweenXandY(1, 1000),
                  "message_left" -> Common.randomNumberBetweenXandY(1, 1000) - 1000))

    .exec {
      session =>
        println(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
          + (session("counter").as[Int] + 1).toString + ",sending page:" + session("message_page").as[String]
          + " top:" + session("message_top").as[String] + " left:" + session("message_left").as[String] + " at " + System.currentTimeMillis())
        session
    }

    .exec { session =>
      val fullOutputPath = outputPath + "/" + session("session_sessionId").as[String] + "-user" + session.userId + "-" + session("user").as[String] + ".log"
      val fw = new BufferedWriter(new FileWriter(fullOutputPath, true))
      try {
        fw.write(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
          + (session("counter").as[Int] + 1).toString + ",sending page:" + session("message_page").as[String]
          + " top:" + session("message_top").as[String] + " left:" + session("message_left").as[String] + " at " + System.currentTimeMillis())
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
      exec {
        session =>
          println(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
            + (session("messageCounter").as[Int] + 1).toString + "," + session("message").as[String])
          session
      }
      .exec { session =>
        val fullOutputPath = outputPath + "/" + session("session_sessionId").as[String] + "-user" + session.userId + "-" + session("user").as[String] + ".log"
        val fw = new BufferedWriter(new FileWriter(fullOutputPath, true))
        try {
          fw.write(session("session_sessionId").as[String] + "," + session("user").as[String] + "," + session("type").as[String] + ","
            + (session("messageCounter").as[Int] + 1).toString + "," + session("message").as[String])
        } finally fw.close()
        session
      }
    }
  }

}


