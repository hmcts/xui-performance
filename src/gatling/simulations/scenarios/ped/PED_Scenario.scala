package scenarios.ped

import io.gatling.core.Predef._
import scenarios.{Homepage, Login, Logout}
import utils.Config

import scala.concurrent.duration._

object PED_Scenario {

  val PEDUserFeeder = csv("UserDataPED.csv")
  val NumberOfMessagesToSend = Config.PED_PRESENTATION_DURATION_MINS * 60 * 2 //one message every 500ms
  val NumberOfTimesToCheckForMessages = Config.PED_PRESENTATION_DURATION_MINS * 6 //one check every 10s

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
    //If any users don't obtain a Connection ID, abort the test
    .crashLoadGeneratorIf("ERROR: One or more of the users couldn't join the presentation, aborting simulation...", "#{connectionId.isUndefined()}")
    .rendezVous(totalUsers) //Wait for the followers to join - everyone should be in the session now
    .exec(requests.Messages.CheckForReceivedMessages)

    /* PRESENTERS SEND MESSAGES*/

    .doIfEquals("#{type}", "Presenter") {
      pause(10.millis, 500.millis) //stagger the Presenters before starting to send messages
      .repeat(NumberOfMessagesToSend, "counter") {
        exec(requests.Messages.PresenterSendMessage)
        .pause(500.millis)
      }
    }

    .doIfEquals("#{type}", "Follower") {
      pause(10.seconds) //delay polling to allow for the first set of messages to be sent
      //messages are polled periodically as to not exhaust the .wsUnmatchedInboundMessageBufferSize(50) configuration defined in the http protocol
      .repeat(NumberOfTimesToCheckForMessages) {
        exec(requests.Messages.CheckForReceivedMessages) // Check for any remaining messages periodically
        .pause(10.seconds) // Polls for messages every 10s. If this value is updated, the definition for NumberOfTimesToCheckForMessages will need amending
      }
    }

    /* FOLLOWERS OUTPUT RECEIVED MESSAGES */

    .rendezVous(totalUsers) //Wait for all messages to be sent before proceeding
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Messages.CheckForReceivedMessages) // Check for any remaining messages
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
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Messages.CheckForReceivedMessages) //Confirmation of Followers leaving
    }

    /* PRESENTERS LEAVE */

    .rendezVous(totalUsers)
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Session.LeaveSession)
    }

    /* ALL USERS LOGOUT OF XUI */

    .rendezVous(totalUsers)
    .exec(Logout.XUILogout)

}


