package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{Environment, ProbateHeader}

object EXUIProbateMC {


  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL

  val MinThinkTime = Environment.minThinkTimePROB
  val MaxThinkTime = Environment.maxThinkTimePROB


/*======================================================================================
*Business process : Following business process is for Probate Case Creation
======================================================================================*/

/*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are when click on create case on MC
======================================================================================*/


  val casecreation=group("XUI${service}_040_CreateCase") {
    exec(http("XUI${service}_040_CreateCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(ProbateHeader.headers_28)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304))).exitHereIfFailed
  }.pause(MinThinkTime, MaxThinkTime)
  
  /*======================================================================================
  *Business process : Following business process is for Probate Case Creation
  * Below group contains all the requests are when satrt create case for probate by selecting jurisdiction as probate and case type as GrantOfRepresentation
    ======================================================================================*/
      .group("XUI${service}_050_StartCreateCase1") {
        exec(http("XUI${service}_050_005_StartCreateCase1")
          .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
          .headers(ProbateHeader.headers_casecreate)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 304))
          .check(jsonPath("$.event_token").optional.saveAs("event_token_probate")))

          .exec(http("XUI${service}_050_010_StartCreateCase2")
            .get("/data/internal/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication?ignore-warning=false")
            .headers(ProbateHeader.headers_casecreate)
            .header("X-XSRF-TOKEN", "${XSRFToken}")
            .check(status.in(200, 304)))

          .exec(http("XUI${service}_050_015_CreateCaseProfile")
            .get("/data/internal/profile")
            .header("X-XSRF-TOKEN", "${XSRFToken}")
            .headers(ProbateHeader.headers_casefilter)
            .check(status.in(200, 304)))
          .exitHereIfFailed
      }
      .pause(MinThinkTime, MaxThinkTime)

 /*======================================================================================
   *Business process : Following business process is for Probate Case Creation
   * Below group contains all the requests are when create application
     ======================================================================================*/      .feed(Feeders.createCaseData)
      .group("XUI${service}_060_CreateApplication") {
        exec(http("XUI${service}_060_005_CreateApplication")
             .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage1")
             .headers(ProbateHeader.headers_casedata)
             .header("X-XSRF-TOKEN", "${XSRFToken}")
             .body(ElFileBody("ProbateCreateApplication.json")).asJson
             .check(status.in(200, 304)))

    .exec(http("XUI${service}_060_010_CreateApplication")
          .post("/data/internal/case-types/GrantOfRepresentation/drafts/")
          .headers(ProbateHeader.headers_draft)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .body(ElFileBody("ProbateCreateApplicationDraft.json")).asJson
          .check(status.in(200, 404, 201))
          .check(jsonPath("$.id").optional.saveAs("draftId")))

}
.pause(MinThinkTime, MaxThinkTime)

 /*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are address look up for applicant
  ======================================================================================*/ .group("XUI${service}_070_AddressLookup") {
  exec(http("XUI${service}_070_AddressLookup")
    .get("/api/addresses?postcode=TW33SD")
    .headers(ProbateHeader.headers_28)
    .header("X-XSRF-TOKEN", "${XSRFToken}")
    .check(status.in(200, 304)))
      }
      .pause(MinThinkTime, MaxThinkTime)


/*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are address look up for applicant
======================================================================================*/
.group("XUI${service}_080_CreateApplication2")
  {
    exec(http("XUI${service}_080_005_CreateApplication2")
         .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorCreateApplicationsolicitorCreateApplicationPage2")
         .headers(ProbateHeader.headers_casedata)
         .header("X-XSRF-TOKEN", "${XSRFToken}")
         .body(ElFileBody("ProbateCreateApplication2.json")).asJson
         .check(status.in(200, 304)))

    .exec(http("XUI${service}_080_010_Draft")
          .put("/data/internal/case-types/GrantOfRepresentation/drafts/${draftId}")
          .headers(ProbateHeader.headers_drfts)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .body(ElFileBody("ProbateApplication2Draft.json")).asJson
          .check(status.in(400,200,201)))

    .exec(http("XUI${service}_080_015_DraftProfile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.headers_casedataprofile)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304)))

  }.pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains adetails of case submission
======================================================================================*/

  .group("XUI${service}_090_CaseSubmitted") {
    exec(http("XUI${service}_090_005_CaseSubmitted")
         .post("/data/case-types/GrantOfRepresentation/cases?ignore-warning=false")
         .headers(ProbateHeader.headers_solappcreated)
         .header("X-XSRF-TOKEN", "${XSRFToken}")
         .body(ElFileBody("ProbateCaseSubmitted.json")).asJson
         .check(status.in(200, 304, 201))
         .check(jsonPath("$.id").optional.saveAs("caseId")))

    /*======================================================================================
*Business process : Following business process is for Probate Case Creation
* Below group contains all the requests are view case and progression bar tab
======================================================================================*/
    .exec(http("XUI${service}_090_010_ViewCase")
        .get("/data/internal/cases/${caseId}")
        .headers(ProbateHeader.headers_saveandviewcase)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 304))
        .check(jsonPath("$.state.name").is("Application created"))
        //.check(regex("Add solicitor details"))
    )
  }
    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Create Deceased Details Event
  ======================================================================================*/

