package com.hideto0710.akkaTypedSample

import akka.typed.{ActorSystem, Props}
import akka.typed.ScalaDSL._

object HelloActor {
  case class Message(value: String)

  def main(args: Array[String]): Unit = {
    val behavior = Static[Message] {
      case Message(value) => println(value)
    }

    val system = ActorSystem("MySystem", Props(behavior))
    system ! Message("Hello World")
    system.terminate()
  }
}
