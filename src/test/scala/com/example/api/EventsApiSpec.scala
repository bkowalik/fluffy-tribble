package com.example.api

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.example.BaseSpec
import com.example.model.Stats
import com.example.processing.ExposeStats
import io.circe.JsonObject
import io.circe.parser._
import org.scalatest.{EitherValues, OptionValues}

class EventsApiSpec
    extends BaseSpec
    with ScalatestRouteTest
    with EitherValues
    with OptionValues {

  private val exposeStats = new ExposeStats {
    override def getStats(): Stats = Stats.Zero
  }
  private val route = new EventsApi(exposeStats).route

  it should "return stats object" in {
    Get("/events/stats") ~> route ~> check {
      status shouldBe StatusCodes.OK
      contentType shouldBe ContentTypes.`application/json`
      (parse(
        responseAs[String]
      ).value \\ "stats").head.asObject.value shouldBe JsonObject()
    }
  }

}
