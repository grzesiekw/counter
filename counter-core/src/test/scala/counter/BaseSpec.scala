package counter

import akka.actor.ActorSystem
import akka.testkit.TestKit._
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

abstract class BaseSpec extends TestKit(ActorSystem("Test")) with DefaultTimeout with ImplicitSender with
WordSpecLike with Matchers with BeforeAndAfterAll {
  override protected def afterAll() = {
    shutdownActorSystem(system)
  }
}
