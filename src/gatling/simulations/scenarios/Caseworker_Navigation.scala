package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

object Caseworker_Navigation {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*====================================================================================
  *Apply a filter from the dropdowns (State = Submitted)
  *=====================================================================================*/

  val ApplyFilter =

    group("XUI_Caseworker_030_ApplyFilter") {
      exec(http("XUI_Caseworker_030_005_FilterRecordsByDraftState")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&state=Submitted&page=1")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(substring("columns"))
        .check(jsonPath("$.total").saveAs("numberOfResults"))
        .check(jsonPath("$.results[*].case_id").findRandom.optional.saveAs("caseId")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Sort By Last Modified Date
  *=====================================================================================*/

  val SortByLastModifiedDate =

    group("XUI_Caseworker_040_SortByLastModifiedDate") {
      exec(http("XUI_Caseworker_040_005_SortByLastModifiedDate")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&state=Submitted&page=1")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"sort":{"column":"[LAST_MODIFIED_DATE]","order":1,"type":"DateTime"},"size":25}"""))
        .check(substring("columns")))

    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Navigate to Page 2
  *=====================================================================================*/

  val LoadPage2 =

    //Only load page 2 if there were more than 25 results returned
    doIf(session => session("numberOfResults").as[Int] > 25) {

      group("XUI_Caseworker_050_LoadPage2") {
        exec(http("XUI_Caseworker_050_005_LoadPage2")
          .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&state=Submitted&page=2")
          .headers(Headers.commonHeader)
          .header("accept", "application/json")
          .formParam("x-xsrf-token", "${XSRFToken}")
          .body(StringBody("""{"sort":{"column":"[LAST_MODIFIED_DATE]","order":1,"type":"DateTime"},"size":25}"""))
          .check(substring("columns")))
      }

      .pause(MinThinkTime, MaxThinkTime)

    }

  /*====================================================================================
  *Search by Case Number
  *=====================================================================================*/

  val SearchByCaseNumber =

    group("XUI_Caseworker_060_SearchByCaseNumber") {
      exec(http("XUI_Caseworker_060_005_SearchByCaseNumber")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&state=Submitted&page=1&case_reference=${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(substring("columns"))
        .check(jsonPath("$.total").ofType[Int].is(1)))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *View Case
  *=====================================================================================*/

  val ViewCase =

    group("XUI_Caseworker_070_ViewCase") {
      exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}"))

      .exec(http("XUI_Caseworker_070_005_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        .header("experimental", "true")
        .check(jsonPath("$.case_id").is("${caseId}"))
        .check(jsonPath("$.tabs[?(@.show_condition==null)].label").findAll.saveAs("tabNames")))

      .exec(Common.caseActivityPost)

      .exec(Common.userDetails)
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Navigate To Each Tab
  *=====================================================================================*/

  val NavigateTabs =

    foreach("${tabNames}", "tabName") {

      group("XUI_Caseworker_080_NavigateTabs") {
        exec(ViewCase)

        .exec(Common.healthcheck("%2Fcases%2Fcase-details%2F${caseId}%23${tabName}".replace(" ", "%2520")))

      }

    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Return to the Case List
  *=====================================================================================*/

  val LoadCaseList =

    group("XUI_Caseworker_080_CaseList") {
      exec(Common.healthcheck("%2Fcases"))

      .exec(http("XUI_080_005_Jurisdictions")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("id")))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))

      .exec(Common.orgDetails)

      .exec(Common.userDetails)

      .exec(http("XUI_080_010_WorkBasketInputs")
        .get("/data/internal/case-types/${caseType}/work-basket-inputs")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8")
        .check(regex("workbasketInputs|Not Found"))
        .check(status.in(200, 404)))

      .exec(http("XUI_080_015_SearchCases")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "${XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(substring("columns")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  val CaseFlags = 

    //Add First Case Flag

    exec(http("XUI_080_CaseFlag1_Tasks")
			.get("/workallocation/case/tasks/${caseId}/event/fl401CreateFlags/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
			.headers(Headers.navigationHeader)
      .header("Content-Type", "application/json"))

    .exec(_.set("currentDate", Common.getDate()))

    .exec(http("XUI_080_CaseFlag1_GetEvent")
			.get("/data/internal/cases/${caseId}/event-triggers/fl401CreateFlags?ignore-warning=false")
			.headers(Headers.navigationHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(jsonPath("$.case_fields[2].value.firstName").saveAs("appFirstName"))
      .check(jsonPath("$.case_fields[2].value.lastName").saveAs("appLastName"))
      .check(jsonPath("$.case_fields[2].value.dateOfBirth").saveAs("appDateOfBirth"))
      .check(jsonPath("$.case_fields[2].value.gender").saveAs("appGender"))
      .check(jsonPath("$.case_fields[2].value.email").saveAs("appEmail"))
      .check(jsonPath("$.case_fields[2].value.phoneNumber").saveAs("appPhoneNumber"))
      .check(jsonPath("$.case_fields[2].value.dxNumber").saveAs("appDxNumber"))
      .check(jsonPath("$.case_fields[2].value.solicitorReference").saveAs("appSolicitorReference"))
      .check(jsonPath("$.case_fields[2].value.representativeFirstName").saveAs("appRepresentativeFirstName"))
      .check(jsonPath("$.case_fields[2].value.representativeLastName").saveAs("appRepresentativeLastName"))
      .check(jsonPath("$.case_fields[2].value.solicitorEmail").saveAs("appSolicitorEmail"))
      .check(jsonPath("$.case_fields[2].value.solicitorTelephone").saveAs("appSolicitorTelephone"))
      .check(jsonPath("$.case_fields[2].value.partyId").saveAs("appPartyId"))
      .check(jsonPath("$.case_fields[2].value.solicitorPartyId").saveAs("solicitorPartyId"))
      .check(jsonPath("$.case_fields[2].value.solicitorOrgUuid").saveAs("solicitorOrgUuId"))
      .check(jsonPath("$.case_fields[2].value.partyLevelFlag.partyName").saveAs("appPartyName"))
      .check(jsonPath("$.case_fields[2].value.partyLevelFlag.roleOnCase").saveAs("appPartyRole"))
      .check(jsonPath("$.case_fields[2].value.address.PostCode").saveAs("appPostCode"))
      .check(jsonPath("$.case_fields[2].value.address.AddressLine1").saveAs("appAddressLine1"))
      .check(jsonPath("$.case_fields[2].value.address.AddressLine2").saveAs("appAddressLine2"))
      .check(jsonPath("$.case_fields[2].value.address.PostTown").saveAs("appPostTown"))
      .check(jsonPath("$.case_fields[2].value.solicitorOrg.OrganisationID").saveAs("appOrganisationID"))
      .check(jsonPath("$.case_fields[2].value.solicitorOrg.OrganisationName").saveAs("appOrganisationName"))
      .check(jsonPath("$.case_fields[2].value.solicitorAddress.PostCode").saveAs("appsSolicitorPostCode"))
      .check(jsonPath("$.case_fields[2].value.solicitorAddress.AddressLine1").saveAs("appsSolicitorAddressLine1"))
      .check(jsonPath("$.case_fields[2].value.solicitorAddress.AddressLine2").saveAs("appsSolicitorAddressLine2"))
      .check(jsonPath("$.case_fields[2].value.solicitorAddress.PostTown").saveAs("appsSolicitorPostTown"))
      .check(jsonPath("$.case_fields[1].value.firstName").saveAs("repFirstName"))
      .check(jsonPath("$.case_fields[1].value.lastName").saveAs("repLastName"))
      .check(jsonPath("$.case_fields[1].value.dateOfBirth").saveAs("repDateOfBirth"))
      .check(jsonPath("$.case_fields[1].value.email").saveAs("repEmail"))
      .check(jsonPath("$.case_fields[1].value.phoneNumber").saveAs("repPhoneNumber"))
      .check(jsonPath("$.case_fields[1].value.partyId").saveAs("repPartyId"))
      .check(jsonPath("$.case_fields[1].value.partyLevelFlag.partyName").saveAs("repPartyName"))
      .check(jsonPath("$.case_fields[1].value.partyLevelFlag.roleOnCase").saveAs("repPartyRole"))
      .check(jsonPath("$.case_fields[1].value.address.PostCode").saveAs("repPostCode"))
      .check(jsonPath("$.case_fields[1].value.address.PostTown").saveAs("repPostTown"))
      .check(jsonPath("$.case_fields[1].value.address.AddressLine1").saveAs("repAddressLine1"))
      .check(jsonPath("$.case_fields[1].value.address.AddressLine2").saveAs("repAddressLine2"))
      )

    .exec(Common.userDetails)
    .exec(Common.caseShareOrgs)

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI_080_CaseFlag1_GetRefDataLocations")
			.get("/refdata/location/orgServices?ccdCaseType=PRLAPPS")
			.headers(Headers.navigationHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("service_short_description")))

    .exec(http("XUI_080_CaseFlag1_GetRefDataServiceId")
			.get("/refdata/commondata/caseflags/service-id=ABA5?flag-type=CASE")
			.headers(Headers.navigationHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("FlagDetails")))

    .exec(Common.userDetails)

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI_090_AddCaseFlag1")
			.post("/data/cases/${caseId}/events")
			.headers(Headers.commonHeader)
      .header("content-type", "application/json")
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
			.body(ElFileBody("bodies/prl/PRLAddCaseFlag.json")))

    .exec(http("XUI_090_GetCase")
			.get("/data/internal/cases/${caseId}")
			.headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json"))

    .exec(http("XUI_090_WASupportedJurisdictions")
			.get("/api/wa-supported-jurisdiction/get")
			.headers(Headers.commonHeader))

    .exec(Common.userDetails)

    .pause(MinThinkTime, MaxThinkTime)

    //Add Second Case Flag

    .exec(http("XUI_100_CaseFlag2_Tasks")
			.get("/workallocation/case/tasks/${caseId}/event/fl401CreateFlags/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
			.headers(Headers.navigationHeader)
      .header("Content-Type", "application/json"))

    .exec(_.set("currentDate", Common.getDate()))

    .exec(http("XUI_100_CaseFlag2_GetEvent")
			.get("/data/internal/cases/${caseId}/event-triggers/fl401CreateFlags?ignore-warning=false")
			.headers(Headers.navigationHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("event_token"))
      .check(jsonPath("$.case_fields[0].value.details[0].id").saveAs("id1"))
      .check(jsonPath("$.case_fields[0].value.details[0].value.path[0].id").saveAs("id2"))
      )

    .exec(Common.userDetails)
    .exec(Common.caseShareOrgs)

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI_100_CaseFlag2_GetRefDataLocations")
			.get("/refdata/location/orgServices?ccdCaseType=PRLAPPS")
			.headers(Headers.navigationHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("service_short_description")))

    .exec(http("XUI_100_CaseFlag2_GetRefDataIds")
			.get("/refdata/commondata/caseflags/service-id=ABA5?flag-type=CASE")
			.headers(Headers.navigationHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("FlagDetails")))

    .exec(Common.userDetails)

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI_110_AddCaseFlag2")
			.post("/data/cases/${caseId}/events")
			.headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
			.body(ElFileBody("bodies/prl/PRLAddCaseFlag2.json")))

    .exec(http("XUI_110_GetCase")
			.get("/data/internal/cases/${caseId}")
			.headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json"))

    .exec(http("XUI_110_WASupportedJurisdictions")
			.get("/api/wa-supported-jurisdiction/get")
			.headers(Headers.commonHeader))

    .exec(Common.userDetails)

    .pause(MinThinkTime, MaxThinkTime)

  val RemoveFlag =

    //Disable Case Flag

    exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    .exec(_.set("currentDate", Common.getDate()))

     .exec(http("XUI_120_RemoveCaseFlag1_Tasks")
			.get("/workallocation/case/tasks/${caseId}/event/fl401ManageFlags/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
			.headers(Headers.navigationHeader))

    .exec(http("XUI_120_RemoveCaseFlag1_GetEvent")
			.get("/data/internal/cases/${caseId}/event-triggers/fl401ManageFlags?ignore-warning=false")
			.headers(Headers.navigationHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(jsonPath("$.event_token").saveAs("event_token2"))
      .check(jsonPath("$.case_fields[2].value.details[0].id").saveAs("caseFlagId1"))
      .check(jsonPath("$.case_fields[2].value.details[0].value.path[0].id").saveAs("caseFlagPathId1"))
      .check(jsonPath("$.case_fields[2].value.details[0].value.flagComment").saveAs("caseFlagComment1"))
      .check(jsonPath("$.case_fields[2].value.details[0].value.dateTimeCreated").saveAs("caseFlagDate1"))
      .check(jsonPath("$.case_fields[2].value.details[0].value.otherDescription").saveAs("caseFlagDescription1"))
      .check(jsonPath("$.case_fields[2].value.details[1].id").saveAs("caseFlagId2"))
      .check(jsonPath("$.case_fields[2].value.details[1].value.path[0].id").saveAs("caseFlagPathId2"))
      .check(jsonPath("$.case_fields[2].value.details[1].value.flagComment").saveAs("caseFlagComment2"))
      .check(jsonPath("$.case_fields[2].value.details[1].value.dateTimeCreated").saveAs("caseFlagDate2"))
      .check(jsonPath("$.case_fields[2].value.details[1].value.otherDescription").saveAs("caseFlagDescription2"))
    )

    .exec(Common.userDetails)
    .exec(Common.caseShareOrgs)

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI_120_RemoveFlagSubmit")
			.post("/data/cases/${caseId}/events")
			.headers(Headers.commonHeader)
      .header("x-xsrf-token", "${XSRFToken}")
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
			.body(ElFileBody("bodies/prl/PRLRemoveCaseFlag.json")))

    .exec(http("XUI_120_GetCase")
			.get("/data/internal/cases/${caseId}")
			.headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json"))

    .exec(http("XUI_120_WASupportedJurisdictions")
			.get("/api/wa-supported-jurisdiction/get")
			.headers(Headers.commonHeader))

}


