package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import utils.{Common, Environment, Headers}

object Caseworker_PCS_AddReviewDate {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
 
 /*====================================================================================
  *Search PCS cases (SEARCH view) by Case ID
  *=====================================================================================*/

  val OpenCaseIssued =
        group("XUI_PCS_Caseworker_ReviewDate_030_ApplyFilter") {
			    exec(http("XUI_PCS_Caseworke_ReviewDate_030_ApplyFilter")
					.get("/auth/keepalive")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN1")))

				.exec(http("/data/internal/searchCases?ctid=PCS&use_case=WORKBASKET&view=WORKBASKET&state=CASE_ISSUED&$page=1")
					.post("/data/internal/searchCases?ctid=PCS&use_case=WORKBASKET&view=WORKBASKET&state=CASE_ISSUED&page=1")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Origin", BaseURL)
					.header("Priority", "u=0")
                    .formParam("X-XSRF-TOKEN", "#{XSRF-TOKEN1}")
					.body(StringBody("{\"size\":25}"))
                    .check(substring("columns"))
				)
                .exec(http("/data/internal/cases/#{caseId}")
					.get("/data/internal/cases/#{caseId}")
					.headers(Headers.commonHeader)
					.header("experimental", "true")
					.header("Priority", "u=0")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
					.check(jsonPath("$.case_type.name").is("Possession"))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", BaseURL)
					.header("Accept", "application/json, text/plain, */*")
                    .check(substring("PCS"))
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN2")))

				.exec(http("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
					.post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", s"$BaseURL/cases")
					.header("Origin", BaseURL)
					.header("Accept", "application/json, text/plain, */*")
					.formParam("X-XSRF-TOKEN", "${XSRF-TOKEN2}")
					.body(StringBody("{}"))
                    .check(status.in(204))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", s"$BaseURL/cases")
					.header("Accept", "application/json, text/plain, */*")
                    .check(substring("PCS"))
				)
				.exec(http("/aggregated/caseworkers/:uid/jurisdictions")
					.get("/aggregated/caseworkers/:uid/jurisdictions")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", s"$BaseURL/cases")
					.queryParam("access", "read")
                    .check(substring("Civil Possession"))
				)

                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN3")))

				.exec(http("/workallocation/case/task/#{caseId}")
					.post("/workallocation/case/task/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", s"$BaseURL/cases")
					.header("Origin", BaseURL)
					.header("Accept", "application/json, text/plain, */*")
					.formParam("X-XSRF-TOKEN", "${XSRF-TOKEN3}")
					.body(StringBody("{\"refined\":true}"))
                    .check(status.in(200))
				)
    }
		.pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *SELECT ADD REVIEW DATE DROP DOWN 
  *=====================================================================================*/
    val AddReviewDateDropDown =
        group("XUI_PCS_Caseworker_AddReviewDate_040_SelectStepAddReviewDate") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_040_SelectStepAddReviewDate")
					.get("/auth/keepalive")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
				)
				.exec(http("/workallocation/case/tasks/#{caseId}/event/addCaseReviewDate/caseType/#{caseType}/jurisdiction/#{caseType}")
					.get("/workallocation/case/tasks/#{caseId}/event/addCaseReviewDate/caseType/#{caseType}/jurisdiction/#{caseType}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Priority", "u=0")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(jsonPath("$.task_required_for_event").ofType[Boolean].is(false))
				)
				.exec(http("/data/internal/profile")
					.get("/data/internal/profile")
					.headers(Headers.commonHeader)
					.header("experimental", "true")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
                    .check(jsonPath("$.jurisdictions[0].name").is("Civil Possession"))
				)
                .exec(http("/data/internal/cases/#{caseId}/event-triggers/addCaseReviewDate")
					.get("/data/internal/cases/#{caseId}/event-triggers/addCaseReviewDate")
					.headers(Headers.commonHeader)
					.header("experimental", "true")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.queryParam("ignore-warning", "false")
					.check(regex("event_token\":\"(.*?)\",\"wizard_pages").saveAs("event_token"))
					.check(regex("""id":"reviewDates-(.*?)","type":""").saveAs("reviewDatesId"))
					.check(jsonPath("$.name").is("Add review date"))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
                    .check(substring("PCS"))
				)

                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN4")))

            }

			.pause(MinThinkTime, MaxThinkTime)
 
 /*====================================================================================
  *ADD REVIEW DATE PAGE DETAILS AND CLICK CONTINUE TAB
  *=====================================================================================*/

    val AddHearingDetails =
        group("XUI_PCS_Caseworker_AddReviewDate_050_EnterDetailsContinue") {
               exec(http("XUI_PCS_Caseworker_AddReviewDate_050_001_EnterDetails")
					.post("/data/case-types/PCS/validate?pageId=addCaseReviewDateaddCaseReviewDate")
					.headers(Headers.commonHeader)
					.header("Origin", BaseURL)
					.header("experimental", "true")
					.header("Priority", "u=0")
					.header("Client-Context", "eyJjbGllbnRfY29udGV4dCI6eyJ1c2VyX2xhbmd1YWdlIjp7Imxhbmd1YWdlIjoiZW4ifX19")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
					.header("X-XSRF-TOKEN", "#{XSRF-TOKEN4}")
					.body(StringBody("{\"data\":{\"reviewDates\":[{\"value\":{\"date\":\"#{ReviewDate}\",\"reason\":\"#{Reason}\",\"description\":\"test\"},\"id\":null}]},\"event\":{\"id\":\"addCaseReviewDate\",\"summary\":\"\",\"description\":\"\"},\"event_data\":{\"reviewDates\":[{\"value\":{\"date\":\"#{ReviewDate}\",\"reason\":\"#{Reason}\",\"description\":\"test\"},\"id\":null}]},\"event_token\":\"#{event_token}\",\"ignore_warning\":false,\"case_reference\":\"#{caseId}\"}"))
					.check(status.in(200))
					.check(jsonPath("$.data.reviewDates[0].value.description").is("test"))
				)
               
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
					.check(substring("CIVIL"))
				)

                 .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN5")))	
			}

