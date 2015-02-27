package com.github.wkicior.helyeah.service

import akka.actor.{ActorSystem, _}
import akka.pattern.ask
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import akka.util.Timeout
import scala.concurrent.duration._

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await


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

  def prepareForecastNone: Forecast = {
    val ce1_1: ConditionEntry = new ConditionEntry(0, 50, 2, 3)
    val ce1_2: ConditionEntry = new ConditionEntry(1, 40, 3, 1)
    val conditionEntries1: Seq[ConditionEntry] = List(ce1_1, ce1_2)
    val day1: Day = new Day(conditionEntries1, "2015-01-01")
    val ce2_1: ConditionEntry = new ConditionEntry(0, 50, 2, 3)
    val ce2_2: ConditionEntry = new ConditionEntry(1, 30, 3, 4)
    val conditionEntries2: Seq[ConditionEntry] = List(ce2_1, ce2_2)
    val day2: Day = new Day(conditionEntries2, "2015-01-02")
    val days: Seq[Day] = List(day1, day2)
    val forecast: Forecast = new Forecast(days)
    forecast
  }

  def prepareForecastPoor: Forecast = {
    val ce1_1: ConditionEntry = new ConditionEntry(0, 50, 8, 5)
    val ce1_2: ConditionEntry = new ConditionEntry(1, 40, 7, 4)
    val conditionEntries1: Seq[ConditionEntry] = List(ce1_1, ce1_2)
    val day1: Day = new Day(conditionEntries1, "2015-01-01")
    val ce2_1: ConditionEntry = new ConditionEntry(0, 50, 12, 8)
    val ce2_2: ConditionEntry = new ConditionEntry(1, 30, 15, 10)
    val conditionEntries2: Seq[ConditionEntry] = List(ce2_1, ce2_2)
    val day2: Day = new Day(conditionEntries2, "2015-01-02")
    val days: Seq[Day] = List(day1, day2)
    val forecast: Forecast = new Forecast(days)
    forecast
  }

  "A ForecastJudge actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        forecastJudge ! "fail"
      }
    }

    "return None rating if no wind found in the forecast" in {
      val plan: NotificationPlan = new NotificationPlan("mail")
      val forecast: Forecast = prepareForecastNone
      val notificationPlanExecuteMessage = new NotificationPlanExecutorMessage(plan, forecast)
      implicit val timeout = Timeout(1000 milliseconds)
      val forecastRatingFuture = forecastJudge ? notificationPlanExecuteMessage
      val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
      forecastRating.rating should be(Rating.NONE)
    }

    "return POOR rating if no wind found in the forecast" in {
      val plan: NotificationPlan = new NotificationPlan("mail")
      val forecast: Forecast = prepareForecastPoor
      val notificationPlanExecuteMessage = new NotificationPlanExecutorMessage(plan, forecast)
      implicit val timeout = Timeout(1000 milliseconds)
      val forecastRatingFuture = forecastJudge ? notificationPlanExecuteMessage
      val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
      forecastRating.rating should be(Rating.POOR)
    }
  }

}
