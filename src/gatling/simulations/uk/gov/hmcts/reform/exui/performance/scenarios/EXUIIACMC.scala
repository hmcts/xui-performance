package uk.gov.hmcts.reform.exui.performance.scenarios

import java.text.SimpleDateFormat
import java.util.Date

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Environment, IACHeader}

import scala.util.Random

object EXUIIACMC {

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular
  //  val feedUserDataIAC = csv("IACDataBackground.csv").circular

  val MinThinkTime = Environment.minThinkTimeIACC
  val MaxThinkTime = Environment.maxThinkTimeIACC
  val MinThinkTimeIACV = Environment.minThinkTimeIACV
  val MaxThinkTimeIACV = Environment.maxThinkTimeIACV

  //headers

  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString



  val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
  val now = new Date()
  val timeStamp = sdfDate.format(now)
  val iaccasecreation =

  /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
======================================================================================*/

  //set the current date as a usable parameter
    exec(session => session.set("currentDate", timeStamp))

      //set the random variables as usable parameters
      .exec(
      _.setAll(
        ("firstName", firstName()),
        ("lastName", lastName())
      ))
      //when click on create

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are when click on create case
======================================================================================*/
        .group("XUI${service}_040_CreateCase") {
      exec(http("XUI${service}_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(IACHeader.headers_createcase)
        .check(status.in(200, 304))).exitHereIfFailed
    }
      .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are when start create case
======================================================================================*/
      
      .group("XUI${service}_050_StartCreateCase1") {
        exec(http("XUI${service}_050_005_StartCreateCase1").get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false").headers(IACHeader.headers_startcreatecase).check(status.is(200)).check(jsonPath("$.event_token").optional.saveAs("event_token")))
          .exec(http("XUI${service}_050_010_StartCreateCase2")
            .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
            .headers(IACHeader.headers_startcreatecase)
            .check(status.is(200))
        )
          .exec(http("XUI${service}_050_015_CaseCreateProfile")
            .get("/data/internal/profile")
            .headers(IACHeader.headers_data_internal)
            .check(status.in(200, 304, 302)))
      }
      .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for start appeal checklist
======================================================================================*/

      .group("XUI${service}_060_StartAppealChecklist") {
        exec(http("XUI${service}_060_StartAppealChecklist")
        .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    }\n  }\n}")).check(status.is(200))).exitHereIfFailed
      }
      .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for appeal home office decision
======================================================================================*/
      .group("XUI${service}_070_StartAppealHomeOfficeDecision") {
        exec(http("XUI${service}_070_StartAppealHomeOfficeDecision")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecision")
          .headers(IACHeader.headers_homeofficedecision)
          .body(StringBody("{\n  \"data\": {\n    \"homeOfficeReferenceNumber\": \"12345678\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  }\n}")).check(status.in(200, 304)))
      }
      .pause(MinThinkTime, MaxThinkTime)

      //below is newly added transaction
    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for upload notification Decision
======================================================================================*/
      .group("XUI${service}_080_StartUpoadNoticeDecision") {
      exec(http("XUI${service}_080_StartUpoadNoticeDecision")
      .post("/data/case-types/Asylum/validate?pageId=startAppealuploadTheNoticeOfDecision")
      .headers(IACHeader.headers_uploadnotice)
      .body(StringBody("{\n  \"data\": {\n    \"uploadTheNoticeOfDecisionExplanation\": null\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null\n  }\n}")).check(status.in(200, 304)))
    }

      .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for Appeal Basic Details
======================================================================================*/
      .group("XUI${service}_090_StartAppealBasicDetails") {
        exec(http("XUI${service}_090_StartAppealBasicDetails")
          .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
          .headers(IACHeader.headers_basicdetails)
          .body(StringBody("{\n  \"data\": {\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\"\n  }\n}"))
          .check(status.in(200, 304)))
      }
      .pause(MinThinkTime, MaxThinkTime)

      //below is the new request

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for Appealant nationality
======================================================================================*/
      .group("XUI${service}_100_StartAppealantNationality") {
      exec(http("XUI${service}_100_StartAppealantNationality")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappellantNationalities")
      .headers(IACHeader.headers_nationality)
      .body(StringBody("{\n  \"data\": {\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ]\n  }\n}")).check(status.in(200, 304)))
    }
      .pause(MinThinkTime, MaxThinkTime)
    /*======================================================================================
    *Business process : Following business process is for IAC  Case Creation
    * Below group contains all the requests are for Appealant address search
    ======================================================================================*/
    
