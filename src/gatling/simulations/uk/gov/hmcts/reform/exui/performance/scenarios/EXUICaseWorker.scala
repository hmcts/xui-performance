package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.CaseworkerHeader

object EXUICaseWorker {

  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  val caseFeeder = csv("CaseworkerSearches.csv").random

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val SearchCase = 

    // exec(http("XUI${service}_030_SearchPage")
    //     .get("/data/internal/case-types/GrantOfRepresentation/work-basket-inputs")
    //     .headers(CaseworkerHeader.headers_0)
    //     .header("X-XSRF-TOKEN", "${xsrfToken}"))

	// 	.pause(2)

    // .exec(http("request_1")
    //     .get("/data/internal/case-types/Caveat/work-basket-inputs")
    //     .headers(CaseworkerHeader.headers_0)
    //     .header("X-XSRF-TOKEN", "${xsrfToken}"))
    // .pause(1)

    // .

    feed(caseFeeder)

    .exec(http("XUI${service}_030_005_SearchPagination")
        .get("/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata")
        .headers(CaseworkerHeader.headers_2)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))

    .exec(http("XUI${service}_030_010_SearchWorkbasket")
        .get("/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=WORKBASKET&page=1")
        .headers(CaseworkerHeader.headers_2)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))

    .pause(MinThinkTime, MaxThinkTime)

    val ViewCase = 

    foreach("${caseNumbers}","caseNumber") {

        exec(http("XUI${service}_040_005_ViewCaseDetails")
            .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseNumber}")
            .headers(CaseworkerHeader.headers_4))

        .exec(http("XUI${service}_040_010_ViewCase")
            .get("/data/internal/cases/${caseNumber}")
            .headers(CaseworkerHeader.headers_5)
            .header("X-XSRF-TOKEN", "${xsrfToken}")
            .check(regex("""internal/documents/(.+?)","document_filename""").find(0).saveAs("Document_ID")))

        .exec(http("XUI${service}_040_015_ViewUndefined")
            .get("/undefined/cases/${caseNumber}")
            .headers(CaseworkerHeader.headers_4))

        .exec(http("XUI${service}_040_020_GetPaymentGroups")
            .get("/payments/cases/${caseNumber}/paymentgroups")
            .headers(CaseworkerHeader.headers_4)
            .check(status.is(404)))
        
        .pause(MinThinkTime, MaxThinkTime)

        //TO DO - put this in a do-if statement, so only do these steps if document_ID is found

        .exec(http("XUI${service}_050_005_ClickDocumentsTab")
            .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseNumber}%23documentsTab")
            .headers(CaseworkerHeader.headers_4))

        .exec(http("XUI_060_005_ViewCaseDocumentUI")
            .get("/external/config/ui")
            .headers(CaseworkerHeader.headers_documents))

        .exec(http("XUI_060_010_ViewCaseDocumentT&C")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(CaseworkerHeader.headers_documents))

        .exec(http("XUI_060_015_ViewCaseDocumentAnnotations")
            .get("/em-anno/annotation-sets/filter?documentId=${Document_ID}")
            .headers(CaseworkerHeader.headers_documents)
            .check(status.in(200, 404,304)))

        .exec(http("XUI_060_020_ViewCaseDocumentBinary")
            .get("/documents/${Document_ID}/binary")
            .headers(CaseworkerHeader.headers_documents)
            .check(status.in(200, 404,304)))

        .pause(MinThinkTime, MaxThinkTime)
	    
	    //Simulate clicking on Case List
    }
}
