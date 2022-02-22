package com.example.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.example.processing.ExposeStats
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

class EventsApi(exposeStats: ExposeStats) {

  val route: Route = pathPrefix("events") {
    path("stats") {
      get {
        complete(exposeStats.getStats())
      }
    }
  }

}

object EventsApi {
  def apply(exposeStats: ExposeStats): EventsApi =
    new EventsApi(exposeStats)
}
