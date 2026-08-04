package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import utils.Environment
import xui.Headers
import xui.XuiHelper

object CCCaseLink {
    val BaseURL = Environment.baseURL
	val IdamURL = Environment.idamURL
	val MinThinkTime = Environment.minThinkTime
	val MaxThinkTime = Environment.maxThinkTime

    val xuiUrl = "https://manage-case.#{env}.platform.hmcts.net"
	val idamUrl = "https://idam-web-public.#{env}.platform.hmcts.net"

    
     }