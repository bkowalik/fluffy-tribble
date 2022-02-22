package com.example.runner

import akka.stream.scaladsl.{Source, StreamConverters}
import com.example.ProcessRunnerConfig
import com.example.runner.ProcessRunner.ProcessControl

import java.io.{InputStream, PipedInputStream, PipedOutputStream}
import scala.sys.process._

class ProcessRunner(config: ProcessRunnerConfig) {

  private def redirectInputStream(
      inStream: PipedInputStream
  )(out: InputStream): Unit = {
    val ostream = new PipedOutputStream(inStream)
    try {
      val buf = Array.fill(100)(0.toByte)
      var br = 0
      while (br >= 0) {
        br = out.read(buf)
        if (br > 0) {
          ostream.write(buf, 0, br)
        }
      }
    } finally {
      out.close()
    }
  }

  def source(): Source[String, ProcessControl] = {
    val inStream = new PipedInputStream()
    val process = config.command run new ProcessIO(
      _.close(),
      redirectInputStream(inStream),
      _.close()
    )

    StreamConverters
      .fromInputStream { () =>
        inStream
      }
      .mapMaterializedValue { _ =>
        ProcessControl(process, inStream)
      }
      .map(_.utf8String)
  }

}

object ProcessRunner {
  final case class ProcessControl(process: Process, stream: PipedInputStream) {
    def close(): Unit = {
      process.destroy()
      stream.close()
    }
  }

  def apply(config: ProcessRunnerConfig): ProcessRunner =
    new ProcessRunner(config)
}
