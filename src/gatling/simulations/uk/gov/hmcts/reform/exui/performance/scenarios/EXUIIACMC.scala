package uk.gov.hmcts.reform.exui.performance.scenarios

import java.text.SimpleDateFormat
import java.util.Date

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.IACHeader
import uk.gov.service.notify.NotificationClient

import scala.util.Random

object EXUIIACMC {

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
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

  val iaccasecreation=
    tryMax(2) {

      //set the current date as a usable parameter
      exec(session => session.set("currentDate", timeStamp))

        //set the random variables as usable parameters
        .exec(
        _.setAll(
          ("firstName", firstName()),
          ("lastName", lastName())
        ))
        //when click on create
        .exec(http("XUI${service}_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(IACHeader.headers_createcase)
        .check(status.in(200, 304))).exitHereIfFailed
    }
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_050_005_StartCreateCase1")
        .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
        .headers(IACHeader.headers_startcreatecase)
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))

      .exec(http("XUI${service}_050_010_StartCreateCase2")
      .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
      .headers(IACHeader.headers_startcreatecase)
      .check(status.is(200))
      //.check(jsonPath("$.event_token").optional.saveAs("event_token"))
    )

      .exec(http("XUI${service}_050_015_CaseCreateProfile")
        .get("/data/internal/profile")
        .headers(IACHeader.headers_data_internal)
        .check(status.in(200,304,302)))

      .pause(MinThinkTime , MaxThinkTime)


      .exec(http("XUI${service}_060_StartAppealChecklist")
        .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    }\n  }\n}"))
        .check(status.is(200))).exitHereIfFailed

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_070_StartAppealHomeOfficeDecision")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecision")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\"\n  }\n}"))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_080_StartAppealBasicDetails")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"appellantTitle\": \"sasasa\",\n    \"appellantGivenNames\": \"asasas\",\n    \"appellantFamilyName\": \"fgfgfgfg\",\n    \"appellantDateOfBirth\": \"1975-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"GB\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/008\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"sasasa\",\n    \"appellantGivenNames\": \"asasas\",\n    \"appellantFamilyName\": \"fgfgfgfg\",\n    \"appellantDateOfBirth\": \"1975-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"GB\"\n        }\n      }\n    ]\n  }\n}"))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_090_StartAppealDetailsAddressSearch")
        .get("/api/addresses?postcode=TW33SD")
        .headers(IACHeader.headers_createcase))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_100_StartAppealAppellantAddress")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    }\n  }\n}"))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_110_AppellantContactPref")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappellantContactPreference")
      .headers(IACHeader.headers_9)
      .body(StringBody("{\n  \"data\": {\n    \"contactPreference\": \"wantsSms\",\n    \"mobileNumber\": \"07540612047\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/003\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"mr\",\n    \"appellantGivenNames\": \"vijay\",\n    \"appellantFamilyName\": \"sdsdsd\",\n    \"appellantDateOfBirth\": \"1985-05-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"LK\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"contactPreference\": \"wantsSms\",\n    \"mobileNumber\": \"07540612047\"\n  }\n}"))
      .check(status.in(200, 304)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_120_StartAppealAppealType")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealType")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"appealType\": \"revocationOfProtection\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\"\n  }\n}"))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_130_StartAppealGroundsRevocation")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsRevocation")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    }\n  }\n}"))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_140_StartAppealNewMatters")
        .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"hasNewMatters\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\"\n  }\n}"))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_150_StartAppealHasOtherAppeals")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"hasOtherAppeals\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\"\n  }\n}"))
        .check(status.in(200, 304)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_160_StartAppealLegalRepresentative")
        .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
        .headers(IACHeader.headers_9)
        .body(StringBody("{\n  \"data\": {\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  }\n}"))
        .check(status.in(200, 304)))

      .exec(http("XUI${service}_170_RepresentativeProfile")
        .get("/data/internal/profile")
        .headers(IACHeader.headers_data_internal_rep)
        .check(status.in(200,304,302)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_180_StartAppealCaseSave")
        .post("/data/case-types/Asylum/cases?ignore-warning=false")
        .headers(IACHeader.headers_32)
        .body(StringBody("{\n  \"data\": {\n    \"checklist\": {\n      \"checklist1\": [\n        \"isAdult\"\n      ],\n      \"checklist2\": [\n        \"isNotDetained\"\n      ],\n      \"checklist3\": [\n        \"isNotFamilyAppeal\"\n      ],\n      \"checklist5\": [\n        \"isResidingInUK\"\n      ]\n    },\n    \"homeOfficeReferenceNumber\": \"A1289136/007\",\n    \"homeOfficeDecisionDate\": \"${currentDate}\",\n    \"appellantTitle\": \"Ms\",\n    \"appellantGivenNames\": \"Tessa\",\n    \"appellantFamilyName\": \"Tickles\",\n    \"appellantDateOfBirth\": \"1990-08-01\",\n    \"appellantNationalities\": [\n      {\n        \"id\": null,\n        \"value\": {\n          \"code\": \"ZW\"\n        }\n      }\n    ],\n    \"appellantHasFixedAddress\": \"Yes\",\n    \"appellantAddress\": {\n      \"AddressLine1\": \"14 Hibernia Gardens\",\n      \"AddressLine2\": \"\",\n      \"AddressLine3\": \"\",\n      \"PostTown\": \"Hounslow\",\n      \"County\": \"\",\n      \"PostCode\": \"TW3 3SD\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"appealType\": \"revocationOfProtection\",\n    \"appealGroundsRevocation\": {\n      \"values\": [\n        \"revocationHumanitarianProtection\"\n      ]\n    },\n    \"hasNewMatters\": \"No\",\n    \"hasOtherAppeals\": \"No\",\n    \"legalRepCompany\": \"${lastName}\",\n    \"legalRepName\": \"${firstName}\",\n    \"legalRepReferenceNumber\": \"ddddddrefa\"\n  },\n  \"event\": {\n    \"id\": \"startAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        .check(status.in(200, 304))
        .check(jsonPath("$.id").optional.saveAs("caseId")))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_190_005_StartSubmitAppeal")
        .get("/case/IA/Asylum/${caseId}/trigger/submitAppeal")
        .headers(IACHeader.headers_42)
        .check(status.in(200, 304))
      )

      .exec(http("XUI${service}_190_010_StartSubmitAppealUI")
        .get("/external/config/ui")
        .headers(IACHeader.headers_42)
        .check(status.in(200, 304))
      )
      .exec(http("XUI${service}_190_015_SubmitAppealTCEnabled1")
        .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(IACHeader.headers_42)
        .check(status.in(200, 304))
      )
      .exec(http("XUI${service}_190_020_SubmitAppealTCEnabled2")
        .get("/api/userTermsAndConditions/${myUserId}")
        .headers(IACHeader.headers_42)
        .check(status.in(200, 304))
      )

      .exec(http("XUI${service}_190_025_SaveCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(IACHeader.headers_submitpro)
        .check(status.in(200,304,302)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_200_005_SubmitAppeal")
        .get("/data/internal/cases/${caseId}/event-triggers/submitAppeal?ignore-warning=false")
        .headers(IACHeader.headers_newsubmitappeal)
        .check(status.in(200, 304))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))

      .exec(http("XUI${service}_200_010_SubmitAppealProfile")
        .get("/data/internal/profile")
        .headers(IACHeader.headers_internal_data_submit)
        .check(status.in(200,304,302)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_210_SubmitAppealDeclaration")
        .post("/data/case-types/Asylum/validate?pageId=submitAppealdeclaration")
        .headers(IACHeader.headers_submitdeclaration)
        .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200, 304))
      )

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_220_AppealDeclarationSubmitted")
        .post("/data/cases/${caseId}/events")
        .headers(IACHeader.headers_declarationsubmitted)
        .body(StringBody("{\n  \"data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false\n}"))
        .check(status.in(200, 304))
      )
      .pause(MinThinkTime , MaxThinkTime )





  val StringBodyVJ="{\n  \"data\": {\n    \"orgListOfUsers\": {\n      \"value\": {\n        \"code\": \"${code1}\",\n        \"label\": \"${label1}\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"${code1}\",\n          \"label\": \"${label1}\"\n        },\n        {\n          \"code\": \"${code2}\",\n          \"label\": \"${label2}\"\n        },\n        {\n          \"code\": \"${code3}\",\n          \"label\": \"${label3}\"\n        },\n        {\n          \"code\": \"${code4}\",\n          \"label\": \"${label4}\"\n        },\n        {\n          \"code\": \"${code5}\",\n          \"label\": \"${label5}\"\n        },\n        {\n          \"code\": \"${code6}\",\n          \"label\": \"${label6}\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"shareACase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token_sharecase}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"orgListOfUsers\": \"${code1}\"\n  },\n  \"case_reference\": \"${caseId}\"\n}"

  val StringBodyVJSubmit="{\n  \"data\": {\n    \"orgListOfUsers\": {\n      \"value\": {\n        \"code\": \"${code}\",\n        \"label\": \"${label}\"\n      },\n      \"list_items\": [\n        {\"code\":\"${code1}\",\"label\":\"${label1}\"},\n        {\"code\":\"${code2}\",\"label\":\"${label2}\"},\n        {\"code\":\"${code3}\",\"label\":\"${label3}\"},\n        {\"code\":\"${code4}\",\"label\":\"${label4}\"},\n        {\"code\":\"${code5}\",\"label\":\"${label5}\"},\n        {\"code\":\"${code6}\",\"label\":\"${label6}\"}\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"shareACase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token_sharecase}\",\n  \"ignore_warning\": false\n}"



  val shareacase =
    tryMax(2) {

      exec(http("XUI${service}_230_005_StartShareCaseEvent")
        .get("/data/internal/cases/${caseId}/event-triggers/shareACase?ignore-warning=false")
        .headers(IACHeader.headers_sharecase1)
        .check(status.in(200, 304, 302))
        .check(jsonPath("$..list_items[0].code").optional.saveAs("code1"))
        .check(jsonPath("$..list_items[0].label").optional.saveAs("label1"))
        .check(jsonPath("$..list_items[1].code").optional.saveAs("code2"))
        .check(jsonPath("$..list_items[1].label").optional.saveAs("label2"))
        .check(jsonPath("$..list_items[2].code").optional.saveAs("code3"))
        .check(jsonPath("$..list_items[2].label").optional.saveAs("label3"))
        .check(jsonPath("$..list_items[3].code").optional.saveAs("code4"))
        .check(jsonPath("$..list_items[3].label").optional.saveAs("label4"))
        .check(jsonPath("$..list_items[4].code").optional.saveAs("code5"))
        .check(jsonPath("$..list_items[4].label").optional.saveAs("label5"))
        .check(jsonPath("$..list_items[5].code").optional.saveAs("code6"))
        .check(jsonPath("$..list_items[5].label").optional.saveAs("label6"))
        .check(jsonPath("$..code").find(0).optional.saveAs("code"))
        .check(jsonPath("$..label").find(2).optional.saveAs("label"))
        .check(jsonPath("$.event_token").optional.saveAs("event_token_sharecase"))
      )
        .exec(http("XUI${service}_230_010_ShareACaseProfile")
          .get("/data/internal/profile")
          .headers(IACHeader.headers_di_casedetails)
          .check(status.in(200, 304, 302))
        )
    }
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_240_005_ShareACaseValidate")
        .post("/data/case-types/Asylum/validate?pageId=shareACaseshareACase")
        .headers(IACHeader.headers_shareacasesubmit)
        // .body(RawFileBody("RecordedSimulationshareacaseiac_0006_request.txt"))
        .body(StringBody(StringBodyVJ))
        // .body(ElFileBody("RecordedSimulationshareacaseiac_0006_request.json")).asJson
        //.body(StringBody("{\n  \"data\": {\n    \"orgListOfUsers\": {\n      \"value\": {\n   \"code\":,\n  \"event\": {\n    \"id\": \"submitAppeal\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${event_token}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"legalRepDeclaration\": [\n      \"hasDeclared\"\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}"))
        .check(status.in(200,304,302)))

      .exec(http("XUI${service}_240_010_ShareaCaseValidateProfile")
        .get("/data/internal/profile")
        .headers(IACHeader.headers_di_shareacase)
        .check(status.in(200,304,302)))
      .pause(MinThinkTime , MaxThinkTime)


    .exec(http("XUI${service}_250_005_ShareACaseEvents")
      .post("/data/cases/${caseId}/events")
      .headers(IACHeader.headers_shareacase12)
      .body(StringBody(StringBodyVJSubmit))
      //.body(ElFileBody("RecordedSimulationshareacaseiac_0012_request.json")).asJson
      .check(status.in(200,304,302)))

    .exec(http("XUI${service}_250_010_ShareACaseViewData")
      .get("/data/internal/cases/${caseId}")
      .headers(IACHeader.headers_shareacase14)
      .check(status.in(200,304,302)))
    .pause(MinThinkTime , MaxThinkTime )







  val findandviewcase =

    exec(http("XUI${service}_040_005_SearchPage")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(IACHeader.headers_search))

    .exec(http("XUI${service}_040_010_SearchPaginationMetaData")
			.get("/data/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases/pagination_metadata?state=appealSubmitted&case.searchPostcode=TW3%203SD")
			.headers(IACHeader.headers_search))

    .exec(http("XUI${service}_040_015_SearchResults")
			.get("/aggregated/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases?view=WORKBASKET&state=appealSubmitted&page=1&case.searchPostcode=TW3%203SD")
			.headers(IACHeader.headers_search)
      .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))

    .exec(http("XUI${service}_040_020_SearchAccessJurisdictions")
			.get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
			.headers(IACHeader.headers_search))

    .pause(MinThinkTimeIACV,MaxThinkTimeIACV)

    .foreach("${caseNumbers}","caseNumber") {
      exec(http("XUI${service}_050_CaseDetails")
        .get("/data/internal/cases/${caseNumber}")
        .headers(IACHeader.headers_data_internal_cases)
        .check(regex("""internal/documents/(.+?)","document_filename""").find(0).saveAs("Document_ID"))
        .check(status.is(200)))

        .pause(MinThinkTimeIACV,MaxThinkTimeIACV)

    .exec(http("XUI${service}_060_005_ViewCaseDocumentUI")
      .get("/external/config/ui")
      .headers(IACHeader.headers_documents))

    .exec(http("XUI${service}_060_010_ViewCaseDocumentT&C")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(IACHeader.headers_documents))

    .exec(http("XUI${service}_060_015_ViewCaseDocumentAnnotations")
      .get("/em-anno/annotation-sets/filter?documentId=${Document_ID}")
      .headers(IACHeader.headers_documents)
      .check(status.in(200, 404,304)))

    .exec(http("XUI${service}_060_020_ViewCaseDocumentBinary")
      .get("/documents/${Document_ID}/binary")
      .headers(IACHeader.headers_documents)
      .check(status.in(200, 404,304)))
  }
}