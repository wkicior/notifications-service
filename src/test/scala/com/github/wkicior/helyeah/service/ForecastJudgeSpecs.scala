package com.github.wkicior.helyeah.service

import akka.actor.{ActorSystem, _}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}



/**
 * Created by disorder on 25.02.15.
 */
class ForecastJudgeSpecs(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("ForecastJudgeSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val forecastJudge = system.actorOf(ForecastJudge.props)

  "A ForecastJudge actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        forecastJudge ! "fail"
      }
    }
  }

}
