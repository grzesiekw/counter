package counter.warning

import akka.actor.{Props, Actor}

class WarningCollector extends Actor {
  import WarningCollector._
  def receive = {
    case CounterExceeded(name, limit, actualValue) =>
      println(s"Counter $name was exceeded (limit: $limit, value: $actualValue)")
  }
}

object WarningCollector {
  case class CounterExceeded(name: String, limit: Long, actualValue: Long)

  def props() = Props(classOf[WarningCollector])
}
