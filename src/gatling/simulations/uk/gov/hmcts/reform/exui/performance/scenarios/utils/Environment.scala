package uk.gov.hmcts.reform.exui.performance.scenarios.utils
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

 val environment: String = System.getProperty("env")
 //if (environment == "perftest") {
  val url_approve = "https://administer-orgs.perftest.platform.hmcts.net"
  val manageOrdURL = "https://manage-org.perftest.platform.hmcts.net"
  val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
  val baseURL = "https://manage-case.perftest.platform.hmcts.net"
  val ccdEnvurl = "https://ccd-case-management-web-perftest.service.core-compute-perftest.internal"
  val baseFPLAURL = "https://manage-case.perftest.platform.hmcts.net"
 val baseDomain="manage-case.perftest.platform.hmcts.net"
val baseDomainOrg="manage-org.perftest.platform.hmcts.net"
  val adminUserAO = "vmuniganti@mailnesia.com"
  val adminPasswordAO = "Monday01"
  val notificationClient="sidam_perftest-b7ab8862-25b4-41c9-8311-cb78815f7d2d-ebb113ff-da17-4646-a39e-f93783a993f4"
  val minThinkTime =10// 100
  //10
  val maxThinkTime = 10// 110
  //30
  val minThinkTimeFPLC =10//  75
  //10
  val maxThinkTimeFPLC = 10// 80

  val minThinkTimeSDO =10//  100
  //10
  val maxThinkTimeSDO = 10// 110
  //30
  val minThinkTimeIACC =10//  100
  //10
  val maxThinkTimeIACC =10//  120
  val minThinkTimeFPLV =10//  450
  //10
  val maxThinkTimeFPLV = 10// 480
  //320
  val minThinkTimeIACV =10//  470
  //10
  val maxThinkTimeIACV =10//  490
  val minThinkTimePROB =10//  300
  val maxThinkTimePROB =10//  310

  val minThinkTimeDIV =10//  140
  val maxThinkTimeDIV =10//  160

  val minThinkTimeCW =10//  50
  val maxThinkTimeCW =10//  50

  val constantthinkTime = 5
  val HttpProtocol = http
// }
 //else {
 //================================================================================
 //below properties are for aat
 //================================================================================
 /* val url_approve = "https://administer-orgs.aat.platform.hmcts.net"
  val manageOrdURL = "https://manage-org.aat.platform.hmcts.net"
  val idamURL = "https://idam-web-public.aat.platform.hmcts.net"
  val baseURL = "https://manage-case.aat.platform.hmcts.net"
  val baseFPLAURL = "https://manage-case.aat.platform.hmcts.net"
  val baseDomain="manage-case.aat.platform.hmcts.net"
  val adminUserAO = "ao.admin.aat@mailinator.com"

  val adminPasswordAO = "Pass19word"
 val notificationClient="sidam_aat-b7ab8862-25b4-41c9-8311-cb78815f7d2d-4ceaf178-56c6-4b78-988b-05e5fee73188"
  val minThinkTime = 5
  //10
  val maxThinkTime = 6
  //30
  val minThinkTimeFPLC = 10
  //10
  val maxThinkTimeFPLC = 12
  //30
  val minThinkTimeIACC = 10
  //10
  val maxThinkTimeIACC = 12
  val minThinkTimeFPLV = 10
  //10
  val maxThinkTimeFPLV = 12
  //320
  val minThinkTimeIACV = 10
  //10
  val maxThinkTimeIACV = 12
  val minThinkTimePROB = 10
  //10
  val maxThinkTimePROB = 12
  val constantthinkTime = 5
  val HttpProtocol = http*/
 //}

 val commonHeader = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Upgrade-Insecure-Requests" -> "1")

}
