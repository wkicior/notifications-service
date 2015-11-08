package com.github.wkicior.helyeah.service

import akka.actor.{ActorSystem, _}
import akka.pattern.ask
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import akka.util.Timeout
import org.joda.time.DateTime
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
    val ce1_1: ConditionEntry = ConditionEntry("00:00", 50, 2, 3)
    val ce1_2: ConditionEntry = ConditionEntry("01:00", 40, 3, 1)
    val conditionEntries1: Seq[ConditionEntry] = List(ce1_1, ce1_2)
    val day1: Day = Day(conditionEntries1, "2015-01-01")
    val ce2_1: ConditionEntry = ConditionEntry("00:00", 50, 2, 3)
    val ce2_2: ConditionEntry = ConditionEntry("01:00", 30, 3, 4)
    val conditionEntries2: Seq[ConditionEntry] = List(ce2_1, ce2_2)
    val day2: Day = Day(conditionEntries2, "2015-01-02")
    val days: Seq[Day] = List(day1, day2)
    val forecast: Forecast = Forecast(days)
    forecast
  }

  def prepareForecastPoor: Forecast = {
    val ce1_1: ConditionEntry = ConditionEntry("00:00", 50, 8, 5)
    val ce1_2: ConditionEntry = ConditionEntry("01:00", 40, 7, 4)
    val conditionEntries1: Seq[ConditionEntry] = List(ce1_1, ce1_2)
    val day1: Day =  Day(conditionEntries1, "2015-01-01")
    val ce2_1: ConditionEntry = ConditionEntry("00:00", 50, 12, 8)
    val ce2_2: ConditionEntry = ConditionEntry("01:00", 30, 15, 11)
    val conditionEntries2: Seq[ConditionEntry] = List(ce2_1, ce2_2)
    val day2: Day = Day(conditionEntries2, "2015-01-02")
    val days: Seq[Day] = List(day1, day2)
    val forecast: Forecast = Forecast(days)
    forecast
  }

  def prepareForecastPromising: Forecast = {
    val ce1_1: ConditionEntry = ConditionEntry("00:00", 50, 8, 5)
    val ce1_2: ConditionEntry = ConditionEntry("01:00", 40, 7, 4)
    val conditionEntries1: Seq[ConditionEntry] = List(ce1_1, ce1_2)
    val day1: Day =  Day(conditionEntries1, "2015-01-01")
    val ce2_1: ConditionEntry = ConditionEntry("00:00", 50, 21, 14)
    val ce2_2: ConditionEntry = ConditionEntry("01:00", 30, 15, 10)
    val conditionEntries2: Seq[ConditionEntry] = List(ce2_1, ce2_2)
    val day2: Day = Day(conditionEntries2, "2015-01-02")
    val days: Seq[Day] = List(day1, day2)
    val forecast: Forecast = Forecast(days)
    forecast
  }

  def prepareForecastHigh: Forecast = {
    val ce1_1: ConditionEntry = ConditionEntry("00:00", 50, 8, 5)
    val ce1_2: ConditionEntry = ConditionEntry("01:00", 40, 7, 40)
    val conditionEntries1: Seq[ConditionEntry] = List(ce1_1, ce1_2)
    val day1: Day =  Day(conditionEntries1, "2015-01-01")
    val ce2_1: ConditionEntry = ConditionEntry("00:00", 50, 21, 14)
    val ce2_2: ConditionEntry = ConditionEntry("01:00", 30, 15, 10)
    val conditionEntries2: Seq[ConditionEntry] = List(ce2_1, ce2_2)
    val day2: Day = Day(conditionEntries2, "2015-01-02")
    val days: Seq[Day] = List(day1, day2)
    val forecast: Forecast = Forecast(days)
    forecast
  }

  "A ForecastJudge actor" must {
    "reject other message than NotificationRequest" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        forecastJudge ! "fail"
      }
    }

    "return None rating if no wind found in the forecast" in {
      val plan: NotificationPlan = NotificationPlan("mail", "href")
      val forecast: Forecast = prepareForecastNone
      val notificationPlanExecuteMessage = NotificationPlanExecutorMessage(plan, forecast)
      implicit val timeout = Timeout(2000 milliseconds)
      val forecastRatingFuture = forecastJudge ? notificationPlanExecuteMessage
      val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
      forecastRating.rating should be(Rating.NONE)
    }

    "return POOR rating if some wind found in the forecast" in {
      val plan: NotificationPlan =  NotificationPlan("mail", "href")
      val forecast: Forecast = prepareForecastPoor
      val notificationPlanExecuteMessage = NotificationPlanExecutorMessage(plan, forecast)
      implicit val timeout = Timeout(2000 milliseconds)
      val forecastRatingFuture = forecastJudge ? notificationPlanExecuteMessage
      val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
      forecastRating.rating should be(Rating.POOR)
      forecastRating.startingFrom should be(new DateTime(2015, 1, 2, 0, 0))
    }

    "return PROMISING rating if nice wind found in the forecast" in {
      val plan: NotificationPlan = NotificationPlan("mail", "href")
      val forecast: Forecast = prepareForecastPromising
      val notificationPlanExecuteMessage = NotificationPlanExecutorMessage(plan, forecast)
      implicit val timeout = Timeout(1000 milliseconds)
      val forecastRatingFuture = forecastJudge ? notificationPlanExecuteMessage
      val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
      forecastRating.rating should be(Rating.PROMISING)
      forecastRating.startingFrom should be(new DateTime(2015, 1, 2, 0, 0))
    }

    "return HIGH rating if good wind found in the forecast" in {
      val plan: NotificationPlan = NotificationPlan("mail", "href")
      val forecast: Forecast = prepareForecastHigh
      val notificationPlanExecuteMessage = NotificationPlanExecutorMessage(plan, forecast)
      implicit val timeout = Timeout(1000 milliseconds)
      val forecastRatingFuture = forecastJudge ? notificationPlanExecuteMessage
      val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
      forecastRating.rating should be(Rating.HIGH)
      forecastRating.startingFrom should be(new DateTime(2015, 1, 1, 1, 0))
    }
  }

}
