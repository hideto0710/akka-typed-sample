package com.hideto0710.akkaTypedSample

import akka.typed.{Behavior, ActorSystem, Props}
import akka.typed.ScalaDSL._

object HelloActor extends App {

  case class Message(value: String)

  val behavior: Behavior[Message] = Static[Message] {
    case Message(value) => println(value)
  }

  val system = ActorSystem("MySystem", Props(behavior))
  system ! Message("Hello World")
  system.terminate()
}