package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import utils._

import scala.io.Source
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType

import scala.concurrent.duration._
import scala.util.Random

class XUI_Simulation extends Simulation {
	val CivilLoginFeeder = csv("CivilLogin.csv").circular
	val CaseworkerUserFeeder = csv("UserDataCaseworkers.csv").circular
	val UserFeederDivorce = csv("UserDataDivorce.csv").circular
	val UserFeederFPL = csv("UserDataFPL.csv").circular
	val UserFeederFR = csv("UserDataFR.csv").circular
	val UserFeederIAC = csv("UserDataIAC.csv").circular
	val UserFeederNFD = csv("UserDataNFD.csv").circular
	val UserFeederProbate = csv("UserDataProbate.csv").circular
	val UserFeederPRL = csv("UserDataPRL.csv").circular
	val UserFeederBails = csv("UserDataBails.csv").circular
	val UserFeederBailsHearings = csv("UserDataBailsHearings.csv").circular
	val UserFeederBailsHO = csv("UserDataBailsHO.csv").circular
	val UserFeederBailsJudge = csv("UserDataBailsJudge.csv").circular
	val UserFeederCivilHearing = csv("CivilHearingsCasesForAllHearings.csv").circular
	val SSCSHearingUserFeeder = csv("SSCSHearingsUserData.csv").circular
	val UserFeederCivilHearingCases = csv("CivilHearingsCasesForAllHearings.csv").circular
	val UserFeederPRLHearing = csv("UserDataPRLHearings.csv").circular
	val UserFeederPRLHearingCases = csv("UserDataPRLHearingsCases.csv").circular
	val nfdSoleLabelsInitialised = Source.fromResource("bodies/nfd/labels/soleLabelsInitialised.txt").mkString
	val nfdSoleLabelsPopulated = Source.fromResource("bodies/nfd/labels/soleLabelsPopulated.txt").mkString
	val nfdJointLabelsInitialised = Source.fromResource("bodies/nfd/labels/jointLabelsInitialised.txt").mkString
	val nfdJointLabelsPopulated = Source.fromResource("bodies/nfd/labels/jointLabelsPopulated.txt").mkString

	val randomFeeder = Iterator.continually(Map("prl-percentage" -> Random.nextInt(100)))

	/* TEST TYPE DEFINITION */
	/* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
	/* perftest (default) = performance test against the perftest environment */
	val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

	//set the environment based on the test type
	val environment = testType match {
		case "perftest" => "perftest"
		//TODO: UPDATE PIPELINE TO 'aat' ONCE DATA STRATEGY IS IMPLEMENTED. UNTIL THEN, PIPELINE WILL RUN AGAINST PERFTEST
		case "pipeline" => "perftest"
		case _ => "**INVALID**"
	}

	/* ******************************** */
	/* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
	val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
	val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
	/* ******************************** */

	/* PERFORMANCE TEST CONFIGURATION */
	val hearingsTargetPerHour: Double = 1

	val bailsTargetPerHour: Double = 100
	val prlTargetPerHour: Double = 100
	val probateTargetPerHour: Double = 238
	val iacTargetPerHour: Double = 20
	val fplTargetPerHour: Double = 7
	val divorceTargetPerHour: Double = 238
	val nfdSoleTargetPerHour: Double = 119
	val nfdJointTargetPerHour: Double = 119
	val frTargetPerHour: Double = 98
	val caseworkerTargetPerHour: Double = 900

	//This determines the percentage split of PRL journeys, by C100 or FL401
	val prlC100Percentage = 66 //Percentage of C100s (the rest will be FL401s) - should be 66 for the 2:1 ratio

	val rampUpDurationMins = 5
	val rampDownDurationMins = 5
	val testDurationMins = 60

	val numberOfPipelineUsers = 5
	val pipelinePausesMillis: Long = 3000 //3 seconds

	//Determine the pause pattern to use:
	//Performance test = use the pauses defined in the scripts
	//Pipeline = override pauses in the script with a fixed value (pipelinePauseMillis)
	//Debug mode = disable all pauses
	val pauseOption: PauseType = debugMode match {
		case "off" if testType == "perftest" => constantPauses
		case "off" if testType == "pipeline" => customPauses(pipelinePausesMillis)
		case _ => disabledPauses
	}

