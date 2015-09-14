package counter.operation

import akka.testkit.TestProbe
import counter.BaseSpec
import counter.manager.Counter.CounterDetails
import counter.manager.CounterManager
import counter.operation.DetailsOperation.GetCounter
import counter.operation.OperationReceiver.{CounterNotFound, Success}
import counter.operation.StartOperation.StartCounter
import counter.operation.StopOperation.StopCounter

class OperationReceiverSpec extends BaseSpec {

  val warningCollector = TestProbe()
  val counterManager = system.actorOf(CounterManager.props(warningCollector.ref))
  val operationReceiver = system.actorOf(OperationReceiver.props(counterManager))

  "OperationReceiver" should {
    "create Counter" in {
      operationReceiver ! StartCounter("A", 100)
      expectMsg(Success())
    }

    "stop Counter" in {
      operationReceiver ! StartCounter("B", 100)
      expectMsg(Success())

      operationReceiver ! StopCounter("B")
      expectMsg(Success())
    }

    "do not stop unknown Counter" in {
      operationReceiver ! StopCounter("C")
      expectMsg(CounterNotFound())
    }

    "get Counter details" in {
      operationReceiver ! StartCounter("D", 100)
      expectMsg(Success())

      operationReceiver ! GetCounter("D")
      expectMsg(CounterDetails(100, 0))
    }

    "do not get unknown Counter details" in {
      operationReceiver ! GetCounter("E")

      expectMsg(CounterNotFound())
    }
  }
}
