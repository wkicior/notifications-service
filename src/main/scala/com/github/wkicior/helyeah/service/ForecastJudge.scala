package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging

/**
 * Created by disorder on 24.02.15.
 */
object ForecastJudge {
  def props(): Props = Props[ForecastJudge]
}

class ForecastJudge extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case _ => log.error("Unknown message") //TODO: accept NotificationPlan and Forecast
  }
}
