package utils

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

/**
 * Collects successfully created Make a Claim case IDs in memory during a Gatling run,
 * then writes one consolidated CSV when the simulation finishes.
 *
 * payNowSuccess values:
 *   - "NOT_REACHED" — case was created but Confirm PBA payment never completed a result
 *   - "true"        — Confirm PBA payment passed its success checks (HTTP 200/201/202)
 *   - "false"       — Confirm PBA payment ran and failed those checks
 */
object MakeAClaimCaseRecorder {

  private case class CreatedCase(user: String, account: String, caseId: String, payNowSuccess: String)

  /** Used only to name the output file uniquely per JVM run; not written as a CSV column. */
  private val outputRunStamp: String =
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))

  private val createdCases = new ConcurrentHashMap[String, CreatedCase]()

  private val outputDir: Path = Paths.get("results", "makeaclaim")

  /** Record a successful case creation. Pay Now is NOT_REACHED until updated later. */
  def recordSuccess(user: String, account: String, caseId: String): Unit = {
    if (caseId != null && caseId.nonEmpty) {
      createdCases.put(caseId, CreatedCase(user, account, caseId, "NOT_REACHED"))
    }
  }

  /**
   * Update Pay Now outcome for an already-recorded case.
   * Safe to call from a response check while exitBlockOnFail may skip later steps.
   */
  def updatePayNowResult(caseId: String, success: Boolean): Unit = {
    if (caseId != null && caseId.nonEmpty) {
      createdCases.computeIfPresent(
        caseId,
        (_, existing) => existing.copy(payNowSuccess = success.toString)
      )
    }
  }

  /**
   * Write all recorded cases to a single run-specific CSV.
   * Intended to be called once from Simulation.after.
   */
  def writeConsolidatedCsv(): Unit = {
    try {
      Files.createDirectories(outputDir)
      val outFile = outputDir.resolve(s"successful-cases-$outputRunStamp.csv")
      val sb = new StringBuilder
      sb.append("user,account,caseId,payNowSuccess\n")
      val iterator = createdCases.values().iterator()
      var count = 0
      while (iterator.hasNext) {
        val row = iterator.next()
        sb.append(escape(row.user)).append(',')
          .append(escape(row.account)).append(',')
          .append(escape(row.caseId)).append(',')
          .append(escape(row.payNowSuccess)).append('\n')
        count += 1
      }
      Files.write(
        outFile,
        sb.toString.getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE
      )
      println(s"[MakeAClaimCaseRecorder] Wrote $count case(s) to ${outFile.toAbsolutePath}")
    } catch {
      case e: Exception =>
        System.err.println(s"[MakeAClaimCaseRecorder] Failed to write successful-cases CSV: ${e.getMessage}")
        e.printStackTrace(System.err)
    }
  }

  private def escape(value: String): String = {
    if (value == null) ""
    else if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      "\"" + value.replace("\"", "\"\"") + "\""
    } else value
  }
}
