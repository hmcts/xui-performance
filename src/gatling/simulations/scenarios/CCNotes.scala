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
			.exec(XuiHelper.Homepage)
			.exec(XuiHelper.Login("#{solicitor_username}", "#{password}"))
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
			exec(XuiHelper.Homepage)
  				.exec(XuiHelper.Login("#{admin_username}", "#{password}"))
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
