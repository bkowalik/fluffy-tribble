package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.example.api.EventsApi
import com.example.processing.EventProcessing
import com.example.runner.ProcessRunner

object Main extends App {

  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher

  private val appConfig = AppConfig()
  private val processRunner = ProcessRunner(appConfig.processRunner)
  private val eventProcessing = EventProcessing(appConfig.eventProcessing)
  private val processControl = eventProcessing.process(processRunner.source())

  private val routes: Route = EventsApi(eventProcessing).route

  val bindFuture = Http()
    .newServerAt(appConfig.http.interface, appConfig.http.port)
    .bind(routes)

  println(
    s"Server running at ${appConfig.http.interface}:${appConfig.http.port}"
  )

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      bindFuture.flatMap(_.unbind()).onComplete { _ =>
        processControl.close()
        system.terminate()
      }
    }
  })
}
