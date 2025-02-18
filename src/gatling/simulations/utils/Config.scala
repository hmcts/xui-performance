package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Config {

  val TEST_DATE_TIME = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"))
  val PED_LOG_OUTPUT_PATH = "logs/ped/" + TEST_DATE_TIME

  val PED_PRESENTATION_DURATION_MINS = 1
  val PED_SEND_MESSAGE_FREQ_MS = 500 //Send messages every 500ms
  val PED_POLL_MESSAGES_FREQ_MS = 10000 //Poll every 10s for new messages - ensure wsUnmatchedInboundMessageBufferSize(50)
                                        //is set high enough to handle the volume of messages in the poll window

}