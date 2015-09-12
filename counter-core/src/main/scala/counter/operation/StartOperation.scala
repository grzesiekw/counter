package counter.operation

import akka.actor.{Props, Actor, ActorRef}
import counter.manager.CounterManager.{Stop, Start}
import counter.operation.OperationReceiver.{Failure, Success}
import counter.operation.StartOperation.{StartCounter, Started}
import counter.operation.StopOperation.{StopCounter, Stopped}

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
      origin ! Success
    case _ =>
      origin ! Failure
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
