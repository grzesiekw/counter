package counter.http

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class BaseSpec extends WordSpec with Matchers with ScalatestRouteTest
