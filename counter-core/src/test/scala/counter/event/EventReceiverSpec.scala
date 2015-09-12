package counter.event

import akka.testkit.TestProbe
import counter.BaseSpec
import counter.event.EventReceiver.Event
import counter.manager.CounterManager
import counter.operation.OperationReceiver
import counter.operation.OperationReceiver.Success
import counter.operation.StartOperation.StartCounter
import counter.warning.WarningCollector.CounterExceeded

import scala.concurrent.duration._

class EventReceiverSpec extends BaseSpec {

  val warningCollector = TestProbe()
  val counterManager = system.actorOf(CounterManager.props(warningCollector.ref))
  val eventReceiver = system.actorOf(EventReceiver.props(counterManager))
  val operationReceiver = system.actorOf(OperationReceiver.props(counterManager))

  "EventReceiver" should {
    "increase Counter below limit" in {
      operationReceiver ! StartCounter("A", 10)
      expectMsg(Success())

      eventReceiver ! Event("A", 9)
      warningCollector.expectNoMsg(100 millis)
    }

    "increase Counter above limit" in {
      operationReceiver ! StartCounter("B", 10)
      expectMsg(Success())

      eventReceiver ! Event("B", 5)
      eventReceiver ! Event("B", 6)
      warningCollector.expectMsg(CounterExceeded("B", 10, 11))
    }
  }

}
