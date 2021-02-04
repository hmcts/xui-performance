package uk.gov.hmcts.reform.exui.performance.scenarios.utils
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

 val environment: String = System.getProperty("env")
 //if (environment == "perftest") {
 val S2S_ServiceName = "rd_professional_api"
  val S2SUrl = "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support"
  val PRDUrl = "http://rd-professional-api-perftest.service.core-compute-perftest.internal"
  val url_approve = "https://administer-orgs.perftest.platform.hmcts.net"
  val manageOrdURL = "https://manage-org.perftest.platform.hmcts.net"
 val manageOrdDomain = "manage-org.perftest.platform.hmcts.net"
  val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
  val baseURL = "https://manage-case.perftest.platform.hmcts.net"
  val ccdEnvurl = "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal"
  val baseFPLAURL = "https://manage-case.perftest.platform.hmcts.net"
 val baseDomain="manage-case.perftest.platform.hmcts.net"
val baseDomainOrg="manage-org.perftest.platform.hmcts.net"
  val idamAPI="https://idam-api.perftest.platform.hmcts.net"
  val notificationClient="sidam_perftest-b7ab8862-25b4-41c9-8311-cb78815f7d2d-ebb113ff-da17-4646-a39e-f93783a993f4"

 val minThinkTime = 10//100
  //10
  val maxThinkTime = 10//110
  //30
  val minThinkTimeFPLC = 75
  //10
  val maxThinkTimeFPLC = 80

  val minThinkTimeSDO = 100
  //10
  val maxThinkTimeSDO = 110
  //30
  val minThinkTimeIACC = 10//100
  //10
  val maxThinkTimeIACC = 10//120
  val minThinkTimeFPLV = 450
  //10
  val maxThinkTimeFPLV = 480
  //320
  val minThinkTimeIACV = 470
  //10
  val maxThinkTimeIACV = 490
  val minThinkTimePROB = 300
  val maxThinkTimePROB = 310

  val minThinkTimeDIV = 140
  val maxThinkTimeDIV = 160

  val minThinkTimeCW = 50
  val maxThinkTimeCW = 50

  val minThinkTimeFR = 10//100
  val maxThinkTimeFR =10//100

  val constantthinkTime = 5
  val HttpProtocol = http
  
 //}

 val commonHeader = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Upgrade-Insecure-Requests" -> "1")

}
