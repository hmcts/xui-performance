package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}
import java.io.{BufferedWriter, FileWriter}

/*======================================================================================
* Private Law application Progresses application to paid status
======================================================================================*/

object Solicitor_PRL_C100_ServiceRequestUpdate {
  val UserFeederHearingCases = csv("cases.csv").circular

  val PaymentViaAPI =

  feed(UserFeederHearingCases)

    .group("XXX_ServiceRequestUpdate") {
      exec(http(requestName = "service_request_update")
        .put("http://prl-cos-perftest.service.core-compute-perftest.internal/service-request-update")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .body(ElFileBody("bodies/prl/c100/PRLServiceRequestUpdate.json"))
        .check(status.is(200))
        .check(bodyString.saveAs("BODY3")))
        .exec {
          session =>
            println(session("BODY3").as[String])
            session
        }

        .exec { session =>
          val fw = new BufferedWriter(new FileWriter("caseSubmitted.csv", true))
          try {
            fw.write(session("caseId").as[String] + "\r\n")
          } finally fw.close()
          session
        }
    }
}