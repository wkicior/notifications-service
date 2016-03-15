package com.github.wkicior.helyeah.service

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.testkit.TestProbe
import akka.actor.{Actor, Props, ActorSystem}
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author disorder
 */
class NotificationPlanServiceProxySpec (_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("NotificationPlanServiceProxySpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))

  override def afterAll {
   TestKit.shutdownActorSystem(system)
  }
  object NotificationPlanServiceProxyMocked {
    def props(): Props = Props(new NotificationPlanServiceProxyMocked())

  }
  class NotificationPlanServiceProxyMocked() extends NotificationPlanServiceProxy() {
    override def callGetNotificationPlansService():Future[Iterable[NotificationPlan]] = {
      return Future {
        notificationPlans
      }
    }
  }
  
  val notificationPlans = Seq(NotificationPlan("diso.junk@gmail.com", "http://notification-plans/plan/1"), 
          NotificationPlan("hcsk8er@wp.pl", "http://notification-plans/plan/2"))
  
  val notificationPlansServiceProxy = system.actorOf(NotificationPlanServiceProxyMocked.props)

  "An NotificationPlanServiceProxy actor" must {
    "reject other message than GetNotificationPlans" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationPlansServiceProxy ! "fail"
      }
    }
    "query a notification-plans service and return notification plans" in {
      implicit val timeout = Timeout(10000 milliseconds)
      val notificationPlansFutureFuture = notificationPlansServiceProxy ? GetNotificationPlans 
      val notificationPlansFuture = Await.result(notificationPlansFutureFuture, timeout.duration).asInstanceOf[Future[Iterable[NotificationPlan]]]
      val returnedNotificationPlans:Iterable[NotificationPlan] = Await.result(notificationPlansFuture, timeout.duration).asInstanceOf[Iterable[NotificationPlan]]
      returnedNotificationPlans should be(notificationPlans) 
    }
  }
}
