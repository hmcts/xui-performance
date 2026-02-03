package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utilities.DateUtils
import utils.{Common, Environment, Headers}

/*======================================================================================
* Create a new FR application as a professional user (e.g. solicitor) for a Contested Divorce case
======================================================================================*/

object Solicitor_FR_Contested {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*======================================================================================
  * Click the Create Case link
  ======================================================================================*/
  val CreateFRCase = {

      exec(
        _.setAll(
          //marriage date between 5 and 10 years ago
          "dateOfMarriage" -> DateUtils.getDatePastRandom("yyyy-MM-dd", minYears = 5, maxYears = 10),
          //separation date between 2 and 5 years ago, after marriage date
          "dateOfSeparation" -> DateUtils.getDatePastRandom("yyyy-MM-dd", minYears = 2, maxYears = 5),
          //Divorce Petition Issue date between 1 and 2 years ago and after separation date
          "divorcePetitionIssueDate" -> DateUtils.getDatePastRandom("yyyy-MM-dd", minYears = 1, maxYears = 2)
        )
      )
    .group("XUI_FR_Contested_010_CreateCase") {
      exec(
        http("XUI_FR_Contested_010_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(substring("DIVORCE"))
      )
    }
      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_FR_Contested_020_SelectCaseType") {
        exec(http("XUI_FR_Contested_020_005_StartApplication")
          .get("/data/internal/case-types/FinancialRemedyContested/event-triggers/FR_solicitorCreate?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(jsonPath("$.id").is("FR_solicitorCreate"))
        )
          .exec(Common.profile)

          .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
       * Click Continue
       ======================================================================================*/

      .group("XUI_FR_Contested_030_ContinueToApplication") {
        exec(http("XUI_FR_030_005_ContinueToApplication")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate1")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRContinueToApplication.json"))
          .check(jsonPath("$.data.applicantSolicitorFirm").saveAs("firmName"))
          .check(jsonPath("$.data.solicitorReference").saveAs("firmRef"))
          .check(jsonPath("$.data.applicantSolicitorAddress.AddressLine1").saveAs("firmAddress1"))
          .check(jsonPath("$.data.applicantSolicitorAddress.AddressLine2").saveAs("firmAddress2"))
          .check(jsonPath("$.data.applicantSolicitorAddress.AddressLine3").saveAs("firmAddress3"))
          .check(jsonPath("$.data.applicantSolicitorAddress.PostTown").saveAs("firmPostTown"))
          .check(jsonPath("$.data.applicantSolicitorAddress.County").saveAs("firmCounty"))
          .check(jsonPath("$.data.applicantSolicitorAddress.PostCode").saveAs("firmPostcode"))
          .check(jsonPath("$.data.solicitorReference").is("#{orgref}"))
          .check(jsonPath("$.data.applicantSolicitorFirm").is("#{orgname}"))
        )

          //select applicant and respondent solicitor orgs now, as this call will be retrieved from cache in future
          .exec(http("XUI_FR_Contested_030_010_GetOrgs")
            .get("/api/caseshare/orgs")
            .headers(Headers.commonHeader)
            .header("accept", "application/json, text/plain, */*")
            .check(regex(""""name":"(.+?)","organisationIdentifier":"([0-9A-Z]+?)"""").ofType[(String, String)].findRandom.saveAs("applicantOrgs"))
            .check(regex(""""name":"(.+?)","organisationIdentifier":"([0-9A-Z]+?)"""").ofType[(String, String)].findRandom.saveAs("respondentOrgs")))
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
       * Complete Applicant Solicitor details (some are pre-populated) and click Continue
       ======================================================================================*/

      .group("XUI_FR_Contested_040_AddSolicitorDetails") {
        exec(http("XUI_FR_Contested_040_005_AddSolicitorDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate2")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRAddSolicitorDetails.json"))
          .check(substring("applicantSolicitorConsentForEmails"))
          .check(jsonPath("$.data.ApplicantOrganisationPolicy.Organisation.OrganisationID").is("#{applicantOrgs(1)}"))
          .check(jsonPath("$.data.ApplicantOrganisationPolicy.Organisation.OrganisationName").is("#{applicantOrgs(0)}"))
          .check(jsonPath("$.data.applicantSolicitorFirm").is("#{firmName}"))
        )

      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Complete Divorce case details (FamilyMan code = EZ12D91234) and click Continue
      ======================================================================================*/
      .exec(Common.uploadFile("3MB.pdf", "PUBLIC", "#{caseType}", "DIVORCE", "Petition"))

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_FR_Contested_050_AddDivorceCaseDetails") {
        exec(
          http("XUI_FR_Contested_050_010_AddDivorceCaseDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate3")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRAddDivorceCaseDetails.json"))
          .check(jsonPath("$.data.divorceStageReached").is("Petition Issued"))
          .check(jsonPath("$.data.divorceUploadPetition.document_hash").is("#{PetitionDocumentHashToken}"))
        )
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
        * Enter the Applicant's details and click Continue
        ======================================================================================*/

      .exec(Common.postcodeLookup)

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_FR_Contested_060_AddApplicantDetails") {
        exec(http("XUI_FR_Contested_060_005_AddApplicantDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate4")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRAddApplicantDetails.json"))
          .check(jsonPath("$.data.applicantFMName").is("ApplicantPerf"))
          .check(jsonPath("$.data.applicantAddress.AddressLine1").saveAs("applicantAddress1"))
          .check(jsonPath("$.data.applicantAddress.AddressLine2").saveAs("applicantAddress2"))
          .check(jsonPath("$.data.applicantAddress.AddressLine3").saveAs("applicantAddress3"))
          .check(jsonPath("$.data.applicantAddress.PostTown").saveAs("applicantPostTown"))
          .check(jsonPath("$.data.applicantAddress.County").saveAs("applicantCounty"))
          .check(jsonPath("$.data.applicantAddress.PostCode").saveAs("applicantPostcode"))
        )
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Enter the Respondent's details and click Continue
      ======================================================================================*/

      .group("XUI_FR_Contested_070_AddRespondentDetails") {
        exec(http("XUI_FR__Contested_070_005_AddRespondentDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate6")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRAddRespondentDetails.json"))
          .check(jsonPath("$.data.respondentFMName").is("RespondentPerf"))
        )
      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
      * Enter the Respondent Solicitor's details and click Continue
      ======================================================================================*/
      .exec(Common.postcodeLookup)

      .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_FR_Contested_080_AddRespondentSolicitorDetails") {

        exec(
          http("XUI_FR_Contested_080_005_AddRespondentSolicitorDetails")
            .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate7" )
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/fr/contested/FRAddRespondentSolicitorDetails.json"))
            .check(jsonPath("$.data.ApplicantOrganisationPolicy.Organisation.OrganisationID").is("#{applicantOrgs(1)}"))
            .check(jsonPath("$.data.rSolicitorAddress.AddressLine1").saveAs("respondentSolicitorAddress1"))
            .check(jsonPath("$.data.rSolicitorAddress.AddressLine2").saveAs("respondentSolicitorAddress2"))
            .check(jsonPath("$.data.rSolicitorAddress.AddressLine3").saveAs("respondentSolicitorAddress3"))
            .check(jsonPath("$.data.rSolicitorAddress.PostTown").saveAs("respondentSolicitorPostTown"))
            .check(jsonPath("$.data.rSolicitorAddress.County").saveAs("respondentSolicitorCounty"))
            .check(jsonPath("$.data.rSolicitorAddress.PostCode").saveAs("respondentSolicitorPostcode"))
        )
      }

        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Select All checkbox as the Nature of the Application and click Continue
        ======================================================================================*/

        .group("XUI_FR_Contested_090_AddNatureOfApplication") {
          exec(http("XUI_FR_Contested_090_005_AddNatureOfApplication")
            .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate8")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/fr/contested/FRAddNatureOfApplication.json"))
            .check(jsonPath("$.data.natureOfApplicationChecklist[0]").is("Maintenance Pending Suit")))
        }

        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Property Adjustment order details and click Continue
        =====================================================================================*/

        .group("XUI_FR_Contested_100_AddPropertyAdjustmentOrderDetails") {
          exec(http("XUI_FR_Contested_100_005_AddPropertyAdjustmentOrderDetails")
            .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate9")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/fr/contested/FRAddPropertyAdjustment.json"))
            .check(jsonPath("$.data.additionalPropertyOrderDecision").is("Yes"))
            .check(jsonPath("$.data.propertyAddress").is("prop1"))
          )
        }

        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Periodical Payments order and click Continue
        ======================================================================================*/

          .group("XUI_FR_Contested_110_AddPeriodicalPaymentsOrderDetails") {
            exec(http("XUI_FR_Contested_110_005_AddPeriodicalPaymentsOrderDetails")
              .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate10")
              .headers(Headers.commonHeader)
              .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
              .header("x-xsrf-token", "#{XSRFToken}")
              .body(ElFileBody("bodies/fr/contested/FRPeriodicPayment.json"))
              .check(jsonPath("$.data.paymentForChildrenDecision").is("Yes"))
            )
          }
        .pause(MinThinkTime, MaxThinkTime)

       /*======================================================================================
        * Written Agreement for benefit of children and click Continue
        ======================================================================================*/

       .group("XUI_FR_Contested_120_AddWrittenAgreementDetails") {
         exec(http("XUI_FR_Contested_120_005_AddWrittenAgreementDetails")
           .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate11")
           .headers(Headers.commonHeader)
           .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
           .header("x-xsrf-token", "#{XSRFToken}")
           .body(ElFileBody("bodies/fr/contested/FRWrittenAgreement.json"))
           .check(jsonPath("$.data.benefitForChildrenDecision").is("No"))
         )
       }
        .pause(MinThinkTime, MaxThinkTime)

        /*======================================================================================
        * Fast Track Procedure and click Continue
        ======================================================================================*/

        .group("XUI_FR_Contested_130_AddFastTrackProcedureDetails") {
          exec(http("XUI_FR_Contested_130_005_AddFastTrackProcedureDetails")
            .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate13")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
            .header("x-xsrf-token", "#{XSRFToken}")
            .body(ElFileBody("bodies/fr/contested/FRFastTrack.json"))
            .check(jsonPath("$.data.fastTrackDecision").is("No"))
          )
        }
        .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Financial Remedy details and click Continue
      ======================================================================================*/

      .group("XUI_FR_Contested_140_AddFinancialRemedyDetails") {
        exec(http("XUI_FR_Contested_140_005_AddFinancialRemedyDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate14")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRFinancialRemedy.json"))
          .check(jsonPath("$.data.estimatedAssetsChecklistV2").is("underOneMillionPounds"))
        )
      }
        .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Select Financial Remedy Court and click Continue
      ======================================================================================*/

      .group("XUI_FR_Contested_150_AddFinancialRemedyCourtDetails") {
        exec(http("XUI_FR_Contested_150_005_AddFinancialRemedyCourtDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate15")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRFinancialRemedyCourt.json"))
          .check(jsonPath("$.data.regionList").is("northwest"))
          .check(jsonPath("$.data.propertyAdjutmentOrderDetail[0].id").saveAs("propertyId"))
        )
      }
        .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Attended MIAM and click Continue
      ======================================================================================*/

      .group("XUI_FR_Contested_160_AddAttendedMIAMDetails") {
        exec(http("XUI_FR_Contested_160_005_AddAttendedMIAMDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate17")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRAttendedMIAM.json"))
          .check(jsonPath("$.data.applicantAttendedMIAM").is("Yes"))
        )
      }
        .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Add MIAM certification details and click Continue
      ======================================================================================*/

      .exec(Common.uploadFile("3MB.pdf", "PUBLIC", "FinancialRemedyContested", "DIVORCE", "Miam"))

        .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_FR_Contested_170_AddMIAMCertificationDetails") {
        exec(http("XUI_FR_Contested_170_005_AddMIAMCertificationDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate23")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRAddMIAMDetails.json"))
          .check(jsonPath("$.data.soleTraderName").is("Wizard divorce service"))
        )
      }
        // Adding additional pauses to avoid http 429 too many requests error from document management service
        .pause(MinThinkTime, MaxThinkTime)
        .pause(MinThinkTime, MaxThinkTime)
        .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
       * Add Variation order details and click Continue
       ======================================================================================*/

      .exec(Common.uploadFile("3MB.pdf", "PUBLIC", "FinancialRemedyContested", "DIVORCE", "Variation"))

        // Adding additional pauses to avoid http 429 too many requests error from document management service
        // (manually setting a higher pause time is overriden by the simulation configuration when running in a pipeline)
        .pause(MinThinkTime, MaxThinkTime)
        .pause(MinThinkTime, MaxThinkTime)
        .pause(MinThinkTime, MaxThinkTime)
        .pause(MinThinkTime, MaxThinkTime)
        .pause(MinThinkTime, MaxThinkTime)

      .exec(Common.uploadFile("3MB.pdf", "PUBLIC", "FinancialRemedyContested", "DIVORCE", "OtherDocument"))

        .pause(MinThinkTime, MaxThinkTime)

      .group("XUI_FR_Contested_180_AddVariationOrderDetails") {

        exec(http("XUI_FR_Contested_180_005_AddVariationOrderDetails")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate24")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRVariationOrderDetails.json"))
          .check(jsonPath("$.data.uploadAdditionalDocument[0].value.additionalDocumentType").is("noticeOfActing"))
        )
      }
        .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
       * Saving the Case and click Continue to Check your answers page
       ======================================================================================*/

      .group("XUI_FR_Contested_190_SaveCase") {
        exec(http("XUI_FR_Contested_190_005_SaveCase")
          .post("/data/case-types/FinancialRemedyContested/validate?pageId=FR_solicitorCreate25")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRSaveCase.json"))
          .check(jsonPath("$.data.consentOrderFRCName").saveAs("Liverpool Civil And Family Court"))
        )
          .exec(Common.caseShareOrgs)
      }
        .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
       *  Review Application - click Submit
       ======================================================================================*/

      .group("XUI_FR_Contested_200_ReviewAndSubmitApplication") {
        exec(http("XUI_FR_Contested_200_005_ReviewAndSubmitApplication")
          .post("/data/case-types/FinancialRemedyContested/cases?ignore-warning=false")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
          .header("x-xsrf-token", "#{XSRFToken}")
          .body(ElFileBody("bodies/fr/contested/FRSubmitApplication.json"))
          .check(jsonPath("$.id").saveAs("caseId"))
          .check(jsonPath("$.state").is("caseAdded"))
        )
          .exec(http("XUI_FR_Contested_200_010_ViewCase")
            .get("/data/internal/cases/#{caseId}")
            .headers(Headers.commonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .check(jsonPath("$.case_id").is("#{caseId}"))
            .check(jsonPath("$.case_type.id").is("#{caseType}"))
          )
          .exec(Common.caseShareOrgs)
          .exec(
            http("XUI_FR_Contested_200_015_WAJurisdictionsGet")
            .get("/api/wa-supported-jurisdiction/get")
            .headers(Headers.commonHeader)
          )
      }

      .pause(MinThinkTime, MaxThinkTime)

        .exec(
          session => {
            println(session)
            session
          }
        )
  }
}
