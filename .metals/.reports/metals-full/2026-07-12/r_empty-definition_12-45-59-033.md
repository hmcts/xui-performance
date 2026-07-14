error id: file://<WORKSPACE>/src/gatling/simulations/scenarios/CCNotes.scala:circular.
file://<WORKSPACE>/src/gatling/simulations/scenarios/CCNotes.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -io/gatling/core/Predef.
	 -io/gatling/core/Predef#
	 -io/gatling/core/Predef().
	 -io/gatling/http/Predef.
	 -io/gatling/http/Predef#
	 -io/gatling/http/Predef().
	 -scala/concurrent/duration.
	 -scala/concurrent/duration#
	 -scala/concurrent/duration().
	 -scala/Predef.
	 -scala/Predef#
	 -scala/Predef().
offset: 2223
uri: file://<WORKSPACE>/src/gatling/simulations/scenarios/CCNotes.scala
text:
```scala
// WARN: Script not converted: Init Timestamped Run Results CSV
// WARN: Script not converted: JSR223 Sampler
// WARN: Script under HTTP 'GET /auth/login' not converted: Extract OAuth from IDAM URL
// WARN: Script under HTTP 'POST /login' not converted: Generate State, Nonce & PKCE
// WARN: Script under HTTP 'POST /login' not converted: Validate Login and Log Status
// WARN: Script under HTTP 'CC_SC01_AddCaseNote_03_Search_Case_By_Reference' not converted: Validate Case Search Result
// WARN: IfController 'Skip CCNotes Flow If No Case ID' has a complex condition; emitted as TODO.
// WARN: IfController '01 Stop Retry On Success' has a complex condition; emitted as TODO.
// WARN: Script under HTTP 'CC_SC01_AddCaseNote_07_Validate_AddCaseNote' not converted: Build AddCaseNote Bodies
// WARN: Script under HTTP 'CC_SC01_AddCaseNote_08_Submit_AddCaseNote' not converted: 06 Log Note Submit Result
// WARN: Script not converted: Switch To Admin User
// WARN: Script under HTTP 'GET /auth/login' not converted: Extract OAuth from IDAM URL
// WARN: Script under HTTP 'POST /login' not converted: Generate State, Nonce & PKCE
// WARN: Script under HTTP 'POST /login' not converted: Validate Login and Log Status
// WARN: IfController 'Skip Verify If Admin Login Failed' has a complex condition; emitted as TODO.
// WARN: Script under HTTP 'CC_SC01_AddCaseNote_12_Verify_Case_Note' not converted: 08 Build Note Text
// WARN: Script under HTTP 'CC_SC01_AddCaseNote_12_Verify_Case_Note' not converted: 08 Log Verify Result
// WARN: IfController '08 Stop Retry On Success' has a complex condition; emitted as TODO.
// WARN: Skipped script element at plan level: CC Run Audit Logger
// WARN: Skipped script element at plan level: CC Run Results CSV Logger
// WARN: Skipped script element at plan level: API Call Trace Logger

package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

import utils.Environment
import xui.XuiHelper

object CCNotes {

	val BaseURL = Environment.baseURL
	val IdamURL = Environment.idamURL
	val MinThinkTime = Environment.minThinkTime
	val MaxThinkTime = Environment.maxThinkTime
	val userFeeder = csv("ccnoteuserdetails.csv").circular@@

	/*====================================================================================
	* Auto-converted from JMeter: CCNotes
	* Review TODOs (JSR223 scripts are not converted).
	 ====================================================================================*/

	val Flow =

		// TODO: convert JMeter script element 'JSR223 Sampler' (JSR223Sampler) manually
		group("CC_SC01_AddCaseNote_01_Launch") {
			exec(http("GET /cases")
				.get("/cases")
				.header("Sec-Purpose", "prefetch;prerender")
				.check(substring("HMCTS Manage cases"))
			)
		}
		.group("CC_SC01_AddCaseNote_02_Login") {
			exec(http("GET /auth/login")
				.get("/auth/login")
				.check(substring("Sign in"))
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
				.check(substring("HMCTS Manage cases"))
			)
		}
		.exec(http("CC_SC01_AddCaseNote_03_Search_Case_By_Reference")
			.post("/data/internal/searchCases?ctid=PCS&use_case=SEARCH&view=SEARCH&page=1&case_reference=#{Case_ID}")
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.header("Origin", "https://manage-case.perftest.platform.hmcts.net")
			.header("experimental", "true")
			.body(StringBody("{\"size\":25}"))
			.check(substring("\"results\""))
		)
		// TODO IfController 'Skip CCNotes Flow If No Case ID': ${__groovy("true".equals(vars.get("cc_case_ready")),)}
		// Children run unconditionally until wrapped in doIf(...).
		.repeat(3) {
			pause(MinThinkTime, MaxThinkTime)
			.exec(http("CC_SC01_AddCaseNote_04_Open_Case")
				.get("/data/internal/cases/#{Case_ID}")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
				.header("Content-Type", "application/json")
				.header("experimental", "true")
				.check(substring("\"case_id\""))
			)
			// TODO IfController '01 Stop Retry On Success': ${JMeterThread.last_sample_ok}
			// Children run unconditionally until wrapped in doIf(...).
		}
		.exec(http("CC_SC01_AddCaseNote_05_Get_Profile")
			.get("/data/internal/profile")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
			.header("experimental", "true")
		)
		.exec(http("CC_SC01_AddCaseNote_06_Start_AddCaseNote")
			.get("/data/internal/cases/#{Case_ID}/event-triggers/addCaseNote?ignore-warning=false")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
			.header("experimental", "true")
			.check(substring("Add a case note"))
			.check(jsonPath("$.event_token").saveAs("event_token"))
		)
		.exec(http("CC_SC01_AddCaseNote_07_Validate_AddCaseNote")
			.post("/data/case-types/PCS/validate?pageId=addCaseNoteaddCaseNote")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
			.header("Content-Type", "application/json")
			.header("Origin", "https://manage-case.perftest.platform.hmcts.net")
			.header("experimental", "true")
			.body(StringBody("#{addCaseNoteValidateBody}"))
			.check(substring("caseTitleMarkdown"))
		)
		.exec(http("CC_SC01_AddCaseNote_08_Submit_AddCaseNote")
			.post("/data/cases/#{Case_ID}/events")
			.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
			.header("Content-Type", "application/json")
			.header("Origin", "https://manage-case.perftest.platform.hmcts.net")
			.header("experimental", "true")
			.body(StringBody("#{addCaseNoteEventBody}"))
			.check(status.is(201))
			.check(substring("\"jurisdiction\":\"PCS\""))
		)
		.exec(http("CC_SC01_AddCaseNote_09_Sign_Out")
			.get("/auth/logout")
		)
		// TODO: convert JMeter script element 'Switch To Admin User' (JSR223Sampler) manually
		.group("CC_SC01_AddCaseNote_10_Launch_Admin") {
			exec(http("GET /cases")
				.get("/cases")
				.header("Sec-Purpose", "prefetch;prerender")
				.check(substring("HMCTS Manage cases"))
			)
		}
		.group("CC_SC01_AddCaseNote_11_Login_Admin") {
			exec(http("GET /auth/login")
				.get("/auth/login")
				.check(substring("Sign in"))
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
				.check(substring("HMCTS Manage cases"))
			)
		}
		// TODO IfController 'Skip Verify If Admin Login Failed': ${__groovy("true".equals(vars.get("admin_login_ok")),)}
		// Children run unconditionally until wrapped in doIf(...).
		.repeat(3) {
			pause(MinThinkTime, MaxThinkTime)
			.exec(http("CC_SC01_AddCaseNote_12_Verify_Case_Note")
				.get("/data/internal/cases/#{Case_ID}")
				.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
				.header("Content-Type", "application/json")
				.header("experimental", "true")
				.check(status.is(200))
				.check(substring("${noteText}"))
			)
			// TODO IfController '08 Stop Retry On Success': ${JMeterThread.last_sample_ok}
			// Children run unconditionally until wrapped in doIf(...).
		}

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 