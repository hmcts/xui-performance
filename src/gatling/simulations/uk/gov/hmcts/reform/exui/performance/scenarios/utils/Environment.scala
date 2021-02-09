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

 val minThinkTime = 10//50
  //10
  val maxThinkTime = 10//55
  //30
  val minThinkTimeFPLC = 10//30
  //10
  val maxThinkTimeFPLC = 10//35

  val minThinkTimeSDO = 10//100
  //10
  val maxThinkTimeSDO = 10//110
  //30
  val minThinkTimeIACC = 10//45
  //10
  val maxThinkTimeIACC = 10//50
  val minThinkTimeFPLV = 10//450
  //10
  val maxThinkTimeFPLV = 10//480
  //320
  val minThinkTimeIACV = 10//470
  //10
  val maxThinkTimeIACV = 10//490
  val minThinkTimePROB = 10//170
  val maxThinkTimePROB = 10//180

  val minThinkTimeDIV = 10//55
  val maxThinkTimeDIV = 10//60

  val minThinkTimeCW = 10//200
  val maxThinkTimeCW = 10//210

  val minThinkTimeFR = 10//50
  val maxThinkTimeFR =10//50

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
