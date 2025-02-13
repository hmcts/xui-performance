package scenarios.ped

import io.gatling.core.Predef._
import scenarios.{Homepage, Login, Logout}

import scala.concurrent.duration._

object PED_Scenario {

  val PEDUserFeeder = csv("UserDataPED.csv")

  def PEDScenario(totalUsers: Int) = scenario("***** PED Websockets Journey ******")

    .feed(PEDUserFeeder)
    .exec(_.set("caseType", "Benefit"))

    /* ALL USERS LOGIN TO XUI */

    .exec(Homepage.XUIHomePage)
    .exec(Login.XUILogin)

    /* PRESENTERS JOIN FIRST AND START PRESENTING */

    .rendezVous(totalUsers) //Wait for everyone to login to XUI
    .exec {
      session =>
        println("Testing Rendezvous 1: " + System.currentTimeMillis())
        session
    }
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Session.GetSessionInfo)
      .exec(requests.Session.JoinSession)
      .exec(requests.Presentation.StartPresenting)
    }

    /* FOLLOWERS JOIN THE PRESENTATION */

    .rendezVous(totalUsers) //Wait for the presenters to start presenting before proceeding
    .exec {
      session =>
        println("Testing Rendezvous 2: " + System.currentTimeMillis())
        session
    }
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Session.GetSessionInfo)
      .exec(requests.Session.JoinSession)
    }
    //If any users don't obtain a Connection ID, abort the test
    .crashLoadGeneratorIf("ERROR: One or more of the users couldn't join the presentation, aborting simulation...", "#{connectionId.isUndefined()}")
    .rendezVous(totalUsers) //Wait for the followers to join - everyone should be in the session now
    .exec {
      session =>
        println("Testing Rendezvous 3: " + System.currentTimeMillis())
        session
    }
    .exec(requests.Messages.CheckForReceivedMessages)

    /* PRESENTERS SEND MESSAGES*/

    .doIfEquals("#{type}", "Presenter") {
      pause(10.millis, 500.millis) //stagger the Presenters before starting to send messages
      .repeat(5, "counter") {
        exec {
          session =>
              println(session("user").as[String] + " (Presenter) sending message " + (session("counter").as[Int] + 1).toString)
              session
          }
        .exec(requests.Messages.PresenterSendMessage)
        .pause(5) //update to 500.milliseconds
      }
    }

    /* FOLLOWERS OUTPUT RECEIVED MESSAGES */

    .rendezVous(totalUsers) //Wait for all messages to be received before proceeding
    .exec {
      session =>
        println("Testing Rendezvous 4: " + System.currentTimeMillis())
        session
    }
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Messages.CheckForReceivedMessages)
    }

    /* PRESENTERS STOP PRESENTING */

    .rendezVous(totalUsers) //Wait for the presentation to finish
    .exec {
      session =>
        println("Testing Rendezvous 5: " + System.currentTimeMillis())
        session
    }
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Presentation.StopPresenting)
    }
    .pause(5)
    .exec(requests.Messages.CheckForReceivedMessages)

    /* FOLLOWERS LEAVE FIRST */

    .rendezVous(totalUsers)
    .exec {
      session =>
        println("Testing Rendezvous 6: " + System.currentTimeMillis())
        session
    }
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Session.LeaveSession)
    }
    .pause(5)
    .exec(requests.Messages.CheckForReceivedMessages)

    /* PRESENTERS LEAVE */

    .rendezVous(totalUsers)
    .exec {
      session =>
        println("Testing Rendezvous 7: " + System.currentTimeMillis())
        session
    }
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Session.LeaveSession)
    }

    /* ALL USERS LOGOUT OF XUI */

    .rendezVous(totalUsers)
    .exec {
      session =>
        println("Testing Rendezvous 8: " + System.currentTimeMillis())
        session
    }
    .exec(Logout.XUILogout)

}


