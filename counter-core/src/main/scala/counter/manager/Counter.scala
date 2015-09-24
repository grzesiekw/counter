package counter.manager

import akka.actor.{Actor, ActorRef, Props}
import akka.persistence.{RecoveryCompleted, PersistentActor}
import counter.manager.Counter._
import counter.operation.StartOperation.Started
import counter.warning.WarningCollector.CounterExceeded

class Counter(name: String, warningCollector: ActorRef) extends PersistentActor {

  override def persistenceId = s"counter-$name"

  def receiveRecover = {
    case CounterInit(limit) =>
      context become {
        active(limit)
      }
    case CounterValue(limit, actualValue) =>
      context become {
        active(limit, actualValue)
      }
  }

  def receiveCommand = init

  def init: Receive = {
    case Init(limit) =>
      initialize(limit)
  }

  def active(limit: Long, actualValue: Long = 0L): Receive = {
    case Init(newLimit) =>
      initialize(newLimit)
    case State =>
      sender() ! CounterDetails(limit, actualValue)
    case Count(value) =>
      val newValue = actualValue + value
      if (newValue >= limit) {
        persist(CounterInit(limit)) { event =>
          warningCollector ! CounterExceeded(name, limit, newValue)

          context become {
            init
          }
        }
      } else {
        persist(CounterValue(limit, newValue)) { event =>
          context become {
            active(limit, newValue)
          }
        }
      }
  }

  def initialize(limit: Long): Unit = {
    persist(CounterInit(limit)) { event =>
      sender() ! Started

      context become {
        active(event.limit)
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

case class CounterInit(limit: Long)
case class CounterValue(limit: Long, actualValue: Long)
