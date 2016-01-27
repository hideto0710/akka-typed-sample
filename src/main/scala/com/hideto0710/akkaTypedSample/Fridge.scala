package com.hideto0710.akkaTypedSample

import akka.typed._
import akka.typed.ScalaDSL._

object Fridge {

  case class Result(actorRef: ActorRef[Action], result: Msg) {
    def getMessage: String = s"{${actorRef.toString()},${result.string}}"
  }

  sealed trait Msg {
    val string: String
  }
  case object Ok extends Msg {
    override val string = "ok"
  }
  case class Ok(val food: String) extends Msg {
    override val string = s"{ok,$food}"
  }
  case object NotFound extends Msg {
    override val string = "not_found"
  }

  sealed trait Action
  case class Store(food: String, replyTo: ActorRef[Result]) extends Action
  case class Take(food: String, replyTo: ActorRef[Result]) extends Action

  def fridge(foodList: Seq[String]): Behavior[Action] = SelfAware[Action] { self =>
    Total[Action] {
      case Store(food, replyTo) =>
        replyTo ! Result(self, Ok)
        fridge(foodList :+ food)

      case Take(food, replyTo) if foodList.contains(food) =>
        replyTo ! Result(self, Ok(food))
        fridge(foodList.filter(_ != food))

      case Take(food, replyTo) if !foodList.contains(food) =>
        replyTo ! Result(self, NotFound)
        Same
    }
  }
}

object FridgeApp extends App {

  import Fridge._

  private val handler = Static[Result] {
    case result => println(result.getMessage)
  }

  val run: Behavior[Unit] = Full[Unit] {
    case Sig(ctx, PreStart) =>
      val h = ctx.spawn(Props(handler), "handler")
      val f = ctx.spawn(Props(fridge(Seq.empty)), "fridge")

      f ! Store("milk", h)
      f ! Store("bacon", h)
      f ! Take("bacon", h)
      f ! Take("turkey", h)

      // waiting...
      Thread.sleep(500)
      Stopped
  }

  val system = ActorSystem("Root", Props(run))
}