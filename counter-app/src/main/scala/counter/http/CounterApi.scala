package counter.http

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import counter.manager.Counter.CounterDetails
import counter.operation.DetailsOperation.GetCounter
import counter.operation.OperationReceiver.{CounterNotFound, Success}
import counter.operation.StartOperation.StartCounter
import counter.operation.StopOperation.StopCounter
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContext, Future}

trait CounterService {
  type Result[T] = Either[CounterNotFound, T]

  def start(name: String, limit: Long): Future[Success]
  def stop(name: String): Future[Result[Success]]
  def details(name: String): Future[Result[CounterDetails]]
}

trait ActorCounterService extends CounterService {
  implicit val executionContext: ExecutionContext
  implicit val operationTimeout: Timeout

  val operationReceiver: ActorRef

  override def start(name: String, limit: Long) = (operationReceiver ? StartCounter(name, limit)).mapTo[Success]

  override def stop(name: String) = (operationReceiver ? StopCounter(name)).map {
    case s: Success => Right(s)
    case cnf: CounterNotFound => Left(cnf)
  }

  override def details(name: String) = (operationReceiver ? GetCounter(name)).map {
    case cd: CounterDetails => Right(cd)
    case cnf: CounterNotFound => Left(cnf)
  }
}

trait CounterProtocol extends DefaultJsonProtocol {
  implicit val counterDetailsFormat = jsonFormat2(CounterDetails)
}

trait CounterApi extends CounterProtocol with SprayJsonSupport { this: CounterService =>

  implicit val executionContext: ExecutionContext

  val route = pathPrefix("counter" / Segment) { name =>
    get {
      complete {
        details(name).map[ToResponseMarshallable] {
          case Right(details) => details
          case Left(CounterNotFound()) => StatusCodes.NotFound
        }
      }
    } ~
    (path("limit" / LongNumber) & post) { limit =>
      complete {
        start(name, limit).map[ToResponseMarshallable] {
          case Success() => StatusCodes.OK
        }
      }
    } ~
    delete {
      complete {
        stop(name).map[ToResponseMarshallable] {
          case Right(Success()) => StatusCodes.OK
          case Left(CounterNotFound()) => StatusCodes.NotFound
        }
      }
    }
  }

}
