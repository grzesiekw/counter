package counter.manager

import akka.actor.{ActorRef, Actor, Props}
import counter.manager.Counter.{Count, Init}
import counter.operation.StopOperation.Stopped
import counter.warning.WarningCollector

class CounterManager(warningCollector: ActorRef) extends Actor {
  import CounterManager._

  def receive = {
    case Start(name, limit) =>
      val counter = context.child(name).getOrElse(context.actorOf(Counter.props(name, warningCollector), name))

      counter forward Init(limit)
    case Stop(name) =>
      context.child(name) match {
        case Some(ref) =>
          context.stop(ref)
          sender() ! Stopped

        case None =>
          sender() ! NotFound
      }
    case Increase(name, value) =>
      context.child(name) match {
        case Some(ref) =>
          ref ! Count(value)
        case None =>
          sender() ! NotFound
      }
  }
}

object CounterManager {
  case class Start(name: String, limit: Long)
  case class Stop(name: String)

  case class Increase(name: String, value: Long)

  case object NotFound

  def props(warningCollector: ActorRef) = Props(new CounterManager(warningCollector))
}
