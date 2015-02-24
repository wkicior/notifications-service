package com.github.wkicior.helyeah.service

import akka.actor.{Props, Actor}
import akka.event.Logging
import com.github.wkicior.helyeah.model.{NotificationPlan, Forecast}

object NotificationPlanExecutor {
  def props(): Props = Props(new NotificationPlanExecutor())

}

case class NotificationPlanExecutorMessage(notificationPlan: NotificationPlan, forecast:Forecast)

/**
 * Created by disorder on 23.02.15.
 */
class NotificationPlanExecutor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case _ => log.error("Unknown message") //TODO: accept NotificationPlan and Forecast
  }

}
