### XUI Gatling Performance Tests

This script runs a suite of services against XUI in a cohabitation scenario:
- General caseworker flows
- Probate solicitor journey
- Divorce solicitor journey
- Financial Remedy solicitor journey
- Family Public Law solicitor journey
- Immigration and Asylum solicitor journey 

To run locally:
- Performance test against the perftest environment: `./gradlew gatlingRun`

Flags:
- Debug (single-user mode): `-Ddebug=on e.g. ./gradlew gatlingRun -Ddebug=on`
- Run against AAT: `Denv=aat e.g. ./gradlew gatlingRun -Denv=aat`

