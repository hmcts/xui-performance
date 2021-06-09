package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{DivorceHeader, Environment}

object EXUIDivorceMC {

  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTimeDIV
  val MaxThinkTime = Environment.maxThinkTimeDIV
  
  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to Click On case Create
======================================================================================*/

  val casecreation=
  group("XUI${service}_040_CreateCase") {
    exec(http("XUI${service}_040_CreateCase")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
      .headers(DivorceHeader.headers_accessCreate)
      .check(status.in(200, 304)))
  }
    .pause(MinThinkTime, MaxThinkTime)
    
    .group("XUI${service}_050_StartCreateCase1") {
      exec(http("XUI${service}_050_005_StartCreateCase1")
        .get("/data/internal/case-types/DIVORCE/event-triggers/solicitorCreate?ignore-warning=false")
        .headers(DivorceHeader.headers_27)
        .check(status.in(200, 304))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))

        .exec(http("XUI${service}_050_010_StartCreateCase2")
          .get("/data/internal/case-types/DIVORCE/event-triggers/solicitorCreate?ignore-warning=false")
          .headers(DivorceHeader.headers_27)
          .check(status.in(200, 304)))

        .exec(http("XUI${service}_050_015_CreateCaseProfile")
          .get("/data/internal/profile")
          .headers(DivorceHeader.headers_31)
          .check(status.in(200, 304))).exitHereIfFailed

        .exec(http("XUI${service}_050_020_ShareCaseOrg")
          .get("/api/caseshare/orgs")
          .headers(DivorceHeader.headers_shareorgs)
          .check(status.in(200, 304)))
    }
      .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to Start Application
======================================================================================*/

    // .group("XUI${service}_060_CreateApplication") {
    //   exec(http("XUI${service}_060_CreateApplication")
    //        .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreatesolicitorCreate")
    //        .headers(DivorceHeader.headers_soldata)
    //        .body(ElFileBody("DivCreateApplication.json")).asJson
    //        .check(status.in(200, 304)))
    // }
    //   .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to Enter Solicitor Details
