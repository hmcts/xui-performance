package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.response.Response

object MakeAClaimCreateCase {

  val StartCreateCase =
    group("MakeAClaim_02_StartCreateCase") {
      exec(http("MakeAClaim_CreateCase_Start")
        .get("/data/internal/case-types/PCS/event-triggers/createPossessionClaim?ignore-warning=false")
        .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .header("experimental", "true")
        .check(status.is(200))
        .check(jsonPath("$.event_token").saveAs("eventToken")))
    }

  val ContinueMakeAClaim =
    group("MakeAClaim_03_ContinueMakeAClaim") {
      exec(http("MakeAClaim_Continue_StartTheService")
        .post("/data/case-types/PCS/validate?pageId=createPossessionClaimstartTheService")
        .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("Content-Type", "application/json")
        .header("experimental", "true")
        .header("X-XSRF-TOKEN", "#{XSRFToken}")
        .body(StringBody("""{
          "data": { "feeAmount": "£415" },
          "event": { "id": "createPossessionClaim", "summary": "", "description": "" },
          "event_data": { "feeAmount": "£415" },
          "event_token": "#{eventToken}",
          "ignore_warning": false
        }"""))
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
   val EnterEnglandPostcode =
    group("MakeAClaim_04_EnterEnglandPostcode") {
      exec(http("MakeAClaim_AddressLookup")
  .get("/api/addresses?postcode=W37RX")
  .header("Accept", "application/json")
  .check(status.is(200)))
    
    }
    val EnterPropertyAddress =
  group("MakeAClaim_05_EnterPropertyAddress") {
    exec(http("MakeAClaim_Validate_PropertyAddress")
      .post("/data/case-types/PCS/validate?pageId=createPossessionClaimenterPropertyAddress")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "legislativeCountry": null,
          "propertyAddress": {
            "AddressLine1": "1 Second Avenue",
            "AddressLine2": "",
            "AddressLine3": "",
            "PostTown": "London",
            "County": "",
            "Country": "United Kingdom",
            "PostCode": "W3 7RX"
          }
        },
        "event": {
          "id": "createPossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "feeAmount": "£415",
          "legislativeCountry": null,
          "propertyAddress": {
            "AddressLine1": "1 Second Avenue",
            "AddressLine2": "",
            "AddressLine3": "",
            "PostTown": "London",
            "County": "",
            "Country": "United Kingdom",
            "PostCode": "W3 7RX"
          }
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
val CheckYourAnswers =
  group("MakeAClaim_06_CheckYourAnswers") {
    exec(http("MakeAClaim_CreateCase")
      .post("/data/case-types/PCS/cases?ignore-warning=false")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "feeAmount": "£415",
          "legislativeCountry": "England",
          "propertyAddress": {
            "AddressLine1": "1 Second Avenue",
            "AddressLine2": "",
            "AddressLine3": "",
            "PostTown": "London",
            "County": "",
            "Country": "United Kingdom",
            "PostCode": "W3 7RX"
          }
        },
        "draft_id": null,
        "event": {
          "id": "createPossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false
      }"""))
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("caseId")))

      .exec { session =>
      println("CREATED caseId=" + session("caseId").as[String])
      session
    }
  }
  val PostCreateRoleSetup =
  group("MakeAClaim_07_PostCreateRoleSetup") {
    exec(http("MakeAClaim_ManageLabellingRoleAssignment")
      .post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("{}"))
      .check(status.in(200, 204)))
  }
  val ContinueNextSteps =
  group("MakeAClaim_08_ContinueNextSteps") {
    exec(http("MakeAClaim_NextSteps_Profile")
      .get("/data/internal/profile")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .check(status.is(200)))

    .exec(http("MakeAClaim_NextSteps_ResumePossessionClaim")
      .get("/data/internal/cases/#{caseId}/event-triggers/resumePossessionClaim?ignore-warning=false")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .header("experimental", "true")
      .check(status.is(200))
      .check(jsonPath("$.event_token").saveAs("eventToken")))
  }
  val ClaimantName =
  group("MakeAClaim_09_ClaimantName") {
    exec(http("MakeAClaim_ClaimantName")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimclaimantInformation")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val ClaimantType =
  group("MakeAClaim_10_ClaimantType") {
    exec(http("MakeAClaim_ClaimantType")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimselectClaimantType")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "legislativeCountry": "England",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ]
          }
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ]
          }
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val TrespassClaim =
  group("MakeAClaim_11_TrespassClaim") {
    exec(http("MakeAClaim_TrespassClaim")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimselectClaimType")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "claimAgainstTrespassers": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          }
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val ContactPreferences =
  group("MakeAClaim_12_ContactPreferences") {
    exec(http("MakeAClaim_ContactPreferences")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimcontactPreferences")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val DefendantDetails =
  group("MakeAClaim_13_DefendantDetails") {
    exec(http("MakeAClaim_DefendantDetails")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimdefendantsDetails")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val UploadTenancyDocument =
  group("MakeAClaim_14_UploadTenancyDocument") {
    exec(http("MakeAClaim_UploadTenancyDocument")
      .post("/documentsv2")
      .header("Accept", "application/json, text/plain, */*")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .bodyPart(
        RawFileBodyPart("files", "#{uploadFilePath}")
          .fileName("#{uploadFileName}")
          .contentType("#{uploadMimeType}")
      )
      .bodyPart(StringBodyPart("classification", "PUBLIC"))
      .bodyPart(StringBodyPart("caseTypeId", "PCS"))
      .bodyPart(StringBodyPart("jurisdictionId", "PCS"))
      .asMultipartForm
      .check(status.is(200))
      .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentUrl"))
      .check(jsonPath("$.documents[0]._links.binary.href").saveAs("documentBinaryUrl"))
      .check(jsonPath("$.documents[0].originalDocumentName").saveAs("documentFilename"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("documentHash")))
  }
  val TenancyLicenceDetails =
  group("MakeAClaim_15_TenancyLicenceDetails") {
    exec(http("MakeAClaim_TenancyLicenceDetails")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimtenancyLicenceDetails")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ]
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ]
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val GroundsForPossession =
  group("MakeAClaim_16_GroundsForPossession") {
    exec(http("MakeAClaim_GroundsForPossession")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimgroundsForPossession")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "claimDueToRentArrears": "No"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val AssuredNoArrearsGrounds =
  group("MakeAClaim_17_AssuredNoArrearsGrounds") {
    exec(http("MakeAClaim_AssuredNoArrearsGrounds")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimassuredNoArrearsGroundsForPossession")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "showRentSectionPage": null,
          "noRentArrears_ShowGroundReasonPage": null,
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": []
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": null,
          "noRentArrears_ShowGroundReasonPage": null,
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": []
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val ReasonsForPossession =
  group("MakeAClaim_18_ReasonsForPossession") {
    exec(http("MakeAClaim_ReasonsForPossession")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimnoRentArrearsGroundsForPossessionReason")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "assuredNoArrearsReasons_OwnerOccupier": "Test"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": null,
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val PreActionProtocol =
  group("MakeAClaim_19_PreActionProtocol") {
    exec(http("MakeAClaim_PreActionProtocol")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimPreActionProtocol")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "preActionProtocolCompleted": "YES"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": null,
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val MediationAndSettlement =
  group("MakeAClaim_20_MediationAndSettlement") {
    exec(http("MakeAClaim_MediationAndSettlement")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimMediationAndSettlement")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "mediationAttempted": "NO",
          "settlementAttempted": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": null,
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val CheckingNotice =
  group("MakeAClaim_21_CheckingNotice") {
    exec(http("MakeAClaim_CheckingNotice")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimCheckingNotice")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "noticeServed": "No"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val ClaimantCircumstances =
  group("MakeAClaim_22_ClaimantCircumstances") {
    exec(http("MakeAClaim_ClaimantCircumstances")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimclaimantCircumstances")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val DefendantCircumstances =
  group("MakeAClaim_23_DefendantCircumstances") {
    exec(http("MakeAClaim_DefendantCircumstances")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimdefendantCircumstances")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "hasDefendantCircumstancesInfo": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val AlternativesToPossession =
  group("MakeAClaim_24_AlternativesToPossession") {
    exec(http("MakeAClaim_AlternativesToPossession")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimalternativesToPossession")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "suspensionOfRTB_ShowHousingActsPage": null,
          "demotionOfTenancy_ShowHousingActsPage": null,
          "suspensionToBuyDemotionOfTenancyPages": null,
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"]
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": null,
          "demotionOfTenancy_ShowHousingActsPage": null,
          "suspensionToBuyDemotionOfTenancyPages": null,
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"]
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val HousingAct =
  group("MakeAClaim_25_HousingAct") {
    exec(http("MakeAClaim_HousingAct")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimsuspensionOfRightToBuyHousingActOptions")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "suspensionOfRTB_HousingAct": "SECTION_82A_2"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
val ReasonsForRequestingASuspension = 
  group("MakeAClaim_26_ReasonsForRequestingASuspension") {
    exec(http("MakeAClaim_ReasonsForRequestingASuspension")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimsuspensionOfRightToBuyOrderReason")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "suspensionOfRTB_Reason": "Test"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val AdditionalReasonsForPossession =
  group("MakeAClaim_27_AdditionalReasonsForPossession") {
    exec(http("MakeAClaim_AdditionalReasonsForPossession")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimadditionalReasonsForPossession")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          }
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          }
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val UnderlesseeOrMortgagee =
  group("MakeAClaim_28_UnderlesseeOrMortgagee") {
    exec(http("MakeAClaim_UnderlesseeOrMortgagee")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimunderlesseeMortgageeEntitledToClaimRelief")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "hasUnderlesseeOrMortgagee": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          },
          "hasUnderlesseeOrMortgagee": "NO"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val UploadAdditionalDocuments =
  group("MakeAClaim_29_UploadAdditionalDocuments") {
    exec(http("MakeAClaim_UploadAdditionalDocuments")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimwantToUploadDocuments")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "wantToUploadDocuments": "NO"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          },
          "hasUnderlesseeOrMortgagee": "NO",
          "wantToUploadDocuments": "NO"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val Applications =
  group("MakeAClaim_30_Applications") {
    exec(http("MakeAClaim_Applications")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimgeneralApplication")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "applicationWithClaim": "YES"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          },
          "hasUnderlesseeOrMortgagee": "NO",
          "wantToUploadDocuments": "NO",
          "applicationWithClaim": "YES"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val LanguageUsed =
  group("MakeAClaim_31_LanguageUsed") {
    exec(http("MakeAClaim_LanguageUsed")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimlanguageUsed")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "languageUsed": "ENGLISH"
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          },
          "hasUnderlesseeOrMortgagee": "NO",
          "wantToUploadDocuments": "NO",
          "applicationWithClaim": "YES",
          "languageUsed": "ENGLISH"
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val CompletingYourClaim =
  group("MakeAClaim_32_CompletingYourClaim") {
    exec(http("MakeAClaim_CompletingYourClaim")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimcompletingYourClaim")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "completionNextStep": "SUBMIT_AND_PAY_NOW",
          "endButtonLabel": null
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          },
          "hasUnderlesseeOrMortgagee": "NO",
          "wantToUploadDocuments": "NO",
          "applicationWithClaim": "YES",
          "languageUsed": "ENGLISH",
          "completionNextStep": "SUBMIT_AND_PAY_NOW",
          "endButtonLabel": null
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val StatementOfTruth =
  group("MakeAClaim_33_StatementOfTruth") {
    exec(http("MakeAClaim_StatementOfTruth")
      .post("/data/case-types/PCS/validate?pageId=resumePossessionClaimstatementOfTruth")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "statementOfTruth": {
            "completedBy": "CLAIMANT",
            "fullNameParty": "Samson",
            "positionParty": "Lure",
            "agreementClaimant": ["BELIEVE_TRUE"],
            "agreementDefendantLegalRep": []
          }
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimAgainstTrespassers": "NO",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO"
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO"
          },
          "hasUnderlesseeOrMortgagee": "NO",
          "wantToUploadDocuments": "NO",
          "applicationWithClaim": "YES",
          "languageUsed": "ENGLISH",
          "completionNextStep": "SUBMIT_AND_PAY_NOW",
          "endButtonLabel": "Submit claim",
          "statementOfTruth": {
            "completedBy": "CLAIMANT",
            "fullNameParty": "Samson",
            "positionParty": "Lure",
            "agreementClaimant": ["BELIEVE_TRUE"],
            "agreementDefendantLegalRep": []
          }
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false,
        "case_reference": "#{caseId}"
      }"""))
      .check(status.is(200))
      .check(jsonPath("$.event_token").optional.saveAs("eventToken")))
  }
  val SubmitClaim =
  group("MakeAClaim_34_SubmitClaim") {
    exec(http("MakeAClaim_SubmitClaim")
      .post("/data/cases/#{caseId}/events")
      .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
      .header("Content-Type", "application/json")
      .header("experimental", "true")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .body(StringBody("""{
        "data": {
          "regionId": "1",
          "caseManagementLocationNumber": "20262",
          "orgNameFound": "Yes",
          "claimantName": "Possessions Claim Solicitor Org",
          "isClaimantNameCorrect": "YES",
          "legislativeCountry": "England",
          "claimantType": {
            "value": {
              "code": "PROVIDER_OF_SOCIAL_HOUSING",
              "label": "Registered provider of social housing or local authority"
            },
            "list_items": [
              { "code": "PRIVATE_LANDLORD", "label": "Private landlord" },
              { "code": "PROVIDER_OF_SOCIAL_HOUSING", "label": "Registered provider of social housing or local authority" },
              { "code": "MORTGAGE_LENDER", "label": "Mortgage lender" },
              { "code": "OTHER", "label": "Other" }
            ],
            "valueCode": "PROVIDER_OF_SOCIAL_HOUSING"
          },
          "claimAgainstTrespassers": "NO",
          "claimantContactEmail": "#{user}",
          "isCorrectClaimantContactEmail": "YES",
          "orgAddressFound": "Yes",
          "organisationAddress": {
            "AddressLine1": "Ministry Of Justice",
            "AddressLine2": "Seventh Floor 102 Petty France",
            "PostTown": "London",
            "PostCode": "SW1H 9AJ",
            "Country": "United Kingdom"
          },
          "formattedClaimantContactAddress": "Ministry Of Justice<br>Seventh Floor 102 Petty France<br>London<br>SW1H 9AJ",
          "isCorrectClaimantContactAddress": "YES",
          "claimantProvidePhoneNumber": "NO",
          "defendant1": {
            "nameKnown": "YES",
            "firstName": "John",
            "lastName": "Smith",
            "addressKnown": "NO",
            "addressSameAsPossession": null,
            "correspondenceAddress": {
              "AddressLine1": null,
              "AddressLine2": null,
              "AddressLine3": null,
              "PostTown": null,
              "County": null,
              "Country": null,
              "PostCode": null
            }
          },
          "addAnotherDefendant": "NO",
          "tenancy_TypeOfTenancyLicence": "ASSURED_TENANCY",
          "tenancy_TenancyLicenceDate": null,
          "tenancy_HasCopyOfTenancyLicence": "YES",
          "tenancy_TenancyLicenceDocuments": [
            {
              "id": null,
              "value": {
                "document_url": "#{documentUrl}",
                "document_binary_url": "#{documentBinaryUrl}",
                "document_filename": "#{documentFilename}",
                "document_hash": "#{documentHash}"
              }
            }
          ],
          "claimDueToRentArrears": "No",
          "showRentSectionPage": "No",
          "noRentArrears_ShowGroundReasonPage": "Yes",
          "noRentArrears_MandatoryGrounds": ["OWNER_OCCUPIER_GROUND1"],
          "noRentArrears_DiscretionaryGrounds": [],
          "noRentArrears_OtherGround": [],
          "assuredNoArrearsReasons_OwnerOccupier": "Test",
          "preActionProtocolCompleted": "YES",
          "mediationAttempted": "NO",
          "settlementAttempted": "NO",
          "noticeServed": "No",
          "claimantNamePossessiveForm": "Possessions Claim Solicitor Org’s",
          "claimantCircumstancesSelect": "NO",
          "hasDefendantCircumstancesInfo": "NO",
          "suspensionOfRTB_ShowHousingActsPage": "Yes",
          "demotionOfTenancy_ShowHousingActsPage": "No",
          "suspensionToBuyDemotionOfTenancyPages": "No",
          "alternativesToPossession": ["SUSPENSION_OF_RIGHT_TO_BUY"],
          "suspensionOfRTB_HousingAct": "SECTION_82A_2",
          "suspensionOfRTB_Reason": "Test",
          "additionalReasonsForPossession": {
            "hasReasons": "NO",
            "reasons": null
          },
          "hasUnderlesseeOrMortgagee": "NO",
          "wantToUploadDocuments": "NO",
          "applicationWithClaim": "YES",
          "languageUsed": "ENGLISH",
          "completionNextStep": "SUBMIT_AND_PAY_NOW",
          "endButtonLabel": "Submit claim",
          "statementOfTruth": {
            "completedBy": "CLAIMANT",
            "fullNameParty": "Samson",
            "positionParty": "Lure",
            "fullNameLegalRep": null,
            "firmNameLegalRep": null,
            "positionLegalRep": null,
            "agreementClaimant": ["BELIEVE_TRUE"],
            "agreementClaimantLegalRep": [],
            "agreementDefendantLegalRep": []
          }
        },
        "event": {
          "id": "resumePossessionClaim",
          "summary": "",
          "description": ""
        },
        "event_token": "#{eventToken}",
        "ignore_warning": false
      }"""))
      .check(status.is(201)))
  }
  // --- Payment steps (you will fix PayTheClaimFee extraction yourself) ---
val PayTheClaimFee =
  group("MakeAClaim_35_PayTheClaimFee") {
    tryMax(15) {
      pause(4)
        .exec(http("MakeAClaim_PaymentGroups")
          .get("/payments/cases/#{caseId}/paymentgroups")
          .header("Accept", "application/json, text/plain, */*")
          .check(status.is(200))
          .check(bodyString.saveAs("paymentGroupsBody"))
        .check(
          bodyString.transform { body =>
            val pattern = """"service_request_reference"\s*:\s*"([^"]+)"""".r
            pattern.findFirstMatchIn(body).map(_.group(1)).getOrElse("")
          }.not("").saveAs("serviceRequestId")
        ))
        .exec { session =>
          println("caseId=" + session("caseId").as[String])
          println("PAYMENTGROUPS BODY: " + session("paymentGroupsBody").asOption[String].getOrElse("<empty>"))
          println("serviceRequestId=" + session("serviceRequestId").asOption[String].getOrElse("<missing>"))
          session
        }
    }
    .exec(http("MakeAClaim_PayTheClaimFee_PaymentOrders")
      .get("/payments/case-payment-orders?case_ids=#{caseId}")
      .header("Accept", "application/json, text/plain, */*")
      .check(status.in(200, 404)))
  }
  val PaynowLink =
  group("MakeAClaim_36_PaynowLink") {
    exec(http("MakeAClaim_PbaAccounts")
      .get("/payments/pba-accounts")
      .header("Accept", "application/json, text/plain, */*")
      .check(status.is(200))
      .check(jsonPath("$.organisationEntityResponse.paymentAccount[0]").saveAs("pbaAccount")))
  }
  val ConfirmPayment =
  group("MakeAClaim_37_ConfirmPayment") {
    exec { session =>
      session.set("idempotencyKey", s"idam-key-${java.util.UUID.randomUUID()}")
    }
    .exec(http("MakeAClaim_ConfirmPayment")
      .post("/payments/service-request/#{serviceRequestId}/pba-payments")
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json")
      .header("X-XSRF-TOKEN", "#{XSRFToken}")
      .header("CSRF-Token", "#{XSRFToken}")
      .body(StringBody("""{
        "account_number": "#{pbaAccount}",
        "amount": 415,
        "currency": "GBP",
        "customer_reference": "Test",
        "organisation_name": "Possessions Claim Solicitor Org",
        "idempotency_key": "#{idempotencyKey}"
      }"""))
      .check(status.is(201)))
  }
}