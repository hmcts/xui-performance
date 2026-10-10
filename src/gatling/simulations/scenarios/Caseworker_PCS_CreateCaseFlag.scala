package scenarios

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.time.Instant
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._

/*======================================================================================
* Caseworker PCS CC Create Case Flag - createFlags (CF0002 Complex Case)
* Source JMX: CC-Create Case Flag (Latest_New_LoadTest_11Sep.jmx)
======================================================================================*/

object Caseworker_PCS_CreateCaseFlag {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  private val mapper = new ObjectMapper()

  private def fieldValue(root: JsonNode, id: String): JsonNode = {
    val fields = root.path("case_fields")
    if (!fields.isArray) mapper.nullNode()
    else {
      fields.asScala
        .find(_.path("id").asText("") == id)
        .map(_.path("value"))
        .getOrElse(mapper.nullNode())
    }
  }

  private def cloneNode(node: JsonNode): JsonNode =
    if (node == null || node.isNull || node.isMissingNode) mapper.nullNode()
    else mapper.readTree(mapper.writeValueAsString(node))

  /*====================================================================================
  *Search PCS cases (SEARCH view) by Case ID from feeder
  *=====================================================================================*/

  val SearchCase =

    group("Caseworker_PCS_CreateCaseFlag_030_SearchCase") {
      exec(http("Caseworker_PCS_CreateCaseFlag_030_005_SearchCase")
        .post("/data/internal/searchCases?ctid=#{caseType}&use_case=SEARCH&view=SEARCH&page=1&case_reference=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("""{"size":25}"""))
        .check(status.is(200))
        .check(substring("\"results\""))
        .check(jsonPath("$.results[*].case_id").find.is("#{caseId}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Open case by Case ID from feeder
  *=====================================================================================*/

  val OpenCase =

    group("Caseworker_PCS_CreateCaseFlag_040_OpenCase") {
      exec(http("Caseworker_PCS_CreateCaseFlag_040_005_OpenCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("experimental", "true")
        .check(status.is(200))
        .check(jsonPath("$.case_id").is("#{caseId}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Start Create Case Flags event (createFlags)
  *=====================================================================================*/

  val StartCreateFlags =

    group("Caseworker_PCS_CreateCaseFlag_060_StartCreateFlags") {
      exec(Common.profile)

      .exec(http("Caseworker_PCS_CreateCaseFlag_060_005_StartCreateFlags")
        .get("/data/internal/cases/#{caseId}/event-triggers/createFlags?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .header("experimental", "true")
        .check(status.is(200))
        .check(substring("Create case flags"))
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(bodyString.saveAs("eventTriggerJson")))
    }

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Get Complex Case (CF0002) flag reference data
  *=====================================================================================*/

  val GetFlagRefData =

    group("Caseworker_PCS_CreateCaseFlag_070_GetFlagRefData") {
      exec(http("Caseworker_PCS_CreateCaseFlag_070_005_GetFlagRefData")
        .get("/refdata/commondata/caseflags/service-id=AAA3?flag-type=CASE&welsh-required=Y&available-external-flag=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .header("experimental", "true")
        .check(status.is(200))
        .check(substring("CF0002")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Build createFlags event body from event-trigger case_fields (JMX Groovy port)
  *=====================================================================================*/

  val BuildCreateFlagsBody =

    exec(_.set("flagComment", "Testing"))

    .exec { session =>
      val trigger = mapper.readTree(session("eventTriggerJson").as[String])
      val eventToken = session("event_token").as[String]
      val flagComment = session("flagComment").as[String]

      val partiesNode = cloneNode(fieldValue(trigger, "parties"))
      val allDefendantsNode = cloneNode(fieldValue(trigger, "allDefendants"))
      val existingCaseFlags = cloneNode(fieldValue(trigger, "caseFlags"))

      val parties: ArrayNode =
        if (partiesNode != null && partiesNode.isArray) partiesNode.deepCopy().asInstanceOf[ArrayNode]
        else mapper.createArrayNode()

      val sortedParties = mapper.createArrayNode()
      val withDefendantFlags = parties.asScala.filter(_.path("value").has("defendantFlags"))
      val withoutDefendantFlags = parties.asScala.filterNot(_.path("value").has("defendantFlags"))
      withDefendantFlags.foreach(sortedParties.add)
      withoutDefendantFlags.foreach(sortedParties.add)

      val allDefendants: JsonNode =
        if (allDefendantsNode != null && !allDefendantsNode.isNull && !allDefendantsNode.isMissingNode) allDefendantsNode
        else mapper.createArrayNode()

      val caseFlagsPayload: ObjectNode =
        if (existingCaseFlags != null && existingCaseFlags.isObject) existingCaseFlags.deepCopy().asInstanceOf[ObjectNode]
        else mapper.createObjectNode()

      val existingDetails: ArrayNode =
        if (caseFlagsPayload.path("details").isArray) caseFlagsPayload.path("details").deepCopy().asInstanceOf[ArrayNode]
        else mapper.createArrayNode()

      val hasActiveCf0002 = existingDetails.asScala.exists { d =>
        d.path("value").path("flagCode").asText("") == "CF0002" &&
          d.path("value").path("status").asText("") == "Active"
      }

      val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

      if (hasActiveCf0002) {
        existingDetails.asScala.foreach { d =>
          val value = d.path("value")
          if (value.isObject &&
            value.path("flagCode").asText("") == "CF0002" &&
            value.path("status").asText("") == "Active") {
            value.asInstanceOf[ObjectNode].put("flagComment", flagComment)
            value.asInstanceOf[ObjectNode].put("dateTimeCreated", now)
          }
        }
        caseFlagsPayload.set("details", existingDetails)
      } else {
        val flagDetail = mapper.createObjectNode()
        val value = mapper.createObjectNode()
        value.put("name", "Complex Case")
        value.put("name_cy", "Achos Cymhleth")
        value.putNull("subTypeValue")
        value.putNull("subTypeValue_cy")
        value.putNull("subTypeKey")
        value.putNull("otherDescription")
        value.putNull("otherDescription_cy")
        value.put("flagComment", flagComment)
        value.putNull("flagComment_cy")
        value.put("dateTimeCreated", now)
        val pathArr = mapper.createArrayNode()
        val pathItem = mapper.createObjectNode()
        pathItem.putNull("id")
        pathItem.put("value", "Case")
        pathArr.add(pathItem)
        value.set("path", pathArr)
        value.put("hearingRelevant", "Yes")
        value.put("flagCode", "CF0002")
        value.put("status", "Active")
        value.put("availableExternally", "No")
        flagDetail.set("value", value)

        val details = existingDetails.deepCopy().asInstanceOf[ArrayNode]
        details.add(flagDetail)
        caseFlagsPayload.set("details", details)
      }

      val data = mapper.createObjectNode()
      data.set("caseFlags", caseFlagsPayload)
      data.set("parties", sortedParties)
      data.set("allDefendants", allDefendants)

      val event = mapper.createObjectNode()
      event.put("id", "createFlags")
      event.put("summary", "")
      event.put("description", "")

      val payload = mapper.createObjectNode()
      payload.set("data", data)
      payload.set("event", event)
      payload.put("event_token", eventToken)
      payload.put("ignore_warning", false)

      session.set("createFlagsEventBody", mapper.writeValueAsString(payload))
    }

  /*====================================================================================
  *Submit Create Case Flags event
  *=====================================================================================*/

  val SubmitCreateFlags =

    group("Caseworker_PCS_CreateCaseFlag_080_SubmitCreateFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_080_005_SubmitCreateFlags")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody(session => session("createFlagsEventBody").as[String]))
        .check(status.is(201))
        .check(substring("CF0002")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *Verify Complex Case flag present on case
  *=====================================================================================*/

  val VerifyFlag =

    group("Caseworker_PCS_CreateCaseFlag_090_VerifyFlag") {
      exec(http("Caseworker_PCS_CreateCaseFlag_090_005_VerifyFlag")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("experimental", "true")
        .check(status.is(200))
        .check(substring("CF0002")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  *View Case Flags tab (SPA + case data)
  *=====================================================================================*/

  val ViewCaseFlags =

    group("Caseworker_PCS_CreateCaseFlag_100_ViewCaseFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_100_005_ViewCaseFlags")
        .get("/cases/case-details/PCS/PCS/#{caseId}")
        .headers(Headers.navigationHeader)
        .check(status.is(200))
        .check(substring("exui-root")))

      .exec(http("Caseworker_PCS_CreateCaseFlag_100_010_LoadCaseFlagsData")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .header("experimental", "true")
        .check(status.is(200))
        .check(substring("Complex Case")))
    }

    .pause(MinThinkTime, MaxThinkTime)

}
