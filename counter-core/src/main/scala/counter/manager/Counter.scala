package counter.manager

import akka.actor.{Actor, ActorRef, Props}
import counter.manager.Counter._
import counter.operation.StartOperation.Started
import counter.warning.WarningCollector.CounterExceeded

class Counter(name: String, warningCollector: ActorRef) extends Actor {

  def receive = init

  def init: Receive = {
    case Init(limit) =>
      sender() ! Started
      context become {
        active(limit)
      }
  }

  def active(limit: Long, actualValue: Long = 0L): Receive = {
    case Init(newLimit) =>
      sender() ! Started

      context become {
        active(newLimit)
      }
    case State =>
      sender() ! CounterDetails(limit, actualValue)
    case Count(value) =>
      context become {
        val newValue = actualValue + value
        if (newValue >= limit) {
          warningCollector ! CounterExceeded(name, limit, newValue)

          init
        } else {
          active(limit, newValue)
        }
      }
  }

}

object Counter {
  case class Init(limit: Long)
  case class Count(value: Long)
  case object State

  case class CounterDetails(limit: Long, used: Long)

  def props(name: String, warningCollector: ActorRef) = Props(new Counter(name, warningCollector))
}
