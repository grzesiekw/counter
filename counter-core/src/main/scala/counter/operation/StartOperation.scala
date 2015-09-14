package counter.operation

import akka.actor.{Actor, ActorRef, Props}
import counter.manager.CounterManager.Start
import counter.operation.OperationReceiver.Success
import counter.operation.StartOperation.{StartCounter, Started}

class StartOperation(counterManager: ActorRef) extends Actor {

  def receive = {
    case StartCounter(name, limit) =>
      counterManager ! Start(name, limit)

      context become {
        replyTo(sender()).andThen(stopItself)
      }
  }

  def replyTo(origin: ActorRef): Receive = {
    case Started =>
      origin ! Success()
  }

  def stopItself: Receive = {
    case _ =>
      context.stop(self)
  }

}

object StartOperation {
  case class StartCounter(name: String, limit: Long)
  case object Started

  def props(counterManager: ActorRef) = Props(new StartOperation(counterManager))
}
