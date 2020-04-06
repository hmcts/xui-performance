package uk.gov.hmcts.reform.exui.performance.scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {
 val manageOrdURL="https://xui-mo-webapp-perftest.service.core-compute-perftest.internal"
  val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
  val baseURL = "https://xui-webapp-perftest.service.core-compute-perftest.internal"
 // val idamURL = "https://idam-web-public.aat.platform.hmcts.net"
  val ccdEnvurl = "https://ccd-case-management-web-aat.service.core-compute-aat.internal"
  //val baseURL = "https://xui-webapp-aat.service.core-compute-aat.internal"
 val baseFPLAURL = "https://ccd-case-management-web-aat.service.core-compute-aat.internal"
 val adminUserAO="vmuniganti@mailnesia.com"
 val adminPasswordAO="Monday01"
  // val baseURL = "https://gateway.ccd.demo.platform.hmcts.net"
  //val idamURL = "https://idam.preprod.ccidam.reform.hmcts.net"
  //val ccdEnvurl = "https://www.ccd.demo.platform.hmcts.net"
  val minThinkTime = 10
  val maxThinkTime = 30
  val constantthinkTime = 5
  //val minWaitForNextIteration = 300
 // val maxWaitForNextIteration = 600
  val HttpProtocol = http

}
