package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.response.Response

import utils.{Common, Environment, Headers}

object Caseworker_AddReviewDate {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val UserFeederHearing = csv("UserDataXUIPCS_Reviewdate.csv").circular
 
 /*====================================================================================
  *Search PCS cases (SEARCH view) by Case ID
  *=====================================================================================*/

  val OpenCaseIssued =
        group("XUI_Caseworker_ReviewDate_030_ApplyFilter") {
			    exec(http("XUI_Caseworker_ReviewDate_030_ApplyFilter")
					.get("/auth/keepalive")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN1")))

				.exec(http("XUI_Caseworker_ReviewDate_030_001_ApplyFilter")
					.post("/data/internal/searchCases?ctid=#{caseType}&use_case=WORKBASKET&view=WORKBASKET&state=CASE_ISSUED&page=1")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Origin", BaseURL)
					.header("Priority", "u=0")
                    .formParam("X-XSRF-TOKEN", "#{XSRF-TOKEN1}")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.body(StringBody("{\"size\":25}"))
                    .check(substring("columns"))
				)
                .exec(http("/data/internal/cases/#{caseId}")
					.get("/data/internal/cases/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("experimental", "true")
					.header("Priority", "u=0")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
				    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", BaseURL)
					.header("Accept", "application/json, text/plain, */*")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
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
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.body(StringBody("{}"))
                    .check(status.in(204))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", s"$BaseURL/cases")
					.header("Accept", "application/json, text/plain, */*")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(substring("PCS"))
				)
				.exec(http("/aggregated/caseworkers/:uid/jurisdictions")
					.get("/aggregated/caseworkers/:uid/jurisdictions")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", s"$BaseURL/cases")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
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
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.body(StringBody("{\"refined\":true}"))
                    .check(status.in(200))
				)

				.pause(MinThinkTime, MaxThinkTime)
    }

  /*====================================================================================
  *SELECT ADD REVIEW DATE DROP DOWN 
  *=====================================================================================*/
    val AddReviewDateDropDown =
        group("XUI_PCS_Caseworker_AddReviewDate_05_SelectNextStepAddReviewDate") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_05_SelectNextStepAddReviewDate")
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
        			.header("accept", "application/json")
					.header("experimental", "true")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(jsonPath("$.jurisdictions[0].name").is("Civil Possession"))
				)
                .exec(http("/data/internal/cases/#{Case_id}/event-triggers/addCaseReviewDate")
					.get("/data/internal/cases/#{Case_id}/event-triggers/addCaseReviewDate")
					.headers(Headers.commonHeader)
					.header("experimental", "true")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.queryParam("ignore-warning", "false")
					.check(regex("event_token\":\"(.*?)\",\"wizard_pages").saveAs("event_token"))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					//.header("x-dtreferer", BaseURL + "/cases/case-details/PCS/PCS/#{caseId}")
					.header("Accept", "application/json, text/plain, */*")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(substring("PCS"))
				)

                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN4")))

				.pause(MinThinkTime, MaxThinkTime)
            }
 
 /*====================================================================================
  *ADD REVIEW DATE PAGE DETAILS
  *=====================================================================================*/

    val AddHearingDetails =
        group("XUI_PCS_Caseworker_AddReviewDate_06_EnterDetailsContinue") {
               exec(http("XUI_PCS_Caseworker_AddReviewDate_06_EnterDetailsContinue")
					.post("/data/case-types/PCS/validate?pageId=addCaseReviewDateaddCaseReviewDate")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Origin", BaseURL)
					.header("experimental", "true")
					.header("Priority", "u=0")
					.header("Client-Context", "eyJjbGllbnRfY29udGV4dCI6eyJ1c2VyX2xhbmd1YWdlIjp7Imxhbmd1YWdlIjoiZW4ifX19")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
					.header("X-XSRF-TOKEN", "#{XSRF-TOKEN4}")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.body(StringBody("{\"data\":{\"reviewDates\":[{\"value\":{\"date\":\"2028-02-01\",\"reason\":\"#{Reason}\",\"description\":\"test\"},\"id\":null}]},\"event\":{\"id\":\"addCaseReviewDate\",\"summary\":\"\",\"description\":\"\"},\"event_data\":{\"reviewDates\":[{\"value\":{\"date\":\"2028-02-01\",\"reason\":\"#{Reason}\",\"description\":\"test\"},\"id\":null}]},\"event_token\":\"#{event_token}\",\"ignore_warning\":false,\"case_reference\":\"#{Case_id}\"}"))
				)
               
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
				)

                 .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN5")))

