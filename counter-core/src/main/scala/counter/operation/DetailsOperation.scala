package counter.operation

import akka.actor.{Actor, ActorRef, Props}
import counter.manager.Counter.CounterDetails
import counter.manager.CounterManager.{Details, NotFound}
import counter.operation.DetailsOperation.GetCounter
import counter.operation.OperationReceiver.CounterNotFound

class DetailsOperation(counterManager: ActorRef) extends Actor {
  def receive = {
    case GetCounter(name) =>
      counterManager ! Details(name)

      context become {
        replyTo(sender()).andThen(stopItself)
      }
  }

  def replyTo(origin: ActorRef): Receive = {
    case details: CounterDetails =>
      origin ! details
    case NotFound =>
      origin ! CounterNotFound()
  }

  def stopItself: Receive = {
    case _ =>
      context.stop(self)
  }
}

object DetailsOperation {
  case class GetCounter(name: String)

  def props(counterManager: ActorRef) = Props(new DetailsOperation(counterManager))
}
