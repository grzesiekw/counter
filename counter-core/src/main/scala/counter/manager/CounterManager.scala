package counter.manager

import akka.actor.{Actor, ActorRef, Props}
import counter.manager.Counter.{Count, Init, State}
import counter.operation.StopOperation.Stopped
import counter.warning.WarningCollector.CounterUnknown

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
    case Details(name) =>
      context.child(name) match {
        case Some(ref) =>
          ref forward State
        case None =>
          sender() ! NotFound
      }
    case Increase(name, value) =>
      context.child(name) match {
        case Some(ref) =>
          ref forward Count(value)
        case None =>
          warningCollector ! CounterUnknown(name)
      }
  }
}

object CounterManager {
  case class Start(name: String, limit: Long)
  case class Stop(name: String)

  case class Details(name: String)

  case class Increase(name: String, value: Long)

  case object NotFound

  def props(warningCollector: ActorRef) = Props(new CounterManager(warningCollector))
}
