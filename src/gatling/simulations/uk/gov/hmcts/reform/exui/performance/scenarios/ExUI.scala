package uk.gov.hmcts.reform.exui.performance.scenarios

import java.io.{BufferedWriter, FileWriter}

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Environment
import uk.gov.service.notify.{NotificationClient, NotificationList}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.util.matching.Regex

object ExUI {

   //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val approveUser=Environment.adminUserAO
  val approveUserPassword=Environment.adminPasswordAO
  val url_approve=Environment.url_approve
  val url_mo = Environment.manageOrdURL
  val notificationClient=Environment.notificationClient
  val feeder = csv("userid-increment.csv").circular
  val feederuser = csv("OrgDetails.csv").circular

  val headers_tc_aat = Map(
    "Content-Type" -> "application/json",
    "Origin" -> "https://manage-case.perftest.platform.hmcts.net",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin")

  val createOrg=
    exec(http("EXUI_RO_Homepage")
      .get("/register-org/register")
      .check(status.is(200)))
      .pause(Environment.minThinkTime,Environment.maxThinkTime)
      .feed(Feeders.createDynamicDataFeeder).exec(http("EXUI_RO_Create")
      .post("/external/register-org/register")
      .body(ElFileBody("CR.json")).asJson
      .check(status.is(200))
      .check(jsonPath("$.organisationIdentifier").optional.saveAs("orgRefCode")))
        .pause(10)

  val approveOrgHomePage=
    exec(http("EXUI_AO_005_Homepage")
      .get(url_approve + "/")
      .check(status.is(200)))
      .exec(http("EXUI_AO_010_Homepage")
        .get(IdamUrl + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri="+url_approve+"/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
        .check(regex("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

    .pause(Environment.minThinkTime)

  val approveOrganisationlogin =

    exec(http("EXUI_AO_005_Login")
      .post(IdamUrl + "/login?response_type=code&client_id=xuiaowebapp&redirect_uri="+url_approve+"/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
      .formParam("username", approveUser)
      .formParam("password", approveUserPassword)
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken}")
      .check(status.is(200))
    )
      .pause(Environment.minThinkTime)
      .exec(http("EXUI_AO_010_Login")
        .get(url_approve + "/api/organisations?status=PENDING")
        .check(status.is(200)))
      .exec(http("EXUI_AO_015_Login")
        .get(url_approve + "/api/organisations")
        .check(status.is(200))
    )

    .pause(30)



  val approveOrganisationApprove =
    exec(http("EXUI_AO_Approve")
      .put(url_approve+"/api/organisations/${orgRefCode}")
      .body(ElFileBody("AO.json")).asJson
      .check(status.is(200)))
      .pause(40)
        .exec {

        session =>
          val client = new NotificationClient(notificationClient)
          val pattern = new Regex("token.+")
          val str = findEmail(client,session("generatedEmail").as[String])
           session.set("activationLink", (pattern findFirstMatchIn str.get).mkString.trim.replace(")", ""))
      }
      .pause(40)
      .exec(http("SelfReg01_TX03_Password")
        .get(IdamUrl+"/users/register?&${activationLink}")
        .check(status.is(200))
        .check(css("input[name='token']", "value").saveAs("token"))
        .check(css("input[name='code']", "value").saveAs("code"))
        .check(css("input[name='_csrf']", "value").saveAs("_csrf")))
      .pause(40)
      .exec(http("SelfReg01_TX04_Activate").post(IdamUrl+"/users/activate")
        .formParam("_csrf", "${_csrf}")
        .formParam("code", "${code}")
        .formParam("token", "${token}")
        .formParam("password1", "Pass19word")
        .formParam("password2", "Pass19word")
        .check(status.is(200)))
      .pause(40)

    .exec {
    session =>
      val fw = new BufferedWriter(new FileWriter("OrgDetails.csv", true))
      try {
        fw.write(session("orgName").as[String] + ","+session("orgRefCode").as[String] + "," + session("generatedEmail").as[String]  + "\r\n")
      }
      finally fw.close()
      session
  }

  val approveOrganisationLogout =
    exec(http("EXUI_AO_005_Logout")
      .get(url_approve + "/api/logout")
      .check(status.is(200)))

  // following code is for manage organisation


  val manageOrgHomePage =
    feed(feederuser).
    exec(http("EXUI_MO_005_Homepage")
      .get(url_mo + "/")
     // .headers(headers_0)
      .check(status.is(200)))

      .exec(http("EXUI_MO_010_Homepage")
        .get(url_mo + "/api/user/details")
       // .headers(headers_1)
        .check(status.is(401)))

      .exec(http("EXUI_MO_015_Homepage")
        .get(IdamUrl + "/?response_type=code&client_id=xuimowebapp&redirect_uri="+url_mo+"/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
        //.headers(headers_2)
        .check(regex("Sign in"))
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken1"))
      )

    .pause(Environment.minThinkTime)

  val manageOrganisationLogin =
    exec(http("EXUI_MO_005_Login")
      .post(IdamUrl + "/login?scope=openid+profile+roles+manage-user+create-user&response_type=code&redirect_uri=https%3a%2f%2fmanage-org.aat.platform.hmcts.net%2foauth2%2fcallback&client_id=xuimowebapp")
      .formParam("username", "${generatedEmail1}")
      .formParam("password", "Pass19word")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrfToken1}")
      .check(status.in(200, 302)))


    .exec(http("EXUI_MO_060_050_Login")
          .get(url_mo + "/api/user/details")
          .check(status.in(200, 302,304))
    .check(jsonPath("$.userId").optional.saveAs("myUserId")))


      .exec( session => {
        println("userId is "+session("myUserId").as[String])
        session
      })

      .exec(http("XUI_020_010_SignInTCEnabled")
            .get(url_mo+"/api/configuration?configurationKey=feature.termsAndConditionsEnabled")
            .check(status.in(200, 304)))

      .exec(http("XUI_020_010_SignInTCEnabled")
            .get(url_mo+"/api/userTermsAndConditions/${myUserId}")
            .check(status.in(200, 304)))


      .exec(http("EXUI_MO_020_Login")
            .get(url_mo + "/api/organisation/")
            .check(status.in(200, 302,304)))
      .pause(Environment.constantthinkTime)

    .exec(http("XUIASD_035_005_ConfirmT&C")
       .post(url_mo+"/api/userTermsAndConditions")
      .headers(headers_tc_aat)
       .body(StringBody("{\"userId\":\"${myUserId}\"}"))
       .check(status.in(200, 304, 302)))


      .exec(http("XUIASD_035_010_ConfirmT&C")
            .post(url_mo+"/api/userTermsAndConditions")
        .headers(headers_tc_aat)
            .body(StringBody("{\"userId\":\"${myUserId}\"}"))
            .check(status.in(200, 304, 302)))


      .exec(http("EXUI_MO_065_050_Login")
            .get(url_mo + "/api/user/details")
            .check(status.in(200, 302,304)))
      .pause(Environment.constantthinkTime)

      .exec(http("XUI_025_010_SignInTCEnabled")
            .get(url_mo+"/api/configuration?configurationKey=feature.termsAndConditionsEnabled")
            .check(status.in(200, 304)))


  val usersPage =
    exec(http("EXUI_MO_005_Userspage")
      .get(url_mo + "/api/userList")
      .check(status.is(200)))
    .pause(Environment.constantthinkTime)

  val inviteUserPage =
    exec(http("EXUI_MO_005_InviteUserpage")
      .get(url_mo + "/api/jurisdictions")
      .check(status.is(200)))
    .pause(Environment.minThinkTime)

  val sendInvitation =
        //feed(feeder).
  feed(Feeders.createDynamicUserDataFeeder).
        exec(http("EXUI_MO_005_SendInvitation")
      .post(url_mo + "/api/inviteUser")
      .body(ElFileBody("MO.json")).asJson
      .check(status.is(200))
      ).exitHereIfFailed
        .pause(40)
      .exec {

        session =>
          val client = new NotificationClient(notificationClient)
          val pattern = new Regex("token.+")
         // val str = findEmail(client,session("orgName").as[String]+"_user"+session("userid").as[String]+"@mailinator.com")
         val str = findEmail(client,session("orgName").as[String]+session("generatedUserEmail").as[String])
          session.set("activationLink", (pattern findFirstMatchIn str.get).mkString.trim.replace(")", ""))
      }
      .pause(40)
      .exec(http("InviteUser_TX03_Password")
        .get(IdamUrl+"/users/register?&${activationLink}")
        .check(status.is(200))
        .check(css("input[name='token']", "value").saveAs("token"))
        .check(css("input[name='code']", "value").saveAs("code"))
        .check(css("input[name='_csrf']", "value").saveAs("_csrf")))
      .pause(40)
      .exec(http("SelfReg01_TX04_Activate").post(IdamUrl+"/users/activate")
        .formParam("_csrf", "${_csrf}")
        .formParam("code", "${code}")
        .formParam("token", "${token}")
        .formParam("password1", "Pass19word")
        .formParam("password2", "Pass19word")
        .check(status.is(200))
        .check(status.saveAs("statusvalue")))
           .doIf(session=>session("statusvalue").as[String].contains("200")) {
            exec {
            session =>
              val fw = new BufferedWriter(new FileWriter("OrgId3.csv", true))
              try {
                fw.write(session("orgName").as[String] + ","+session("orgRefCode").as[String] + "," + session("generatedEmail1").as[String] +","+ session("orgName").as[String]+session("generatedUserEmail").as[String] + "\r\n")
              }
              finally fw.close()
              session
          }
        }

      .pause(Environment.constantthinkTime)

  val manageOrganisationLogout =
    exec(http("EXUI_MO_005_Logout")
      .get(url_mo + "/api/logout")
      .check(status.is(200)))

      .exec(http("EXUI_MO_010_Logout")
        .get(url_mo + "/api/user/details")
        .check(status.is(401)))

      .exec(http("EXUI_MO_015_Logout")
        .get(IdamUrl + "/?response_type=code&client_id=xuimowebapp&redirect_uri="+url_mo+"/oauth2/callback&scope=openid%20profile%20roles%20manage-user%20create-user")
    )

  // email notification related stuff
  def findEmail(client: NotificationClient, emailAddress:String) : Option[String] = {
    var emailBody = findEmailByStatus(client, emailAddress, "created")
    if (emailBody.isDefined) {
      return emailBody
    }
    emailBody = findEmailByStatus(client, emailAddress, "sending")
    if (emailBody.isDefined) {
      return emailBody
    }
    emailBody = findEmailByStatus(client, emailAddress, "delivered")
    if (emailBody.isDefined) {
      return emailBody
    }
    findEmailByStatus(client, emailAddress, "failed")
  }

  def findEmailByStatus(client: NotificationClient, emailAddress: String, status: String) : Option[String] = {
    val notificationList = client.getNotifications(status, "email", null, null)
    println("Searching notifications from " + status)
    val emailBody = getEmailBodyByEmailAddress(notificationList, emailAddress)
    if (emailBody.isDefined) {
      return emailBody
    }
    None
  }

  def getEmailBodyByEmailAddress(notifications: NotificationList, emailAddress: String) : Option[String] = {
    for(notification <- notifications.getNotifications.asScala) {
      if (notification.getEmailAddress.get().equalsIgnoreCase(emailAddress)) {
        println("Found match for email " + emailAddress)
        return Some(notification.getBody)
      } else {
        println("Comparing " + notification.getEmailAddress.get() + " with " + emailAddress)
      }
    }
    None
  }

}
