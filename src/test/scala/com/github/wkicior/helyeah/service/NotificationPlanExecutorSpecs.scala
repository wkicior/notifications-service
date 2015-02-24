package com.github.wkicior.helyeah.service

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
 * Created by disorder on 24.02.15.
 */
class NotificationPlanExecutorSpecs (_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {
  def this() = this(ActorSystem("NotificationPlanExecutorSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val notificationPlanExecutor = system.actorOf(NotificationPlanExecutor.props())

  "A NotificationPlanExecutor actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationPlanExecutor ! "fail"
      }
    }
  }

}
