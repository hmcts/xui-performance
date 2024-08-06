package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import scala.collection.immutable.ArraySeq

object ETAddDocumentsToMultiple {

  val CcdAPIURL = Environment.ccdAPIURL
  val CaseDocAPIURL = Environment.caseDocAPIURL

  // Initialise array to store extracted variables within the session.
  val initialiseDocLinkSelfArray = exec { session =>
    session.set("responseArrayDocLinkSelfHash", List.empty[String])
  }

  val numberOfDocumentsToUpload: Int = 25 // Configure this value (10,25,50) for below switch case

  // Use a match case to define different arrays and caseName based on the number of documents to upload
  val (files, caseName) = numberOfDocumentsToUpload match {
    case 10 =>
      val caseName = "10 Docs"
      //Define all 10 Documents as an array with mapped assosciated params
      val files = Array(
        Map("fileName" -> "et1-eng-original_16Page_388KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1 Vetting - Perf Test_1Page_41KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Judgment_2Page_83KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Judgment", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Reasons_3Page_56KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Reasons", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Change of Party Details_1Page_13KB.docx", "topLevelDocuments" -> "Case Management", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ACAS Cert_Image_19KB.jpg", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ACAS Certificate", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET Notice of Hearing_Image_241KB.jpeg", "topLevelDocuments" -> "Hearings", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "Notice of Hearing", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1_CASE_DOCUMENT_Citizen_Five_16Page_419KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1 Vetting - Citizen Five_caseStart_2Page_43KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET3_Response_9page_92KB.pdf", "topLevelDocuments" -> "Response to a Claim", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> "ET3")
      )
      (files, caseName)

    case 25 =>
      val caseName = "25 Docs"
      val files = Array(
        Map("fileName" -> "et1-eng-original_16Page_388KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1 Vetting - Perf Test_1Page_41KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Judgment_2Page_83KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Judgment", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Reasons_3Page_56KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Reasons", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Change of Party Details_1Page_13KB.docx", "topLevelDocuments" -> "Case Management", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ACAS Cert_Image_19KB.jpg", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ACAS Certificate", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET Notice of Hearing_Image_241KB.jpeg", "topLevelDocuments" -> "Hearings", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "Notice of Hearing", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1_CASE_DOCUMENT_Citizen_Five_16Page_419KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1 Vetting - Citizen Five_caseStart_2Page_43KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET3_Response_9page_92KB.pdf", "topLevelDocuments" -> "Response to a Claim", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> "ET3"),
        Map("fileName" -> "et1-eng-original_16Page_388KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1 Vetting - Perf Test_1Page_41KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Judgment_2Page_83KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Judgment", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Reasons_3Page_56KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Reasons", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Change of Party Details_1Page_13KB.docx", "topLevelDocuments" -> "Case Management", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ACAS Cert_Image_19KB.jpg", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ACAS Certificate", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET Notice of Hearing_Image_241KB.jpeg", "topLevelDocuments" -> "Hearings", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "Notice of Hearing", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1_CASE_DOCUMENT_Citizen_Five_16Page_419KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1 Vetting - Citizen Five_caseStart_2Page_43KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET3_Response_9page_92KB.pdf", "topLevelDocuments" -> "Response to a Claim", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> "ET3"), 
        Map("fileName" -> "et1-eng-original_16Page_388KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "ET1 Vetting - Perf Test_1Page_41KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Judgment_2Page_83KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Judgment", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Court_Reasons_3Page_56KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Reasons", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
        Map("fileName" -> "Change of Party Details_1Page_13KB.docx", "topLevelDocuments" -> "Case Management", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> "")
     )
      (files, caseName)

    case 50 =>
      val caseName = "50 Docs"
      val files = Array(
        // Add 50 documents here in the same format as above
        // Copy and modify the documents accordingly
      )
      (files, caseName)

    case _ =>
      throw new IllegalArgumentException("Invalid number of documents to upload. Must be 10, 20, or 50.")
  }

/* FIX THIS CODE ** IMPROVEMENT FOR CASE NAME ***
  val numberOfDocumentsToUpload: Int = 10 // Configure this value (10,25,50) for below switch case

 // // Use a match case to define different arrays based on the number of documents to upload
  val caseName = numberOfDocumentsToUpload match {
    case 10 =>
      exec(session => session.set("caseName", session("10 Docs").as[String]))
    case 25 => 
      exec(session => session.set("caseName", session("25 Docs").as[String]))  
  case 50 =>
      exec(session => session.set("caseName", session("50 Docs").as[String]))    
  case _ =>
    throw new IllegalArgumentException("Invalid number of documents to upload. Must be 10, 25, or 50.")
} */

 /* //Define all 10 Documents as an array with mapped assosciated params
  val files = Array(
    Map("fileName" -> "et1-eng-original_16Page_388KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "ET1 Vetting - Perf Test_1Page_41KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "Court_Judgment_2Page_83KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Judgment", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "Court_Reasons_3Page_56KB.pdf", "topLevelDocuments" -> "Judgment and Reasons", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "Reasons", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "Change of Party Details_1Page_13KB.docx", "topLevelDocuments" -> "Case Management", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "ACAS Cert_Image_19KB.jpg", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ACAS Certificate", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "ET Notice of Hearing_Image_241KB.jpeg", "topLevelDocuments" -> "Hearings", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "Notice of Hearing", "responseClaimDocuments" -> ""),
    Map("fileName" -> "ET1_CASE_DOCUMENT_Citizen_Five_16Page_419KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "ET1 Vetting - Citizen Five_caseStart_2Page_43KB.pdf", "topLevelDocuments" -> "Starting a Claim", "startingClaimDocuments" -> "ET1 Vetting", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> ""),
    Map("fileName" -> "ET3_Response_9page_92KB.pdf", "topLevelDocuments" -> "Response to a Claim", "startingClaimDocuments" -> "", "judgmentAndReasonsDocuments" -> "", "caseManagementDocuments" -> "", "hearingsDocuments" -> "", "responseClaimDocuments" -> "ET3")
  ) */

  // Create FileFeeder
  val fileFeeder = Iterator.continually(files).flatten.map(file => file + ("FileToUpload" -> file("fileName")))

  val AddDocumentsToMultipleUpload =
    exec(initialiseDocLinkSelfArray)
    .repeat(files.length) { // Repeat for the number of files
      feed(fileFeeder)
      .exec(http("CaseDocApi_UploadDoc")
        .post(CaseDocAPIURL + "/cases/documents")
        .header("Authorization", "Bearer #{bearerToken}")
        .header("ServiceAuthorization", "#{authToken}")
        .header("accept", "application/json")
        .header("Content-Type", "multipart/form-data")
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "ET_EnglandWales_Multiple")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .bodyPart(RawFileBodyPart("files", "#{FileToUpload}")
          .fileName("#{FileToUpload}")
          .transferEncoding("binary"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("docLinkSelfHash"))
      )
      // Add the extracted vars to array
      .exec { session =>
        val newValueDocLinkSelfHash = session("docLinkSelfHash").as[String]
        val updatedArrayDocLinkSelfHash = session("responseArrayDocLinkSelfHash").as[List[String]] :+ newValueDocLinkSelfHash

        // Output a summary of the document uploads 
        println("Summary:\n========")
        println(s"Doc Upload ${session("FileToUpload").as[String]}")
        println(s"Doc Link Array ${updatedArrayDocLinkSelfHash.toString} records: ${updatedArrayDocLinkSelfHash.length}")
        println("\n======== \n")

        //Set for later use
        session.set("responseArrayDocLinkSelfHash", updatedArrayDocLinkSelfHash)
        }
      .pause(2)
  } // End of Repeat Loop

      // Get Event token for Document Upload
      .exec(http("ET_000_GetCCDEventToken")
        .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/event-triggers/uploadDocument/token")
        .header("Authorization", "Bearer #{bearerToken}")
        .header("ServiceAuthorization", "#{authToken}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$.token").saveAs("eventToken")))

