package counter.operation

import akka.testkit.TestProbe
import counter.BaseSpec
import counter.manager.CounterManager
import counter.operation.OperationReceiver.{Failure, Success}
import counter.operation.StartOperation.StartCounter
import counter.operation.StopOperation.StopCounter

class OperationReceiverSpec extends BaseSpec {

  val warningCollector = TestProbe()
  val counterManager = system.actorOf(CounterManager.props(warningCollector.ref))
  val operationReceiver = system.actorOf(OperationReceiver.props(counterManager))

  "OperationReceiver" should {
    "create Counter" in {
      operationReceiver ! StartCounter("A", 100)
      expectMsg(Success)
    }

    "stop Counter" in {
      operationReceiver ! StartCounter("B", 100)
      expectMsg(Success)

      operationReceiver ! StopCounter("B")
      expectMsg(Success)
    }

    "do not stop unknown Counter" in {
      operationReceiver ! StopCounter("C")
      expectMsg(Failure)
    }
  }
}


