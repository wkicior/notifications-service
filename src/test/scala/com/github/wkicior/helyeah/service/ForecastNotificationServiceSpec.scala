package com.github.wkicior.helyeah.service

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.{ TestActors, TestKit, ImplicitSender }
import com.github.wkicior.helyeah.model.{ConditionEntry, Day, Forecast, NotificationRequest}
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll

/**
 * Created by disorder on 22.02.15.
 */
class ForecastNotificationServiceSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("ForecastNotificationServiceSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  "An Notification actor" must {

    "handle notificationRequest" in {
      val notificationService = system.actorOf(Props[ForecastNotificationService])
      val condEntry: ConditionEntry = new ConditionEntry(12, 13, 14, 15)
      val days: Day = new Day(List(condEntry), "date")
      val forecast: Forecast = new Forecast(List(days))
      val notificationRequest = new NotificationRequest(forecast)
      notificationService ! notificationRequest
    }

  }

}
