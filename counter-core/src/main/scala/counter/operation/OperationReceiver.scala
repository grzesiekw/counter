package counter.operation

import akka.actor.{Actor, ActorRef, Props}

class OperationReceiver(counterManager: ActorRef) extends Actor {
  import StartOperation.StartCounter
  import StopOperation.StopCounter

  def receive = {
    case start: StartCounter =>
      context.actorOf(StartOperation.props(counterManager)) forward start

    case stop: StopCounter =>
      context.actorOf(StopOperation.props(counterManager)) forward stop
  }
}

object OperationReceiver {
  abstract class Response
  case class Success() extends Response
  case class Failure() extends Response

  def props(counterManager: ActorRef) = Props(new OperationReceiver(counterManager))
}