				.pause(MinThinkTime, MaxThinkTime)
			}

        .group("XUI_PCS_Caseworker_AddReviewDate_07_ClickSubmit") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_07_ClickSubmit")
					.post("/data/cases/#{Case_id}/events")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Origin", BaseURL)
					.header("experimental", "true")
					.header("Priority", "u=0")
					.header("Client-Context", "eyJjbGllbnRfY29udGV4dCI6eyJ1c2VyX2xhbmd1YWdlIjp7Imxhbmd1YWdlIjoiZW4ifX19")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
					.header("X-XSRF-TOKEN", "#{XSRF-TOKEN5}")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.body(StringBody("{\"data\":{\"reviewDates\":[{\"value\":{\"date\":\"2028-02-01\",\"reason\":\"#{Reason}\",\"description\":\"test\"},\"id\":\"83a1fd0e-4263-4174-b0db-a46afed841ab\"}]},\"event\":{\"id\":\"addCaseReviewDate\",\"summary\":\"\",\"description\":\"\"},\"event_token\":\"#{event_token}\",\"ignore_warning\":false}"))
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN6")))

				.exec(http("/data/internal/cases/#{caseId}")
					.get("/data/internal/cases/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("experimental", "true")
					.header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
					.header("Content-Type", "application/json")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.check(substring("Possession"))
                    .check(jsonPath("$.case_type.name").is("Possession"))
				)

				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", BaseURL + "/cases/case-details/PCS/PCS/#{caseId}/trigger/manageHearing/submit")
					.header("Accept", "application/json, text/plain, */*")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(substring("PCS"))
		 		)
				.pause(MinThinkTime, MaxThinkTime)
			}

 /*====================================================================================
  * CLOSE CASE AND RETURN TO CASE DETAILS
  *=====================================================================================*/
     val CloseCase =
        group("XUI_PCS_Caseworker_AddReviewDate_08_CloseToReturnCaseDetails&Return") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_08_CloseToReturnCaseDetails&Return")
					.get("/auth/keepalive")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN6")))

				.exec(http("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
					.post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", BaseURL + "/cases/case-details/PCS/PCS/#{caseId}/trigger/manageHearing/confirm")
					.header("Origin", BaseURL)
					.header("Priority", "u=0")
					.header("Accept", "application/json, text/plain, */*")
					.formParam("X-XSRF-TOKEN", "${XSRF-TOKEN6}")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.body(StringBody("{}"))
                    .check(status.in(204))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", BaseURL + "/cases/case-details/PCS/PCS/#{caseId}/trigger/manageHearing/confirm")
					.header("Priority", "u=0")
					.header("Accept", "application/json, text/plain, */*")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(substring("PCS"))
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("x-dtreferer", BaseURL + "/cases/case-details/PCS/PCS/#{caseId}")
					.header("Accept", "application/json, text/plain, */*")
					//.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(substring("PCS"))
				)
                .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRF-TOKEN7")))

				.exec(http("/workallocation/case/task/#{caseId}")
					.post("/workallocation/case/task/#{caseId}")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Origin", BaseURL)
					.header("Accept", "application/json, text/plain, */*")
					.formParam("X-XSRF-TOKEN", "${XSRF-TOKEN7}")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.body(StringBody("{\"refined\":true}"))
                    .check(status.in(200))
				)
				.pause(MinThinkTime, MaxThinkTime)
	}
 /*====================================================================================
  * GO TO HISTORY TAB
  *=====================================================================================*/
    val HistoryView =
        group("XUI_PCS_Caseworker_AddReviewDate_08_clickhistoryTab") {
				exec(http("XUI_PCS_Caseworker_AddReviewDate_08_clickhistoryTab")
					.get("/auth/keepalive")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					.header("Accept", "application/json, text/plain, */*")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
				)
				.exec(http("/api/wa-supported-jurisdiction/get")
					.get("/api/wa-supported-jurisdiction/get")
					.headers(Headers.commonHeader)
        			.header("accept", "application/json")
					//.header("x-dtreferer", BaseURL + "/cases/case-details/PCS/PCS/#{caseId}")
					.header("Priority", "u=0")
					.header("Accept", "application/json, text/plain, */*")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
                    .check(substring("PCS"))
				)
				.pause(MinThinkTime, MaxThinkTime)

                .exec(http("/aggregated/caseworkers/:uid/jurisdictions")
					.get("/aggregated/caseworkers/:uid/jurisdictions")
					.headers(Headers.commonHeader)
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.header("Accept-Encoding", "gzip, deflate, br, zstd")
					.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:153.0) Gecko/20100101 Firefox/153.0")
					.header("Sec-Fetch-Dest", "empty")
					.queryParam("access", "read")
					.check(substring("Civil Possession"))
				)
			} 


} 