      .group("XUI${service}_110_StartAppealDetailsAddressSearch") {
        exec(http("XUI${service}_110_StartAppealDetailsAddressSearch")
          .get("/api/addresses?postcode=TW33SD")
          .headers(IACHeader.headers_postcode))
      }
        .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
  *Business process : Following business process is for IAC  Case Creation
  * Below group contains all the requests are for Appealant address
  ======================================================================================*/
          .group("XUI${service}_120_StartAppealAppellantAddress") {
            exec(http("XUI${service}_120_StartAppealAppellantAddress").post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress").headers(IACHeader.headers_appelantaddress).body(StringBody("{\n  \"data\": {\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  }\n}")).check(status.in(200, 304)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for contact preference
======================================================================================*/
      
          .group("XUI${service}_130_AppellantContactPref") {
            exec(http("XUI${service}_130_AppellantContactPref").post("/data/case-types/Asylum/validate?pageId=startAppealappellantContactPreference").headers(IACHeader.headers_contactpref).body(StringBody("{\n  \"data\": {\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"asasassa\",\n    \"appellantFamilyName\": \"fgfgfgfgfgfgfgfgfgf\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\"\n  }\n}")).check(status.in(200, 304)))
          }

          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for enter the details of appealtype
======================================================================================*/
          .group("XUI${service}_140_StartAppealAppealType") {
            exec(http("XUI${service}_140_StartAppealAppealType").post("/data/case-types/Asylum/validate?pageId=startAppealappealType").headers(IACHeader.headers_appealtype).body(StringBody("{\n  \"data\": {\n    \"appealType\": \"refusalOfHumanRights\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\",\n    \"appealType\": \"refusalOfHumanRights\"\n  }\n}")).check(status.in(200, 304)))
          }
      
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for enter the details of ground revocation
======================================================================================*/
          .group("XUI${service}_150_StartAppealGroundsRevocation") {
            exec(http("XUI${service}_150_StartAppealGroundsRevocation").post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsHumanRightsRefusal").headers(IACHeader.headers_humanrights).body(StringBody("{\n  \"data\": {\n    \"appealGroundsHumanRightsRefusal\": {\n      \"values\": [\n        \"protectionHumanRights\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\",\n    \"appealType\": \"refusalOfHumanRights\",\n    \"appealGroundsHumanRightsRefusal\": {\n      \"values\": [\n        \"protectionHumanRights\"\n      ]\n    }\n  }\n}")).check(status.in(200, 304)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for enter the details of appeal new matters
======================================================================================*/
          .group("XUI${service}_160_StartAppealNewMatters") {
            exec(http("XUI${service}_160_StartAppealNewMatters").post("/data/case-types/Asylum/validate?pageId=startAppealdeportationOrderPage").headers(IACHeader.headers_orderpage).body(StringBody("{\n  \"data\": {\n    \"deportationOrderOptions\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\",\n    \"appealType\": \"refusalOfHumanRights\",\n    \"appealGroundsHumanRightsRefusal\": {\n      \"values\": [\n        \"protectionHumanRights\"\n      ]\n    },\n    \"deportationOrderOptions\": \"No\"\n  }\n}")).check(status.in(200, 304)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for enter the details of appeal new matters
======================================================================================*/
          .group("XUI${service}_170_StartAppealNewMatters") {
            exec(http("XUI${service}_170_StartAppealNewMatters")
              .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
              .headers(IACHeader.headers_newmatters)
              .body(StringBody("{\n  \"data\": {\n    \"hasNewMatters\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\",\n    \"appealType\": \"refusalOfHumanRights\",\n    \"appealGroundsHumanRightsRefusal\": {\n      \"values\": [\n        \"protectionHumanRights\"\n      ]\n    },\n    \"deportationOrderOptions\": \"No\",\n    \"hasNewMatters\": \"No\"\n  }\n}"))
              .check(status.in(200, 304)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for enter the details of appelant has any other appeals
======================================================================================*/
          .group("XUI${service}_180_StartAppealHasOtherAppeals") {
            exec(http("XUI${service}_180_StartAppealHasOtherAppeals")
              .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
              .headers(IACHeader.headers_otherappeals)
              .body(StringBody("{\n  \"data\": {\n    \"hasOtherAppeals\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\",\n    \"appealType\": \"refusalOfHumanRights\",\n    \"appealGroundsHumanRightsRefusal\": {\n      \"values\": [\n        \"protectionHumanRights\"\n      ]\n    },\n    \"deportationOrderOptions\": \"No\",\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\"\n  }\n}"))
              .check(status.in(200, 304)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for enter the details of appelant legal representative details
======================================================================================*/
          .group("XUI${service}_190_StartAppealLegalRepresentative") {
            exec(http("XUI${service}_190_StartAppealLegalRepresentative")
              .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
              .headers(IACHeader.headers_repdetails)
              .body(StringBody("{\n  \"data\": {\n    \"legalRepCompany\": \"legalrepC\",\n    \"legalRepName\": \"legalrepName\",\n    \"legalRepReferenceNumber\": \"myref\",\n    \"isFeePaymentEnabled\": null\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\",\n    \"appealType\": \"refusalOfHumanRights\",\n    \"appealGroundsHumanRightsRefusal\": {\n      \"values\": [\n        \"protectionHumanRights\"\n      ]\n    },\n    \"deportationOrderOptions\": \"No\",\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"legalrepC\",\n     \"legalRepName\": \"legalrepName\",\n    \"legalRepReferenceNumber\": \"myref\",\n    \"isFeePaymentEnabled\": null\n  }\n}"))
              .check(status.in(200, 304)))
          }
          .group("XUI${service}_200_RepresentativeProfile") {
            exec(http("XUI${service}_200_RepresentativeProfile")
              .get("/data/internal/profile")
              .headers(IACHeader.headers_repprofile)
              .check(status.in(200, 304, 302)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for start appeal case save
======================================================================================*/
          .group("XUI${service}_210_StartAppealCaseSave") {
            exec(http("XUI${service}_210_StartAppealCaseSave").post("/data/case-types/Asylum/cases?ignore-warning=false").headers(IACHeader.headers_casesave).body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ],\n      \"checklist1\": [],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist7\": [\n        \"isNotEUDecision\"\n      ],\n      \"checklist3\": [],\n      \"checklist4\": [],\n      \"checklist6\": []\n    },\n    \"homeOfficeReferenceNumber\": \"123456783\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"uploadTheNoticeOfDecisionExplanation\": null,\n    \"appellantTitle\": \"Mr\",\n    \"appellantGivenNames\": \"appealFname\",\n    \"appellantFamilyName\": \"appealLname\",\n    \"appellantDateOfBirth\": \"1995-08-01\",\n    \"appellantStateless\": \"hasNationality\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"10 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsEmail\",\n    \"email\": \"iacpost@mailinator.com\",\n    \"mobileNumber\": null,\n    \"appealType\": \"refusalOfHumanRights\",\n    \"appealGroundsHumanRightsRefusal\": {\n      \"values\": [\n        \"protectionHumanRights\"\n      ]\n    },\n    \"deportationOrderOptions\": \"No\",\n    \"hasNewMatters\": \"No\",\n    \"newMatters\": null,\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"legalrepC\",\n    \"legalRepName\": \"legalrepN\",\n    \"legalRepReferenceNumber\": \"myref\",\n    \"isFeePaymentEnabled\": null\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}")).check(status.in(200, 304, 201)).check(jsonPath("$.id").optional.saveAs("caseId")))
          }

          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for start start submit appeal
======================================================================================*/
          .group("XUI${service}_220_005_StartSubmitAppeal") {
            exec(http("XUI${service}_220_005_StartSubmitAppeal").get("/case/IA/Asylum/${caseId}/trigger/submitAppeal").headers(IACHeader.headers_submitappeal).check(status.in(200, 304)))
              .exec(http("XUI${service}_220_010_StartSubmitAppealUI").get("/external/config/ui")
                .headers(IACHeader.headers_configui)
                .check(status.in(200, 304)))
              .exec(http("XUI${service}_220_015_SubmitAppealTCEnabled1")
                .get("/api/configuration?configurationKey=termsAndConditionsEnabled").headers(IACHeader.headers_configui)
                .check(status.in(200, 304))).exec(http("XUI${service}_220_020_IsAuthenticated")
              .get("/auth/isAuthenticated")
              .headers(IACHeader.headers_configui)
              .check(status.in(200, 304)))
              .exec(http("XUI${service}_220_025_SaveCaseView")
                .get("/data/internal/cases/${caseId}")
                .headers(IACHeader.headers_caseview)
                .check(status.in(200, 304, 302)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for start  submit appeal
======================================================================================*/
          .group("XUI${service}_230_SubmitAppeal") {
            exec(http("XUI${service}_230_005_SubmitAppeal").get("/data/internal/cases/${caseId}/event-triggers/submitAppeal?ignore-warning=false")
              .headers(IACHeader.headers_newsubmitappeal)
              .check(status.in(200, 304))
              .check(jsonPath("$.event_token").optional.saveAs("event_token_submit")))
            
          .exec(http("XUI${service}_230_010_IsAuthenticated")
            .get("/auth/isAuthenticated")
            .headers(IACHeader.headers_isauthenticatedsubmit)
            .check(status.in(200, 304, 302)))

          .exec(http("XUI${service}_230_015_UserDetails")
            .get("/api/user/details")
            .headers(IACHeader.headers_isauthenticatedsubmit)
            .check(status.in(200, 304, 302)))
            
          .exec(http("XUI${service}_230_020_DataInternalProfile")
            .get("/data/internal/profile")
            .headers(IACHeader.headers_internalprofiledatasubmit)
            .check(status.in(200, 304, 302)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for start  submit appeal declaration
======================================================================================*/
          .group("XUI${service}_240_SubmitAppealDeclaration") {
            exec(http("XUI${service}_240_05_SubmitAppealDeclaration").post("/data/case-types/Asylum/validate?pageId=submitAppealdeclaration")
            .headers(IACHeader.headers_submitdeclaration)
            .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token_submit}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
            .check(status.in(200, 304)))
            
            .exec(http("XUI${service}_240_010_SubmitAppealProfile")
              .get("/data/internal/profile")
              .headers(IACHeader.headers_internaldeclaration)
              .check(status.in(200, 304)))
          }
          .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
*Business process : Following business process is for IAC  Case Creation
* Below group contains all the requests are for start  submit appeal declaration submitted
======================================================================================*/
          .group("XUI${service}_250_AppealDeclarationSubmitted") {
            exec(http("XUI${service}_250_AppealDeclarationSubmitted")
              .post("/data/cases/${caseId}/events")
              .headers(IACHeader.headers_declarationsubmitted)
              .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token_submit}\",\n  \"ignore_warning\": false\n}"))
              .check(status.in(200, 304, 201)))
          }
          .pause(MinThinkTime, MaxThinkTime)
  
  
  /*====================================================================================
  * IAC share a case
   ======================================================================================*/
  val shareacase =
    group("XUI${service}_260_ShareACase") {
      exec(http("XUI${service}_260_005_ShareACase")
           .get("/api/caseshare/cases?case_ids=${caseId}")
           .headers(IACHeader.headers_scase1)
           .check(status.in(200, 304))
           .check(jsonPath("$..email").find(0).optional.saveAs("user0"))
           .check(jsonPath("$..firstName").find(0).optional.saveAs("firstName"))
           .check(jsonPath("$..lastName").find(0).optional.saveAs("lastName"))
           .check(jsonPath("$..idamId").find(0).optional.saveAs("idamId"))
    
      )
    
      .exec(http("XUI${service}_260_010_ShareACaseUsers")
            .get("/api/caseshare/users")
            .headers(IACHeader.headers_scase1)
            .check(status.in(200, 304))
            .check(jsonPath("$..email").find(0).optional.saveAs("user1"))
            .check(jsonPath("$..firstName").find(0).optional.saveAs("firstName1"))
            .check(jsonPath("$..lastName").find(0).optional.saveAs("lastName1"))
            .check(jsonPath("$..idamId").find(0).optional.saveAs("idamId1"))
      )
    }
    .pause(MinThinkTime , MaxThinkTime )
    .group("XUI${service}_270_ShareACaseConfirm") {
      exec(http("XUI${service}_270_ShareACaseAssignments")
           .post("/api/caseshare/case-assignments")
           .headers(IACHeader.headers_userassignment)
           .body(ElFileBody("IACShareACase.json")).asJson
           .check(status.in(200, 201)))
    }
    .pause(MinThinkTime , MaxThinkTime )
  
      }
