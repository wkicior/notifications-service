package com.github.wkicior.helyeah.service

import akka.actor.{Props, Actor}
import akka.event.Logging
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import com.github.wkicior.helyeah.model.{NotificationPlan, Forecast}

object NotificationPlanExecutor {
  def props(): Props = Props(new NotificationPlanExecutor(ForecastJudge.props))
  def props(fjep: Props): Props = Props(new NotificationPlanExecutor(fjep))
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
class NotificationPlanExecutor(forecastJudgeProps: Props) extends Actor {
  val log = Logging(context.system, this)
  val forecastJudge = context.actorOf(forecastJudgeProps)

  def executeNotificationPlan(message: NotificationPlanExecutorMessage) {
    log.info("executing message: ${message}")
    implicit val timeout = Timeout(100 milliseconds)
    forecastJudge ? message
  }

  def receive = {
    case npeMsg: NotificationPlanExecutorMessage => executeNotificationPlan(npeMsg)
    case _ => log.error("Unknown message")
  }

}
