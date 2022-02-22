package com.example.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class EventTypeStats(wordsCount: Int, eventsCount: Int)

object EventTypeStats {
  implicit val encoder: Encoder[EventTypeStats] = deriveEncoder
}

final case class Stats(stats: Map[String, EventTypeStats])

object Stats {
  val Zero: Stats = Stats(Map.empty)

  implicit val encoder: Encoder[Stats] = deriveEncoder
}
