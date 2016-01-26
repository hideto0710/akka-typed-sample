package com.hideto0710.akkaTypedSample

import akka.typed.ScalaDSL._
import akka.typed._

object Fridge extends App {

  sealed trait Action
  case class Store(from: ActorRef[Result], food: String) extends Action
  case class Take(from: ActorRef[Result], food: String) extends Action

  def fridge(foodList: Seq[String]): Behavior[Action] = Total[Action] {
    case Store(from, food) =>
      from ! Ok
      fridge(foodList :+ food)

    case Take(from, food) if foodList.contains(food) =>
      from ! Ok(food)
      fridge(foodList.filter( _ != food ))

    case Take(from, food) if !foodList.contains(food) =>
      from ! NotFound
      Same
  }

  sealed trait Result
  case object Ok extends Result
  case class Ok(food: String) extends Result
  case object NotFound extends Result

  val handler: Behavior[Result] = Static[Result] {
    case Ok => println("ok")
    case Ok(food) => println(s"ok: $food")
    case NotFound => println("not_found")
  }

  object Root {
    val run = Full[Unit] {
      case Sig(ctx, PreStart) =>
        val h = ctx.spawn(Props(handler), "Handler")
        val f = ctx.spawn(Props(fridge(Seq.empty)), "Fridge")
        f ! Store(h, "milk")
        f ! Store(h, "bacon")
        f ! Take(h, "bacon")
        f ! Take(h, "turkey")

        // waiting...
        Thread.sleep(500)
        Stopped
    }
  }

  val system = ActorSystem("Root", Props(Root.run))
}