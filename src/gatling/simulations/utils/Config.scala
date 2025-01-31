package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Config {

  val TEST_DATE_TIME = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"))
  val LOG_OUTPUT_PATH = "logs/" + TEST_DATE_TIME

}