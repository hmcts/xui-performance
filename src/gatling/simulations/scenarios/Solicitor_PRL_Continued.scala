package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers, CsrfCheck}

/*======================================================================================
* Create a new Private Law application as a professional user (e.g. solicitor)
======================================================================================*/

object Solicitor_PRL_Continued {
  
  val BaseURL = Environment.baseURL
  val prlURL = "https://privatelaw.${env}.platform.hmcts.net"
  val IdamUrl = Environment.idamURL

  val postcodeFeeder = csv("postcodes.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime


  val PRL =

  /*======================================================================================
* Citizen Home
======================================================================================*/

    group("XUI_PRL_241_PRLCitizenHome") {

      exec(_.setAll(
        "PRLRandomString" -> (Common.randomString(7)),
        "PRLAppDobDay" -> Common.getDay(),
        "PRLAppDobMonth" -> Common.getMonth(),
        "PRLAppDobYear" -> Common.getDobYear(),
        "caseId" -> "1669037828569337"))

        .exec(http("XUI_PRL_241_005_PRLCitizenHome")
          .get(prlURL + "/citizen-home")
          .headers(Headers.navigationHeader)
          .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
          .check(CsrfCheck.save)
          .check(substring("Enter your access details")))
    }

      .pause(MinThinkTime, MaxThinkTime)


      /*===============================================================================================
* Enter Your Access Details
===============================================================================================*/

      .group("XUI_PRL_242_AccessDetails") {
        exec(http("XUI_PRL_242_005_AccessDetails")
          .post(prlURL + "/citizen-home")
          .headers(Headers.commonHeader)
          .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("_csrf", "${csrf}")
          .formParam("caseCode", "${caseId}")
          .formParam("accessCode", "${accessCode}")
          .formParam("accessCodeCheck", "true")
          .check(substring("Sign in or create an account")))
      }
      .pause(MinThinkTime, MaxThinkTime)


      /*===============================================================================================
* Login
===============================================================================================*/

      .group("XUI_PRL_243_PRLLogin") {
        exec(http("XUI_PRL_243_005_PRLLogin")
          .post(IdamUrl + "/login?client_id=prl-citizen-frontend&response_type=code&redirect_uri=" + prlURL + "/receiver")
          .headers(Headers.commonHeader)
          .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("username", "familyprivatelaw@gmail.com")
          .formParam("password", "Password12")
          .formParam("save", "Sign in")
          .formParam("selfRegistrationEnabled", "true")
          .formParam("_csrf", "${csrf}")
          .check(substring("Your private law account")))
      }
      .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select Case
======================================================================================*/

  .group("XUI_PRL_244_PRLSelectCase") {

    exec(http("XUI_PRL_244_005_PRLHomePage")
        .get(prlURL + "/respondent/task-list/${caseId}")
        .headers(Headers.navigationHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .check(substring("You have a new order from the court")))

  }

    .pause(MinThinkTime, MaxThinkTime)



  /*======================================================================================
* Select 'Check the application(PDF)'
======================================================================================*/

  .group("XUI_PRL_480_ApplicationPDFDownload") {

    exec(http("XUI_PRL_480_005_ApplicationPDFDownload")
      .get(prlURL + "/yourdocuments/alldocuments/cadafinaldocumentrequest?updateCase=Yes")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"))
     //need to find what to check for .check(substring("Your current postcode")))

  }

    .pause(MinThinkTime, MaxThinkTime)



  /*======================================================================================
* Select 'Respond to the allegations of harm and violence'
======================================================================================*/

  .group("XUI_PRL_500_AllegationsOfHarm") {

    exec(http("XUI_PRL_500_005_AllegationsOfHarm")
      .get(prlURL + "/tasklistresponse/international-factors/start")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Do the children live outside of England or Wales?")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Do the children live outside of England or Wales? - No
======================================================================================*/

  .group("XUI_PRL_510_OutsideOfEngland") {

    exec(http("XUI_PRL_510_005_OutsideOfEngland")
      .post(prlURL + "/tasklistresponse/international-factors/start")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("iFactorsStartProvideDetails", "")
      .formParam("start", "No")
      .formParam("saveAndContinue", "true")
      .check(substring("Do the children&#39;s parents or anyone significant to the children live outside of England or Wales?")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Do the children's parents or anyone significant to the children live outside of England or Wales? - No
======================================================================================*/

  .group("XUI_PRL_520_ParentsOutsideOfEngland") {

    exec(http("XUI_PRL_520_005_ParentsOutsideOfEngland")
      .post(prlURL + "/tasklistresponse/international-factors/parents")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("iFactorsParentsProvideDetails", "")
      .formParam("parents", "No")
      .formParam("saveAndContinue", "true")
      .check(substring("Could another person in the application apply for a similar order in a country outside England or Wales?")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Could another person in the application apply for a similar order in a country outside England or Wales? - No
======================================================================================*/

  .group("XUI_PRL_530_AnotherOutsideOfEngland") {

    exec(http("XUI_PRL_530_005_AnotherOutsideOfEngland")
      .post(prlURL + "/tasklistresponse/international-factors/jurisdiction")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("iFactorsJurisdictionProvideDetails", "")
      .formParam("jurisdiction", "No")
      .formParam("saveAndContinue", "true")
      .check(substring("Has another country asked (or been asked) for information or help for the children?")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Has another country asked (or been asked) for information or help for the children?
======================================================================================*/

  .group("XUI_PRL_540_AnotherCountryAsked") {

    exec(http("XUI_PRL_540_005_AnotherCountryAsked")
      .post(prlURL + "/tasklistresponse/international-factors/request")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("iFactorsRequestProvideDetails", "")
      .formParam("request", "No")
      .formParam("saveAndContinue", "true")
      .check(substring("International elements")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Respond to the allegations of harm and violence Check Your Answers
======================================================================================*/

  .group("XUI_PRL_550_RespondToTheAllegationsCheck") {

    exec(http("XUI_PRL_550_005_RespondToThe")
      .post(prlURL + "/tasklistresponse/international-factors/summary")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("saveAndContinue", "true")
      .check(substring("Respond to the application")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select 'Respond to the application'
======================================================================================*/

  .group("XUI_PRL_550_RespondToApplication") {

    exec(http("XUI_PRL_550_005_RespondToApplication")
      .get(prlURL + "/tasklistresponse/start")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Legal representation")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select 'Do you have a legal representative?'
======================================================================================*/

  .group("XUI_PRL_560_LegalRepresentative") {

    exec(http("XUI_PRL_560_005_LegalRepresentative")
      .get(prlURL + "/tasklistresponse/legalrepresentation/start")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Will you be using a legal representative to respond to the application?")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Will you be using a legal representative to respond to the application? - No
======================================================================================*/

  .group("XUI_PRL_570_UseLegalRepresentative") {

    exec(http("XUI_PRL_570_005_UseLegalRepresentative")
      .post(prlURL + "/tasklistresponse/legalrepresentation/start")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("legalRepresentation", "No")
      .check(substring("Complete your response")))

  }

    .pause(MinThinkTime, MaxThinkTime)



  /*======================================================================================
* Transfer your case to your legal representative
======================================================================================*/

  .group("XUI_PRL_580_TransferRepresentative") {

    exec(http("XUI_PRL_580_005_TransferRepresentative")
      .post(prlURL + "/redirect/tasklistresponse")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .check(substring("""id="do_you_have_legal_representation-status" class="govuk-tag app-task-list__tag govuk-tag--green">Completed""")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Do you consent to the application?
======================================================================================*/

  .group("XUI_PRL_590_ConsentToApplication") {

    exec(http("XUI_PRL_590_005_ConsentToApplication")
      .get(prlURL + "/tasklistresponse/consent-to-application/consent/${caseId}")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Your understanding of the application")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Your understanding of the application
======================================================================================*/

  .group("XUI_PRL_600_UnderstandingOfApplication") {

    exec(http("XUI_PRL_600_005_UnderstandingOfApplication")
      .post(prlURL + "/tasklistresponse/consent-to-application/consent")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("doYouConsent", "Yes")
      .formParam("reasonForNotConsenting", "")
      .formParam("applicationReceivedDate-day", "${PRLAppDobDay}")
      .formParam("applicationReceivedDate-month", "${PRLAppDobMonth}")
      .formParam("applicationReceivedDate-year", Common.getDobYearChild())
      .formParam("courtOrderDetails", "")
      .formParam("courtPermission", "No")
      .formParam("onlyContinue", "true")
      .check(substring("Your consent to the application")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Your consent to the application submit
======================================================================================*/

  .group("XUI_PRL_610_ConsentSubmit") {

    exec(http("XUI_PRL_610_005_ConsentSubmit")
      .post(prlURL + "/tasklistresponse/consent-to-application/summary")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("saveAndContinue", "true")
      .check(substring("""id="consent-to-the-application-status" class="govuk-tag app-task-list__tag govuk-tag--green">Completed""")))

  }

    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
* Select 'Keep your details private'
======================================================================================*/

    .group("XUI_PRL_250_DetailsPrivate") {
      exec(http("XUI_PRL_250_005_DetailsPrivate")
        .get(prlURL + "/respondent/keep-details-private/details_known/${caseId}")
        .headers(Headers.navigationHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .check(CsrfCheck.save)
        .check(substring("Do the other people named in this application (the applicants) know any of your contact details?")))

        .exec(Common.userDetails)

      //   .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
    }

    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* Do the other people named in this application (the applicants) know any of your contact details? - Yes
===============================================================================================*/

    .group("XUI_PRL_251_KnowContactDetails") {
      exec(http("XUI_PRL_251_005_KnowContactDetails")
        .post(prlURL + "/respondent/keep-details-private/details_known")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("detailsKnown", "yes")
        .formParam("onlyContinue", "true")
        .formParam("saveAndContinue", "true")
        .check(CsrfCheck.save)
        .check(substring("Do you want to keep your contact details private from the other people named in the application (the applicants)?")))
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* Do you want to keep your contact details private from the other people named in the application (the applicants)? - Yes
===============================================================================================*/

    .group("XUI_PRL_252_ContactDetailsPrivate") {
      exec(http("XUI_PRL_252_005_ContactDetailsPrivate")
        .post(prlURL + "/respondent/keep-details-private/start_alternative")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("contactDetailsPrivate", "")
        .formParam("contactDetailsPrivate", "")
        .formParam("contactDetailsPrivate", "")
        .formParam("startAlternative", "No")
        .formParam("saveAndContinue", "true")
        .check(substring("The court will not keep your contact details private")))
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* What the court will do
===============================================================================================*/

    .group("XUI_PRL_253_CourtWillDo") {
      exec(http("XUI_PRL_253_005_CourtWillDo")
        .post(prlURL + "/respondent/keep-details-private/private_details_confirmed")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("saveAndContinue", "true")
        .check(substring("""id="keep-your-details-private-status" class="govuk-tag app-task-list__tag govuk-tag--green">Completed""")))
    }
    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select 'Confirm or edit your contact details'
======================================================================================*/

  .group("XUI_PRL_260_ContactDetails") {

    exec(http("XUI_PRL_260_005_ContactDetails")
      .get(prlURL + "/respondent/confirm-contact-details/checkanswers/{caseId}")
      .headers(Headers.navigationHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
      .check(substring("Name")))
  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select 'Name'
======================================================================================*/

  .group("XUI_PRL_270_Name") {

    exec(http("XUI_PRL_270_005_Name")
      .get(prlURL + "/respondent/confirm-contact-details/personaldetails")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Your name and date of birth")))

      .exec(Common.userDetails)

    //   .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
  }

    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
  * Enter Name, DoB and Address
  ===============================================================================================*/

    .group("XUI_PRL_280_EnterName") {
      exec(http("XUI_PRL_280_005_EnterName")
        .post(prlURL + "/respondent/confirm-contact-details/personaldetails")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("citizenUserFirstNames", "${PRLRandomString} First")
        .formParam("citizenUserLastNames", "${PRLRandomString} Last")
        .formParam("previousName", "")
        .formParam("citizenUserDateOfBirth-day", "${PRLAppDobDay}")
        .formParam("citizenUserDateOfBirth-month", "${PRLAppDobMonth}")
        .formParam("citizenUserDateOfBirth-year", "${PRLAppDobYear}")
        .formParam("citizenUserPlaceOfBirth", "${PRLRandomString} Address")
        .formParam("saveAndContinue", "true")
        .check(CsrfCheck.save)
        .check(substring("Read the information to make sure it is correct, and add any missing details")))
    }
    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select 'Address'
======================================================================================*/

  .group("XUI_PRL_290_Address") {

    exec(http("XUI_PRL_290_005_Address")
      .get(prlURL + "/respondent/confirm-contact-details/address/lookup")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Your current postcode")))

      .exec(Common.userDetails)

    //   .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
  }

    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
  * Enter PostCode
  ===============================================================================================*/

    .group("XUI_PRL_300_PostCode") {
      feed(postcodeFeeder)

        .exec(http("XUI_PRL_300_005_PostCode")
          .post(prlURL + "/respondent/confirm-contact-details/address/lookup")
          .headers(Headers.commonHeader)
          .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("_csrf", "${csrf}")
          .formParam("citizenUserAddressPostcode", "${postcode}")
          .formParam("saveAndContinue", "true")
          .check(regex("""<option value="([0-9]+)">""").findRandom.saveAs("addressIndex")))
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* Select Address
===============================================================================================*/

    .group("XUI_PRL_310_SelectAddress") {

      exec(http("XUI_PRL_310_005_SelectAddress")
        .post(prlURL + "/respondent/confirm-contact-details/address/select")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("citizenUserSelectAddress", "{$addressIndex}")
        .formParam("saveAndContinue", "true")
        .check(regex("""name="citizenUserAddress1" type="text" value="(.+)""").saveAs("address"))
        .check(regex("""name="citizenUserAddressTown" type="text" value="(.+)""").saveAs("town"))
      //  .check(regex("""name="citizenUserAddressCounty" type="text" value="(.+)"""").saveAs("addressLines(2)"))
        .check(substring("Building and street")))
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* Your Address
===============================================================================================*/

    .group("XUI_PRL_320_YourAddress") {

      exec(http("XUI_PRL_320_005_YourAddress")
        .post(prlURL + "/respondent/confirm-contact-details/addressconfirmation")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("citizenUserAddress1", "${address}")
        .formParam("citizenUserAddress2", "")
        .formParam("citizenUserAddressTown", "${town}")
        .formParam("citizenUserAddressCounty", "")
        .formParam("citizenUserAddressPostcode", "${postcode}")
        .formParam("saveAndContinue", "true")
        .check(substring("Have you lived at this address for more than 5 years?")))

    }
    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* Have you lived at this address for more than 5 years? - Yes
===============================================================================================*/

    .group("XUI_PRL_330_LivedAtAddress") {

      exec(http("XUI_PRL_330_005_LivedAtAddress")
        .post(prlURL + "/respondent/confirm-contact-details/addresshistory")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("isAtAddressLessThan5Years", "Yes")
        .formParam("citizenUserAddressHistory", "")
        .formParam("saveAndContinue", "true")
        .check(substring("Check your details")))
    }
    .pause(MinThinkTime, MaxThinkTime)


  /*===============================================================================================
* Select 'Phone Number'
===============================================================================================*/

  .group("XUI_PRL_340_PhoneNumber") {

    exec(http("XUI_PRL_340_005_PhoneNumber")
      .get(prlURL + "/respondent/confirm-contact-details/contactdetails")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("UK telephone number")))

      .exec(Common.userDetails)

    //   .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).saveAs("XSRFToken")))
  }

    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* Contact Details
===============================================================================================*/

    .group("XUI_PRL_350_ContactDetails") {

      exec(http("XUI_PRL_350_005_ContactDetails")
        .post(prlURL + "/respondent/confirm-contact-details/contactdetails")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("citizenUserPhoneNumber", "07000000000")
        .formParam("citizenUserEmailAddress", "${PRLRandomString}@gmail.com")
        .formParam("citizenUserSafeToCall", "${PRLRandomString} Call")
        .formParam("saveAndContinue", "true")
        .check(substring("Check your details")))
    }
    .pause(MinThinkTime, MaxThinkTime)


    /*===============================================================================================
* Save your Details
===============================================================================================*/

//    .group("XUI_PRL_360_DetailsSubmit") {

  //    exec(http("XUI_PRL_360_005_DetailsSubmit")
   //     .post(prlURL + "/respondent/confirm-contact-details/checkanswers")
   //     .headers(Headers.commonHeader)
   //     .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
   //     .header("content-type", "application/x-www-form-urlencoded")
   //     .formParam("_csrf", "${csrf}")
   //     .formParam("saveAndContinue", "true")
   //     .check(substring("""id="confirm-or-edit-your-contact-details-status" class="govuk-tag app-task-list__tag govuk-tag--green">Completed""")))
   // }
   // .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Miam
======================================================================================*/

  .group("XUI_PRL_620_Miam") {

    exec(http("XUI_PRL_620_005_Miam")
      .get(prlURL + "/tasklistresponse/miam/miam-start/${caseId}")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Have you attended a Mediation Information and Assessment Meeting (MIAM)?")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Have you attended a Mediation Information and Assessment Meeting (MIAM)? - yes
======================================================================================*/

  .group("XUI_PRL_630_AttendedMiam") {

    exec(http("XUI_PRL_630_005_AttendedMiam")
      .post(prlURL + "/tasklistresponse/miam/miam-start")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("miamStart", "Yes")
      .formParam("onlyContinue", "true")
      .check(substring("Check your answers")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Miam Submit
======================================================================================*/

  .group("XUI_PRL_640_MiamSubmit") {

    exec(http("XUI_PRL_640_005_MiamSubmit")
      .post(prlURL + "/tasklistresponse/miam/summary")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("saveAndContinue", "true")
      .check(substring("""id="medation-miam-status" class="govuk-tag app-task-list__tag govuk-tag--green">Completed""")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Review and Submit
======================================================================================*/

  .group("XUI_PRL_650_ReviewAndSubmit") {

    exec(http("XUI_PRL_650_005_ReviewAndSubmit")
      .get(prlURL + "/tasklistresponse/summary?onlyContinue=true")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Please review your answers before you complete your response.")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Check Your Answers - final submit
======================================================================================*/

  .group("XUI_PRL_660_ReviewSubmit") {

    exec(http("XUI_PRL_660_005_ReviewSubmit")
      .post(prlURL + "/tasklistresponse/summary?onlyContinue=true")
      .headers(Headers.commonHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .header("content-type", "application/x-www-form-urlencoded")
      .formParam("_csrf", "${csrf}")
      .formParam("declarationCheck", "declaration")
      .formParam("saveAndContinue", "true")
      .check(substring("Response submitted successfully")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select 'Upload Document'
======================================================================================*/

  .group("XUI_PRL_670_UploadDocument") {

    exec(http("XUI_PRL_670_005_UploadDocument")
      .get(prlURL + "/respondent/upload-document")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(substring("Select the type of document")))

  }

    .pause(MinThinkTime, MaxThinkTime)


  /*======================================================================================
* Select 'Your position statements '
======================================================================================*/

  .group("XUI_PRL_680_YourPositionStatements") {

    exec(http("XUI_PRL_680_005_YourPositionStatements")
      .get(prlURL + "/respondent/upload-document")
      .headers(Headers.navigationHeader)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
      .check(CsrfCheck.save)
      .check(substring("Select the type of document")))

  }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
* Has the court asked for this document? - yes
======================================================================================*/

    .group("XUI_PRL_690_CourtAsked") {

      exec(http("XUI_PRL_690_005_CourtAsked")
        .post(prlURL + "/respondent/upload-document/start?caption=Witness%20statements%20and%20evidence&document_type=Your%20position%20statements")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("start", "Yes")
        .formParam("onlyContinue", "true")
        .check(substring("How your documents will be shared")))

    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
* How your documents will be shared
======================================================================================*/

    .group("XUI_PRL_700_DocumentsShared") {

      exec(http("XUI_PRL_700_005_DocumentsShared")
        .post(prlURL + "/respondent/upload-document/document-sharing-details?caption=Witness%20statements%20and%20evidence&document_type=Your%20position%20statements")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        .formParam("_csrf", "${csrf}")
        .formParam("onlyContinue", "true")
        .check(substring("Provide the documents")))

    }

    .pause(MinThinkTime, MaxThinkTime)


    /*======================================================================================
* Give the court more information about the documents you are uploading
======================================================================================*/

    .group("XUI_PRL_710_SubmitInformation") {

      exec(http("XUI_PRL_710_005_SubmitInformation")
        .post(prlURL + "/document-manager/generatePdf?_csrf=${csrf}&parentDocumentType=Witness%20statements%20and%20evidence&documentType=Your%20position%20statements&isApplicant=No")
        .headers(Headers.commonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .header("content-type", "application/x-www-form-urlencoded")
        /*
        .formParam("_csrf", "${csrf}")
        .formParam("parentDocumentType", "Witness statements and evidence")
        .formParam("documentType", "Your position statements")
        .formParam("isApplicant", "No")

         */
        .formParam("freeTextAreaForUpload", "${PRLRandomString}")
        .check(substring("Provide the documents")))

    }

    .pause(MinThinkTime, MaxThinkTime)



    /*======================================================================================
* Position Statements Upload
======================================================================================*/

    .group("XUI_PRL_720_PositionStatementsUpload") {
      exec(http("XUI_PRL_720_005_PositionStatementsUpload")
        .post(prlURL + "/document-manager?_csrf=${csrf}&parentDocumentType=Witness%20statements%20and%20evidence&documentType=Your%20position%20statements&isApplicant=No")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .bodyPart(RawFileBodyPart("files", "UploadTest.pdf")
          .fileName("UploadTest.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
      //  .formParam("classification", "PUBLIC")
      //  .formParam("caseTypeId", "PRLAPPS")
      //  .formParam("jurisdictionId", "PRIVATELAW")
        .check(substring("UploadTest")))
       // .check(jsonPath("$.documents[0].hashToken").saveAs("documentHashAdditional"))
       // .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLAdditional")))

    }
    .pause(MinThinkTime, MaxThinkTime)


      /*======================================================================================
* Docuemnst Upload Submit
======================================================================================*/

      .group("XUI_PRL_730_PositionUploadSubmit") {

        exec(http("XUI_PRL_730_005_PositionUploadSubmit")
          .post(prlURL + "/respondent/upload-document/upload-your-documents?caption=Witness%20statements%20and%20evidence&document_type=Your%20position%20statements")
          .headers(Headers.commonHeader)
          .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("_csrf", "${csrf}")
          .formParam("declarationCheck", "")
          .formParam("declarationCheck", "declaration")
          .formParam("onlyContinue", "true")
          .check(substring("Your documents have been uploaded")))

      }

      .pause(MinThinkTime, MaxThinkTime)

}