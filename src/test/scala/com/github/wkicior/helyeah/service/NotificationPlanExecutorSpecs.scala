package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props, ActorSystem}
import akka.testkit.{TestProbe, EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
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

  val notificationComposerProbe = TestProbe()
  val notificationComposerProps = Props(new Actor {
    def receive = {
      case x => notificationComposerProbe.ref forward x
    }
  })

  val notificationPlanExecutor = system.actorOf(NotificationPlanExecutor.props(forecastJudgeProps, notificationComposerProps))

  "A NotificationPlanExecutor actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationPlanExecutor ! "fail"
      }
    }
    """ask ForecastJudge for the forecast against the NotificationPlan on NotificationExecutor message.
      |Giving up on poor conditions""".stripMargin in {
      val plan: NotificationPlan = new NotificationPlan("mail", "href")
      val forecast: Forecast = prepareForecast
      val notificationPlanExecuteMessage = new NotificationPlanExecutorMessage(plan, forecast)
      notificationPlanExecutor ! notificationPlanExecuteMessage
      forecastJudgeProbe.expectMsg(notificationPlanExecuteMessage)
      forecastJudgeProbe.reply(ForecastRating(Rating.POOR, DateTime.now))
    }

    """ask ForecastJudge for the forecast against the NotificationPlan on NotificationExecutor message.
      |Sends notification to NotificationSender on good conditions""".stripMargin in {
      val plan: NotificationPlan = new NotificationPlan("mail", "href")
      val forecast: Forecast = prepareForecast
      val notificationPlanExecuteMessage = new NotificationPlanExecutorMessage(plan, forecast)
      notificationPlanExecutor ! notificationPlanExecuteMessage
      forecastJudgeProbe.expectMsg(notificationPlanExecuteMessage)
      val forecastRating = new ForecastRating(Rating.PROMISING, DateTime.now)
      forecastJudgeProbe.reply(forecastRating)
      notificationComposerProbe.expectMsg(new NotificationComposerMessage(plan, forecastRating, forecast))
    }
  }

  private def prepareForecast: Forecast = {
    val ce: ConditionEntry = new ConditionEntry("12:00", 13, 14, 15)
    val conditionEntries: Seq[ConditionEntry] = List(ce)
    val day: Day = new Day(conditionEntries, "2015-01-01")
    val days: Seq[Day] = List(day)
    val forecast: Forecast = new Forecast(days)
    forecast
  }
}
