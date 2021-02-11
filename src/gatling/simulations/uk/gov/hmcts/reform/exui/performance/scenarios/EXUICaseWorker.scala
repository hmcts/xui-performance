package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{CaseworkerHeader, Environment}

object EXUICaseWorker {

val IdamUrl = Environment.idamURL
val baseURL = Environment.baseURL
val caseFeeder = csv("CaseworkerSearches.csv").random

val MinThinkTime = Environment.minThinkTimeCW
val MaxThinkTime = Environment.maxThinkTimeCW

/*======================================================================================
*Business process : As part of the login as case worker and retrieve complete case details
* Below group contains all the requests related to enter details and filter case
======================================================================================*/

val ApplyFilters =
feed(caseFeeder)
.group("XUI${service}_030_ApplyFilter") {
  exec(http("XUI${service}_030_005_AccessRead")
    .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
    .headers(CaseworkerHeader.headers_casefilter)
    .header("X-XSRF-TOKEN", "${xsrfToken}")
    .check(status.in(200, 304)))
  
    .exec(http("XUI${service}_030_010_BasketInputs")
      .get("/data/internal/case-types/${caseType}/work-basket-inputs")
      .headers(CaseworkerHeader.workbasketinputs)
      .header("X-XSRF-TOKEN", "${xsrfToken}")
      .check(status.in(200, 304)))
}
    .pause(MinThinkTime, MaxThinkTime)
    
.exec(http("XUI${service}_040_SubmitApplyFilter")
  .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
  .headers(CaseworkerHeader.headers_2)
  .header("X-XSRF-TOKEN", "${xsrfToken}")
  .body(StringBody("{\n  \"size\": 25\n}"))
  .check(status.is(200))
  .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers"))
)

  .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : As part of the login as case worker and retrieve complete case details
* Below group contains all the requests related to sort the case details
======================================================================================*/

val ApplySort=
group("XUI${service}_050_SortCases") {
exec(http("XUI${service}_050_ApplySortCaseRef")
.post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
.headers(CaseworkerHeader.headers_sort)
.header("X-XSRF-TOKEN", "${xsrfToken}")
.body(StringBody("{\n  \"sort\": {\n    \"column\": \"${sortName}\",\n    \"order\": 1,\n    \"type\": \"${type}\"\n  },\n  \"size\": 25\n}"))
.check(status.is(200)))
//.check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))
}
.pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : As part of the login as case worker and retrieve complete case details
* Below group contains all the requests related to view case details
======================================================================================*/

val ClickFindCase=
  group("XUI${service}_060_ClickFindCase") {
    exec(http("XUI${service}_060_005_FindCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(CaseworkerHeader.headers_read)
      .header("X-XSRF-TOKEN", "${xsrfToken}")
      .check(status.in(200, 304)))
      
      .exec(http("XUI${service}_060_010_FindCaseSearch")
        .get("/data/internal/case-types/${caseType}/search-inputs")
        .headers(CaseworkerHeader.headers_findcasesearchinput)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200, 304)))
  }
  .pause(MinThinkTime, MaxThinkTime)

//submit find a case

.exec(http("XUI${service}_070_FindSearchResults")
  .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
  .headers(CaseworkerHeader.headers_results)
  .header("X-XSRF-TOKEN", "${xsrfToken}")
  .body(StringBody("{\n  \"size\": 25\n}"))
  //.check(jsonPath("$..case_id").find(0).optional.saveAs("caseNumber"))
  .check(status.in(200,304)))
  
  .pause(MinThinkTime, MaxThinkTime)

val ViewCase =
//Loop through each of the found cases and view
doIf(session => session.contains("caseNumbers")) {
foreach("${caseNumbers}", "caseNumber", "counter") {
doIf(session => session("counter").as[Int].<=(1)) {
group("XUI${service}_080_ViewCase") {
  exec(http("XUI${service}_080_005_ViewCase")
    .get("/data/internal/cases/${caseNumber}")
    .headers(CaseworkerHeader.headers_5)
    .header("X-XSRF-TOKEN", "${xsrfToken}")
    .check(regex("""internal/documents/(.+?)","document_filename""").find(0).optional.saveAs("Document_ID")))
    
    /*.exec(http("XUI${service}_080_010_ViewUndefined")
      .get("/undefined/cases/${caseNumber}")
      .headers(CaseworkerHeader.headers_undefined)
    .check(status.in(200,304,404)))
    
    .exec(http("XUI${service}_080_015_GetPaymentGroups")
    .get("/payments/cases/${caseNumber}/paymentgroups")
    .headers(CaseworkerHeader.headers_search)
    .check(status.in(200,304,404)))*/
}
  .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : As part of the login as case worker and retrieve complete case details
* Below group contains all the requests related to click case tabs
======================================================================================*/
  
    
      .exec(http("XUI${service}_090_ViewTab_${viewTab1}")
      .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseNumber}%23${viewTab1}")
      .headers(CaseworkerHeader.headers_4)
      .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)
      
      .exec(http("XUI${service}_100_ViewTab_${viewTab2}")
        .get("/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseNumber}%23${viewTab2}")
        .headers(CaseworkerHeader.headers_4)
        .check(status.in(200,304)))
      .pause(MinThinkTime, MaxThinkTime)
      
     
  
/*======================================================================================
*Business process :below steps are executed if document id is found
======================================================================================*/
.doIf(session => session.contains("Document_ID")) {
  group("XUI${service}_110_ViewCaseDocumentUI") {
    exec(http("XUI${service}_110_005_ViewCaseDocumentUI")
      .get("/external/config/ui")
      .headers(CaseworkerHeader.headers_documents)
      .header("X-XSRF-TOKEN", "${xsrfToken}"))
      
      .exec(http("XUI${service}_110_010_ViewCaseDocumentT&C")
        .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(CaseworkerHeader.headers_documents)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
      .check(status.in(200,304)))
      .exec(http("XUI${service}_110_015_ViewCaseDocumentAnnotations")
        .get("/em-anno/annotation-sets/filter?documentId=${Document_ID}")
        .headers(CaseworkerHeader.headers_documents)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304,404)))
      
      .exec(http("XUI${service}_110_020_ViewCaseDocumentBinary")
        .get("/documents/${Document_ID}/binary")
        .headers(CaseworkerHeader.headers_documents)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304)))
    
  }
}
  .pause(MinThinkTime, MaxThinkTime)

//Simulate clicking on Case List
    .group("XUI${service}_120_CaseListViewHealthcheck") {
    exec(http("XUI${service}_120_005_CaseListViewHealthcheck")
      .get("/api/healthCheck?path=%2Fcases"
      ).headers(CaseworkerHeader.headers_6)
      .header("X-XSRF-TOKEN", "${xsrfToken}"))
      
      .exec(http("XUI${service}_120_010_CaseListViewGetJurisdictions")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(CaseworkerHeader.headers_7)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))
      
      .exec(http("XUI${service}_120_015_CaseListViewWorkBasketInputs")
        .get("/data/internal/case-types/${caseType}/work-basket-inputs")
        .headers(CaseworkerHeader.headers_8)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))
      
      .exec(http("XUI${service}_120_020_CaseListView")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&state=1_OPEN&page=1")
        .headers(CaseworkerHeader.headers_9)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .body(StringBody("{\n  \"size\": 25\n}")))
  }
  .pause(MinThinkTime, MaxThinkTime)
}
}
}
}


