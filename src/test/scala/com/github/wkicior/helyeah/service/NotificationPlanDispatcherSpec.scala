package com.github.wkicior.helyeah.service

import akka.actor._
import akka.testkit.{EventFilter, ImplicitSender, TestKit, TestProbe}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.Future

/**
 * Created by disorder on 22.02.15.
 */
class NotificationPlanDispatcherSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("NotificationPlanDispatcherSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val notificationPlanRepositoryProbe = TestProbe()
  val notificationRepositoryProps = Props(new Actor {
    def receive = {
      case x => notificationPlanRepositoryProbe.ref forward x
    }
  })
  val notificationExecutorProbe = TestProbe()
  object NotificationPlanDispatcherMocked {
    def props(notificationRepositoryProps: Props): Props = Props(new NotificationPlanDispatcherMocked(notificationRepositoryProps))

  }
  class NotificationPlanDispatcherMocked(nrp: Props) extends NotificationPlanDispatcher(nrp) {
    override def createNotificationExecutor():ActorRef = {
      notificationExecutorProbe.ref
    }
  }
  val notificationPlanDispatcher = system.actorOf(NotificationPlanDispatcherMocked.props(notificationRepositoryProps))

  "An NotificationPlanDispatcher actor" must {
    "handle notificationRequest on empty notification repository" in {

      val condEntry: ConditionEntry = ConditionEntry("12:00", 13, 14, 15)
      val days: Day = Day(List(condEntry), "date")
      val forecast: Forecast = Forecast(List(days))
      val notificationRequest = NotificationRequest(forecast)
      notificationPlanDispatcher ! notificationRequest
      notificationPlanRepositoryProbe.expectMsg(GetNotificationPlans)
      notificationPlanRepositoryProbe.reply(Seq())
    }
  }

  "A NotificationPlanDispatcher actor" must {
    "handle notificationRequest on notifications in repository and dispatch them to NotificationExecutors" in {
      val condEntry: ConditionEntry = ConditionEntry("12:00", 13, 14, 15)
      val days: Day = Day(List(condEntry), "date")
      val forecast: Forecast = Forecast(List(days))
      val notificationRequest = NotificationRequest(forecast)
      notificationPlanDispatcher ! notificationRequest

      notificationPlanRepositoryProbe.expectMsg(GetNotificationPlans)
      notificationPlanRepositoryProbe.reply(Future{Seq(NotificationPlan("test@mail", "href"), NotificationPlan("test2@mail", "href"))})
      notificationExecutorProbe.expectMsg(NotificationPlanExecutorMessage(NotificationPlan("test@mail", "href"), forecast))
      notificationExecutorProbe.expectMsg(NotificationPlanExecutorMessage(NotificationPlan("test2@mail", "href"), forecast))
    }
  }

  "A NotificationPlanDispatcher actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationPlanDispatcher ! "fail"
      }
    }
  }

}
