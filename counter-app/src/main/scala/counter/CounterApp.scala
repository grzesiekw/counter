package counter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import counter.http.{ActorCounterService, CounterApi}
import counter.manager.CounterManager
import counter.operation.OperationReceiver
import counter.warning.WarningCollector

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object CounterApp extends App with CounterApi with ActorCounterService {
  val config = ConfigFactory.load()

  implicit val system = ActorSystem("counter")
  implicit val materializer = ActorMaterializer()

  val executionContext: ExecutionContext = system.dispatcher
  val operationTimeout: Timeout = config.getLong("api.timeout") millis

  val warningCollector = system.actorOf(WarningCollector.props())
  val counterManager = system.actorOf(CounterManager.props(warningCollector))
  val operationReceiver = system.actorOf(OperationReceiver.props(counterManager))

  val binding = Http().bindAndHandle(route, config.getString("api.host"), config.getInt("api.port"))

  println("Counter is up and running")

}
