package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.FPLAHeader

import scala.util.Random

object EXUIFPLAMC {

  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL
  val loginFeeder = csv("FPLUserData.csv").circular

  val MinThinkTime = Environment.minThinkTimeFPLC
  val MaxThinkTime = Environment.maxThinkTimeFPLC
  val MinThinkTimeFPLV = Environment.minThinkTimeFPLV
  val MaxThinkTimeFPLV = Environment.maxThinkTimeFPLV
  
  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString
  
  /*======================================================================================
    *Business process : Click On Create Case for FPL
    * Below group contains all the requests related to Create Case
    ======================================================================================*/
  val fplacasecreation =
  group("XUI${service}_040_CreateCase") {
    exec(http("XUI${service}_040_CreateCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(FPLAHeader.headers_casecreation)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304))).exitHereIfFailed
  }
      .pause(MinThinkTime , MaxThinkTime )
    
  /*======================================================================================
      *Business process : Select Jurisdiction as Family Law and Casetype as CARE_SUPERVISION_EPO
      * Below group contains all the requests related to Start the Create Case
    ======================================================================================*/
    .group("XUI${service}_050_StartCreateCase") {
      exec(http("XUI${service}_050_005_StartCreateCase")
        .get("/data/internal/case-types/CARE_SUPERVISION_EPO/event-triggers/openCase?ignore-warning=false")
        .headers(FPLAHeader.headers_startcreatecase)
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
        .exec(http("XUI${service}_050_010_CreateCaseProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_createprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304))).exitHereIfFailed
    }
      .pause(MinThinkTime , MaxThinkTime )

      //set the random variables as usable parameters
      .exec(
      _.setAll(
        ("firstName", firstName()),
        ("lastName", lastName())
      ))

  /*======================================================================================
    *Business process : As part of the FPL Case Creation there are different steps
    * Below group contains all the requests related to CaseName details
  ======================================================================================*/
    
    .group("XUI${service}_060_CaseNameContinue") {
      exec(http("XUI${service}_060_005_CaseNameContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=openCase1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"${firstName}\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"caseName\": \"${firstName}\"\n  }\n}"))
        .check(status.is(200)))


        .exec(http("XUI${service}_060_010_CaseNameProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_opencaseprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
  *Business process : As part of the FPL Case Creation there are different steps
  * Below group contains all the requests related to CaseName Continue after enter the casename details
======================================================================================*/
    
    .group("XUI${service}_070_CaseNameSaveContinue") {
      exec(http("XUI${service}_070_005_CaseNameSaveContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/cases?ignore-warning=false")
        .headers(FPLAHeader.headers_72)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"caseName\": \"${firstName}\"\n  },\n  \"event\": {\n    \"id\": \"openCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        .check(status.in(200, 304, 201, 201))
        .check(jsonPath("$.id").optional.saveAs("caseId")))


        .exec(http("XUI${service}_070_010_CaseNameViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

      //Orders Needed
  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Order Direction  after enter the case Order  details
======================================================================================*/
    
    .group("XUI${service}_080_OrdersDirectionNeededGo") {
    exec(http("XUI${service}_080_005_OrdersDirectionNeededGo")
      .get("/data/internal/cases/${caseId}/event-triggers/ordersNeeded?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_080_010_OrdersDirectionProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_orddersprofile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
  }
      .pause(MinThinkTime , MaxThinkTime )
  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Order Direction Continue after enter the case Order  details
======================================================================================*/
    
    .group("XUI${service}_090_OrdersDirectionNeededContinue") {
      exec(http("XUI${service}_090_005_OrdersDirectionNeededContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=ordersNeeded1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"ordersNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_090_010_OrdersDirectionContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_ordersneed1profile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Order Direction Save  after enter the case Order  details
======================================================================================*/
    
    .group("XUI${service}_100_OrdersDirectionNeededSaveContinue") {
      exec(http("XUI${service}_100_005_OrdersDirectionNeededSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_81)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"orders\": {\n      \"orderType\": [\n        \"CARE_ORDER\"\n      ],\n      \"directions\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"ordersNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_100_010_OrdersDirectionViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Hearing Needed data to be entered
======================================================================================*/
    
    .group("XUI${service}_110_HearingNeededGo") {
    exec(http("XUI${service}_110_005_HearingNeededGo")
      .get("/data/internal/cases/${caseId}/event-triggers/hearingNeeded?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_110_010_HearingNeededGoProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_orddersprofile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
  }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Hearing Needed Continue to be entered
======================================================================================*/
    
    .group("XUI${service}_120_HearingNeededContinue") {
      exec(http("XUI${service}_120_005_HearingNeededContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=hearingNeeded1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"hearingNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_120_010_HearingNeededContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_hearingneededprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Hearing Needed Save Continue to be entered
======================================================================================*/
    
    .group("XUI${service}_130_HearingNeededSaveContinue") {
      exec(http("XUI${service}_130_005_HearingNeededSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"hearing\": {\n      \"timeFrame\": \"Within 18 days\",\n      \"type\": null,\n      \"withoutNotice\": null,\n      \"reducedNotice\": null,\n      \"respondentsAware\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"hearingNeeded\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))


        .exec(http("XUI${service}_130_010_HearingNeededSaveViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Children Details to Go
======================================================================================*/
    .group("XUI${service}_140_ChildrenGo") {
    exec(http("XUI${service}_140_005_ChildrenGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterChildren?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_140_010_ChildrenGoProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_ordersneed1profile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
  }
      .pause(MinThinkTime , MaxThinkTime )
  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Children Continue to Go
======================================================================================*/
    .group("XUI${service}_150_ChildrenContinue") {
      exec(http("XUI${service}_150_005_ChildrenContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterChildren1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2011-02-22\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-06-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889966\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterChildren\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2011-02-22\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-06-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889966\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_150_010_ChildrenContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_childrenprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }

      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Children Save Continue to Go
======================================================================================*/
    
    .group("XUI${service}_160_ChildrenSaveContinue") {
      exec(http("XUI${service}_160_005_ChildrenSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"children1\": [\n      {\n        \"id\": \"d01fbe4f-95df-4023-b2cd-0312639a9700\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"test\",\n            \"lastName\": \"testing\",\n            \"dateOfBirth\": \"2011-02-22\",\n            \"gender\": \"Boy\",\n            \"livingSituation\": \"Living with respondents\",\n            \"addressChangeDate\": \"2020-06-01\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 14\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"keyDates\": \"test\",\n            \"careAndContactPlan\": \"test\",\n            \"adoption\": \"No\",\n            \"mothersName\": \"tess\",\n            \"fathersName\": \"dan\",\n            \"fathersResponsibility\": \"Yes\",\n            \"socialWorkerName\": \"test\",\n            \"socialWorkerTelephoneNumber\": {\n              \"telephoneNumber\": \"02088889966\",\n              \"contactDirection\": \"test\"\n            },\n            \"additionalNeeds\": \"No\",\n            \"detailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterChildren\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))


        .exec(http("XUI${service}_160_010_ChildrenSaveViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Respondent Details to Go
======================================================================================*/
    
    .group("XUI${service}_170_RespondentsGo") {
    exec(http("XUI${service}_170_005_RespondentsGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterRespondents?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_170_010_RespondentsGoProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_ordersneed1profile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
  }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Respondent Address
======================================================================================*/
    
    .group("XUI${service}_180_RespondentsGetAddress") {
      exec(http("XUI${service}_180_005_RespondentsGetAddress")
        .get("/addresses?postcode=TW33SD")
        .headers(FPLAHeader.headers_16)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )
    .group("XUI${service}_190_RespondentsContinue") {
      exec(http("XUI${service}_190_005_RespondentsContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterRespondents1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1981-02-22\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889966\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterRespondents\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1981-02-22\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889999\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_190_010_RespondentsContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_respondantprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Respondent Details Ssave
======================================================================================*/
    .group("XUI${service}_200_RespondentsSaveContinue") {
      exec(http("XUI${service}_200_005_RespondentsSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"respondents1\": [\n      {\n        \"id\": \"416c4c9c-fdae-4259-8e32-fc7877dc1abf\",\n        \"value\": {\n          \"party\": {\n            \"firstName\": \"tess\",\n            \"lastName\": \"tickles\",\n            \"dateOfBirth\": \"1981-02-22\",\n            \"gender\": \"Female\",\n            \"placeOfBirth\": \"london\",\n            \"address\": {\n              \"AddressLine1\": \"Flat 12\",\n              \"AddressLine2\": \"Bramber House\",\n              \"AddressLine3\": \"Seven Kings Way\",\n              \"PostTown\": \"Kingston Upon Thames\",\n              \"County\": \"\",\n              \"PostCode\": \"KT2 5BU\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"02088889966\",\n              \"contactDirection\": \"tess\"\n            },\n            \"relationshipToChild\": \"test\",\n            \"contactDetailsHidden\": \"No\",\n            \"litigationIssues\": \"NO\"\n          }\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"enterRespondents\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))


        .exec(http("XUI${service}_200_010_RespondentsSaveViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter applicant  Details Save
======================================================================================*/
    .group("XUI${service}_210_ApplicantGo") {
    exec(http("XUI${service}_210_005_ApplicantGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterApplicant?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_210_010_ApplicantGoProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_respondantprofile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))

  }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter applicant get address  Details Save
======================================================================================*/
    
    .group("XUI${service}_220_ApplicantGetAddress") {
      exec(http("XUI${service}_220_ApplicantGetAddress")
        .get("/addresses?postcode=TW33SD")
        .headers(FPLAHeader.headers_16)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter applicant  Details Save
======================================================================================*/
    
    .group("XUI${service}_230_ApplicantContinue") {
      exec(http("XUI${service}_230_005_ApplicantContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterApplicant1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"enterApplicant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_230_010_ApplicantGoProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_applicantprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }

      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter applicant  Details Save
======================================================================================*/
    
    .group("XUI${service}_240_ApplicantSaveContinue") {
      exec(http("XUI${service}_240_005_ApplicantSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"applicants\": [\n      {\n        \"id\": \"9de5744b-26c2-4653-919d-d9b828fc4c3f\",\n        \"value\": {\n          \"party\": {\n            \"organisationName\": \"${firstName}\",\n            \"pbaNumber\": \"PBA1234567\",\n            \"clientCode\": null,\n            \"customerReference\": null,\n            \"address\": {\n              \"AddressLine1\": \"8 Hibernia Gardens\",\n              \"AddressLine2\": \"\",\n              \"AddressLine3\": \"\",\n              \"PostTown\": \"Hounslow\",\n              \"County\": \"\",\n              \"PostCode\": \"TW3 3SD\",\n              \"Country\": \"United Kingdom\"\n            },\n            \"telephoneNumber\": {\n              \"telephoneNumber\": \"07540634567\",\n              \"contactDirection\": \"${firstName}\"\n            },\n            \"jobTitle\": \"kkkuuhhh\",\n            \"mobileNumber\": {\n              \"telephoneNumber\": null\n            },\n            \"email\": {\n              \"email\": \"dddffff@la.gov.uk\"\n            }\n          }\n        }\n      }\n    ],\n    \"solicitor\": {\n      \"name\": \"nhhffsol\",\n      \"mobile\": \"07540687298\",\n      \"telephone\": \"05673245678\",\n      \"email\": \"joe.bloggs@la.gov.uk\",\n      \"dx\": null,\n      \"reference\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"enterApplicant\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_240_010_ApplicantSaveViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter Ground details Go
======================================================================================*/
    
    .group("XUI${service}_250_GroundApplicationGo") {
    exec(http("XUI${service}_250_005_GroundApplicationGo")
      .get("/data/internal/cases/${caseId}/event-triggers/enterGrounds?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_250_010__GroundApplicationGoProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_ordersneed1profile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
  }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter applicant  Details Continue
======================================================================================*/
    .group("XUI${service}_260_GroundApplicationContinue") {
      exec(http("XUI${service}_260_005_GroundApplicationContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=enterGrounds1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"event\": {\n    \"id\": \"enterGrounds\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_260_010_GroundApplicationContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_groundsprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter applicant  Details Save
======================================================================================*/
    .group("XUI${service}_270_GroundApplicationSaveContinue") {
      exec(http("XUI${service}_270_005_GroundApplicationSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"grounds\": {\n      \"thresholdReason\": [\n        \"beyondControl\"\n      ],\n      \"thresholdDetails\": \"sdsdsds\"\n    }\n  },\n  \"event\": {\n    \"id\": \"enterGrounds\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))


        .exec(http("XUI${service}_270_010_GroundApplicationSaveViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter Allocation proposal Go
======================================================================================*/
    
    .group("XUI${service}_280_AllocationProposalGo") {
      //other proposal
      exec(http("XUI${service}_280_005_AllocationProposalGo")
        .get("/data/internal/cases/${caseId}/event-triggers/otherProposal?ignore-warning=false")
        .headers(FPLAHeader.headers_76)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_280_010_AllocationProposalGoProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_ordersneed1profile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter Allocation proposal Continue
======================================================================================*/
    
    .group("XUI${service}_290_AllocationProposalContinue") {
      exec(http("XUI${service}_290_005_AllocationProposalContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=otherProposal1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"event\": {\n    \"id\": \"otherProposal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_290_010_AllocationProposalContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_otherprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to enter Allocation proposal Save Continue
======================================================================================*/
    
    .group("XUI${service}_300_AllocationProposalSaveContinue") {
      exec(http("XUI${service}_300_005_AllocationProposalSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"allocationProposal\": {\n      \"proposal\": \"District judge\",\n      \"proposalReason\": \"xccxcx\"\n    }\n  },\n  \"event\": {\n    \"id\": \"otherProposal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))


        .exec(http("XUI${service}_300_010_AllocationProposalSaveViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Upload Documents page
======================================================================================*/
    .group("XUI${service}_310_DocumentsGo") {
    exec(http("XUI${service}_310_DocumentsGo")
      .get("/case/PUBLICLAW/CARE_SUPERVISION_EPO/${caseId}/trigger/uploadDocuments")
      .headers(FPLAHeader.commonHeader)
      .check(status.in(200, 304, 201)))
  }
    .pause(MinThinkTime , MaxThinkTime )
  /*======================================================================================
  *Business process : As part of the FPL Case Creation there are different steps
  * Below group contains all the requests related to Upload Documents page
  ======================================================================================*/
.group("XUI${service}_320_DocumentsUploadPage"){
  exec(http("XUI${service}_320_005_DocumentsUploadPage")
    .get("/external/configuration-ui/")
    .headers(FPLAHeader.headers_323)
    .check(status.in(200, 304, 201)))

    .exec(http("XUI${service}_320_010_DocumentsUploadPage")
    .get("/assets/config/config.json")
    .headers(FPLAHeader.commonHeader)
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_320_015_DocumentsUploadPage")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(FPLAHeader.commonHeader)
        .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_320_020_DocumentsUploadPage")
      .get("/auth/isAuthenticated")
      .headers(FPLAHeader.commonHeader)
        .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_320_025_DocumentsUploadPage")
      .get("/api/user/details")
      .headers(FPLAHeader.commonHeader)
        .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_320_030_DocumentsUploadPageXUI${service}_320_010_DocumentsUploadPage")
      .get("/data/internal/cases/${caseId}/event-triggers/uploadDocuments?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))
  

      .exec(http("XUI${service}_320_035_DocumentsGoProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_ordersneed1profile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
  }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Upload Documents files
======================================================================================*/

    .group("XUI${service}_330_UploadFile") {
      exec(http("XUI${service}_330_UploadFile")
        .post("/documents")
        .headers(FPLAHeader.headers_uploadfile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .check(status.is(200))
        .check(regex("""http://(.+)/""").saveAs("DMURL"))
        .check(regex("""internal/documents/(.+?)/binary""").saveAs("Document_ID")))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Upload Documents upload Continue
======================================================================================*/

    .group("XUI${service}_340_DocumentsContinue") {
      exec(http("XUI${service}_340_005_DocumentsContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=uploadDocumentsaddApplicationDocuments")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"applicationDocuments\": [\n      {\n        \"value\": {\n          \"documentType\": \"CARE_PLAN\",\n          \"includedInSWET\": null,\n          \"document\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n            \"document_filename\": \"3MB.pdf\"\n          },\n          \"uploadedBy\": null\n        },\n        \"id\": null\n      }\n    ],\n    \"applicationDocumentsToFollowReason\": \"This is perftest reason\"\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_data\": {\n    \"applicationDocuments\": [\n      {\n        \"value\": {\n          \"documentType\": \"CARE_PLAN\",\n          \"includedInSWET\": null,\n          \"document\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n            \"document_filename\": \"200MB.mp3\"\n          },\n          \"uploadedBy\": null\n        },\n        \"id\": null\n      }\n    ],\n    \"applicationDocumentsToFollowReason\": \"This is perftest reason\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_340_010_DocumentsContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_uploaddocprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Upload Documents upload save Continue
======================================================================================*/
    
    .group("XUI${service}_350_DocumentsSaveContinue") {
      exec(http("XUI${service}_350_005_DocumentsSaveContinue")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"applicationDocuments\": [\n      {\n        \"value\": {\n          \"documentType\": \"CARE_PLAN\",\n          \"includedInSWET\": null,\n          \"document\": {\n            \"document_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}\",\n            \"document_binary_url\": \"http://dm-store-perftest.service.core-compute-perftest.internal/documents/${Document_ID}/binary\",\n            \"document_filename\": \"200MB.mp3\"\n          },\n          \"uploadedBy\": null\n        },\n        \"id\": null\n      }\n    ],\n    \"applicationDocumentsToFollowReason\": \"This is Performance Test\"\n  },\n  \"event\": {\n    \"id\": \"uploadDocuments\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))


        .exec(http("XUI${service}_350_010_DocumentsSaveViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Submit Application Go
======================================================================================*/
    
    .group("XUI${service}_360_SubmitApplicationGo") {
    exec(http("XUI${service}_360_005_SubmitApplicationGo")
      .get("/data/internal/cases/${caseId}/event-triggers/submitApplication?ignore-warning=false")
      .headers(FPLAHeader.headers_76)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.event_token").saveAs("existing_case_event_token"))
      .check(status.in(200, 304, 201)))

      .exec(http("XUI${service}_360_010_SubmitApplicationGoProfile")
        .get("/data/internal/profile")
        .headers(FPLAHeader.headers_ordersneed1profile)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304, 201)))
  }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Submit Application
======================================================================================*/
    
    .group("XUI${service}_370_SubmitApplicationContinue") {
      exec(http("XUI${service}_370_005_SubmitApplicationContinue")
        .post("/data/case-types/CARE_SUPERVISION_EPO/validate?pageId=submitApplication1")
        .headers(FPLAHeader.headers_71)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${user} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"event\": {\n    \"id\": \"submitApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${user} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304, 201)))

        .exec(http("XUI${service}_370_010_SubmitApplicationContinueProfile")
          .get("/data/internal/profile")
          .headers(FPLAHeader.headers_submitprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )

  /*======================================================================================
*Business process : As part of the FPL Case Creation there are different steps
* Below group contains all the requests related to Application Submitted
======================================================================================*/
    
    .group("XUI${service}_380_ApplicationSubmitted") {
      exec(http("XUI${service}_380_005_ApplicationSubmitted")
        .post("/data/cases/${caseId}/events")
        .headers(FPLAHeader.headers_80)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(StringBody("{\n  \"data\": {\n    \"submissionConsent\": [\n      \"agree\"\n    ],\n    \"submissionConsentLabel\": \"I, ${user} (local-authority), believe that the facts stated in this application are true.\"\n  },\n  \"event\": {\n    \"id\": \"submitApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${existing_case_event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304, 201)))


        .exec(http("XUI${service}_380_010_ApplicationSubmittedViewCase")
          .get("/data/internal/cases/${caseId}")
          .headers(FPLAHeader.headers_casesprofile)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304, 201)))
    }
      .pause(MinThinkTime , MaxThinkTime )



  



}