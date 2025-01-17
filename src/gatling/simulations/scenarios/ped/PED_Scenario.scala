package scenarios.ped

import io.gatling.core.Predef._
import scenarios.{Homepage, Login, Logout}

object PED_Scenario {

  val PEDUserFeeder = csv("UserDataPED.csv").circular

  def PEDScenario(totalUsers: Int) = scenario("***** PED Websockets Journey ******")

    .feed(PEDUserFeeder)
    .exec(_.set("caseType", "Benefit"))

    /* ALL USERS LOGIN TO XUI */

    .exec(Homepage.XUIHomePage)
    .exec(Login.XUILogin)

    /* PRESENTERS JOIN FIRST AND START PRESENTING */

    .rendezVous(totalUsers) //Wait for everyone to login to XUI
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Session.GetSessionInfo)
      .exec(requests.Session.JoinSession)
      .exec(requests.Presentation.StartPresenting)
    }

    /* FOLLOWERS JOIN THE PRESENTATION */

    .rendezVous(totalUsers) //Wait for the presenters to start presenting before proceeding
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Session.GetSessionInfo)
      .exec(requests.Session.JoinSession)
    }
    .rendezVous(totalUsers) //Wait for the followers to join - everyone should be in the session now
    .exec(requests.Messages.CheckForReceivedMessages)

    /* PRESENTERS SEND MESSAGES*/

    .repeat(5, "counter") {
      doIfEquals("#{type}", "Presenter") {
        exec {
          session =>
            println(session("user").as[String] + " (Presenter) sending message " + (session("counter").as[Int] + 1).toString)
            session
        }
        .exec(requests.Messages.PresenterSendMessage)
      }
      .pause(1)
    }

    /* FOLLOWERS OUTPUT RECEIVED MESSAGES */

    .rendezVous(totalUsers) //Wait for all messages to be received before proceeding
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Messages.CheckForReceivedMessages)
    }

    /* PRESENTERS STOP PRESENTING */

    .rendezVous(totalUsers) //Wait for the presentation to finish
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Presentation.StopPresenting)
    }
    .pause(5)
    .exec(requests.Messages.CheckForReceivedMessages)

    /* FOLLOWERS LEAVE FIRST */

    .rendezVous(totalUsers)
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Session.LeaveSession)
    }
    .pause(5)
    .exec(requests.Messages.CheckForReceivedMessages)

    /* PRESENTERS LEAVE */

    .rendezVous(totalUsers)
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Session.LeaveSession)
    }

    /* ALL USERS LOGOUT OF XUI */

    .rendezVous(totalUsers)
    .exec(Logout.XUILogout)

}


