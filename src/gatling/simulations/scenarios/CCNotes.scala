package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import utils.Environment
import xui.Headers
import xui.XuiHelper

object CCNotes {

	val BaseURL = Environment.baseURL
	val IdamURL = Environment.idamURL
	val MinThinkTime = Environment.minThinkTime
	val MaxThinkTime = Environment.maxThinkTime
	val userFeeder = csv("ccnoteuserdetails.csv").circular

	val xuiUrl = "https://manage-case.#{env}.platform.hmcts.net"
	val idamUrl = "https://idam-web-public.#{env}.platform.hmcts.net"

	/**
	 * IDAM Access UI: /auth/login redirects to /o/authorize then /enter-email.
	 * OAuth params are on the authorize Location header. Does not modify XuiHelper.
	 */
	val HomepageCompat = {
		exec(flushHttpCache)
			.exec(flushCookieJar)
			.group("CC_XUI_Homepage_Compat") {
				exec(http("CC_Homepage_HomepageRequest")
					.get(xuiUrl)
					.headers(Headers.navigationHeader)
					.header("sec-fetch-site", "none"))
				.exec(http("CC_Homepage_ConfigurationUI")
					.get(xuiUrl + "/external/configuration-ui/")
					.headers(Headers.commonHeader)
					.header("accept", "*/*")
					.check(substring("ccdGatewayUrl")))
				.exec(http("CC_Homepage_ConfigJson")
					.get(xuiUrl + "/assets/config/config.json")
					.header("accept", "application/json, text/plain, */*")
					.check(substring("caseEditorConfig")))
				.exec(http("CC_Homepage_TsAndCs")
					.get(xuiUrl + "/api/configuration?configurationKey=termsAndConditionsEnabled")
					.headers(Headers.commonHeader)
					.header("accept", "application/json, text/plain, */*")
					.check(substring("false")))
				.exec(http("CC_Homepage_ConfigUI")
					.get(xuiUrl + "/external/config/ui")
					.headers(Headers.commonHeader)
					.header("accept", "application/json, text/plain, */*")
					.check(substring("ccdGatewayUrl")))
				.exec(http("CC_Homepage_UserDetails")
					.get(xuiUrl + "/api/user/details?refreshRoleAssignments=undefined")
					.headers(Headers.commonHeader)
					.header("accept", "application/json, text/plain, */*")
					.check(status.in(200, 304, 401)))
				.exec(http("CC_Homepage_IsAuthenticated")
					.get(xuiUrl + "/auth/isAuthenticated")
					.headers(Headers.commonHeader)
					.header("accept", "application/json, text/plain, */*")
					.check(regex("true|false")))
				.exec(http("CC_Homepage_AuthLogin")
					.get(xuiUrl + "/auth/login")
					.headers(Headers.navigationHeader)
					.header("sec-fetch-site", "same-origin")
					.disableFollowRedirect
					.check(status.in(301, 302, 303, 307))
					.check(header("Location").saveAs("authorizeUrl")))
				.exec(session => {
					var s = session
					val authUrl = s("authorizeUrl").as[String]
					def q(name: String): Option[String] = {
						val m = (("[?&]" + name + "=([^&]+)").r).findFirstMatchIn(authUrl)
						m.map(x => java.net.URLDecoder.decode(x.group(1), "UTF-8"))
					}
					q("state").foreach(v => s = s.set("state", v))
					q("nonce").foreach(v => s = s.set("nonce", v))
					q("code_challenge").foreach(v => s = s.set("code_challenge", v))
					s
				})
				.exec(http("CC_Homepage_Authorize")
					.get("#{authorizeUrl}")
					.headers(Headers.navigationHeader)
					.header("sec-fetch-site", "cross-site")
					.check(status.is(200))
					.check(css("input[name='_csrf']", "value").saveAs("csrf"))
					.check(currentLocation.saveAs("idamLandingUrl")))
				.exec(session =>
					if (session.contains("csrf") && session.contains("state")) session else session.markAsFailed
				)
			}
	}

