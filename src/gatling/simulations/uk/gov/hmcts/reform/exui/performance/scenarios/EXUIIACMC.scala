package uk.gov.hmcts.reform.exui.performance.scenarios

import java.text.SimpleDateFormat
import java.util.Date

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Environment, Headers}

import scala.util.Random

object EXUIIACMC {

  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTimeIACC
  val MaxThinkTime = Environment.maxThinkTimeIACC

  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString

  val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
  val now = new Date()
  val timeStamp = sdfDate.format(now)

  val postcodeFeeder = csv("postcodes.csv").random

  //Common objects required as part of the e2e journey

  val profile =
    exec(http("XUI${service}_000_Profile")
      .get("/data/internal/profile")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .check(jsonPath("$.user.idam.id").notNull))

  val configurationui =
    exec(http("XUI${service}_000_ConfigurationUI")
      .get("/external/configuration-ui/")
      .headers(Headers.commonHeader)
      .header("accept", "*/*")
      .check(substring("ccdGatewayUrl")))

  val configUI =
    exec(http("XUI${service}_000_ConfigUI")
      .get("/external/config/ui")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("ccdGatewayUrl")))

  val TsAndCs =
    exec(http("XUI${service}_000_TsAndCs")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("false")))

  val userDetails =
    exec(http("XUI${service}_000_UserDetails")
      .get("/api/user/details")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("userInfo")))

  val isAuthenticated =
    exec(http("XUI${service}_000_IsAuthenticated")
      .get("/auth/isAuthenticated")
      .headers(Headers.commonHeader)
      .header("accept", "application/json, text/plain, */*")
      .check(substring("true")))

  val postcodeLookup =
    feed(postcodeFeeder)
      .exec(http("XUI${service}_000_PostcodeLookup")
        .get("/api/addresses?postcode=${postcode}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(jsonPath("$.header.totalresults").ofType[Int].gt(0))
        .check(regex(""""(?:BUILDING|ORGANISATION)_.+" : "(.+?)",(?s).*?"(?:DEPENDENT_LOCALITY|THOROUGHFARE_NAME)" : "(.+?)",.*?"POST_TOWN" : "(.+?)",.*?"POSTCODE" : "(.+?)"""")
          .ofType[(String, String, String, String)].findRandom.saveAs("addressLines")))

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  ======================================================================================*/

  val iaccasecreation =

  //set the current date as a usable parameter
    exec(session => session.set("currentDate", timeStamp))

    //set the random variables as usable parameters
    .exec(_.setAll(
      ("firstName", firstName()),
      ("lastName", lastName())
    ))

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests are when clicking on create case
  ======================================================================================*/

    .group("XUI${service}_040_CreateCase") {
      exec(http("XUI${service}_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json"))
        .exitHereIfFailed
    }
    
    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests when starting create case
  ======================================================================================*/
      
    .group("XUI${service}_050_StartCreateCase1") {
      exec(http("XUI${service}_050_005_StartCreateCase1")
        .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(profile)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(Environment.baseDomain).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests for starting appeal checklist
  ======================================================================================*/

    .group("XUI${service}_060_StartAppealChecklist") {
      exec(http("XUI${service}_060_StartAppealChecklist")
      .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/iac/IACStartChecklist.json"))  )
      .exitHereIfFailed
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI${service}_070_StartAppealOutOfCountry") {
      exec(http("XUI${service}_070_StartAppealOutOfCountry")
      .post("/data/case-types/Asylum/validate?pageId=startAppealoutOfCountry")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/iac/IACOutOfCountry.json")))
    }
    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests for appealing home office decision
  ======================================================================================*/

    .group("XUI${service}_080_StartAppealHomeOfficeDecision") {
      exec(http("XUI${service}_080_StartAppealHomeOfficeDecision")
      .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecision")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACHomeOfficeDecision.json")))
    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for uploading notification Decision
======================================================================================*/

    .group("XUI${service}_090_UploadNoticeDecision") {
      exec(http("XUI${service}_090_005_UploadNoticeDecision")
        .post("/documents")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$._embedded.documents[0]._links.self.href").saveAs("DocumentURL")))
    }

    .group("XUI${service}_090_010_StartUploadNoticeDecision") {
      exec(http("XUI${service}_090_010_StartUploadNoticeDecision")
      .post("/data/case-types/Asylum/validate?pageId=startAppealuploadTheNoticeOfDecision")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/iac/IACUploadNoticeDecision.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for Appeal Basic Details
======================================================================================*/

    .group("XUI${service}_100_StartAppealBasicDetails") {
      exec(http("XUI${service}_100_StartAppealBasicDetails")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppellantBasicDetails.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

 /*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for Appealant nationality
======================================================================================*/

    .group("XUI${service}_110_StartAppealantNationality") {
      exec(http("XUI${service}_110_StartAppealantNationality")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantNationalities")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppellantNationalities.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
* Below group contains all the requests for Appealant address search
======================================================================================*/
    
    exec(postcodeLookup)

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
* Below group contains all the requests for Appealant address
======================================================================================*/

    .group("XUI${service}_130_StartAppealAppellantAddress") {
      exec(http("XUI${service}_130_StartAppealAppellantAddress")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppellantAddress.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for contact preference
======================================================================================*/
      
    .group("XUI${service}_140_AppellantContactPref") {
      exec(http("XUI${service}_140_AppellantContactPref")
      .post("/data/case-types/Asylum/validate?pageId=startAppealappellantContactPreference")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "${XSRFToken}")
      .body(ElFileBody("bodies/iac/IACContactPreference.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for entering the details of appeal type
======================================================================================*/

    .group("XUI${service}_150_StartAppealAppealType") {
      exec(http("XUI${service}_150_StartAppealAppealType")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealType")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealType.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for entering the details of ground revocation
======================================================================================*/

    .group("XUI${service}_160_StartAppealGroundsRevocation") {
      exec(http("XUI${service}_160_StartAppealGroundsRevocation")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsHumanRightsRefusal")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealGrounds.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for entering the details of appeal new matters
======================================================================================*/

    .group("XUI${service}_170_StartAppealNewMatters") {
      exec(http("XUI${service}_170_StartAppealNewMatters")
        .post("/data/case-types/Asylum/validate?pageId=startAppealdeportationOrderPage")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACDeportationOrder.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for entering the details of appeal new matters
======================================================================================*/

    .group("XUI${service}_180_StartAppealNewMatters") {
      exec(http("XUI${service}_180_StartAppealNewMatters")
        .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACNewMatters.json")))
    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for entering the details if appealant has any other appeals
======================================================================================*/

    .group("XUI${service}_190_StartAppealHasOtherAppeals") {
      exec(http("XUI${service}_190_StartAppealHasOtherAppeals")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACOtherAppeals.json")))
    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC  Case Creation
*Below group contains all the requests for entering the details of appeallant legal representative details
======================================================================================*/

    .group("XUI${service}_200_StartAppealLegalRepresentative") {
      exec(http("XUI${service}_200_StartAppealLegalRepresentative")
        .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACLegalRepresentative.json")))

      .exec(profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for starting appeal case save
======================================================================================*/

    .group("XUI${service}_220_StartAppealCaseSave") {
      exec(http("XUI${service}_220_StartAppealCaseSave")
        .post("/data/case-types/Asylum/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACSaveCase.json"))
        .check(jsonPath("$.id").saveAs("caseId")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for starting start submit appeal
======================================================================================*/

    .group("XUI${service}_230_005_StartSubmitAppeal") {
      exec(http("XUI${service}_230_005_StartSubmitAppeal")
        .get("/case/IA/Asylum/${caseId}/trigger/submitAppeal")
        .headers(Headers.navigationHeader))

      .exec(configurationui)

      .exec(configUI)

      .exec(TsAndCs)

      .exec(userDetails)

      .exec(isAuthenticated)

      .exec(http("XUI${service}_230_035_SaveCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "${XSRFToken}")
        // .check(status.in(200, 304, 302))
        )
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for starting submit appeal
======================================================================================*/

    .group("XUI${service}_240_SubmitAppeal") {
      exec(http("XUI${service}_240_005_SubmitAppeal")
      .get("/data/internal/cases/${caseId}/event-triggers/submitAppeal?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token_submit")))

    .exec(isAuthenticated)

    .exec(userDetails)

    .exec(profile)

    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for starting submit appeal declaration
======================================================================================*/

    .group("XUI${service}_250_SubmitAppealDeclaration") {
      exec(http("XUI${service}_250_05_SubmitAppealDeclaration")
        .post("/data/case-types/Asylum/validate?pageId=submitAppealdeclaration")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealDeclaration.json")))

      .exec(profile)
      
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
* Below group contains all the requests for starting submit appeal declaration submitted
======================================================================================*/

    .group("XUI${service}_260_AppealDeclarationSubmitted") {
      exec(http("XUI${service}_260_AppealDeclarationSubmitted")
        .post("/data/cases/${caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/iac/IACSubmitAppeal.json")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*====================================================================================
* IAC share a case
====================================================================================*/

  val shareacase =

    group("XUI${service}_270_ShareACase") {
      exec(http("XUI${service}_270_005_ShareACase")
        .get("/api/caseshare/cases?case_ids=${caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$..email").find(0).saveAs("user0"))
        .check(jsonPath("$..firstName").find(0).saveAs("firstName"))
        .check(jsonPath("$..lastName").find(0).saveAs("lastName"))
        .check(jsonPath("$..idamId").find(0).saveAs("idamId")))
    
      .exec(http("XUI${service}_270_010_ShareACaseUsers")
        .get("/api/caseshare/users")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$..email").find(0).saveAs("user1"))
        .check(jsonPath("$..firstName").find(0).saveAs("firstName1"))
        .check(jsonPath("$..lastName").find(0).saveAs("lastName1"))
        .check(jsonPath("$..idamId").find(0).saveAs("idamId1")))
    }

    .pause(MinThinkTime , MaxThinkTime)

    .group("XUI${service}_280_ShareACaseConfirm") {
      exec(http("XUI${service}_280_ShareACaseAssignments")
        .post("/api/caseshare/case-assignments")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/iac/IACShareACase.json")))
    }

    .pause(MinThinkTime , MaxThinkTime )

}