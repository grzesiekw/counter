package counter.operation

import akka.actor.{Actor, ActorRef, Props}
import counter.operation.DetailsOperation.GetCounter

class OperationReceiver(counterManager: ActorRef) extends Actor {
  import StartOperation.StartCounter
  import StopOperation.StopCounter

  def receive = {
    case get: GetCounter =>
      context.actorOf(DetailsOperation.props(counterManager)) forward get

    case start: StartCounter =>
      context.actorOf(StartOperation.props(counterManager)) forward start

    case stop: StopCounter =>
      context.actorOf(StopOperation.props(counterManager)) forward stop  
  }
}

object OperationReceiver {
  sealed trait Response
  case class Success() extends Response
  case class CounterNotFound() extends Response

  def props(counterManager: ActorRef) = Props(new OperationReceiver(counterManager))
}
