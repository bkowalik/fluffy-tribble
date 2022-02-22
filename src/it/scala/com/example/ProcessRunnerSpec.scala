package com.example

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink}
import akka.testkit.TestKitBase
import com.example.runner.ProcessRunner
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.IOException

class ProcessRunnerSpec extends AsyncFlatSpec with Matchers with TestKitBase {

  override implicit def system: ActorSystem = ActorSystem()

  private val input: String = "test"
  private val config: ProcessRunnerConfig = ProcessRunnerConfig(
    s"echo -n '$input'"
  )
  private val processRunner = new ProcessRunner(config)

  "process runner" should "capture process output" in {
    val (processControl, result) =
      processRunner.source().toMat(Sink.head)(Keep.both).run()

    result.map { result =>
      result shouldBe input
      processControl.process.isAlive() shouldBe false
      an[IOException] shouldBe thrownBy(processControl.stream.read())
    }
  }

}