======================================================================================*/
    
    .group("XUI${service}_070_SolicitorDetails") {
      exec(http("XUI${service}_070_SolicitorDetails")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolAboutTheSolicitor")
           .headers(DivorceHeader.headers_aboutsol)
           .body(ElFileBody("bodies/divorce/DivSolDetails.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to Enter Solicitor Details
======================================================================================*/
    
    .group("XUI${service}_080_PetitionerDetails") {
      exec(http("XUI${service}_080_PetitionerDetails")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolAboutThePetitioner")
           .headers(DivorceHeader.headers_aboutpetitioner)
           .body(ElFileBody("bodies/divorce/DivPetitionDetails.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to Enter Respondent Details
======================================================================================*/
    .group("XUI${service}_090_RespondantDetails") {
      exec(http("XUI${service}_090_RespondantDetails")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolAboutTheRespondent")
           .headers(DivorceHeader.headers_headerRespondant)
           .body(ElFileBody("bodies/divorce/DivRespondentDetails.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)
  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter Marriage Certificate details
======================================================================================*/
    .group("XUI${service}_100_MarriageCertificate") {
      exec(http("XUI${service}_100_MarriageCertificate")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolMarriageCertificate")
           .headers(DivorceHeader.headers_marriagecertificate)
           .body(ElFileBody("bodies/divorce/DivMarriageCertificate.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)
  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter Solicitor jurisdiction
======================================================================================*/
    .group("XUI${service}_110_SolJurisdiction") {
      exec(http("XUI${service}_110_SolJurisdiction")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolJurisdiction")
           .headers(DivorceHeader.headers_createjurisdiction)
           .body(ElFileBody("bodies/divorce/DivSolJurisdiction.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)
  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter Reason For Divorce details
======================================================================================*/
    
    .group("XUI${service}_120_ReasonForDivorce") {
      exec(http("XUI${service}_120_ReasonForDivorce")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolReasonForDivorce")
           .headers(DivorceHeader.headers_reasonfordiv)
           .body(ElFileBody("bodies/divorce/DivReasonForDivorce.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)
  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter behavioural details
======================================================================================*/
    .group("XUI${service}_130_Behaviour") {
      exec(http("XUI${service}_130_Behaviour")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolSOCBehaviour1")
           .headers(DivorceHeader.headers_behaviour)
           .body(ElFileBody("bodies/divorce/DivBehaviour.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)
  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter existing court case details
======================================================================================*/
    .group("XUI${service}_140_ExistingCourtCases") {
      exec(http("XUI${service}_140_ExistingCourtCases")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolExistingCourtCases")
           .headers(DivorceHeader.headers_courtcases)
           .body(ElFileBody("bodies/divorce/DivExistingCourtCases.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)
  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter property division details
======================================================================================*/
    
    .group("XUI${service}_150_DivideProperty") {
      exec(http("XUI${service}_150_DivideProperty")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolDividingMoneyAndProperty")
           .headers(DivorceHeader.headers_devideprops)
           .body(ElFileBody("bodies/divorce/DivDivideProperty.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter claim costs
======================================================================================*/
    .group("XUI${service}_160_ClaimCosts") {
      exec(http("XUI${service}_160_ClaimCosts")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolApplyToClaimCosts")
           .headers(DivorceHeader.headers_claimcosts)
           .body(ElFileBody("bodies/divorce/DivClaimCosts.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to upload docs
======================================================================================*/
    .group("XUI${service}_170_UploadDocs") {
      exec(http("XUI${service}_170_UploadDocs")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreateSolUploadDocs")
           .headers(DivorceHeader.headers_upload)
           .body(ElFileBody("bodies/divorce/DivUploadDocs.json")).asJson
           .check(status.is(200)))
    }
      .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter language preference
======================================================================================*/
    .group("XUI${service}_180_CreateLangPref") {
      exec(http("XUI${service}_180_005_CreateLangPref")
           .post("/data/case-types/DIVORCE/validate?pageId=solicitorCreatelangPref")
           .headers(DivorceHeader.headers_langpref)
           .body(ElFileBody("bodies/divorce/DivCreateLangPref.json")).asJson
           .check(status.is(200)))

        .exec(http("XUI${service}_180_010_LangPrefProfile")
          .get("/data/internal/profile")
          .headers(DivorceHeader.headers_internalprofile)
          .check(status.in(200, 304)))
    }
      .pause(MinThinkTime, MaxThinkTime)

  /*======================================================================================
*Business process : As part of the Divorce Case Creation there are different steps
* Below group contains all the requests related to enter aplication draft final
======================================================================================*/
    .group("XUI${service}_190_ApplicationDraftfinal") {
      exec(http("XUI${service}_190_005_ApplicationDraftfinal")
           .post("/data/case-types/DIVORCE/cases?ignore-warning=false")
           .headers(DivorceHeader.headers_ignorewarning)
           .body(ElFileBody("bodies/divorce/DivApplicationDraftFinal.json")).asJson
           .check(status.in(200, 201))
           .check(jsonPath("$.id").optional.saveAs("caseId")))
        /*.exec( session => {
             println("the case Id is  "+session("caseId").as[String])
             session
           })*/

        .exec(http("XUI${service}_190_010_ApplicationDraftfinalProfile")
        .get("/data/internal/cases/${caseId}")
        .headers(DivorceHeader.headers_datainternal)
        .check(status.in(200, 304)))
    }
      .pause(MinThinkTime, MaxThinkTime)


   
  val GETUsersByOrganisation = 
  
  exec(http("GetUsersByOrg")
    .get("http://rd-professional-api-perftest.service.core-compute-perftest.internal/refdata/internal/v1/organisations/${orgref}/users?returnRoles=false")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Authorization", " Bearer ${accessToken}")
    .header("Content-Type", "application/json")
    .check(status is 200)
    .check(jsonPath("$..email").find(0).optional.saveAs("user1"))
    .check(jsonPath("$..email").find(1).optional.saveAs("user2"))
    .check(jsonPath("$..email").find(2).optional.saveAs("user3"))
    .check(jsonPath("$..email").find(3).optional.saveAs("user4"))
    .check(jsonPath("$..email").find(4).optional.saveAs("user5"))
    .check(jsonPath("$..userIdentifier").find(0).optional.saveAs("userIdentifier1"))
    .check(jsonPath("$..userIdentifier").find(1).optional.saveAs("userIdentifier2"))
    .check(jsonPath("$..userIdentifier").find(2).optional.saveAs("userIdentifier3"))
    .check(jsonPath("$..userIdentifier").find(3).optional.saveAs("userIdentifier4"))
    .check(jsonPath("$..userIdentifier").find(4).optional.saveAs("userIdentifier5"))
  )
    .pause(MinThinkTime , MaxThinkTime )


  val shareacase =
  group("XUI${service}_200_ShareACase") {
    exec(http("XUI${service}_200_005_ShareACase")
      .get("/api/caseshare/cases?case_ids=${caseId}")
      .headers(DivorceHeader.headers_shareacasebyid)
      .check(status.in(200, 304))
      .check(jsonPath("$..email").find(0).optional.saveAs("user0"))
      .check(jsonPath("$..firstName").find(0).optional.saveAs("firstName"))
      .check(jsonPath("$..lastName").find(0).optional.saveAs("lastName"))
      .check(jsonPath("$..idamId").find(0).optional.saveAs("idamId"))

    )

      .exec(http("XUI${service}_200_010_ShareACaseUsers")
        .get("/api/caseshare/users")
        .headers(DivorceHeader.headers_shareacasesuserslist)
        .check(status.in(200, 304))
        .check(jsonPath("$..email").find(0).optional.saveAs("user1"))
        .check(jsonPath("$..firstName").find(0).optional.saveAs("firstName1"))
        .check(jsonPath("$..lastName").find(0).optional.saveAs("lastName1"))
        .check(jsonPath("$..idamId").find(0).optional.saveAs("idamId1"))
      )
  }
        .pause(MinThinkTime , MaxThinkTime )
    .group("XUI${service}_210_ShareACaseAssignments") {
      exec(http("XUI${service}_210_ShareACaseAssignments")
           .post("/api/caseshare/case-assignments")
           .headers(DivorceHeader.headers_shareacaseconfirm)
           .body(ElFileBody("bodies/divorce/DivShareACase.json")).asJson
           .check(status.in(200, 201)))
    }
        .pause(MinThinkTime , MaxThinkTime )




}
