package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Config {

  val TEST_DATE_TIME = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"))

  /* PED CONFIG */
  val PED_WRITE_LOGS_TO_FILES = true //Users will write messages to files, so response times can be calculated
  val PED_LOG_OUTPUT_PATH = "logs/ped/" + TEST_DATE_TIME
  val PED_USERS_CSV_PATH = TestVolumesToCsvMapping.USERS40_SESSIONS5 //This controls the test volume
  val PED_PRESENTATION_DURATION_MINS = 20 //The duration for which Presenters will send messages
  val PED_SEND_MESSAGE_FREQ_MS = 500 //Send messages every 500ms
  val PED_POLL_MESSAGES_FREQ_MS = 5000 //Poll every 10s for new messages - ensure wsUnmatchedInboundMessageBufferSize(50)
                                        //is set high enough to handle the volume of messages in the poll window

  /* PED USER FILE MAPPINGS - Define CSV user files here for running the PED scenario with different user/session configurations */
  object TestVolumesToCsvMapping {

    val USERS40_SESSIONS5 = "UserDataPED-40users-5sessions.csv" //CSV containing 5 Presenters and 35 Followers, across 5 sessions
    val USERS80_SESSIONS10 = "UserDataPED-80users-10sessions.csv" //CSV containing 10 Presenters and 70 Followers, across 10 sessions
    val USERS160_SESSIONS20 = "UserDataPED-160users-20sessions.csv" //CSV containing 20 Presenters and 140 Followers, across 20 sessions
    val USERS520_SESSIONS65 = "UserDataPED-160users-20sessions.csv" //CSV containing 65 Presenters and 455 Followers, across 65 sessions
    val USERS1000_SESSIONS125 = "UserDataPED-1000users-125sessions.csv" //CSV containing 125 Presenters and 875 Followers, across 125 sessions

  }

}