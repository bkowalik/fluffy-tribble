package com.example.processing

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.example.model
import com.example.model.{Event, EventTypeStats, Stats}
import io.circe.parser.decode

import scala.concurrent.duration.FiniteDuration

abstract class ProcessingFlow {
  protected val statsFlow: Flow[Seq[Event], Stats, _] = Flow[Seq[Event]].map {
    events =>
      val stats = events
        .groupBy(_.eventType)
        .view
        .mapValues { events =>
          val wordsCount = events.flatMap(_.data.split("\\s")).size
          val eventsCount = events.size
          EventTypeStats(wordsCount, eventsCount)
        }
        .toMap

      model.Stats(stats)
  }

  protected def processFlow(
      maxElements: Int,
      length: FiniteDuration
  ): Flow[String, Stats, NotUsed] =
    Flow[String]
      .mapConcat(_.split("\\n"))
      .async
      .mapConcat(decode[Event](_).toSeq)
      .groupedWithin(maxElements, length)
      .via(statsFlow)
}
