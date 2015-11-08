package com.github.wkicior.helyeah.service

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.testkit.TestProbe
import akka.actor.Props
import akka.actor.Actor

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
  object NotificationSenderMocked {
    def props(notificationRepositoryProps: Props): Props = Props(new NotificationSenderMocked(notificationRepositoryProps))

  }
  class NotificationSenderMocked(nrp: Props) extends NotificationSender(nrp) {
     override def sendMessage(notification: Notification): Unit = {
       //
     }
  }
  
  val notificationRepositoryProbe = TestProbe()
  val notificationRepositoryProps = Props(new Actor {
    def receive = {
      case x => notificationRepositoryProbe.ref forward x
    }
  })

  val notificationSender = system.actorOf(NotificationSenderMocked.props(notificationRepositoryProps))

  "An NotificationSender actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationSender ! "fail"
      }
    }

    "compose, save and send a message" in {
      val plan: NotificationPlan = NotificationPlan("test@localhost", "href")
      val rating: ForecastRating = ForecastRating(Rating.HIGH, DateTime.now)
      val forecast: Forecast = Forecast(Seq())
      notificationSender ! NotificationComposerMessage(plan, rating, forecast)
      val notification = Notification(plan, "It's windy ;)", rating, forecast)
      val saveNotificationMessage = SaveNotificationMessage(notification)
      notificationRepositoryProbe.expectMsg(saveNotificationMessage)
    }
  }
}
