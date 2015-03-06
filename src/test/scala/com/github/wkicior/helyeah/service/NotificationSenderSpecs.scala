package com.github.wkicior.helyeah.service

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
 * Created by disorder on 04.03.15.
 */
class NotificationSenderSpecs (_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("NotificationSenderSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val notificationSender = system.actorOf( NotificationSender.props())

  "An NotificationSender actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationSender ! "fail"
      }
    }

    "compose and send a message" in {
      val plan: NotificationPlan = NotificationPlan("test@localhost")
      val rating: ForecastRating = ForecastRating(Rating.HIGH, DateTime.now)
      val forecast: Forecast = Forecast(Seq())
      notificationSender ! NotificationComposerMessage(plan, rating, forecast)
    }
  }
}
