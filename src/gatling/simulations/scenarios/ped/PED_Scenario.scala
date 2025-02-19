package scenarios.ped

import io.gatling.core.Predef._
import scenarios.{Homepage, Login, Logout}
import utils.Config

import scala.concurrent.duration._

object PED_Scenario {

  val PEDUserFeeder = csv("UserDataPED.csv")

  val sendMessageFreqMs = Config.PED_SEND_MESSAGE_FREQ_MS
  val pollMessageFreqMs = Config.PED_POLL_MESSAGES_FREQ_MS
  //val numberOfMessagesToSend = Config.PED_PRESENTATION_DURATION_MINS * 60 * 1000 / sendMessageFreqMs
  //val numberOfTimesToCheckForMessages = Config.PED_PRESENTATION_DURATION_MINS * 60 * 1000 / pollMessageFreqMs

  def PEDScenario(totalUsers: Int, debugMode: String) = scenario("***** PED Websockets Journey ******")

    .feed(PEDUserFeeder)

    //initialise session variables
    .exec { session =>
      val numberOfMessagesToSend = if(debugMode == "off") Config.PED_PRESENTATION_DURATION_MINS * 60 * 1000 / sendMessageFreqMs else 1
      val numberOfTimesToCheckForMessages = if(debugMode == "off")  Config.PED_PRESENTATION_DURATION_MINS * 60 * 1000 / pollMessageFreqMs else 1
      session.setAll( "numberOfMessagesToSend" -> numberOfMessagesToSend,
                      "numberOfTimesToCheckForMessages" -> numberOfTimesToCheckForMessages,
                      "caseType" -> "Benefit")
    }

    /* ALL USERS LOGIN TO XUI */

    .exec(Homepage.XUIHomePage)
    .exec(Login.XUILogin)

    /* PRESENTERS JOIN FIRST AND START PRESENTING */

    .rendezVous(totalUsers) //Wait for everyone to login to XUI
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Session.GetSessionInfo)
      .exec(requests.Session.JoinSession)
      .exec(requests.Presentation.StartPresenting)
      //If any users can't start presenting, abort the test
      .crashLoadGeneratorIf("ERROR: One or more of the users couldn't start the presentation, aborting simulation...", "#{PresentationStarted.isUndefined()}")
    }

    /* FOLLOWERS JOIN THE PRESENTATION */

    .rendezVous(totalUsers) //Wait for the presenters to start presenting before proceeding
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Session.GetSessionInfo)
      .exec(requests.Session.JoinSession)
    }
    //If any users can't connect, abort the test
    .crashLoadGeneratorIf("ERROR: One or more of the users couldn't connect to the presentation, aborting simulation...", "#{connectionId.isUndefined()}")
    .crashLoadGeneratorIf("ERROR: One or more of the users couldn't join the presentation, aborting simulation...", "#{JoinedSessionEvent.isUndefined()}")
    .rendezVous(totalUsers) //Wait for the followers to join - everyone should be in the session now
    .exec(requests.Messages.CheckForReceivedMessages)

    /* PRESENTERS SEND MESSAGES*/

    .doIfEquals("#{type}", "Presenter") {
      pause(10.millis, sendMessageFreqMs.millis) //Stagger the Presenters before starting to send messages
      .repeat("#{numberOfMessagesToSend}", "counter") {
        exec(requests.Messages.PresenterSendMessage)
        .pause(sendMessageFreqMs.millis)
      }
    }

    .doIfEquals("#{type}", "Follower") {
      pause(pollMessageFreqMs.millis) //Delay polling to allow for the first set of messages to be sent
      //Messages are polled periodically as to not exhaust the .wsUnmatchedInboundMessageBufferSize(50) configuration defined in the http protocol
      //update the config if this value is expected to be exceeded per poll
      .repeat("#{numberOfTimesToCheckForMessages}") {
        exec(requests.Messages.CheckForReceivedMessages) //Check for messages periodically
        .pause(pollMessageFreqMs.millis)
      }
    }

    /* FOLLOWERS OUTPUT RECEIVED MESSAGES */

    .rendezVous(totalUsers) //Wait for all messages to be sent before proceeding
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Messages.CheckForReceivedMessages) //Check for any remaining messages
    }

    /* PRESENTERS STOP PRESENTING */

    .rendezVous(totalUsers) //Wait for the presentation to finish
    .doIfEquals("#{type}", "Presenter") {
      exec(requests.Presentation.StopPresenting)
    }

    /* WAIT FOR PRESENTERS TO STOP PRESENTING */

    .rendezVous(totalUsers)
    .exec(requests.Messages.CheckForReceivedMessages) //Confirmation of end of presentation

    /* FOLLOWERS LEAVE FIRST */

    .rendezVous(totalUsers)
    .doIfEquals("#{type}", "Follower") {
      exec(requests.Session.LeaveSession)
    }

    /* WAIT FOR FOLLOWERS TO LEAVE */

    .rendezVous(totalUsers)
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


