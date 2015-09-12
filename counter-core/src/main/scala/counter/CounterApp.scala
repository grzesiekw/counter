package counter

import akka.actor.ActorSystem
import counter.event.EventReceiver
import counter.manager.CounterManager
import counter.operation.OperationReceiver
import counter.warning.WarningCollector

object CounterApp extends App {

  println("Counter is up and running")

  implicit val system = ActorSystem("counter")

  val warningCollector = system.actorOf(WarningCollector.props())
  val counterManager = system.actorOf(CounterManager.props(warningCollector))
  val operationReceiver = system.actorOf(OperationReceiver.props(counterManager))
  val eventReceiver = system.actorOf(EventReceiver.props(counterManager))


}
