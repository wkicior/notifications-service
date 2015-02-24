package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props, ActorSystem}
import akka.testkit.{TestProbe, EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model.{ConditionEntry, Day, Forecast, NotificationPlan}
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

  val forecastJudgeProbe = TestProbe()
  val forecastJudgeProps = Props(new Actor {
    def receive = {
      case x => forecastJudgeProbe.ref forward x
    }
  })

  val notificationPlanExecutor = system.actorOf(NotificationPlanExecutor.props(forecastJudgeProps))

  "A NotificationPlanExecutor actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationPlanExecutor ! "fail"
      }
    }
    "ask ForecastJudge for the forecast against the NotificationPlan on NotificationExecutor message" in {
      val plan: NotificationPlan = new NotificationPlan("mail")
      val forecast: Forecast = prepareForecast
      val notificationPlanExecuteMessage = new NotificationPlanExecutorMessage(plan, forecast)
      notificationPlanExecutor ! notificationPlanExecuteMessage
      forecastJudgeProbe.expectMsg(notificationPlanExecuteMessage)
    }
  }

  private def prepareForecast: Forecast = {
    val ce: ConditionEntry = new ConditionEntry(12, 13, 14, 15)
    val conditionEntries: Seq[ConditionEntry] = List(ce)
    val day: Day = new Day(conditionEntries, "2015-01-01")
    val days: Seq[Day] = List(day)
    val forecast: Forecast = new Forecast(days)
    forecast
  }
}
