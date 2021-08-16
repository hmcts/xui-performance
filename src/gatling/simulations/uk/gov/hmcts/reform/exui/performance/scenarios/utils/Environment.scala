package uk.gov.hmcts.reform.exui.performance.scenarios.utils
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

 val S2S_ServiceName = "rd_professional_api"
  val S2SUrl = "http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal/testing-support"
  val PRDUrl = "http://rd-professional-api-${env}.service.core-compute-${env}.internal"
  val url_approve = "https://administer-orgs.${env}.platform.hmcts.net"
  val manageOrdURL = "https://manage-org.${env}.platform.hmcts.net"
 val manageOrdDomain = "manage-org.${env}.platform.hmcts.net"
  val idamURL = "https://idam-web-public.${env}.platform.hmcts.net"
  val baseURL = "https://manage-case.${env}.platform.hmcts.net"
  val ccdEnvurl = "https://ccd-case-management-web-${env}.service.core-compute-${env}.internal"
  val baseFPLAURL = "https://manage-case.${env}.platform.hmcts.net"
 val baseDomain="manage-case.${env}.platform.hmcts.net"
val baseDomainOrg="manage-org.${env}.platform.hmcts.net"
  val idamAPI="https://idam-api.${env}.platform.hmcts.net"
  val notificationClient="sidam_${env}-b7ab8862-25b4-41c9-8311-cb78815f7d2d-ebb113ff-da17-4646-a39e-f93783a993f4"

 val minThinkTime = 30 //30
  //10
  val maxThinkTime = 35 //30
  //30
  val minThinkTimeFPLC = 32
  //10
  val maxThinkTimeFPLC = 32

  val minThinkTimeSDO = 10//100
  //10
  val maxThinkTimeSDO = 10//110
  //30
  val minThinkTimeIACC = 50
  val maxThinkTimeIACC = 50
  val minThinkTimeFPLV = 10//450
  //10
  val maxThinkTimeFPLV = 10//480
  //320
  val minThinkTimeIACV = 10//470
  //10
  val maxThinkTimeIACV = 10//490
  val minThinkTimePROB = 35 //180
  val maxThinkTimePROB = 35 //190

  val minThinkTimeDIV = 60
  val maxThinkTimeDIV = 60

  val minThinkTimeCW = 40
  val maxThinkTimeCW = 40

  val minThinkTimeFR = 55
  val maxThinkTimeFR =55

  val constantthinkTime = 5

}
