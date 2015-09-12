package counter.http

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.testkit.ScalatestRouteTest
import counter.operation.OperationReceiver.Success
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class CounterRouteSpec extends WordSpec with ScalatestRouteTest with Matchers with CounterRoute with SuccessCounterService {

  implicit val executionContext = system.dispatcher

  "Counter" should {
    "response OK when Counter was started" in {
      Post("/counter/test1/limit/10") ~> route ~> check {
        status shouldBe OK
      }
    }

    "response OK when Counter was stopped" in {
      Delete("/counter/test1") ~> route ~> check {
        status shouldBe OK
      }
    }
  }
}

trait SuccessCounterService extends CounterService {
  override def start(name: String, limit: Int) = Future.successful(Success())
  override def stop(name: String) = Future.successful(Success())
}
