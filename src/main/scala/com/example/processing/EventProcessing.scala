package com.example.processing

import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.example.EventProcessingConfig
import com.example.model.Stats
import com.example.runner.ProcessRunner.ProcessControl

import java.util.concurrent.atomic.AtomicReference

class EventProcessing(config: EventProcessingConfig)(implicit mat: Materializer)
    extends ProcessingFlow
    with ExposeStats {

  private val currentStats = new AtomicReference(Stats.Zero)

  def process(source: Source[String, ProcessControl]): ProcessControl = {
    val flow = processFlow(config.windowMaxElements, config.windowLength)
    source
      .via(flow)
      .toMat(Sink.fold(Stats.Zero) { case (_, current) =>
        currentStats.set(current)
        current
      })(Keep.left)
      .run()
  }

  def getStats(): Stats = currentStats.get()
}

object EventProcessing {
  def apply(config: EventProcessingConfig)(implicit
      mat: Materializer
  ): EventProcessing =
    new EventProcessing(config)
}