	val httpProtocol = http
		.baseUrl(Environment.baseURL.replace("${env}", s"${env}"))
		//	.inferHtmlResources()
		.silentResources
		.header("experimental", "true") //used to send through client id, s2s and bearer tokens. Might be temporary
	before {
		println(s"Test Type: ${testType}")
		println(s"Test Environment: ${env}")
		println(s"Debug Mode: ${debugMode}")
	}

	/*===============================================================================================
	* XUI Solicitor Private Law Scenario
 	===============================================================================================*/
	val PRLSolicitorScenario = scenario("***** Private Law Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederPRL)
				.exec(_.set("env", s"${env}")
					.set("caseType", "PRLAPPS"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.feed(randomFeeder)
				.doIfOrElse(session => session("prl-percentage").as[Int] < prlC100Percentage) {
					//C100 Journey
					exec(Solicitor_PRL_C100.CreatePrivateLawCase)
						.exec(Solicitor_PRL_C100.TypeOfApplication)
						.exec(Solicitor_PRL_C100.HearingUrgency)
						.exec(Solicitor_PRL_C100.ApplicantDetails)
						.exec(Solicitor_PRL_C100.ChildDetails)
						.exec(Solicitor_PRL_C100.RespondentDetails)
						.exec(Solicitor_PRL_C100.MIAM)
						.exec(Solicitor_PRL_C100.AllegationsOfHarm)
						.exec(Solicitor_PRL_C100.ViewPdfApplication)
						.exec(Solicitor_PRL_C100.SubmitAndPay)

				} {
					//FL401 Journey
					exec(Solicitor_PRL_FL401.CreatePrivateLawCase)
						.exec(Solicitor_PRL_FL401.TypeOfApplication)
						.exec(Solicitor_PRL_FL401.WithoutNoticeOrder)
						.exec(Solicitor_PRL_FL401.ApplicantDetails)
						.exec(Solicitor_PRL_FL401.RespondentDetails)
						.exec(Solicitor_PRL_FL401.ApplicantsFamily)
						.exec(Solicitor_PRL_FL401.Relationship)
						.exec(Solicitor_PRL_FL401.Behaviour)
						.exec(Solicitor_PRL_FL401.TheHome)
						.exec(Solicitor_PRL_FL401.UploadDocuments)
						.exec(Solicitor_PRL_FL401.ViewPDF)
						.exec(Solicitor_PRL_FL401.StatementOfTruth)
				}
				.exec(Logout.XUILogout)
		}


	/*===============================================================================================
	* XUI Legal Rep Bails Scenario
 	===============================================================================================*/
	val BailsScenario = scenario("***** Bails Create Application *****")
		.exitBlockOnFail {
			feed(UserFeederBails)
				.exec(_.set("env", s"${env}")
					.set("caseType", "Bail"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_Bails.CreateBailApplication)
				.exec(Solicitor_Bails.SubmitBailApplication)
				.exec(Logout.XUILogout)
				.feed(UserFeederBailsHO)
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_Bails.UploadBailSummary)
				.exec(Logout.XUILogout)
				.feed(UserFeederBailsJudge)
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_Bails.RecordBailDecision)
				.exec(Solicitor_Bails.UploadSignedDecision)
				.exec(Logout.XUILogout)

				.exec {
					session =>
						println(session)
						session
				}
		}

	/*===============================================================================================
	* XUI Solicitor Probate Scenario
	 ===============================================================================================*/
	val ProbateSolicitorScenario = scenario("***** Probate Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederProbate)
				.exec(_.set("env", s"${env}")
					.set("caseType", "GrantOfRepresentation"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_Probate.CreateProbateCase)
						.exec(Solicitor_Probate.AddDeceasedDetails)
						.exec(Solicitor_Probate.AddApplicationDetails)
						.exec(Solicitor_Probate.ReviewAndSubmitApplication)
				}
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor IAC Scenario
	 ===============================================================================================*/
	val ImmigrationAndAsylumSolicitorScenario = scenario("***** Civil Hearing Management *****")
		.exitBlockOnFail {
			feed(UserFeederIAC)
				.exec(_.set("env", s"${env}")
					.set("caseType", "Asylum"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_IAC.CreateIACCase)
						.exec(Solicitor_IAC.shareacase)
				}
				.exec(Logout.XUILogout)
		}





	/*===============================================================================================
* XUI Civil Hearing Management Scenario
 ===============================================================================================*/
	// in the below scenario we may need conditional statements as per the requisite

	val SSCSHearingsScenario = scenario("***** SSCS Hearing Management *****")

		.feed(SSCSHearingUserFeeder)
		//.exitBlockOnFail {

		.repeat(1) {
			exec(_.set("env", s"${env}")
				.set("caseType", "Civil hearings")
			)
				.exitBlockOnFail {
					exec(Homepage.XUIHomePage)
						.exec(Login.XUILogin)
				}
				.pause(10)
				.repeat(1) {
					exec(SSCS_Hearings.ViewAllHearings)
						.exec(SSCS_Hearings.RequestHearing)
						.exec(SSCS_Hearings.GetHearing)
						.exec(SSCS_Hearings.UpdateHearing)
						.pause(10)
						.exec(SSCS_Hearings.GetHearing)
						.exec(SSCS_Hearings.cancelHearing)
						.repeat(6) {
							exec(SSCS_Hearings.ViewAllHearings)
								.exec(SSCS_Hearings.RequestHearing)
								.exec(SSCS_Hearings.GetHearing)

								.repeat(11) {
									exec(SSCS_Hearings.ViewAllHearings)
										.exec(SSCS_Hearings.GetHearing)
								}
						}
				}
				.exec(Logout.XUILogout)
		}
	//	}


	/*===============================================================================================
  * XUI Civil Hearing Management Scenario
   ===============================================================================================*/
	// in the below scenario we may need conditional statements as per the requisite

	val CivilHearingsScenario = scenario("***** Civil Hearing Management *****")

		.feed(CivilLoginFeeder)
		//.exitBlockOnFail {

		.repeat(1) {
			exec(_.set("env", s"${env}")
				.set("caseType", "Civil hearings")
			)
				.exitBlockOnFail {
					exec(Homepage.XUIHomePage)
						.exec(Login.XUILogin)
				}
				.pause(10)
				.repeat(1) {
					exec(Civil_Hearings.ViewAllHearings)
						.exec(Civil_Hearings.RequestHearing)
						.exec(Civil_Hearings.GetHearing)
						.exec(Civil_Hearings.UpdateHearing)
						.exec(Civil_Hearings.GetHearing)
						.pause(20)
						.exec(Civil_Hearings.CancelHearing)
						.exec(Civil_Hearings.GetHearing)
						.repeat(times = 6) {
							exec(Civil_Hearings.ViewAllHearings)
								.exec(Civil_Hearings.RequestHearing)
								.exec(Civil_Hearings.GetHearing)
						}
						.repeat(times = 19) {
							exec(Civil_Hearings.ViewAllHearings)
								.exec(Civil_Hearings.GetHearing)
						}
				}


				.exec(Logout.XUILogout)
		}
	//	}


	/*===============================================================================================
* XUI PRL Hearing Management Scenario
 ===============================================================================================*/
	// in the below scenario we may need conditional statements as per the requisite

	val PRLHearingsScenario = scenario("***** PRL Hearing Management *****")
		.feed(UserFeederPRLHearing)
		//	.exitBlockOnFail {
		.repeat(1) {

			exec(_.set("env", s"${env}")
				.set("caseType", "Benefit"))
				.exitBlockOnFail {
					exec(Homepage.XUIHomePage)
						.exec(Login.XUILogin)
				}
				.pause(10)
				.repeat(1) {
					exec(PRL_Hearings.ViewAllHearings)
						.exec(PRL_Hearings.RequestHearing)
						.exec(PRL_Hearings.GetHearing)
						.exec(PRL_Hearings.UpdateHearing)
						.exec(PRL_Hearings.GetHearing)
						.pause(20)
						.exec(PRL_Hearings.CancelHearing)
						.exec(PRL_Hearings.GetHearing)
						.repeat(2) {
							exec(PRL_Hearings.ViewAllHearings)
								.exec(PRL_Hearings.RequestHearing)
								.exec(PRL_Hearings.GetHearing)
						}
				}
				.exec(Logout.XUILogout)
		}
	//		}


	/*===============================================================================================
* XUI Hearing Bails Scenario
 ===============================================================================================*/
	val BailsHearingsScenario = scenario("***** Bails Hearing *****")
		.exitBlockOnFail {
			feed(UserFeederBails)
				.repeat(1) {
					exec(_.set("env", s"${env}")
						.set("caseType", "Bail")
					)
						.exec(Homepage.XUIHomePage)
						.exec(Login.XUILogin)
				}
				.pause(10)
					.exec(Solicitor_BailsHearings.ViewAllHearings)
						.exec(Solicitor_BailsHearings.RequestHearing)
						.exec(Solicitor_BailsHearings.ViewHearing)
						.exec(Solicitor_BailsHearings.AmendHearing)
						.exec(Solicitor_BailsHearings.ViewHearing)
						.exec(Solicitor_BailsHearings.CancelHearing)
							.exec(Solicitor_BailsHearings.ViewAllHearings)
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor Divorce Scenario
	 ===============================================================================================*/
	val DivorceSolicitorScenario = scenario("***** Divorce Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederDivorce)
				.exec(_.set("env", s"${env}")
					.set("caseType", "DIVORCE"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_Divorce.CreateDivorceCase)
				}
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor NFD Scenario (Sole Application)
	 ===============================================================================================*/
	val NoFaultDivorceSolicitorSoleScenario = scenario("***** NFD Create Case (Sole) *****")
		.exitBlockOnFail {
			//feed two rows of data - applicant1's solicitor and applicant2's solicitor
			feed(UserFeederNFD, 2)
				.exec(_.set("env", s"${env}")
					.set("caseType", "NFD")
					.set("nfdCaseType", "sole")
					.set("NFDLabelsInitialised", nfdSoleLabelsInitialised) //sets the initialised labels for JSON bodies
					.set("NFDLabelsPopulated", nfdSoleLabelsPopulated)) //sets the populated labels for JSON bodies
				//Solicitor 1 - Divorce Application
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.CreateNFDCase)
				.exec(Solicitor_NFD.SignAndSubmitSole)
				.exec(Logout.XUILogout)
				//Caseworker - Issue Application
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-issue-application", "bodies/nfd/CWIssueApplication.json"))
				//set 'user'/'password' to the second one (applicant2's solicitor) for assigning the case and login
				.exec(session => session.set("user", session("user2").as[String]).set("password", session("password2").as[String]))
				//Update the case in CCD to assign it to the second solicitor
				.exec(CCDAPI.AssignCase)
				//Solicitor 2 - Respond to Divorce Application
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.RespondToNFDCase)
				.exec(Logout.XUILogout)
				//Caseworker - Mark the Case as Awaiting Conditional Order (to bypass 20-week holding)
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-progress-held-case", "bodies/nfd/CWAwaitingConditionalOrder.json"))
				//Solicitor 1 - Apply for Conditional Order
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.ApplyForFO)
				.exec(Logout.XUILogout)
				//Legal Advisor - Grant Conditional Order
				.exec(CCDAPI.CreateEvent("Legal", "DIVORCE", "NFD", "legal-advisor-make-decision", "bodies/nfd/LAMakeDecision.json"))
				//Caseworker - Make Eligible for Final Order
				.exec(
					//link with bulk case
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-link-with-bulk-case", "bodies/nfd/CWLinkWithBulkCase.json"),
					//set case hearing and decision dates to a date in the past
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-update-case-court-hearing", "bodies/nfd/CWUpdateCaseWithCourtHearing.json"),
					//set judge details, CO granted and issued dates in the past
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-amend-case", "bodies/nfd/CWSetCODetails.json"),
					//pronounce case
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-pronounce-case", "bodies/nfd/CWPronounceCase.json"),
					//set final order eligibility dates
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-amend-case", "bodies/nfd/CWSetFOEligibilityDates.json"),
					//set case as awaiting final order
					CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-progress-case-awaiting-final-order", "bodies/nfd/CWAwaitingFinalOrder.json"))
			//TODO: ADD FINAL ORDER HERE ONCE DEVELOPED
		}

	/*===============================================================================================
	* XUI Solicitor NFD Scenario (Joint Application)
	 ===============================================================================================*/
	val NoFaultDivorceSolicitorJointScenario = scenario("***** NFD Create Case (Joint) *****")
		.exitBlockOnFail {
			//feed two rows of data - applicant1's solicitor and applicant2's solicitor
			feed(UserFeederNFD, 2)
				.exec(_.set("env", s"${env}")
					.set("caseType", "NFD")
					.set("nfdCaseType", "joint")
					.set("NFDLabelsInitialised", nfdJointLabelsInitialised) //sets the initialised labels for JSON bodies
					.set("NFDLabelsPopulated", nfdJointLabelsPopulated)) //sets the populated labels for JSON bodies
				//Solicitor 1 - Divorce Application
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.CreateNFDCase)
				.exec(Solicitor_NFD.JointInviteApplicant2)
				.exec(Logout.XUILogout)
				//set 'user'/'password' to the second one (applicant2's solicitor) for assigning the case and login
				.exec(session => session.set("user", session("user2").as[String]).set("password", session("password2").as[String]))
				//Update the case in CCD to assign it to the second solicitor
				.exec(CCDAPI.AssignCase)
				//Solicitor 2 - Confirm Divorce Application
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.SubmitJointApplication)
				.exec(Logout.XUILogout)
				//Solicitor 1 - Submit Application
				.exec(Homepage.XUIHomePage)
				//since two records were grabbed, set 'user'/'password' to the first one (applicant1's solicitor) for login
				.exec(session => session.set("user", session("user1").as[String]).set("password", session("password1").as[String]))
				.exec(Login.XUILogin)
				.exec(Solicitor_NFD.SignAndSubmitJoint)
				.exec(Logout.XUILogout)
				//Caseworker - Issue Application
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "caseworker-issue-application", "bodies/nfd/CWIssueApplication.json"))
				//Caseworker - Mark the Case as Awaiting Conditional Order (to bypass 20-week holding)
				.exec(CCDAPI.CreateEvent("Caseworker", "DIVORCE", "NFD", "system-progress-held-case", "bodies/nfd/CWAwaitingConditionalOrder.json"))
			//TODO: ADD CONDITIONAL ORDER HERE ONCE DEVELOPED
			//TODO: ADD FINAL ORDER HERE ONCE DEVELOPED
		}

	/*===============================================================================================
	* XUI Solicitor Financial Remedy (FR) Scenario
	 ===============================================================================================*/
	val FinancialRemedySolicitorScenario = scenario("***** FR Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederFR)
				.exec(_.set("env", s"${env}")
					.set("caseType", "FinancialRemedyMVP2"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.repeat(2) {
					exec(Solicitor_FR.CreateFRCase)
				}
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Solicitor Family Public Law (FPL) Scenario
	 ===============================================================================================*/
	val FamilyPublicLawSolicitorScenario = scenario("***** FPL Create Case *****")
		.exitBlockOnFail {
			feed(UserFeederFPL)
				.exec(_.set("env", s"${env}")
					.set("caseType", "CARE_SUPERVISION_EPO"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Solicitor_FPL.CreateFPLCase)
				.exec(Solicitor_FPL.fplOrdersAndDirections)
				.exec(Solicitor_FPL.fplHearingUrgency)
				.exec(Solicitor_FPL.fplGrounds)
				.exec(Solicitor_FPL.fplLocalAuthority)
				.exec(Solicitor_FPL.fplChildDetails)
				.exec(Solicitor_FPL.fplRespondentDetails)
				.exec(Solicitor_FPL.fplAllocationProposal)
				.exec(Solicitor_FPL.fplSubmitApplication)
				.exec(Solicitor_FPL.fplReturnToCase)
				.exec(Logout.XUILogout)
		}

	/*===============================================================================================
	* XUI Caseworker - Search & View Case Scenario
	 ===============================================================================================*/
	val CaseworkerScenario = scenario("***** Caseworker Journey ******")
		.exitBlockOnFail {
			feed(CaseworkerUserFeeder)
				//TODO: UPDATE caseType with something more dynamic
				.exec(_.set("env", s"${env}")
					.set("caseType", "NFD"))
				.exec(Homepage.XUIHomePage)
				.exec(Login.XUILogin)
				.exec(Caseworker_Navigation.ApplyFilter)
				.exec(Caseworker_Navigation.SortByLastModifiedDate)
				.exec(Caseworker_Navigation.LoadPage2)
				//Only continue with the case activities if results were returned
				.doIf(session => session("numberOfResults").as[Int] > 0) {
					exec(Caseworker_Navigation.SearchByCaseNumber)
						.exec(Caseworker_Navigation.ViewCase)
						.exec(Caseworker_Navigation.NavigateTabs)
				}
				.exec(Caseworker_Navigation.LoadCaseList)
				.exec(Logout.XUILogout) exec (SSCS_Hearings.ViewAllHearings)
				//		exec(Solicitor_Hearings.UploadResponse)
				.exec(SSCS_Hearings.RequestHearing)
		}

	/*===============================================================================================
          * XUI Civil Hearing Management DATA PREP SCENARIO for Request Hearing
           ===============================================================================================*/
	// in the below scenario we may need conditional statements as per the requisite

	val CivilHearingsDataPrep = scenario("***** Civil Hearing Data Prep *****")

		.feed(CivilLoginFeeder)
		.exitBlockOnFail {

			repeat(1) {
				exec(_.set("env", s"${env}")
					.set("caseType", "Civil hearings")
				)

					.exec(Homepage.XUIHomePage)
					.exec(Login.XUILogin)
					.pause(10)
					.repeat(1) {
						//		exec(Civil_Hearings.ViewAllHearings)
						exec(Civil_Hearings.RequestHearing)
							//		.exec(Civil_Hearings.GetHearing)
							//		.exec(Civil_Hearings.UpdateHearing)
							//		.exec(Civil_Hearings.GetHearing)
							.pause(1)
						//		.exec(Civil_Hearings.CancelHearing)
						//				.exec(Civil_Hearings.GetHearing)
						//		.repeat(times=6)
						//		{
						//			exec(Civil_Hearings.ViewAllHearings)
						//				exec(Civil_Hearings.RequestHearing)
						//				.exec(Civil_Hearings.GetHearing)
					}
					//				.repeat(times = 19) {
					//					exec(Civil_Hearings.ViewAllHearings)
					//						.exec(Civil_Hearings.GetHearing)
									//}
					.exec(Logout.XUILogout)
			}
		}



	/*===============================================================================================
* XUI PRL Hearing Data Prep for Request hearing
 ===============================================================================================*/
	// in the below scenario we may need conditional statements as per the requisite

	val PRLHearingDataPrep = scenario("***** PRL Hearing Data Prep for Request Hearing *****")
	.feed(UserFeederPRLHearing)
			.exitBlockOnFail {
				repeat(1) {

					exec(_.set("env", s"${env}")
						.set("caseType", "Benefit"))
						.exec(Homepage.XUIHomePage)
						.exec(Login.XUILogin)
						.pause(10)
						.repeat(1) {
                  //              exec(PRL_Hearings.ViewAllHearings)
							    exec(PRL_Hearings.RequestHearing)
					//			.exec(PRL_Hearings.GetHearing)
					//			.exec(PRL_Hearings.UpdateHearing)
					//			.exec(PRL_Hearings.GetHearing)
					//			.pause(20)
					//			.exec(PRL_Hearings.CancelHearing)
					//			.exec(PRL_Hearings.GetHearing)
								.repeat(2)
							{
					//			exec(PRL_Hearings.ViewAllHearings)
									exec(PRL_Hearings.RequestHearing)
					//				.exec(PRL_Hearings.GetHearing)
							}
						}
								.exec(Logout.XUILogout)
						}
				}

	/*===============================================================================================
	* Simulation Configuration
	 ===============================================================================================*/

	def simulationProfile(simulationType: String, userPerHourRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
		val userPerSecRate = userPerHourRate / 3600
		simulationType match {
			case "perftest" =>
				if (debugMode == "off") {
					Seq(
						rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins minutes),
						constantUsersPerSec(userPerSecRate) during (testDurationMins minutes),
						rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins minutes)
					)
				}
				else {
					Seq(atOnceUsers(1))
				}
			case "pipeline" =>
				Seq(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
			case _ =>
				Seq(nothingFor(0))
		}
	}

	//defines the test assertions, based on the test type
	def assertions(simulationType: String): Seq[Assertion] = {
		simulationType match {
			case "perftest" | "pipeline" => //currently using the same assertions for a performance test and the pipeline
				if (debugMode == "off") {
					Seq(global.successfulRequests.percent.gte(95),
						details("XUI_PRL_C100_460_SubmitAndPayNow").successfulRequests.percent.gte(80),
						details("XUI_PRL_FL401_490_SOTSubmit").successfulRequests.percent.gte(80),
						details("XUI_Probate_300_ViewCase").successfulRequests.percent.gte(80),
						details("XUI_IAC_310_ShareACaseConfirm").successfulRequests.percent.gte(80),
						details("XUI_FPL_330_ReturnToCase").successfulRequests.percent.gte(80),
						details("XUI_Divorce_270_SubmitPetition").successfulRequests.percent.gte(80),
						details("XUI_000_CCDEvent-system-progress-case-awaiting-final-order").successfulRequests.percent.gte(80), //NFD Sole
						details("XUI_000_CCDEvent-system-progress-held-case").successfulRequests.percent.gte(80), //NFD Joint
						details("XUI_FR_170_SubmitApplication").successfulRequests.percent.gte(80),
						details("XUI_Caseworker_080_CaseList").successfulRequests.percent.gte(80))
				}
				else {
					Seq(global.successfulRequests.percent.is(100))
				}
			case _ =>
				Seq()
		}
	}

//	setUp(
	//	HearingsScenario.inject(simulationProfile(testType, hearingsTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption)
			/*
		BailsScenario.inject(simulationProfile(testType, bailsTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		PRLSolicitorScenario.inject(simulationProfile(testType, prlTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		ProbateSolicitorScenario.inject(simulationProfile(testType, probateTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		ImmigrationAndAsylumSolicitorScenario.inject(simulationProfile(testType, iacTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		FamilyPublicLawSolicitorScenario.inject(simulationProfile(testType, fplTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		DivorceSolicitorScenario.inject(simulationProfile(testType, divorceTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		NoFaultDivorceSolicitorSoleScenario.inject(simulationProfile(testType, nfdSoleTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		NoFaultDivorceSolicitorJointScenario.inject(simulationProfile(testType, nfdJointTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		FinancialRemedySolicitorScenario.inject(simulationProfile(testType, frTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption),
		CaseworkerScenario.inject(simulationProfile(testType, caseworkerTargetPerHour, numberOfPipelineUsers)).pauses(pauseOption)

			 */
	//).protocols(httpProtocol)
//		.assertions(assertions(testType))
//		.maxDuration(60 minutes)


	//Below setup is for running the Hearing Scenario

	setUp(
	(SSCSHearingsScenario.inject(nothingFor(10),rampUsers(50).during(2800))),//50, 2800
		(PRLHearingsScenario.inject(nothingFor(30),rampUsers(40).during(2800))),//40,2800
		(CivilHearingsScenario.inject(nothingFor(60),rampUsers(14).during(2800))) //14, 2800
	)
		.protocols(httpProtocol)
	.maxDuration(4000)

	//	setUp(
    	//	(PRLHearingsScenario.inject(nothingFor(5),rampUsers(1).during(1)))
    	//	(CivilHearingsDataPrep.inject(nothingFor(1),rampUsers(2000).during(5000)))
    //	)
    //		.protocols(httpProtocol)
		//	.maxDuration(20000000)
	
	

}
