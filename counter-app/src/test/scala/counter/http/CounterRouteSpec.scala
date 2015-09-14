package counter.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import counter.manager.Counter.CounterDetails
import counter.operation.OperationReceiver.{CounterNotFound, Success}

import scala.concurrent.Future

class CounterRouteSpec extends BaseSpec with CounterProtocol with SprayJsonSupport {

  val counterApi = new CounterApi with SuccessCounterService {
    implicit val executionContext = system.dispatcher
  }

  val counterApiFailure = new CounterApi with FailureCounterService {
    implicit val executionContext = system.dispatcher
  }

  "Get Counter via CounterApi" should {
    "response with details" in {
      Get("/counter/test1") ~> counterApi.route ~> check {
        status shouldBe OK
        responseAs[CounterDetails] shouldBe CounterDetails(1, 1)
      }
    }

    "response NotFound when Counter does not exist" in {
      Get("/counter/test1") ~> counterApiFailure.route ~> check {
        status shouldBe NotFound
      }
    }
  }

  "Starting Counter via CounterApi" should {
    "response OK when Counter was started" in {
      Post("/counter/test1/limit/10") ~> counterApi.route ~> check {
        status shouldBe OK
      }
    }
  }

  "Stopping Counter via CounterApi" should {
    "response OK when Counter was stopped" in {
      Delete("/counter/test1") ~> counterApi.route ~> check {
        status shouldBe OK
      }
    }

    "response NotFound when Counter does not exist" in {
      Delete("/counter/test1") ~> counterApiFailure.route ~> check {
        status shouldBe NotFound
      }
    }
  }
}

trait SuccessCounterService extends CounterService {
  override def details(name: String) = Future.successful(Right(CounterDetails(1, 1)))
  override def start(name: String, limit: Long) = Future.successful(Success())
  override def stop(name: String) = Future.successful(Right(Success()))
}

trait FailureCounterService extends CounterService {
  override def details(name: String) = Future.successful(Left(CounterNotFound()))
  override def start(name: String, limit: Long) = Future.successful(Success())
  override def stop(name: String) = Future.successful(Left(CounterNotFound()))
}
