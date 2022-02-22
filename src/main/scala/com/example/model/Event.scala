package com.example.model

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{
  deriveConfiguredDecoder,
  deriveConfiguredEncoder
}
import io.circe.{Decoder, Encoder}

final case class Event(eventType: String, data: String, timestamp: Long)

object Event {
  implicit val configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val decoder: Decoder[Event] = deriveConfiguredDecoder
  implicit val encoder: Encoder[Event] = deriveConfiguredEncoder
}
