package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import com.github.wkicior.helyeah.model.NotificationRequest

object ForecastNotificationService {
  def props(): Props = Props(new ForecastNotificationService(Props[NotificationRepository]))
  def props(notificationRepositoryProps: Props): Props = Props(new ForecastNotificationService(notificationRepositoryProps))
}

/**
 * Created by disorder on 21.02.15.
 */
class ForecastNotificationService(notificationRepositoryProps: Props) extends Actor{
  val notificationRepository = context.actorOf(notificationRepositoryProps, "notification-repository")
  val log = Logging(context.system, this)

  def processNotification(notificationRequest: NotificationRequest): Unit = {
    log.info(s"processing notification: ${notificationRequest.toString}")
    implicit val timeout = Timeout(5 seconds)
    val plans = notificationRepository ? GetNotificationPlans
  }

  def receive = {
    case notificationRequest : NotificationRequest => processNotification(notificationRequest)
    case _ => log.error("Unknown message2")
  }

}
