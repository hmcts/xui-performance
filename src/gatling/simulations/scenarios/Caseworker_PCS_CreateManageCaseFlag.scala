package scenarios

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import java.time.{Instant, ZoneOffset}
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._

/*======================================================================================
* Caseworker PCS Create Manage Case Flag
* Object: Caseworker_PCS_CreateManageCaseFlag
* Simulation scenario: CaseworkerPCSCreateManageCaseFlagScenario
* Feeder: UserDataCaseworkerPCS_CreateManageCaseFlag.csv
*
* Operation vals (repo convention — one val per business operation):
*   SearchCase / OpenCase — case navigation
*   CreateCaseFlag        — createFlags (start → ref data → build → submit → verify → view)
*   ManageCaseFlag        — amendFlags update (start → build → submit → verify)
*   InactiveCaseFlag      — amendFlags inactive (start → build → submit → verify)

======================================================================================*/

object Caseworker_PCS_CreateManageCaseFlag {

  val BaseURL = Environment.baseURL
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  private val mapper = new ObjectMapper()
  private val ccdDateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)

  private def fieldValue(root: JsonNode, id: String): JsonNode = {
    val fields = root.path("case_fields")
    if (!fields.isArray) mapper.nullNode()
    else fields.asScala.find(_.path("id").asText("") == id).map(_.path("value")).getOrElse(mapper.nullNode())
  }

  private def partiesFromTrigger(trigger: JsonNode): ArrayNode = {
    val raw = fieldValue(trigger, "parties")
    if (!raw.isArray) mapper.createArrayNode()
    else {
      val parties = raw.deepCopy().asInstanceOf[ArrayNode]
      val ordered = mapper.createArrayNode()
      parties.asScala.filter(_.path("value").has("defendantFlags")).foreach(ordered.add)
      parties.asScala.filterNot(_.path("value").has("defendantFlags")).foreach(ordered.add)
      ordered
    }
  }

  private def allDefendantsFromTrigger(trigger: JsonNode): JsonNode = {
    val raw = fieldValue(trigger, "allDefendants")
    if (raw.isArray) raw.deepCopy() else mapper.createArrayNode()
  }

  /** Deep-copy caseFlags. groupId/visibility defaults match successful Case Flag HAR payloads. */
  private def caseFlagsFromTrigger(trigger: JsonNode): ObjectNode = {
    val raw = fieldValue(trigger, "caseFlags")
    val caseFlags =
      if (raw.isObject) raw.deepCopy().asInstanceOf[ObjectNode] else mapper.createObjectNode()
    if (!caseFlags.has("groupId")) caseFlags.putNull("groupId")
    if (!caseFlags.hasNonNull("visibility")) caseFlags.put("visibility", "Internal")
    caseFlags
  }

  private def ccdEventJson(eventId: String, data: ObjectNode, eventToken: String): String = {
    val event = mapper.createObjectNode()
    event.put("id", eventId)
    event.put("summary", "")
    event.put("description", "")

    val payload = mapper.createObjectNode()
    payload.set("data", data)
    payload.set("event", event)
    payload.put("event_token", eventToken)
    payload.put("ignore_warning", false)
    mapper.writeValueAsString(payload)
  }

  private def buildAmendFlagsSessionBody(
    session: Session,
    comment: String,
    setStatusInactive: Boolean,
    sessionBodyKey: String
  ): Session = {
    val trigger = mapper.readTree(session("eventTriggerJson").as[String])
    val eventToken = session("event_token").as[String]
    val flagIdToAmend = session("flagIdToAmend").as[String]

    val caseFlagsPayload = caseFlagsFromTrigger(trigger)
    val details: ArrayNode =
      if (caseFlagsPayload.path("details").isArray) caseFlagsPayload.path("details").deepCopy().asInstanceOf[ArrayNode]
      else mapper.createArrayNode()

    val flagValue = details.asScala
      .find(_.path("id").asText("") == flagIdToAmend)
      .map(_.path("value"))
      .filter(_.isObject)
      .map(_.asInstanceOf[ObjectNode])

    flagValue match {
      case None =>
        session.markAsFailed
      case Some(value) =>
        val now = ccdDateTimeFormatter.format(Instant.now())
        value.put("flagComment", comment)
        value.put("flagUpdateComment", comment)
        if (setStatusInactive) value.put("status", "Inactive")
        value.put("dateTimeModified", now)

        caseFlagsPayload.set("details", details)
        val data = mapper.createObjectNode()
        data.set("caseFlags", caseFlagsPayload)
        data.set("parties", partiesFromTrigger(trigger))
        data.set("allDefendants", allDefendantsFromTrigger(trigger))
        session.set(sessionBodyKey, ccdEventJson("amendFlags", data, eventToken))
    }
  }

  private def refreshXsrf =
    exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(BaseURL.replace("https://", "")).withSecure(true).saveAs("XSRFToken")))

  /*====================================================================================
  * Search PCS cases (SEARCH view) by Case ID from feeder
  *=====================================================================================*/

  val SearchCase =

    group("Caseworker_PCS_CreateCaseFlag_01_SearchCase") {
      exec(http("Caseworker_PCS_CreateCaseFlag_01_005_SearchCase")
        .post("/data/internal/searchCases?ctid=#{caseType}&use_case=SEARCH&view=SEARCH&page=1&case_reference=#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .formParam("x-xsrf-token", "#{XSRFToken}")
        .body(ElFileBody("bodies/pcs/caseflag/SearchCases.json"))
        .check(status.is(200))
        .check(substring("\"results\""))
        .check(jsonPath("$.results[*].case_id").find.is("#{caseId}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  * Open case + role labelling
  *=====================================================================================*/

  val OpenCase =

    group("Caseworker_PCS_CreateCaseFlag_02_OpenCase") {
      exec(http("Caseworker_PCS_CreateCaseFlag_02_005_OpenCase")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(status.is(200))
        .check(jsonPath("$.case_id").is("#{caseId}")))

      .exec(Common.manageLabellingRoleAssignment)
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  * Create Case Flag — createFlags (start → ref data → build → submit → verify → view)
  *=====================================================================================*/

  val CreateCaseFlag =

    group("Caseworker_PCS_CreateCaseFlag_03_StartCreateFlags") {
      exec(Common.profile)

      .exec(http("Caseworker_PCS_CreateCaseFlag_03_005_CreateFlagsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/createFlags/caseType/#{caseType}/jurisdiction/#{caseType}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(status.is(200))
        .check(substring("task_required_for_event")))

      .exec(http("Caseworker_PCS_CreateCaseFlag_03_010_StartCreateFlags")
        .get("/data/internal/cases/#{caseId}/event-triggers/createFlags?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(status.is(200))
        .check(substring("Create case flags"))
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(bodyString.saveAs("eventTriggerJson")))
    }

    .exec(refreshXsrf)

    .pause(MinThinkTime, MaxThinkTime)

    .group("Caseworker_PCS_CreateCaseFlag_04_GetFlagRefData") {
      exec(http("Caseworker_PCS_CreateCaseFlag_04_005_GetFlagRefData")
        .get("/refdata/commondata/caseflags/service-id=AAA3?flag-type=CASE&welsh-required=Y&available-external-flag=N")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(status.is(200))
        .check(substring("CF0002")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    .exec(_.set("flagComment", "Test"))

    .exec { session =>
      val trigger = mapper.readTree(session("eventTriggerJson").as[String])
      val eventToken = session("event_token").as[String]
      val flagComment = session("flagComment").as[String]

      val caseFlagsPayload = caseFlagsFromTrigger(trigger)
      val existingDetails: ArrayNode =
        if (caseFlagsPayload.path("details").isArray) caseFlagsPayload.path("details").deepCopy().asInstanceOf[ArrayNode]
        else mapper.createArrayNode()

      val existingFlagIds = existingDetails.asScala
        .map(_.path("id").asText(""))
        .filter(_.nonEmpty)
        .mkString(",")

      val now = ccdDateTimeFormatter.format(Instant.now())

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
      value.put("flagUpdateComment", flagComment)
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

      val details = mapper.createArrayNode()
      details.add(flagDetail)
      existingDetails.asScala.foreach(details.add)
      caseFlagsPayload.set("details", details)

      val data = mapper.createObjectNode()
      data.set("caseFlags", caseFlagsPayload)
      data.set("parties", partiesFromTrigger(trigger))
      data.set("allDefendants", allDefendantsFromTrigger(trigger))

      session
        .set("existingFlagIds", existingFlagIds)
        .set("createFlagsEventBody", ccdEventJson("createFlags", data, eventToken))
    }

    .exec(refreshXsrf)

    .group("Caseworker_PCS_CreateCaseFlag_05_SubmitCreateFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_05_005_SubmitCreateFlags")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("#{createFlagsEventBody}"))
        .check(status.is(201))
        .check(substring("CF0002"))
        .check(substring("#{flagComment}"))
        .check(bodyString.saveAs("createFlagsSubmitBody")))
    }

    .exec { session =>
      val before = session("existingFlagIds").as[String].split(',').map(_.trim).filter(_.nonEmpty).toSet
      val root = mapper.readTree(session("createFlagsSubmitBody").as[String])
      val details = root.path("data").path("caseFlags").path("details")
      val createdId = details.asScala.collectFirst {
        case d if {
          val id = d.path("id").asText("")
          id.nonEmpty &&
            !before.contains(id) &&
            d.path("value").path("flagCode").asText("") == "CF0002" &&
            d.path("value").path("flagComment").asText("") == session("flagComment").as[String]
        } => d.path("id").asText("")
      }
      createdId match {
        case Some(id) => session.set("flagIdToAmend", id)
        case None => session.markAsFailed
      }
    }

    .pause(MinThinkTime, MaxThinkTime)

    .group("Caseworker_PCS_CreateCaseFlag_06_VerifyFlag") {
      exec(http("Caseworker_PCS_CreateCaseFlag_06_005_VerifyFlag")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(status.is(200))
        .check(substring("CF0002"))
        .check(substring("#{flagComment}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    .group("Caseworker_PCS_CreateCaseFlag_07_ViewCaseFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_07_005_ViewCaseFlags")
        .get("/cases/case-details/#{caseType}/#{caseType}/#{caseId}")
        .headers(Headers.navigationHeader)
        .check(status.is(200))
        .check(substring("exui-root")))

      .exec(http("Caseworker_PCS_CreateCaseFlag_07_010_LoadCaseFlagsData")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(status.is(200))
        .check(substring("Complex Case")))

      .exec(Common.manageLabellingRoleAssignment)
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  * Manage Case Flag — amendFlags update (start → build → submit → verify)
    *=====================================================================================*/

  val ManageCaseFlag =

    group("Caseworker_PCS_CreateCaseFlag_08_StartAmendFlags") {
      exec(Common.profile)

      .exec(http("Caseworker_PCS_CreateCaseFlag_08_005_AmendFlagsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/amendFlags/caseType/#{caseType}/jurisdiction/#{caseType}")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(status.is(200))
        .check(substring("task_required_for_event")))

      .exec(http("Caseworker_PCS_CreateCaseFlag_08_010_StartAmendFlags")
        .get("/data/internal/cases/#{caseId}/event-triggers/amendFlags?ignore-warning=false")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(status.is(200))
        .check(substring("Manage case flags"))
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(bodyString.saveAs("eventTriggerJson")))
    }

    .exec(refreshXsrf)

    .pause(MinThinkTime, MaxThinkTime)

    .exec(_.set("flagUpdateComment", "Update flag"))

    .exec { session =>
      buildAmendFlagsSessionBody(
        session,
        session("flagUpdateComment").as[String],
        setStatusInactive = false,
        "amendFlagsEventBody"
      )
    }

    .exec(refreshXsrf)

    .group("Caseworker_PCS_CreateCaseFlag_09_SubmitAmendFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_09_005_SubmitAmendFlags")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("#{amendFlagsEventBody}"))
        .check(status.is(201))
        .check(substring("Update flag"))
        .check(substring("#{flagIdToAmend}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    .group("Caseworker_PCS_CreateCaseFlag_10_VerifyAmendFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_10_005_VerifyAmendFlags")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(status.is(200))
        .check(substring("Update flag"))
        .check(substring("#{flagIdToAmend}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

  /*====================================================================================
  * Inactive Case Flag — amendFlags inactive (start → build → submit → verify)
  * Same backend event id as Manage; payload sets status Inactive on flagIdToAmend
  * Gatling 3.15.1 has disableCaching only on HttpProtocolBuilder (not per-request).
  * Distinct xuiPhase=inactive query avoids VU content-cache ETag → 304 on 2nd amendFlags GET.
  *=====================================================================================*/

  val InactiveCaseFlag =

    group("Caseworker_PCS_CreateCaseFlag_11_StartInactiveFlags") {
      exec(Common.profile)

      .exec(http("Caseworker_PCS_CreateCaseFlag_11_005_InactiveFlagsWACheckTask")
        .get("/workallocation/case/tasks/#{caseId}/event/amendFlags/caseType/#{caseType}/jurisdiction/#{caseType}?xuiPhase=inactive")
        .headers(Headers.commonHeader)
        .header("accept", "application/json")
        .check(status.is(200))
        .check(substring("task_required_for_event")))

      .exec(http("Caseworker_PCS_CreateCaseFlag_11_010_StartInactiveFlags")
        .get("/data/internal/cases/#{caseId}/event-triggers/amendFlags?ignore-warning=false&xuiPhase=inactive")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(status.is(200))
        .check(substring("Manage case flags"))
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(bodyString.saveAs("eventTriggerJson")))
    }

    .exec(refreshXsrf)

    .pause(MinThinkTime, MaxThinkTime)

    .exec(_.set("flagInactiveComment", "Inactivated"))

    .exec { session =>
      buildAmendFlagsSessionBody(
        session,
        session("flagInactiveComment").as[String],
        setStatusInactive = true,
        "inactiveFlagsEventBody"
      )
    }

    .exec(refreshXsrf)

    .group("Caseworker_PCS_CreateCaseFlag_12_SubmitInactiveFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_12_005_SubmitInactiveFlags")
        .post("/data/cases/#{caseId}/events")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "#{XSRFToken}")
        .body(StringBody("#{inactiveFlagsEventBody}"))
        .check(status.is(201))
        .check(substring("Inactive"))
        .check(substring("Inactivated"))
        .check(substring("#{flagIdToAmend}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

    .group("Caseworker_PCS_CreateCaseFlag_13_VerifyInactiveFlags") {
      exec(http("Caseworker_PCS_CreateCaseFlag_13_005_VerifyInactiveFlags")
        .get("/data/internal/cases/#{caseId}")
        .headers(Headers.commonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("x-xsrf-token", "#{XSRFToken}")
        .check(status.is(200))
        .check(substring("Inactive"))
        .check(substring("Inactivated"))
        .check(substring("#{flagIdToAmend}")))
    }

    .pause(MinThinkTime, MaxThinkTime)

}