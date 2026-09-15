package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utilities._
import utils._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Solicitor_IAC {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

//  val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
//  val patternDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//  val patternYear = DateTimeFormatter.ofPattern("yyyy")
  val now = LocalDateTime.now()
  val patternTimeNow = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
//  val timeStamp = sdfDate.format(now)

  /*======================================================================================
  * IAC Case Creation
  ======================================================================================*/

  val CreateIACCase =

    //set session variables
    exec(_.setAll("firstName" -> ("Perf" + StringUtils.randomString(5)),
                  "lastName" -> ("Test" + StringUtils.randomString(5)),
                  "dobDay" -> DateUtils.getRandomDayOfMonth(),
                  "dobMonth" -> DateUtils.getRandomMonthOfYear(),
                  "dobYear" -> DateUtils.getDatePastRandom("yyyy", minYears = 25, maxYears = 70),
                  "currentDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests are when clicking on create case
  ======================================================================================*/

    .group("XUI_IAC_040_CreateCase") {
      exec(http("XUI_IAC_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("IA")))
    }

    .exec(getCookieValue(CookieKey("__auth__").saveAs("authToken")))

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests when starting create case
  ======================================================================================*/

    .group("XUI_IAC_050_StartCreateCase1") {
      exec(http("XUI_IAC_050_005_StartCreateCase1")
        .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Start the appeal")))

      .exec(Common.profile)

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests for starting appeal checklist
  ======================================================================================*/

    .group("XUI_IAC_060_StartAppealChecklist") {
      exec(http("XUI_IAC_060_StartAppealChecklist")
        .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACStartChecklist.json"))
        .check(substring("isOutOfCountryEnabled")))
    }
    .pause(MinThinkTime, MaxThinkTime)

    .group("XUI_IAC_070_StartAppealOutOfCountry") {
      exec(http("XUI_IAC_070_StartAppealOutOfCountry")
        .post("/data/case-types/Asylum/validate?pageId=startAppealoutOfCountry")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACOutOfCountry.json"))
        .check(substring("appellantInUk")))
    }
    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Below group contains all the requests for appealing home office decision
  ======================================================================================*/

    .group("XUI_IAC_080_StartAppealHomeOfficeDecision") {
      exec(http("XUI_IAC_080_StartAppealHomeOfficeDecision")
      .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeReferenceNumber")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/iac/IACHomeOfficeDecision.json"))
      .check(substring("letterSentOrReceived")))
    }
    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Enter the appeal basic details
  ======================================================================================*/

    .group("XUI_IAC_100_StartAppealBasicDetails") {
      exec(http("XUI_IAC_100_StartAppealBasicDetails")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppellantBasicDetails.json"))
        .check(substring("appellantGivenNames")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Enter the appealant nationality
  ======================================================================================*/

    .group("XUI_IAC_110_StartAppealantNationality") {
      exec(http("XUI_IAC_110_StartAppealantNationality")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantNationalities")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppellantNationalities.json"))
        .check(substring("hasNationality")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    .exec(Common.postcodeLookup)

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Enter postcode and confirm address
  ======================================================================================*/

    .group("XUI_IAC_130_StartAppealAppellantAddress") {
      exec(http("XUI_IAC_130_StartAppealAppellantAddress")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppellantAddress.json"))
        .check(substring("appellantHasFixedAddress"))
        .check(jsonPath("$.data.appellantNationalities[0].id").saveAs("nationalityId"))
       )
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Select contact preferences & enter email address
  ======================================================================================*/

    .group("XUI_IAC_140_AppellantContactPref") {
      exec(http("XUI_IAC_140_AppellantContactPref")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantContactPreference")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACContactPreference.json"))
        .check(substring("contactPreference")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Select the appeal type
  ======================================================================================*/

    .group("XUI_IAC_150_StartAppealAppealType") {
      exec(http("XUI_IAC_150_StartAppealAppealType")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealType")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealType.json"))
        .check(substring("appealType")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Select the grounds for appeal
  ======================================================================================*/

    .group("XUI_IAC_160_StartAppealGroundsRevocation") {
      exec(http("XUI_IAC_160_StartAppealGroundsRevocation")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsHumanRightsRefusal")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealGrounds.json"))
        .check(substring("appealGroundsDecisionHumanRightsRefusal")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Enter the Home Office letter decision date
  ======================================================================================*/

    .group("XUI_IAC_170_StartAppealHomeOfficeDecisionDate") {
      exec(http("XUI_IAC_170_StartAppealHomeOfficeDecisionDate")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecisionLetter")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACHomeOfficeDecisionDate.json"))
        .check(substring("homeOfficeDecisionDate")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Upload the notice decision & continue
  ======================================================================================*/

    .group("XUI_IAC_180_UploadNoticeDecision") {
      exec(http("XUI_IAC_180_005_UploadNoticeDecision")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "#{XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "Asylum")
        .formParam("jurisdictionId", "IA")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("DocumentHash"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL1")))
    }

    .group("XUI_IAC_190_010_StartUploadNoticeDecision") {
      exec(http("XUI_IAC_190_010_StartUploadNoticeDecision")
      .post("/data/case-types/Asylum/validate?pageId=startAppealuploadTheNoticeOfDecision")
      .headers(Headers.commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("x-xsrf-token", "#{XSRFToken}")
      .body(ElFileBody("bodies/iac/IACUploadNoticeDecision.json"))
      .check(substring("uploadTheNoticeOfDecisionDocs")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Select no sponsor & continue
  ======================================================================================*/

    .group("XUI_IAC_200_StartAppealSponsor") {
      exec(http("XUI_IAC_200_StartAppealSponsor")
        .post("/data/case-types/Asylum/validate?pageId=startAppealsponsor")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealSponsor.json"))
        .check(substring("hasSponsor")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Select any deportation order options (no)
  ======================================================================================*/

    .group("XUI_IAC_210_StartAppealDeportationOrder") {
      exec(http("XUI_IAC_210_StartAppealDeportationOrder")
        .post("/data/case-types/Asylum/validate?pageId=startAppealdeportationOrderPage")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACDeportationOrder.json"))
        .check(substring("deportationOrderOptions")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  *Business process : Following business process is for IAC Case Creation
  *Enter any new appeal matters
  ======================================================================================*/

    .group("XUI_IAC_220_StartAppealNewMatters") {
      exec(http("XUI_IAC_220_StartAppealNewMatters")
        .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACNewMatters.json"))
        .check(substring("hasNewMatters")))
    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Enter any other new appeals
======================================================================================*/

    .group("XUI_IAC_230_StartAppealHasOtherAppeals") {
      exec(http("XUI_IAC_230_StartAppealHasOtherAppeals")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACOtherAppeals.json"))
        .check(substring("hasOtherAppeals")))
    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC  Case Creation
*Enter the legal representative details
======================================================================================*/

    .group("XUI_IAC_240_StartAppealLegalRepresentative") {
      exec(http("XUI_IAC_240_StartAppealLegalRepresentative")
        .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACLegalRepresentative.json"))
        .check(substring("legalRepReferenceNumber")))

      .exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC  Case Creation
*Select the Fee Decision option
======================================================================================*/

    .group("XUI_IAC_250_StartAppealFeeDecision") {
      exec(http("XUI_IAC_250_StartAppealFeeDecision")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhearingFeeDecision")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealFeeDecision.json"))
        .check(substring("decisionHearingFeeOption")))

      .exec(Common.profile)
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for starting appeal case save
======================================================================================*/

    .group("XUI_IAC_260_StartAppealCaseSave") {
      exec(http("XUI_IAC_260_StartAppealCaseSave")
        .post("/data/case-types/Asylum/cases?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACSaveCase.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(substring("appealStarted")))
    }

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Check all details and Submit
======================================================================================*/

    .group("XUI_IAC_270_005_StartSubmitAppeal") {
      exec(http("XUI_IAC_270_005_StartSubmitAppeal")
        .get("/case/IA/Asylum/#{caseId}/trigger/submitAppeal")
        .headers(Headers.navigationHeader)
        .check(substring("HMCTS Manage")))

      .exec(Common.configurationui)

      .exec(Common.configUI)

      .exec(Common.TsAndCs)

      .exec(Common.userDetails)

      .exec(Common.isAuthenticated)

      .exec(http("XUI_IAC_270_035_SaveCaseView")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(substring("Create")))
    }

    .exec(Common.caseActivityGet)
    .pause(2)
    .exec(Common.caseActivityPost)

    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
*Below group contains all the requests for starting submit appeal
======================================================================================*/

    .group("XUI_IAC_280_PayAndSubmitAppeal") {
      exec(http("XUI_IAC_280_005_PayAndSubmitAppeal")
      .get("/data/internal/cases/#{caseId}/event-triggers/submitAppeal?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(jsonPath("$.event_token").saveAs("event_token_submit"))
        .check(substring("Submit your appeal")))

      .exec(Common.isAuthenticated)

      .exec(Common.userDetails)

      .exec(Common.profile)

      .exec(Common.caseActivityGet)
      .pause(2)
      .exec(Common.caseActivityPost)

    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
* Below group contains all the requests for confirmation statement and clicking continue
======================================================================================*/

    .group("XUI_IAC_290_ConfirmDeclaration") {
      exec(http("XUI_IAC_290_005_ConfirmDeclaration")
        .post("/data/case-types/Asylum/validate?pageId=payAndSubmitAppealdeclaration")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACAppealDeclaration.json"))
        .check(substring("hasDeclared")))

      .exec(Common.profile)
   }

/*======================================================================================
*Business process : Following business process is for IAC Case Creation
* Below group contains all the requests for starting submit appeal declaration submitted
======================================================================================*/

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .group("XUI_IAC_300_AppealDeclarationSubmitted") {
      exec(http("XUI_IAC_300_AppealDeclarationSubmitted")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACSubmitAppeal.json"))
        .check(substring("hasDeclared")))
    }

    .exec(Common.caseActivityGet)
    .pause(2)
    .exec(Common.caseActivityPost)

    .pause(MinThinkTime, MaxThinkTime)

/*====================================================================================
* IAC share a case
====================================================================================*/

  val shareacase =

    group("XUI_IAC_310_ShareACase") {
      exec(http("XUI_IAC_310_005_ShareACase")
        .get("/api/caseshare/cases?case_ids=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$..email").find(0).saveAs("user0"))
        .check(jsonPath("$..firstName").find(0).saveAs("firstName"))
        .check(jsonPath("$..lastName").find(0).saveAs("lastName"))
        .check(jsonPath("$..idamId").find(0).saveAs("idamId")))

      .exec(http("XUI_IAC_290_010_ShareACaseUsers")
        .get("/api/caseshare/users")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(jsonPath("$..email").find(0).saveAs("user1"))
        .check(jsonPath("$..firstName").find(0).saveAs("firstName1"))
        .check(jsonPath("$..lastName").find(0).saveAs("lastName1"))
        .check(jsonPath("$..idamId").find(0).saveAs("idamId1")))
    }

    .pause(MinThinkTime , MaxThinkTime)

    .group("XUI_IAC_300_ShareACaseConfirm") {
      exec(http("XUI_IAC_300_ShareACaseAssignments")
        .post("/api/caseshare/case-assignments")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/iac/IACShareACase.json")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val QueryManagement =

    group("XUI_IAC_310_RaiseNewQuery") {
      exec(http("XUI_IAC_310_005_RaiseNewQuery")
        .get("/query-management/query/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("HMCTS Manage cases"))) // No page specific text is returned

      .exec(Common.isAuthenticated)

      .exec(http("XUI_IAC_310_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("case_id")))
    }

      .exec(getCookieValue(CookieKey("__userid__").withDomain(BaseURL.replace("https://", "")).saveAs("idamId")))
      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

      .pause(MinThinkTime , MaxThinkTime )

      .group("XUI_IAC_320_ConfirmQueryDetails") {
        exec(http("XUI_IAC_320_005_ConfirmQueryDetails")
          .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRaiseQuery?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token")))
      }

      .pause(MinThinkTime , MaxThinkTime )

      .group("XUI_IAC_330_RaiseNewQuery") {
        exec(http("XUI_IAC_330_005_RaiseNewQuery")
          .get("/query-management/query/#{caseId}raiseAQuery")
          .headers(Headers.commonHeader)
          .check(substring("HMCTS Manage cases")))
      }

      .pause(MinThinkTime , MaxThinkTime )

      .exec(_.setAll("currentTime" -> now.format(patternTimeNow),
                     "currentDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

      .group("XUI_IAC_340_SubmitNewQuery") {
        exec(http("XUI_IAC_340_005_SubmitNewQuery")
          .post("/data/cases/#{caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/iac/IACRaiseNewQuery.json")))
      }

      .pause(MinThinkTime , MaxThinkTime )

  val RespondToQueryManagement =

    group("XUI_IAC_350_ViewCase") {
      exec(http("XUI_IAC_350_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("case_id")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.set("taskName", "respondToQuery"))
    .exec(session => session.set("counter", 0))

    .doWhile(session => !session.contains("taskId") && session("counter").as[Int] < 20, "counter") {

      pause(60)

      .group("XUI_IAC_360_SelectCaseTask") {
        exec(http("XUI_IAC_360_SelectCaseTask_#{counter}")
          .post("/workallocation/case/task/#{caseId}")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(StringBody("""{"refined":true}"""))
          .check(jsonPath("$[?(@.type=='#{taskName}')].id").optional.saveAs("taskId"))
          .check(jsonPath("$[?(@.type=='#{taskName}')].type").optional.saveAs("taskType")))
      }
    }

    .doIf(session => !session.contains("taskId")) {
      exec { session =>
        println("Could not retrieve task after 20 attempts, exiting user...")
        println(s"Iteration ${session("counter").as[Int]}, caseId: ${session("caseId").as[String]}, taskId present: ${session.contains("taskId")}")
        session.markAsFailed
      }
        .exitHereIfFailed
    }

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_IAC_370_AssignTaskToMe") {
      exec(http("XUI_IAC_370_AssignTaskToMe_Claim")
        .post("/workallocation/task/#{taskId}/claim")
        .headers(Headers.commonHeader)
        .header("content-type", "application/json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{}""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.setAll("currentTime" -> now.format(patternTimeNow),
      "currentDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

    .group("XUI_IAC_380_ViewQuery") {
      exec(Common.isAuthenticated)

      .exec(http("XUI_IAC_380_005_ViewQuery")
        .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRespondQuery?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].id").saveAs("raiseQueryParentId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.id").saveAs("raiseQueryId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.createdBy").saveAs("queryCreatedBy"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.createdOn").saveAs("queryCreatedOn")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.setAll("currentTime" -> now.format(patternTimeNow)))
    .exec(getCookieValue(CookieKey("__userid__").withDomain(BaseURL.replace("https://", "")).saveAs("idamId")))

    .group("XUI_IAC_390_SubmitQueryResponse") {
      exec(http("XUI_IAC_390_005_SubmitQueryResponse")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACRespondToQuery.json")))

      .exec(http("XUI_IAC_390_005_CompleteTask")
        .post("/workallocation/task/#{taskId}/complete")
        .headers(Headers.commonHeader)
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"actionByEvent":true,"eventName":"Respond Query"}""")))
    }

    //Removing this session variable because a new one needs to be captured for a future response
    .exec(_.remove("taskId"))

    .pause(MinThinkTime , MaxThinkTime )

  val FollowUpQuestionQueryManagement =

    group("XUI_IAC_400_ViewCase") {
      exec(http("XUI_IAC_400_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("case_id")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_IAC_410_AskFollowUpQuery") {
      exec(http("XUI_IAC_410_005_AskFollowUpQuery")
        .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRaiseQuery?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.id").saveAs("raiseQueryParentId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.createdBy").saveAs("raiseQueryCreatedBy"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.createdOn").saveAs("raiseQueryCreatedOn"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].id").saveAs("raiseQueryId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[1].id").saveAs("raiseQueryParentId2"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[1].value.id").saveAs("raiseQueryId2"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[1].value.createdBy").saveAs("queryCreatedBy2"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[1].value.createdOn").saveAs("queryCreatedOn2"))
        .check(jsonPath("$.event_token").saveAs("event_token")))

      .exec(Common.waJurisdictions)
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.setAll("currentTime" -> now.format(patternTimeNow),
                   "currentDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

    .group("XUI_IAC_420_ValidateFollowUpDetails") {
      exec(http("XUI_IAC_420_005_ValidateFollowUpDetails")
        .post("/data/case-types/Asylum/validate?pageId=queryManagementRaiseQuery")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACFollowUpQuery.json"))
        .check(substring("Followup")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_IAC_430_SubmitFollowUpQuery") {
      exec(http("XUI_IAC_430_005_SubmitFollowUpQuery")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/iac/IACFollowUpQuery.json")))
    }

    .pause(MinThinkTime , MaxThinkTime )

  val RespondToFollowUpQueryManagement =

    group("XUI_IAC_440_ViewCase") {
      exec(http("XUI_IAC_440_005_ViewCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .check(substring("case_id")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.set("taskName", "respondToQuery"))
    .exec(session => session.set("counter", 0))

    .doWhile(session => !session.contains("taskId") && session("counter").as[Int] < 20, "counter") {

      pause(60)

      .group("XUI_IAC_450_SelectCaseTask") {
        exec(http("XUI_IAC_450_SelectCaseTask_#{counter}")
          .post("/workallocation/case/task/#{caseId}")
          .headers(Headers.commonHeader)
          .header("Accept", "application/json, text/plain, */*")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(StringBody("""{"refined":true}"""))
          .check(jsonPath("$[?(@.type=='#{taskName}')].id").optional.saveAs("taskId"))
          .check(jsonPath("$[?(@.type=='#{taskName}')].type").optional.saveAs("taskType")))
      }
    }

    .doIf(session => !session.contains("taskId")) {
      exec { session =>
        println("Could not retrieve task after 20 attempts, exiting user...")
        println(s"Iteration ${session("counter").as[Int]}, caseId: ${session("caseId").as[String]}, taskId present: ${session.contains("taskId")}")
        session.markAsFailed
      }
        .exitHereIfFailed
    }

    .pause(MinThinkTime , MaxThinkTime )

    .group("XUI_IAC_460_AssignTaskToMe") {
      exec(http("XUI_IAC_460_AssignTaskToMe_Claim")
        .post("/workallocation/task/#{taskId}/claim")
        .headers(Headers.commonHeader)
        .header("content-type", "application/json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{}""")))
    }

    .pause(MinThinkTime , MaxThinkTime )

    .exec(_.setAll("currentTime" -> now.format(patternTimeNow),
      "currentDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

    .group("XUI_IAC_470_ViewQuery") {
      exec(Common.isAuthenticated)

      .exec(http("XUI_IAC_470_005_ViewQuery")
        .get("/data/internal/cases/#{caseId}/event-triggers/queryManagementRespondQuery?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.id").saveAs("raiseQueryId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.createdBy").saveAs("raiseQueryCreatedBy"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].value.createdOn").saveAs("raiseQueryCreatedOn"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[0].id").saveAs("raiseQueryParentId"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[1].value.id").saveAs("raiseQueryId1"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[1].value.createdBy").saveAs("raiseQueryCreatedBy1"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[1].id").saveAs("raiseQueryParentId1"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[2].value.id").saveAs("raiseQueryId2"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[2].value.createdOn").saveAs("raiseQueryCreatedOn2"))
        .check(jsonPath("$.case_fields[?(@.id=='qmLegalRepresentativeQueries')].value.caseMessages[2].id").saveAs("raiseQueryParentId2"))
        .check(jsonPath("$.event_token").saveAs("event_token")))
      }

      .pause(MinThinkTime , MaxThinkTime )

      .exec(_.setAll("currentTime" -> now.format(patternTimeNow),
                     "currentDate" -> DateUtils.getDateNow("yyyy-MM-dd")))

      .group("XUI_IAC_480_SubmitQueryResponse") {
        exec(http("XUI_IAC_480_005_SubmitQueryResponse")
          .post("/data/cases/#{caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/iac/IACRespondToFollowUpQuery.json")))

        .exec(http("XUI_IAC_480_005_CompleteTask")
          .post("/workallocation/task/#{taskId}/complete")
          .headers(Headers.commonHeader)
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(StringBody("""{"actionByEvent":true,"eventName":"Respond Query"}""")))
      }

      .pause(MinThinkTime , MaxThinkTime )
}