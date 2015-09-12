package counter.http

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import counter.operation.OperationReceiver.{Response, Failure, Success}
import counter.operation.StartOperation.StartCounter
import counter.operation.StopOperation.StopCounter

import scala.concurrent.{Future, ExecutionContext}

trait CounterService {
  def start(name: String, limit: Int): Future[Response]
  def stop(name: String): Future[Response]
}

trait ActorCounterService extends CounterService {

  implicit val executionContext: ExecutionContext
  implicit val operationTimeout: Timeout

  val operationReceiver: ActorRef

  override def start(name: String, limit: Int) = (operationReceiver ? StartCounter(name, limit)).mapTo[Response]
  override def stop(name: String) = (operationReceiver ? StopCounter(name)).mapTo[Response]
}

trait CounterApi { this: CounterService =>

  implicit val executionContext: ExecutionContext

  val route = pathPrefix("counter") {
    (path(Segment / "limit" / IntNumber) & post) { (name, limit) =>
      complete {
        start(name, limit).map[ToResponseMarshallable] {
          case Success() => StatusCodes.OK
        }
      }
    } ~
    (path (Segment) & delete) { name =>
      complete {
        stop(name).map[ToResponseMarshallable] {
          case Success() => StatusCodes.OK
          case Failure() => StatusCodes.NotFound
        }
      }
    }
  }

}
