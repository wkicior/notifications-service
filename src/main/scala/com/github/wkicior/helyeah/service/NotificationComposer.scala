package com.github.wkicior.helyeah.service

import akka.actor.{Props, Actor}
import akka.event.Logging
import com.github.wkicior.helyeah.model.{ForecastRating, NotificationPlan}

case class NotificationComposerMessage(plan: NotificationPlan, forecastRating: ForecastRating)

/**
 * Created by disorder on 25.02.15.
 */
object NotificationComposer {
  def props(): Props = Props(new NotificationComposer())
}

class NotificationComposer extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case _ => log.error("Unknown message")
  }
}
