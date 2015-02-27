package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.github.wkicior.helyeah.model.{Rating, ForecastRating}
import org.joda.time.DateTime

/**
 * Created by disorder on 24.02.15.
 */
object ForecastJudge {
  def props(): Props = Props[ForecastJudge]
}

class ForecastJudge extends Actor {
  val log = Logging(context.system, this)

  def judgeForecast(message: NotificationPlanExecutorMessage) {
    val rating = new ForecastRating(Rating.NONE, DateTime.now)
    sender() ! rating
  }

  def receive = {
    case msg: NotificationPlanExecutorMessage => judgeForecast(msg)
    case _ => log.error("Unknown message")
  }
}
