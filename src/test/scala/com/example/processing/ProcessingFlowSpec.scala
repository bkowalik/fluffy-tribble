package com.example.processing

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKitBase
import com.example.model.{EventTypeStats, Stats}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AsyncFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.duration._

class ProcessingFlowSpec
    extends ProcessingFlow
    with AsyncFlatSpecLike
    with Matchers
    with TestKitBase
    with BeforeAndAfterAll {

  override implicit def system: ActorSystem = ActorSystem()

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 5.seconds)
  }

  "processing flow" should "process input dropping broken events" in {
    Source
      .single(
        """
        |{ "�c$�����
        |{ "event_type": "foo", "data": "ipsum", "timestamp": 1645481198 }
        |{ "event_type": "bar", "data": "lorem", "timestamp": 1645481198 }
        |{ "event_type": "bar", "data": "dolor", "timestamp": 1645481198 }
        |{ "event_type": "bar", "data": "sit", "timestamp": 1645481198 }
        |{ "�@FF�ơ��
        |{ "event_type": "bar", "data": "lorem", "timestamp": 1645481198 }
        |{ "event_type": "bar", "data": "lorem", "timestamp": 1645481198 }
        |{ "I�1���
        |{ "event_type": "baz", "data": "dolor", "timestamp": 1645481198 }
        |{ "event_type": "bar", "data": "ipsum", "timestamp": 1645481198 }""".stripMargin
      )
      .via(processFlow(10, 100.millis))
      .runWith(Sink.seq)
      .map { stats =>
        stats should have size 1
        stats.head shouldBe Stats(
          Map(
            "foo" -> EventTypeStats(1, 1),
            "bar" -> EventTypeStats(6, 6),
            "baz" -> EventTypeStats(1, 1)
          )
        )
      }
  }
}
