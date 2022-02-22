package com.example

import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

final case class HttpConfig(interface: String, port: Int)

final case class ProcessRunnerConfig(command: String)

final case class EventProcessingConfig(
    windowLength: FiniteDuration,
    windowMaxElements: Int
)

final case class AppConfig(
    http: HttpConfig,
    processRunner: ProcessRunnerConfig,
    eventProcessing: EventProcessingConfig
)

object AppConfig {
  def apply(): AppConfig =
    ConfigSource.default.loadOrThrow[AppConfig]
}
