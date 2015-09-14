package counter.operation

import akka.actor.{Actor, ActorRef, Props}
import counter.manager.CounterManager.{NotFound, Stop}
import counter.operation.OperationReceiver.{CounterNotFound, Success}
import counter.operation.StopOperation.{StopCounter, Stopped}

class StopOperation(counterManager: ActorRef) extends Actor {
  def receive = {
    case StopCounter(name) =>
      counterManager ! Stop(name)

      context become {
        replyTo(sender()).andThen(stopItself)
      }
  }

  def replyTo(origin: ActorRef): Receive = {
    case Stopped =>
      origin ! Success()
    case NotFound =>
      origin ! CounterNotFound()
  }

  def stopItself: Receive = {
    case _ =>
      context.stop(self)
  }
}

object StopOperation {
  case class StopCounter(name: String)

  case object Stopped

  def props(counterManager: ActorRef) = Props(new StopOperation(counterManager))
}
