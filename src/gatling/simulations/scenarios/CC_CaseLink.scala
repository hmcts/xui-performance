// WARN: Script not converted: Init Timestamped Run Results CSV
// WARN: Script not converted: JSR223 Sampler
// WARN: Script under HTTP 'GET /auth/login' not converted: Extract OAuth from IDAM URL
// WARN: Script under HTTP 'POST /login' not converted: Generate State, Nonce & PKCE
// WARN: Script under HTTP 'POST /login' not converted: Validate Login and Log Status
// WARN: Script under HTTP '00_Search_Workbasket_Cases' not converted: Select Case And Linking Case From List
// WARN: IfController 'Skip Case Link Flow If No Case IDs' has a complex condition; emitted as TODO.
// WARN: IfController '01 Stop Retry On Success' has a complex condition; emitted as TODO.
// WARN: Script under HTTP '04_Fetch_Linking_Case' not converted: Extract Linking Case Metadata
// WARN: Script under HTTP '05_Validate_CreateCaseLink' not converted: Build Case Link Validate Body
// WARN: Script under HTTP '06_Submit_CreateCaseLink' not converted: Build Case Link Event Body
// WARN: Script under HTTP '06_Submit_CreateCaseLink' not converted: 06 Log Link Submit Result
// WARN: Script under HTTP '09_Open_Linked_Case' not converted: 09 Validate Linking Case ID
// WARN: Script under HTTP '09_Open_Linked_Case' not converted: 09 Log HTTP Result
// WARN: IfController '09 Stop Retry On Success' has a complex condition; emitted as TODO.
// WARN: Skipped script element at plan level: CC Run Audit Logger
// WARN: Skipped script element at plan level: CC Run Results CSV Logger
// WARN: Skipped script element at plan level: API Call Trace Logger

package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

import utils.Environment

object CC_CaseLink {

	val BaseURL = Environment.baseURL
	val IdamURL = Environment.idamURL
	val MinThinkTime = Environment.minThinkTime
	val MaxThinkTime = Environment.maxThinkTime

	/*====================================================================================
	* Auto-converted from JMeter: Launch-trial
	* Review TODOs (JSR223 scripts are not converted).
	 ====================================================================================*/

	val Flow =

		// TODO: convert JMeter script element 'JSR223 Sampler' (JSR223Sampler) manually
		group("Launch and Login") {
			exec(http("GET /cases")
				.get("/cases")
				.header("Sec-Purpose", "prefetch;prerender")
			)
			.exec(http("GET /auth/login")
				.get("/auth/login")
				.check(regex("name=\"_csrf\".*?value=\"([^\"]+)\"").saveAs("csrf"))
			)
			.exec(http("POST /login")
				.post("https://idam-web-public.#{env}.platform.hmcts.net/login")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.formParam("username", "#{username}")
				.formParam("password", "#{password}")
				.formParam("state", "#{state}")
				.formParam("nonce", "#{nonce}")
				.formParam("code_challenge", "#{code_challenge}")
				.formParam("_csrf", "#{csrf}")
				.formParam("scope", "profile openid roles manage-user create-user search-user")
				.formParam("response_type", "code")
				.formParam("redirect_uri", "https://manage-case.perftest.platform.hmcts.net/oauth2/callback")
				.formParam("code_challenge_method", "S256")
				.formParam("prompt", "")
				.formParam("client_id", "xuiwebapp")
				.formParam("mojLoginEnabled", "true")
				.formParam("selfRegistrationEnabled", "false")
				.formParam("azureLoginEnabled", "true")
			)
			.exec(http("GET /cases")
				.get("/cases")
			)
		}
		.exec(http("00_Search_Workbasket_Cases")
			.post("/data/internal/searchCases?ctid=PCS&use_case=WORKBASKET&view=WORKBASKET&page=1")
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.header("Origin", "https://manage-case.perftest.platform.hmcts.net")
			.header("experimental", "true")
			.body(StringBody("{\"size\":25}"))
		)
		// TODO IfController 'Skip Case Link Flow If No Case IDs': ${__groovy("true".equals(vars.get("cc_pair_ready")),)}
		// Children run unconditionally until wrapped in doIf(...).
		.repeat(3) {
			pause(MinThinkTime, MaxThinkTime)
			.exec(http("01_Open_Case")
				.get("/data/internal/cases/#{Case_ID}")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
				.header("Content-Type", "application/json")
				.header("experimental", "true")
			)
			// TODO IfController '01 Stop Retry On Success': ${JMeterThread.last_sample_ok}
			// Children run unconditionally until wrapped in doIf(...).
		}
		.exec(http("02_Get_Linked_Cases")
			.get("/getLinkedCases/#{Case_ID}")
		)
		.exec(http("03_Start_CreateCaseLink")
			.get("/data/internal/cases/#{Case_ID}/event-triggers/createCaseLink?ignore-warning=false")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
			.header("Content-Type", "application/json")
			.header("experimental", "true")
			.check(jsonPath("$.event_token").saveAs("event_token"))
		)
		.exec(http("04_Fetch_Linking_Case")
			.get("/data/internal/cases/#{Linking_Case_ID}")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
			.header("Content-Type", "application/json")
			.header("experimental", "true")
		)
		.exec(http("05_Validate_CreateCaseLink")
			.post("/data/case-types/PCS/validate?pageId=createCaseLinkcreateCaseLink")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
			.header("Content-Type", "application/json")
			.header("Origin", "https://manage-case.perftest.platform.hmcts.net")
			.header("experimental", "true")
			.body(StringBody("#{caseLinkValidateBody}"))
		)
		.exec(http("06_Submit_CreateCaseLink")
			.post("/data/cases/#{Case_ID}/events")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
			.header("Content-Type", "application/json")
			.header("Origin", "https://manage-case.perftest.platform.hmcts.net")
			.header("experimental", "true")
			.body(StringBody("#{caseLinkEventBody}"))
		)
		.exec(http("07_Refresh_Main_Case")
			.get("/data/internal/cases/#{Case_ID}")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
			.header("Content-Type", "application/json")
			.header("experimental", "true")
		)
		.exec(http("08_Get_Linked_Cases_After")
			.get("/getLinkedCases/#{Case_ID}")
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
		)
		.repeat(3) {
			pause(MinThinkTime, MaxThinkTime)
			.exec(http("09_Open_Linked_Case")
				.get("/data/internal/cases/#{Linking_Case_ID}")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
				.header("Content-Type", "application/json")
				.header("experimental", "true")
				.check(status.is(200))
			)
			// TODO IfController '09 Stop Retry On Success': ${JMeterThread.last_sample_ok}
			// Children run unconditionally until wrapped in doIf(...).
		}

}
