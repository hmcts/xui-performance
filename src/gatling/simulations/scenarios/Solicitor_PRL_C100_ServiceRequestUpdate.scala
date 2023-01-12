package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}
import java.io.{BufferedWriter, FileWriter}

/*======================================================================================
* Private Law application Progresses application to paid status
======================================================================================*/

object Solicitor_PRL_C100_ServiceRequestUpdate {

  val PaymentViaAPI = scenario(scenarioName = "XXX_ServiceRequestUpdate")
    .group("XXX_ServiceRequestUpdate") {
      exec(http(requestName = "service_request_update")
        .put("http://prl-cos-demo.service.core-compute-demo.internal/service-request-update")
        .headers(Headers.commonHeader)
        .body(ElFileBody("bodies/prl/c100/PRLServiceRequestUpdate.json")).asJson
        .check(status.is(200))
        .check(bodyString.saveAs("BODY3")))
        .exec {
          session =>
            println(session("BODY3").as[String])
            session
        }
    }
}