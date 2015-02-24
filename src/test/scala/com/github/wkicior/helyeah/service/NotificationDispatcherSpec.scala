package com.github.wkicior.helyeah.service

import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.github.wkicior.helyeah.model._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
 * Created by disorder on 22.02.15.
 */
class NotificationDispatcherSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("NotificationDispatcherSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val notificationRepositoryProbe = TestProbe()
  val notificationRepositoryProps = Props(new Actor {
    def receive = {
      case x => notificationRepositoryProbe.ref forward x
    }
  })
  val notificationExecutorProbe = TestProbe()
  object NotificationDispatcherMocked {
    def props(notificationRepositoryProps: Props): Props = Props(new NotificationDispatcherMocked(notificationRepositoryProps))

  }
  class NotificationDispatcherMocked(nrp: Props) extends NotificationDispatcher(nrp) {
    override def createNotificationExecutor():ActorRef = {
      notificationExecutorProbe.ref
    }
  }
  val notificationDispatcher = system.actorOf(NotificationDispatcherMocked.props(notificationRepositoryProps))

  "An Notification actor" must {
    "handle notificationRequest on empty notification repository" in {

      val condEntry: ConditionEntry = new ConditionEntry(12, 13, 14, 15)
      val days: Day = new Day(List(condEntry), "date")
      val forecast: Forecast = new Forecast(List(days))
      val notificationRequest = new NotificationRequest(forecast)
      notificationDispatcher ! notificationRequest
      notificationRepositoryProbe.expectMsg(GetNotificationPlans)
      notificationRepositoryProbe.reply(Seq())
    }
  }

  "An Notification actor" must {
    "handle notificationRequest on notifications in repository" in {

      val condEntry: ConditionEntry = new ConditionEntry(12, 13, 14, 15)
      val days: Day = new Day(List(condEntry), "date")
      val forecast: Forecast = new Forecast(List(days))
      val notificationRequest = new NotificationRequest(forecast)
      notificationDispatcher ! notificationRequest

      notificationRepositoryProbe.expectMsg(GetNotificationPlans)
      notificationRepositoryProbe.reply(Seq(new NotificationPlan("test@mail"), new NotificationPlan("test2@mail")))
      notificationExecutorProbe.expectMsg(new NotificationExecutorMessage(new NotificationPlan("test@mail"), forecast))
      notificationExecutorProbe.expectMsg(new NotificationExecutorMessage(new NotificationPlan("test2@mail"), forecast))
    }
  }

}
