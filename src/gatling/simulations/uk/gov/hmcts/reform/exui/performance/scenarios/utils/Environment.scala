package uk.gov.hmcts.reform.exui.performance.scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {
 val manageOrdURL="https://manage-org.perftest.platform.hmcts.net"
  val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
// val baseURL = "https://xui-webapp-perftest.service.core-compute-perftest.internal"
 val baseURL = "https://manage-case.perftest.platform.hmcts.net"
 // val idamURL = "https://idam-web-public.aat.platform.hmcts.net"
  val ccdEnvurl = "https://ccd-case-management-web-aat.service.core-compute-aat.internal"
  //val baseURL = "https://xui-webapp-aat.service.core-compute-aat.internal"
 val baseFPLAURL = "https://manage-case.perftest.platform.hmcts.net"
 val adminUserAO="vmuniganti@mailnesia.com"
 val adminPasswordAO="Monday01"
  // val baseURL = "https://gateway.ccd.demo.platform.hmcts.net"
  //val idamURL = "https://idam.preprod.ccidam.reform.hmcts.net"
  //val ccdEnvurl = "https://www.ccd.demo.platform.hmcts.net"
  val minThinkTime = 5//10
  val maxThinkTime = 7 //30
  val constantthinkTime = 5
  //val minWaitForNextIteration = 300
 // val maxWaitForNextIteration = 600
  val HttpProtocol = http

 val commonHeader = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Upgrade-Insecure-Requests" -> "1")

  

}
