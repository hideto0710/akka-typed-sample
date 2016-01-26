package com.hideto0710.akkaTypedSample

import akka.typed.{Behavior, ActorSystem, Props}
import akka.typed.ScalaDSL._

object Hello extends App {

  case class Message(value: String)

  val behavior: Behavior[Message] = Static[Message] {
    case Message(value) => println(value)
  }

  val system = ActorSystem("Hello", Props(behavior))
  system ! Message("Hello World")
  system.terminate()
}