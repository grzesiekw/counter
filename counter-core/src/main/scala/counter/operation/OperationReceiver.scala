package counter.operation

import akka.actor.{ActorRef, Props, Actor}

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
  case object Success
  case object Failure

  def props(counterManager: ActorRef) = Props(new OperationReceiver(counterManager))
}
