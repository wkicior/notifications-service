package com.github.wkicior.helyeah.service

import akka.actor.Actor
import akka.event.Logging
import com.github.wkicior.helyeah.model.{NotificationRequest, Forecast}

/**
 * Created by disorder on 21.02.15.
 */
class ForecastNotificationService extends Actor{
  val log = Logging(context.system, this)
  def receive = {
    case notificationRequest : NotificationRequest => log.info(notificationRequest.toString)
    case _ => log.error("Unknown message2")
  }

}
