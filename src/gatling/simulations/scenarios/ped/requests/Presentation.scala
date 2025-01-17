package scenarios.ped.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Presentation {

  val StartPresenting =

    exec(ws("PED_040_010_StartPresenting")
      .sendText(ElFileBody("bodies/ped/StartPresenting.json"))
      .await(10)(ws.checkTextMessage("PED_040_020_StartPresentingResponse")
        .check(jsonPath("$.data.eventName").is("IcpPresenterUpdated"))
        .check(jsonPath("$.data.data.id").is("#{connectionId}"))))

  val StopPresenting =

    exec(ws("PED_900_010_StopPresenting")
      .sendText(ElFileBody("bodies/ped/StopPresenting.json"))
      .await(10)(ws.checkTextMessage("PED_997_020_StopPresentingResponse")
        .check(jsonPath("$.data.eventName").is("IcpPresenterUpdated")))
    )

}


