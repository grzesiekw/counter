package counter.event

import akka.actor.{Actor, ActorRef, Props}
import counter.manager.CounterManager.Increase

class EventReceiver(counterManager: ActorRef) extends Actor {
  import EventReceiver._

  def receive = {
    case Event(counterName, value) =>
      counterManager ! Increase(counterName, value)
  }
}

object EventReceiver {
  case class Event(counterName: String, value: Long)

  def props(counterManager: ActorRef) = Props(new EventReceiver(counterManager))

}
