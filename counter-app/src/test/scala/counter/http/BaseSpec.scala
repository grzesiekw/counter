package counter.http

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

abstract class BaseSpec extends WordSpec with Matchers with ScalatestRouteTest