	/**
	 * IDAM Access login: POST /enter-email -> GET /enter-password -> POST /enter-password.
	 * (XuiHelper.Login still posts /login which returns 404 on Access UI.)
	 */
	def LoginCompat(email: String, password: String) = {
		group("CC_XUI_Login_Compat") {
			exec(http("CC_Login_EnterEmail")
				.post(idamUrl + "/enter-email")
				.headers(Headers.navigationHeader)
				.headers(Headers.postHeader)
				.header("origin", idamUrl)
				.formParam("email", email)
				.formParam("_csrf", "#{csrf}")
				.disableFollowRedirect
				.check(status.in(302, 303, 307))
				.check(header("Location").saveAs("enterPasswordPath")))
			.exec(http("CC_Login_EnterPasswordPage")
				.get(idamUrl + "/enter-password")
				.headers(Headers.navigationHeader)
				.check(status.is(200))
				.check(css("input[name='_csrf']", "value").saveAs("csrf"))
				.check(substring("password")))
			.exec(http("CC_Login_EnterPasswordSubmit")
				.post(idamUrl + "/enter-password")
				.headers(Headers.navigationHeader)
				.headers(Headers.postHeader)
				.header("origin", idamUrl)
				.formParam("password", password)
				.formParam("action", "_submit")
				.formParam("_csrf", "#{csrf}")
				.check(regex("Manage cases")))
			.exec(http("CC_Login_ConfigUI")
				.get(xuiUrl + "/external/config/ui/")
				.headers(Headers.commonHeader)
				.header("accept", "*/*")
				.check(substring("ccdGatewayUrl")))
			.exec(http("CC_Login_ConfigJson")
				.get(xuiUrl + "/assets/config/config.json")
				.header("accept", "application/json, text/plain, */*")
				.check(substring("caseEditorConfig")))
			.exec(http("CC_Login_TsAndCs")
				.get(xuiUrl + "/api/configuration?configurationKey=termsAndConditionsEnabled")
				.headers(Headers.commonHeader)
				.header("accept", "application/json, text/plain, */*")
				.check(substring("false")))
			.exec(http("CC_Login_UserDetails")
				.get(xuiUrl + "/api/user/details?refreshRoleAssignments=true")
				.headers(Headers.commonHeader)
				.header("accept", "application/json, text/plain, */*")
				.check(status.in(200, 304)))
			.exec(http("CC_Login_IsAuthenticated")
				.get(xuiUrl + "/auth/isAuthenticated")
				.headers(Headers.commonHeader)
				.header("accept", "application/json, text/plain, */*")
				.check(regex("true|false")))
			.exec(http("CC_Login_MonitoringTools")
				.get(xuiUrl + "/api/monitoring-tools")
				.headers(Headers.commonHeader)
				.header("accept", "application/json, text/plain, */*")
				.check(jsonPath("$.key").notNull))
			.exec(http("CC_Login_Jurisdictions")
				.get(xuiUrl + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
				.headers(Headers.commonHeader)
				.header("accept", "application/json")
				.check(substring("id")))
			.exec(getCookieValue(
				CookieKey("XSRF-TOKEN")
					.withDomain("manage-case.#{env}.platform.hmcts.net")
					.withSecure(true)
					.saveAs("XSRFToken")
			))
			.exec(http("CC_Login_OrgDetails")
				.get(xuiUrl + "/api/organisation")
				.headers(Headers.commonHeader)
				.header("accept", "application/json, text/plain, */*")
				.check(regex("name|Organisation route error"))
				.check(status.in(200, 304, 401, 403)))
			.exec(http("CC_Login_WorkBasketInputs")
				.get(xuiUrl + "/data/internal/case-types/#{caseType}/work-basket-inputs")
				.headers(Headers.commonHeader)
				.header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8")
				.check(regex("workbasketInputs|Not Found"))
				.check(status.in(200, 404)))
			.exec(http("CC_Login_SearchCases")
				.post(xuiUrl + "/data/internal/searchCases?ctid=#{caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
				.headers(Headers.commonHeader)
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.body(StringBody("""{"size":25}"""))
				.check(substring("columns")))
			.exitHereIfFailed
		}
	}

	val Flow =

		group("CC_SC01_AddCaseNote_01_Login") {
			exec(session => {
				val solicitor = session("solicitor_username").as[String].trim
				val caseId = session("Case_ID").as[String].trim
				val admin = session("admin_username").as[String].trim
				session
					.set("solicitor_username", solicitor)
					.set("username", solicitor)
					.set("admin_username", admin)
					.set("Case_ID", caseId)
					.set("caseType", "PCS")
			})
			.exec(HomepageCompat)
			.exec(LoginCompat("#{solicitor_username}", "#{password}"))
		}

		.exec(http("CC_SC01_AddCaseNote_03_Search_Case_By_Reference")
			.post("/data/internal/searchCases?ctid=PCS&use_case=SEARCH&view=SEARCH&page=1&case_reference=#{Case_ID}")
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.header("experimental", "true")
			.body(StringBody("""{"size":25}"""))
			.check(status.is(200)))

		.doIf(session => session.contains("Case_ID")) {
			tryMax(3) {
				pause(MinThinkTime, MaxThinkTime)
				.exec(http("CC_SC01_AddCaseNote_04_Open_Case")
					.get("/data/internal/cases/#{Case_ID}")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
					.header("Content-Type", "application/json")
					.header("experimental", "true")
					.check(jsonPath("$.case_id").exists.saveAs("caseOpened")))
			}
			.exec(http("CC_SC01_AddCaseNote_05_Get_Profile")
				.get("/data/internal/profile")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
				.header("experimental", "true"))
			.exec(http("CC_SC01_AddCaseNote_06_Start_AddCaseNote")
				.get("/data/internal/cases/#{Case_ID}/event-triggers/addCaseNote?ignore-warning=false")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
				.header("experimental", "true")
				.check(substring("Add a case note"))
				.check(jsonPath("$.event_token").saveAs("event_token")))
			.exec(session => {
				val noteText =
					"Testing-" + Thread.currentThread().getName + "-" + System.currentTimeMillis()
				val caseId = session("Case_ID").as[String]
				val eventToken = session("event_token").as[String]
				val validateBody =
					s"""{"data":{"note":"$noteText"},"event":{"id":"addCaseNote","summary":"","description":""},"event_data":{"note":"$noteText"},"event_token":"$eventToken","ignore_warning":false,"case_reference":"$caseId"}"""
				val eventBody =
					s"""{"data":{"note":"$noteText"},"event":{"id":"addCaseNote","summary":"","description":""},"event_token":"$eventToken","ignore_warning":false}"""
				session
					.set("noteText", noteText)
					.set("addCaseNoteValidateBody", validateBody)
					.set("addCaseNoteEventBody", eventBody)
			})
			.exec(http("CC_SC01_AddCaseNote_07_Validate_AddCaseNote")
				.post("/data/case-types/PCS/validate?pageId=addCaseNoteaddCaseNote")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("experimental", "true")
				.header("X-XSRF-TOKEN", "#{XSRFToken}")
				.body(StringBody("#{addCaseNoteValidateBody}"))
				.check(substring("caseTitleMarkdown")))
			.exec(http("CC_SC01_AddCaseNote_08_Submit_AddCaseNote")
				.post("/data/cases/#{Case_ID}/events")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("experimental", "true")
				.header("X-XSRF-TOKEN", "#{XSRFToken}")
				.body(StringBody("#{addCaseNoteEventBody}"))
				.check(status.is(201))
				.check(substring("\"jurisdiction\":\"PCS\"")))
		}
		.exec(XuiHelper.Logout)
		.group("CC_SC01_AddCaseNote_10_Admin_Login") {
			exec(HomepageCompat)
				.exec(LoginCompat("#{admin_username}", "#{password}"))
		}
		.repeat(3) {
			pause(MinThinkTime, MaxThinkTime)
			.exec(http("CC_SC01_AddCaseNote_12_Verify_Case_Note")
				.get("/data/internal/cases/#{Case_ID}")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
				.header("Content-Type", "application/json")
				.header("experimental", "true")
				.check(status.is(200))
				.check(substring("#{noteText}")))
		}
}