      // Set each file's details to the session
      .exec(session => {
      // Process each file in the files array, retrieve properties, and store the properties in the Gatling session with unique keys.
      // The keys are constructed using the index of the file, ensuring that each file's details are stored separately and can be accessed later
      files.zipWithIndex.foldLeft(session) { case (sess, (file, idx)) =>
        sess
          .set(s"topLevelDocuments$idx", file("topLevelDocuments"))
          .set(s"startingClaimDocuments$idx", file("startingClaimDocuments"))
          .set(s"judgmentAndReasonsDocuments$idx", file("judgmentAndReasonsDocuments"))
          .set(s"caseManagementDocuments$idx", file("caseManagementDocuments"))
          .set(s"hearingsDocuments$idx", file("hearingsDocuments"))
          .set(s"responseClaimDocuments$idx", file("responseClaimDocuments"))
          .set(s"fileName$idx", file("fileName"))
      }
    })
        .exec(http("AddDocToMultipleCase_DocUpload")
          .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/events")
            .header("Authorization", "Bearer #{bearerToken}")
            .header("ServiceAuthorization", "#{authToken}")
            .header("Content-Type", "application/json")
            .body(ElFileBody("bodies/et/uploadTwentyFiveDocuments.json")) //Change Json here to upload more/less documents
            .check(status.in(200,201)))

    .pause(2)

