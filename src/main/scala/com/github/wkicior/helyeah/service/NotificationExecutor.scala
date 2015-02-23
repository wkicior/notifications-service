package com.github.wkicior.helyeah.service

import akka.actor.{Props, Actor}
import akka.event.Logging

object NotificationExecutor {
  def props(): Props = Props(new NotificationExecutor())

}

/**
 * Created by disorder on 23.02.15.
 */
class NotificationExecutor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case _ => log.error("Unknown message") //TODO: accept NotificationPlan and Forecast
  }

}
