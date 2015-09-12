package counter.http

import akka.http.scaladsl.model.StatusCodes._
import counter.operation.OperationReceiver.{Failure, Success}

import scala.concurrent.Future

class CounterRouteSpec extends BaseSpec {

  val counterApi = new CounterApi with SuccessCounterService {
    implicit val executionContext = system.dispatcher
  }

  val counterApiFailure = new CounterApi with FailureCounterService {
    implicit val executionContext = system.dispatcher
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
  override def start(name: String, limit: Int) = Future.successful(Success())
  override def stop(name: String) = Future.successful(Success())
}

trait FailureCounterService extends CounterService {
  override def start(name: String, limit: Int) = Future.successful(Success())
  override def stop(name: String) = Future.successful(Failure())
}
