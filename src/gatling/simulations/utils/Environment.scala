package utils

object Environment {

 val baseURL = "https://manage-case.perftest.platform.hmcts.net"
 val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
 val idamAPIURL = "https://idam-api.${env}.platform.hmcts.net"
 val rpeAPIURL = "http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal"
 val ccdAPIURL = "http://ccd-data-store-api-${env}.service.core-compute-${env}.internal"
 val baseDomain = scala.util.Properties.envOrElse("baseDomain", "manage-case.perftest.platform.hmcts.net")

 val minThinkTime = 5
 val maxThinkTime = 7

}
