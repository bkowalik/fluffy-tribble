package com.example.model

import enumeratum._

sealed abstract class EventType(override val entryName: String)
    extends EnumEntry

object EventType extends Enum[EventType] with CirceEnum[EventType] {
  val values: IndexedSeq[EventType] = findValues

  case object Bar extends EventType("bar")
  case object Baz extends EventType("baz")
  case object Foo extends EventType("foo")
}