			.pause(MinThinkTime, MaxThinkTime)

        .group("XUI_PCS_Caseworker_AddReviewDate_050_ContinueClickSubmit") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_050_ContinueClickSubmit")
					.post("/data/cases/#{caseId}/events")
					.headers(Headers.commonHeader)
					.header("Origin", BaseURL)
					.header("experimental", "true")
					.header("Priority", "u=0")
					.header("Client-Context", "eyJjbGllbnRfY29udGV4dCI6eyJ1c2VyX2xhbmd1YWdlIjp7Imxhbmd1YWdlIjoiZW4ifX19")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
					.header("X-XSRF-TOKEN", "#{XSRF-TOKEN5}")
					.body(StringBody("{\"data\":{\"reviewDates\":[{\"value\":{\"date\":\"#{ReviewDate}\",\"reason\":\"#{Reason}\",\"description\":\"test\"},\"id\":\"#{reviewDatesId}\"}]},\"event\":{\"id\":\"addCaseReviewDate\",\"summary\":\"\",\"description\":\"\"},\"event_token\":\"#{event_token}\",\"ignore_warning\":false}"))
					.check(substring("Work allocation tasks will be created for court staff"))
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN6")))

				.exec(http("/data/internal/cases/#{caseId}")
					.get("/data/internal/cases/#{caseId}")
					.headers(Headers.commonHeader)
					.header("experimental", "true")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
					.check(substring("Possession"))
                    .check(jsonPath("$.case_type.name").is("Possession"))
				)

				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
                    .check(substring("PCS"))
		 		)
			}
			.pause(MinThinkTime, MaxThinkTime)

 /*====================================================================================
  * CLOSE CASE AND RETURN TO CASE DETAILS
  *=====================================================================================*/
     val CloseCase =
        group("XUI_PCS_Caseworker_AddReviewDate_060_CloseToReturnCaseDetails") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_060_CloseToReturnCaseDetails")
					.get("/auth/keepalive")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN6")))

				.exec(http("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
					.post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Origin", BaseURL)
					.header("Priority", "u=0")
					.header("Accept", "application/json, text/plain, */*")
					.formParam("X-XSRF-TOKEN", "${XSRF-TOKEN6}")
					.body(StringBody("{}"))
                    .check(status.in(204))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", BaseURL + "/cases/case-details/PCS/PCS/#{caseId}")
					.header("Accept", "application/json, text/plain, */*")
                    .check(substring("PUBLICLAW"))
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN7")))

				.exec(http("/workallocation/case/task/#{caseId}")
					.post("/workallocation/case/task/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Origin", BaseURL)
					.header("Accept", "application/json, text/plain, */*")
					.formParam("X-XSRF-TOKEN", "${XSRF-TOKEN7}")
					.body(StringBody("{\"refined\":true}"))
                    .check(status.in(200))
				)
	}

	.pause(MinThinkTime, MaxThinkTime)
 /*====================================================================================
  * GO TO HISTORY TAB TO VIEW RECORDS 
  *=====================================================================================*/
    val HistoryView =
        group("XUI_PCS_Caseworker_AddReviewDate_060_ClickHistoryTab") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_060_ClickHistoryTab")
					.get("/auth/keepalive")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Priority", "u=0")
					.header("Accept", "application/json, text/plain, */*")
                    .check(substring("PCS"))
				)

                .exec(http("/aggregated/caseworkers/:uid/jurisdictions")
					.get("/aggregated/caseworkers/:uid/jurisdictions")
					.headers(Headers.commonHeader)
					.header("Accept", "application/json")
					.queryParam("access", "read")
					.check(substring("Civil Possession"))
				)
			} 
			.pause(MinThinkTime, MaxThinkTime)
} 

