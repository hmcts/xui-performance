package utils

object Environment {

 val baseURL = "https://manage-case.${env}.platform.hmcts.net"
 val idamURL = "https://idam-web-public.${env}.platform.hmcts.net"

 val minThinkTime = 5
 val maxThinkTime = 7

}
