package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import com.github.wkicior.helyeah.model.{Rating, ForecastRating, Forecast, NotificationPlan}

import scala.concurrent.Await
import scala.concurrent.duration._

object NotificationPlanExecutor {
  def props(): Props = Props(new NotificationPlanExecutor(ForecastJudge.props, NotificationComposer.props))
  def props(fjep: Props, nsp:Props): Props = Props(new NotificationPlanExecutor(fjep, nsp))
}

/**
 * Message to be handled by NotificationPlanExecutor
 * @param notificationPlan
 * @param forecast
 */
case class NotificationPlanExecutorMessage(notificationPlan: NotificationPlan, forecast:Forecast)

/**
 * The executor actor for a single NotificationPlan against current forecast conditions
 * Created by disorder on 23.02.15.
 */
class NotificationPlanExecutor(forecastJudgeProps: Props, notificationComposerProps: Props) extends Actor {
  val log = Logging(context.system, this)
  val forecastJudge = context.actorOf(forecastJudgeProps)

  def executeNotificationPlan(message: NotificationPlanExecutorMessage) {
    log.info("executing message: ${message}")
    implicit val timeout = Timeout(1000 milliseconds)
    val forecastRatingFuture = forecastJudge ? message
    val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
    forecastRating.rating match {
      case Rating.NONE | Rating.POOR => log.info("No wind this time")
      case Rating.PROMISING | Rating.HIGH => sendNotificationToComposer(message, forecastRating)
      case default => log.error("Unrecognized rating ${default}")
    }
  }

  def sendNotificationToComposer(message: NotificationPlanExecutorMessage, forecastRating: ForecastRating): Unit = {
    log.debug("It's a surf time")
    val notificationComposer = context.actorOf(notificationComposerProps)
    notificationComposer ! new NotificationComposerMessage(message.notificationPlan, forecastRating)
  }

  def receive = {
    case npeMsg: NotificationPlanExecutorMessage => executeNotificationPlan(npeMsg)
    case _ => log.error("Unknown message")
  }
}
