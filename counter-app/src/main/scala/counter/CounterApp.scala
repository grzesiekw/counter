package counter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import counter.http.{ActorCounterService, CounterRoute}
import counter.manager.CounterManager
import counter.operation.OperationReceiver
import counter.warning.WarningCollector

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object CounterApp extends App with CounterRoute with ActorCounterService {
  implicit val system = ActorSystem("counter")
  implicit val materializer = ActorMaterializer()

  val executionContext: ExecutionContext = system.dispatcher
  val operationTimeout: Timeout = 200 millis

  val warningCollector = system.actorOf(WarningCollector.props())
  val counterManager = system.actorOf(CounterManager.props(warningCollector))
  val operationReceiver = system.actorOf(OperationReceiver.props(counterManager))

  val binding = Http().bindAndHandle(route, "localhost", 8080)

  println("Counter is up and running")

}