  .group("XUI${service}_100_AddDeceasedDetails") {
    exec(http("XUI${service}_100_010_DeceasedDetails")
      .get("/data/internal/cases/${caseId}/event-triggers/solicitorUpdateApplication?ignore-warning=false")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.name").is("Deceased details")))

    .exec(http("XUI${service}_100_020_Profile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.user.idam.id")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Deceased Details - Name, DOD, DOB
  ======================================================================================*/

  .group("XUI${service}_100_SubmitNameDodDob") {
    exec(http("XUI${service}_100_010_SubmitNameDodDob")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage1")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateDeceasedDodDob.json")).asJson
      .check(substring("deceasedDateOfDeath")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Postcode Lookup
  ======================================================================================*/

  .group("XUI${service}_110_AddressLookup") {
    exec(http("XUI${service}_110_AddressLookup")
      .get("/api/addresses?postcode=TW33SD")
      .headers(ProbateHeader.headers_28)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304)))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Deceased Details - Name, DOD, DOB
  ======================================================================================*/

  .group("XUI${service}_120_SubmitNameDodDob") {
    exec(http("XUI${service}_120_010_SubmitNameDodDob")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage2")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateDeceasedAddressIHT.json")).asJson
      .check(substring("deceasedAddress")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Type of Application
  ======================================================================================*/

    .group("XUI${service}_130_SubmitTypeOfApplication") {
      exec(http("XUI${service}_130_010_SubmitTypeOfApplication")
        .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage3")
        .headers(ProbateHeader.probate_commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .body(ElFileBody("ProbateTypeOfApplication.json")).asJson
        .check(substring("solsWillType")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Estate in England or Wales
  ======================================================================================*/

  .group("XUI${service}_140_SubmitEnglandOrWales") {
    exec(http("XUI${service}_140_010_SubmitEnglandOrWales")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateApplicationsolicitorUpdateApplicationPage4")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateEnglandOrWales.json")).asJson
      .check(substring("appointExec")))

    .exec(http("XUI${service}_140_020_Profile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.user.idam.id")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Confirm deceased details - check your answers
  ======================================================================================*/

  .group("XUI${service}_150_ConfirmDeceasedDetails") {
    exec(http("XUI${service}_150_010_ConfirmDeceasedDetails")
      .post("/data/cases/${caseId}/events")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateConfirmDeceasedDetails.json")).asJson
      .check(jsonPath("$.state").is("SolProbateCreated")))

    .exec(http("XUI${service}_150_020_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.state.name").is("Grant of probate created")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Create Grant of Probate Details Event
  ======================================================================================*/

  .group("XUI${service}_160_GrantOfProbateDetails") {
    exec(http("XUI${service}_160_010_GrantOfProbateDetails")
      .get("/data/internal/cases/${caseId}/event-triggers/solicitorUpdateProbate?ignore-warning=false")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.name").is("Grant of probate details")))

    .exec(http("XUI${service}_160_020_Profile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.user.idam.id")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Grant of Probate Details
  ======================================================================================*/

  .group("XUI${service}_170_SubmitGrantOfProbateDetails") {
    exec(http("XUI${service}_170_010_SubmitGrantOfProbateDetails")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage1")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateGrantOfProbateDetails.json")).asJson
      .check(substring("solicitorIsMainApplicant")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Postcode Lookup
  ======================================================================================*/

  .group("XUI${service}_110_AddressLookup") {
    exec(http("XUI${service}_110_AddressLookup")
      .get("/api/addresses?postcode=TW33SD")
      .headers(ProbateHeader.headers_28)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(status.in(200, 304)))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Executor Details
  ======================================================================================*/

  .group("XUI${service}_180_SubmitExecutorDetails") {
    exec(http("XUI${service}_180_010_SubmitExecutorDetails")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage2")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateExecutorDetails.json")).asJson
      .check(substring("additionalApplying")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Extra Information
  ======================================================================================*/

  .group("XUI${service}_190_SubmitExtraInfo") {
    exec(http("XUI${service}_190_010_SubmitExtraInfo")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorUpdateProbatesolicitorUpdateProbatePage3")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateExtraInfo.json")).asJson
      .check(substring("solsAdditionalInfo")))

    .exec(http("XUI${service}_190_020_Profile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.user.idam.id")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Confirm grant of probate details - check your answers
  ======================================================================================*/

  .group("XUI${service}_200_ConfirmGrantOfProbateDetails") {
    exec(http("XUI${service}_200_010_ConfirmGrantOfProbateDetails")
      .post("/data/cases/${caseId}/events")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateConfirmGrantOfProbateDetails.json")).asJson
      .check(jsonPath("$.state").is("SolAppUpdated")))

    .exec(http("XUI${service}_200_020_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.state.name").is("Application updated")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Create Complete Application Event
  ======================================================================================*/

  .group("XUI${service}_210_CompleteAppliction") {
    exec(http("XUI${service}_210_010_CompleteApplication")
      .get("/data/internal/cases/${caseId}/event-triggers/solicitorReviewAndConfirm?ignore-warning=false")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.case_fields[?(@.id=='solsLegalStatementDocument')].value.document_url").saveAs("documentURL"))
      .check(jsonPath("$.id").is("solicitorReviewAndConfirm")))

    .exec(http("XUI${service}_210_020_Profile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.user.idam.id")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Grant of Probate Details
  ======================================================================================*/

  .group("XUI${service}_220_SubmitLegalStatement") {
    exec(http("XUI${service}_220_010_SubmitLegalStatement")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateLegalStatement.json")).asJson
      .check(substring("solsLegalStatementDocument")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit IHT Submission Date
  ======================================================================================*/

  .group("XUI${service}_230_SubmitIHTSubmissionDate") {
    exec(http("XUI${service}_230_010_SubmitIHTSubmissionDate")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorReviewLegalStatementPage2")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateIHTSubmissionDate.json")).asJson
      .check(jsonPath("$.data.state").is("SolAppUpdated")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Solicitor Confirmation
  ======================================================================================*/

  .group("XUI${service}_240_SubmitSolicitorConfirmation") {
    exec(http("XUI${service}_240_010_SubmitSolicitorConfirmation")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorReviewLegalStatementPage3")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateSolicitorConfirmation.json")).asJson
      .check(substring("""{"data":{}""")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Solicitor Details
  ======================================================================================*/

  .group("XUI${service}_250_SubmitSolicitorDetails") {
    exec(http("XUI${service}_250_010_SubmitSolicitorDetails")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorConfirmPage1")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateSolicitorDetails.json")).asJson
      .check(substring("solsSOTJobTitle")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Extra Copies
  ======================================================================================*/

  .group("XUI${service}_260_SubmitExtraCopies") {
    exec(http("XUI${service}_260_010_SubmitExtraCopies")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorConfirmPage2")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateExtraCopies.json")).asJson
      .check(substring("extraCopiesOfGrant")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Payment Method
  ======================================================================================*/

  .group("XUI${service}_270_SubmitPaymentMethod") {
    exec(http("XUI${service}_270_010_SubmitPaymentMethod")
      .post("/data/case-types/GrantOfRepresentation/validate?pageId=solicitorReviewAndConfirmsolicitorConfirmPage3")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbatePaymentMethod.json")).asJson
      .check(substring("solsPaymentMethods")))

    .exec(http("XUI${service}_270_020_Profile")
      .get("/data/internal/profile")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.user.idam.id")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Submit Application
  ======================================================================================*/

  .group("XUI${service}_280_SubmitApplication") {
    exec(http("XUI${service}_280_010_SubmitApplication")
      .post("/data/cases/${caseId}/events")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(ElFileBody("ProbateSubmitApplication.json")).asJson
      .check(jsonPath("$.state").is("CaseCreated")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
  * Return to Case
  ======================================================================================*/

  .group("XUI${service}_290_ViewCase") {
    exec(http("XUI${service}_290_010_ViewCase")
      .get("/data/internal/cases/${caseId}")
      .headers(ProbateHeader.probate_commonHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .check(jsonPath("$.state.id").is("CaseCreated")))
  }

  .pause(MinThinkTime, MaxThinkTime)

}
