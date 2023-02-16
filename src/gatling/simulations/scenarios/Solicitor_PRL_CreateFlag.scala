package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.io.{BufferedWriter, FileWriter}

/*======================================================================================
* Create a new Private Law application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_PRL_CreateFlag {
  
  val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val PRLcases = csv("casePrepareForHearing.csv").circular


  val postcodeFeeder = csv("postcodes.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime



  val CreateAFlag =


  /*======================================================================================
  * Select case
  ======================================================================================*/

    group("XUI_PRL_030_SelectCase") {

      exec(_.setAll(
        "PRLRandomString" -> (Common.randomString(7)),
        "PRLAppDobDay" -> Common.getDay(),
        "PRLAppDobMonth" -> Common.getMonth(),
        "PRLAppDobYear" -> Common.getDobYear()))

     //   .feed(PRLcases)

        .exec(http("XUI_PRL_030_005_SelectCase")
          .get(BaseURL + "/data/internal/cases/${caseId}")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*"))
        //  .check(substring("PRIVATELAW")))

        .exec(Common.userDetails)
    }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
      * Select 'Create Case Flag'
      ======================================================================================*/

      .group("XUI_PRL_040_CreateCaseFlag") {
        exec(Common.profile)

        .exec(http("XUI_PRL_040_005_SelectIssue")
          .get(BaseURL + "/workallocation/case/tasks/${caseId}/event/c100CreateFlags/caseType/PRLAPPS/jurisdiction/PRIVATELAW")
          .headers(Headers.navigationHeader))
     //     .check(jsonPath("$.event_token").saveAs("event_token"))
     //   .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("local_Court_Id"))
        //  .check(jsonPath("$.id").is("createCaseFlag")))

        .exec(http("XUI_PRL_040_010_SelectIssue")
          .get(BaseURL + "/data/internal/cases/${caseId}/event-triggers/c100CreateFlags?ignore-warning=false")
          .headers(Headers.navigationHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(jsonPath("$.event_token").saveAs("event_token"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].id").saveAs("AppId"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].id").saveAs("RepId"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.email").saveAs("repEmail"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.address.PostCode").saveAs("repPostCode"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.address.PostTown").saveAs("repPostTown"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.address.AddressLine1").saveAs("repAddressLine1"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.address.AddressLine2").saveAs("repAddressLine2"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.lastName").saveAs("repLastName"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.firstName").saveAs("repFirstName"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.dateOfBirth").saveAs("repDateOfBirth"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.solicitorOrg.OrganisationID").saveAs("repOrganisationID"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.solicitorOrg.OrganisationName").saveAs("repOrganisationName"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.partyLevelFlag.partyName").saveAs("repPartyName"))
          .check(jsonPath("$.case_fields[1].formatted_value[0].value.addressLivedLessThan5YearsDetails").saveAs("repAddressLivedLessThan5YearsDetails"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.email").saveAs("appEmail"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.address.PostCode").saveAs("appPostCode"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.address.AddressLine1").saveAs("appAddressLine1"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.address.AddressLine2").saveAs("appAddressLine2"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.dxNumber").saveAs("appDxNumber"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.lastName").saveAs("appLastName"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.firstName").saveAs("appFirstName"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.dateOfBirth").saveAs("appDateOfBirth"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorOrg.OrganisationID").saveAs("appOrganisationID"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorOrg.OrganisationName").saveAs("appOrganisationName"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.partyLevelFlag.partyName").saveAs("appPartyName"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorEmail").saveAs("appsSolicitorEmail"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorAddress.PostCode").saveAs("appsSolicitorPostCode"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorAddress.AddressLine1").saveAs("appsSolicitorAddressLine1"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorAddress.AddressLine2").saveAs("appsSolicitorAddressLine2"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorAddress.PostTown").saveAs("appsSolicitorPostTown"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorReference").saveAs("appSolicitorReference"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.representativeFirstName").saveAs("appRepresentativeFirstName"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.representativeLastName").saveAs("appRepresentativeLastName"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorOrgUuid").saveAs("solicitorOrgUuId"))
          .check(jsonPath("$.case_fields[2].formatted_value[0].value.solicitorPartyId").saveAs("solicitorPartyId")))
          //   .check(jsonPath("$.case_fields[0].formatted_value[0].id").saveAs("local_Court_Id"))
        //  .check(jsonPath("$.id").is("createCaseFlag")))


          .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      *Where should this flag be added? - Case Level
      ======================================================================================*/

      .group("XUI_PRL_050_WhereFlagAdded") {
        exec(http("XUI_PRL_050_005_WhereFlagAdded")
          .get(BaseURL + "/refdata/location/orgServices?ccdServiceNames=PRIVATELAW")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("service_short_description")))


        exec(http("XUI_PRL_050_010_WhereFlagAdded")
          .get(BaseURL + "/refdata/commondata/caseflags/service-id=ABA5?flag-type=CASE")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("FlagDetails")))
      }

      .pause(MinThinkTime, MaxThinkTime)

      /*======================================================================================
      * Add comments for this flag
      ======================================================================================*/

      .group("XUI_PRL_060_AddComments") {
        exec(http("XUI_PRL_060_005_AddComments")
          .get(BaseURL + "/api/user/details")
          .headers(Headers.navigationHeader)
          .header("accept", "application/json, text/plain, */*")
          .check(substring("canShareCases")))

      }

      .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
      * Review flag details
      ======================================================================================*/

      .group("XUI_PRL_070_ReviewFlagDetails") {
        exec(http("XUI_PRL_070_005_ReviewFlagDetails")
          .post(BaseURL + "/data/cases/${caseId}/events")
          .headers(Headers.commonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/hearings/prl/PRLCreateFlagSubmit.json"))
          .check(substring("case_type")))

          .exec(Common.userDetails)
      }

      .pause(MinThinkTime, MaxThinkTime)

      .exec { session =>
        val fw = new BufferedWriter(new FileWriter("caseFlagsAdded.csv", true))
        try {
          fw.write(session("caseId").as[String] + "\r\n")
        } finally fw.close()
        session
      }






}