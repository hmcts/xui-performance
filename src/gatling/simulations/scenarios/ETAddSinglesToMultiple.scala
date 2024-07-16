package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import scala.collection.immutable.ArraySeq

object ETAddSinglesToMultiple {

  val CcdAPIURL = Environment.ccdAPIURL

  val caseFeeder = csv("singlesEthosIdsToAddToMultiple.csv")
  val totalCasesToProcess = csv("singlesEthosIdsToAddToMultiple.csv").recordsCount

  val multipleCaseIdFeeder = csv("multipleCaseId.csv")

  val batchSizeLimit: Int = 50 // SAFEGUARD - DON'T INCREASE THIS VALUE UNLESS YOU HAVE TESTED IT FIRST
  val desiredBatchSize: Int = 1 // The number of cases to process per API request
  // Enforce the batch size limit and ensure there are sufficient records in the feeder
  val batchSize: Int = if (desiredBatchSize <= totalCasesToProcess) if (desiredBatchSize <= batchSizeLimit) desiredBatchSize else batchSizeLimit else totalCasesToProcess

  val batchSizeRemainder: Int = totalCasesToProcess % batchSize
  val numberOfBatchesToProcess: Int = totalCasesToProcess / batchSize + (if (batchSizeRemainder == 0) 0 else 1)


  val AddSinglesToMultiple = {

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

  }

}