    // Get Event Token for Multiple Rename 
     .exec(http("ET_000_GetCCDEventToken")
        .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/event-triggers/amendMultipleDetails/token")
        .header("Authorization", "Bearer #{bearerToken}")
        .header("ServiceAuthorization", "#{authToken}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$.token").saveAs("eventToken"))
        .check(jsonPath("$.case_details.case_data.caseCounter").saveAs("caseCounterBeforeUpdate"))
        .check(jsonPath("$.case_details.case_data.leadCase").transform(str => str.replace(""""""","""\"""")).saveAs("leadCaseHTML")))

      .pause(2)

    // Rename Multiple
      .exec(http("ET_000_CCDEvent-amendMultipleDetails")
        .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/events")
        .header("Authorization", "Bearer #{bearerToken}")
        .header("ServiceAuthorization", "#{authToken}")
        .header("Content-Type", "application/json")
        .body(ElFileBody("bodies/et/amendMultipleName.json")).asJson
        .check(status.in(200,201)))

  }


/// CLEAN UP LATER

/*

object ETAddDocumentsToMultiple {

  val CcdAPIURL = Environment.ccdAPIURL
  val CaseDocAPIURL = Environment.caseDocAPIURL

  val totalMultiCasesToUpdate = csv("multipleCaseId.csv").recordsCount
  val multipleCaseIdFeeder = csv("multipleCaseId.csv")

  /*val files = Array(
  Map("fileName" -> "document1.pdf",),
  Map("fileName" -> "document2.pdf",),
  Map("fileName" -> "document3.pdf",),
  Map("fileName" -> "document1.pdf",),
  Map("fileName" -> "document2.pdf",),
  Map("fileName" -> "document3.pdf",),
  Map("fileName" -> "document1.pdf",),
  Map("fileName" -> "document2.pdf",),
  Map("fileName" -> "document3.pdf",),
  Map("fileName" -> "document1.pdf",)
)*/

  //val batchSizeLimit: Int = 50 // SAFEGUARD - DON'T INCREASE THIS VALUE UNLESS YOU HAVE TESTED IT FIRST
  //val desiredBatchSize: Int = 4 // The number of cases to process per API request
  // Enforce the batch size limit and ensure there are sufficient records in the feeder
  //val batchSize: Int = if (desiredBatchSize <= totalCasesToProcess) if (desiredBatchSize <= batchSizeLimit) desiredBatchSize else batchSizeLimit else totalCasesToProcess

  //val batchSizeRemainder: Int = totalCasesToProcess % batchSize
  //val numberOfBatchesToProcess: Int = totalCasesToProcess / batchSize + (if (batchSizeRemainder == 0) 0 else 1)

  // Initialise arrays to store extracted variables within.
  val initialiseDocLinkSelfArray = exec { session => session.set("responseArrayDocLinkSelf", List.empty[String])
  }
  val initialiseDocLinkBinaryArray = exec { session => session.set("responseArrayDocLinkBinary", List.empty[String])
  } 

  //Define all 10 Documents as an array
  val files = Array("et1-eng-original_16Page_388KB.pdf", "ET1 Vetting - Perf Test_1Page_41KB.pdf", "Court_Judgment_2Page_83KB.pdf","Court_Reasons_3Page_56KB.pdf","Change of Party Details_1Page_13KB.docx","ACAS Cert_Image_19KB.jpg","ET Notice of Hearing_Image_241KB.jpeg","ET1_CASE_DOCUMENT_Citizen_Five_16Page_419KB.pdf","ET1 Vetting - Citizen Five_caseStart_2Page_43KB.pdf","ET3_Response_9page_92KB.pdf") 
  //Create FileFeeder
  val fileFeeder = Iterator.continually(files.map(fileName => Map("FileToUpload" -> fileName))).flatten

  val AddDocumentsToMultipleUpload = {
   feed(multipleCaseIdFeeder)
    .exec(initialiseDocLinkSelfArray)
    .exec(initialiseDocLinkBinaryArray)
    .foreach(files, "file") {
      feed(fileFeeder)
      .exec(http("CaseDocApi_UploadDoc")
      .post(CaseDocAPIURL + "/cases/documents")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "#{BearerToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "FT_CaseFileView_1")
      .formParam("jurisdictionId", "BEFTA_MASTER")
      .bodyPart(RawFileBodyPart("files", "#{file}")
        .fileName("#{file}")
        .transferEncoding("binary"))
      //.check(regex("""documents/([0-9a-z-]+?)/binary""").saveAs("Document_ID"))
      .check(jsonPath("$.x._embedded.documents[0]._links.self.href").saveAs("docLinkSelf"))
      .check(jsonPath("$.x._embedded.documents[0]._links.binary.href").saveAs("docLinkBinary")))
      
      // Add the extracted vars to array
      .exec { session =>
      val newValueDocLinkSelf = session(docLinkSelf).as[String]
      val updatedArrayDocLinkSelf = session("responseArrayDocLinkSelf").as[List[String]] :+ newValueDocLinkSelf
      session.set("responseArrayDocLinkSelf", updatedArrayDocLinkSelf)
    }
    .pause(2.seconds)

    } // end of foreach

    //val docLinkSelf = docLinkSelf
    //val docLiinkBinary = docLinkBinary

  }




/*

    feed(multipleCaseIdFeeder) // Fetch the multiple Case ID to update

    // Process each batch by getting a token and submitting a post request to CCD to update the multiple case
    .repeat(numberOfBatchesToProcess, "batchCounter") {

      exec(http("ET_000_GetCCDEventToken")
        .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/event-triggers/amendMultipleDetails/token")
        .header("Authorization", "Bearer #{bearerToken}")
        .header("ServiceAuthorization", "#{authToken}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$.token").saveAs("eventToken"))
        .check(jsonPath("$.case_details.case_data.caseCounter").saveAs("caseCounterBeforeUpdate"))
        .check(jsonPath("$.case_details.case_data.leadCase").transform(str => str.replace(""""""","""\"""")).saveAs("leadCaseHTML")))

      .pause(1)

      // Calculate the current batch size and initialise the payload string
      .exec {
        session =>
          val batchIteration: Int = session("batchCounter").as[Int]
          val currentBatchSize: Int = if ((batchIteration + 1 == numberOfBatchesToProcess) && (batchSizeRemainder != 0)) batchSizeRemainder else batchSize
          session.setAll( "collectionPayload" -> "",
                          "currentBatchSize" -> currentBatchSize,
                          "singlesCountInMultiple" -> (session("caseCounterBeforeUpdate").as[Int] + currentBatchSize))
      }

      // Feed the number of cases required for the current batch - NOTE: for this to work properly, Gatling 3.10 or higher is required due to this change:
      // https://docs.gatling.io/release-notes/upgrading/3.9-to-3.10/#feeding-multiple-records-at-once
      .feed(caseFeeder, "#{currentBatchSize}")

      // If there is only one case being fed, Gatling will create it as a string rather than a SeqArray in the session, resulting in the
      // foreach loop below failing (as it expects an object containing multiple items. To get around this, if there is one case fed,
      // replace it with a SeqArray containing the single value
      .doIf(session => session("currentBatchSize").as[Int].equals(1)) {
          exec(session => session.set("ethosId", ArraySeq(session("ethosId").as[String])))
      }

      // Build up a string of JSON elements in the collection (one element per case in the batch)
      .foreach("#{ethosId}", "id", "caseCounter") {
        exec {
          session =>
            val element =
              """{
                |   "value": {
                |     "ethos_CaseReference": """".stripMargin + session("id").as[String] + "\"" +
                """},
                  |   "id": null
                  |}""".stripMargin

            session.set("collectionPayload", session("collectionPayload").as[String] + element + (if (session("caseCounter").as[Int] + 1 == session("currentBatchSize").as[Int]) "" else ","))
        }
      }

      .exec(http("ET_000_CCDEvent-amendMultipleDetails")
        .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/events")
        .header("Authorization", "Bearer #{bearerToken}")
        .header("ServiceAuthorization", "#{authToken}")
        .header("Content-Type", "application/json")
        .body(ElFileBody("bodies/et/amendMultipleDetails.json")).asJson
        .check(jsonPath("$.case_data.caseCounter").saveAs("caseCounterAfterUpdate")))

      .exec {
        session =>
          val batchIteration: Int = session("batchCounter").as[Int]
          val casesProcessedThisBatch: Int = session("currentBatchSize").as[Int]

          // Output a summary of the current batch to the console
          println("Summary:\n========")
          println("Batch " + (batchIteration + 1).toString + " of " + numberOfBatchesToProcess)
          println("Processed " + casesProcessedThisBatch.toString + " records")
          println("Total records processed: " + (batchIteration * batchSize + casesProcessedThisBatch).toString)

          // If something went wrong (before + cases != after) then output some useful information
          if (session("caseCounterBeforeUpdate").as[Int] + casesProcessedThisBatch != session("caseCounterAfterUpdate").as[Int]) {
            println("ERROR: Issue updating cases:")
            println("Number of singles before iteration: " + session("caseCounterBeforeUpdate").as[Int])
            println("Number of singles attempted to add: " + casesProcessedThisBatch)
            println("Number of singles after iteration: " + session("caseCounterAfterUpdate").as[Int])
          }

          println(session)
          session
      }

      .pause(1)

    } // End repeat loop 

  } */

}


*/