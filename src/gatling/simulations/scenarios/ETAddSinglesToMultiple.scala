package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object ETAddSinglesToMultiple {

  val CcdAPIURL = Environment.ccdAPIURL

  val records: Seq[Map[String, Any]] = csv("singlesEthosIdsToAddToMultiple.csv").readRecords
  val recordsCount = csv("singlesEthosIdsToAddToMultiple.csv").recordsCount
  val recordsFeeder = Iterator.continually(Map("allRecords" -> records))

  val multipleCaseIdFeeder = csv("multipleCaseId.csv")

  val batchSize: Int = 5 //the number of singles cases to add to the multiple case per API request
  val batchSizeRemainder: Int = recordsCount % batchSize
  val numberOfBatchesToProcess: Int = recordsCount / batchSize + (if (batchSizeRemainder == 0) 0 else 1)

  val AddSinglesToMultiple = {

    feed(multipleCaseIdFeeder) //fetch the multiple Case ID to update

    .feed(recordsFeeder) //fetch the Ethos IDs of the singles cases to add to the multiple case

    .repeat(numberOfBatchesToProcess, "counter") {

      exec(http("ET_000_GetCCDEventToken")
          .get(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/event-triggers/amendMultipleDetails/token")
          .header("Authorization", "Bearer #{bearerToken}")
          .header("ServiceAuthorization", "#{authToken}")
          .header("Content-Type", "application/json")
          .check(jsonPath("$.token").saveAs("eventToken"))
          .check(jsonPath("$.case_details.case_data.caseCounter").saveAs("caseCounterBeforeUpdate"))
          .check(jsonPath("$.case_details.case_data.leadCase").saveAs("leadCaseHTML")))

      .exec {
        session =>
          val iteration: Int = session("counter").as[Int]
          val sliceStartIndex: Int = iteration * batchSize
          //if processing the final batch, set the correct final batch size
          val currentBatchSize: Int = if ((iteration + 1 == numberOfBatchesToProcess) && (batchSizeRemainder != 0)) batchSizeRemainder else batchSize
          val mapsSlice = session("allRecords").as[Seq[Map[String, Any]]].slice(sliceStartIndex, sliceStartIndex + currentBatchSize)

          println("Iteration " + (iteration + 1).toString + " of " + numberOfBatchesToProcess)
          println("Processing " + currentBatchSize.toString + " records")
          println("Total records processed: " + (iteration * batchSize + currentBatchSize).toString)

          session.setAll("recordsToProcess" -> mapsSlice,
                         "singlesCountInMultiple" -> (session("caseCounterBeforeUpdate").as[Int] + currentBatchSize))
      }

      .pause(1)

      // The JSON body of the following request needs to contain both Gatling session variables and Pebble logic.
      // There doesn't appear to be a way to process Gatling EL (i.e. session variables) in a Pebble template, so you can either evaluate the Pebble logic
      // using a PebbleFileBody OR Gatling EL using a ElFileBody, but not both together. Therefore the simplest way to overcome this is to evaluate the
      // Pebble logic first and then manually substitute the session variables with the values from the session

      .exec {
        session =>
          val pebbleBody = PebbleFileBody("bodies/et/amendMultipleDetails.json").apply(session).toOption.get
          val jsonPayload = pebbleBody
                              .replace("#{singlesCountInMultiple}", session("singlesCountInMultiple").as[String])
                              .replace("#{leadCaseHTML}", session("leadCaseHTML").as[String])
                              .replace("#{eventToken}", session("eventToken").as[String])
          session.set("jsonPayload", jsonPayload)
      }

      .exec(http("ET_000_CCDEvent-amendMultipleDetails")
        .post(CcdAPIURL + "/caseworkers/#{idamId}/jurisdictions/Employment/case-types/ET_EnglandWales_Multiple/cases/#{caseId}/events")
        .header("Authorization", "Bearer #{bearerToken}")
        .header("ServiceAuthorization", "#{authToken}")
        .header("Content-Type", "application/json")
        .body(StringBody("#{jsonPayload}"))
        .check(jsonPath("$.case_data.caseCounter").saveAs("caseCounterAfterUpdate")))

      .exec {
        session =>
          val iteration: Int = session("counter").as[Int]
          val currentBatchSize: Int = if ((iteration + 1 == numberOfBatchesToProcess) && (batchSizeRemainder != 0)) batchSizeRemainder else batchSize
          if(session("caseCounterBeforeUpdate").as[Int] + currentBatchSize != session("caseCounterAfterUpdate").as[Int]){
            println("ERROR: Issue updating cases:")
            println("Number of singles before iteration: " + session("caseCounterBeforeUpdate").as[Int])
            println("Number of singles attempted to add: " + currentBatchSize)
            println("Number of singles after iteration: " + session("caseCounterAfterUpdate").as[Int])
          }
          println(session)
          session
      }

    } //end repeat loop

  }

}
