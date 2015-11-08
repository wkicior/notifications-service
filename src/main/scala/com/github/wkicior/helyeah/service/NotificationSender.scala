package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.github.wkicior.helyeah.model.{Forecast, ForecastRating, Notification, NotificationPlan}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class NotificationComposerMessage(plan: NotificationPlan, forecastRating: ForecastRating, forecast: Forecast)

/**
 * Creates the notification and sends it to the customer
 * Created by disorder on 25.02.15.
 */
object NotificationSender {
  def props(): Props = Props(new NotificationSender(NotificationRepository.props))
  val MAIL_SERVICE_URL = "http://mail-gateway/notifications/send"
}

case class NotificationSenderException(cause:Throwable) extends Exception
  
class NotificationSender(notificationRepositoryProps: Props) extends Actor {
  val log = Logging(context.system, this)
  val notificationRepository = context.actorOf(notificationRepositoryProps)

  def composeMessage(message: NotificationComposerMessage) = {
    val notification = Notification(message.plan, "It's windy ;)", message.forecastRating, message.forecast)
    import com.github.wkicior.helyeah.application.JsonProtocol._
    import spray.json._
    log.info(s"Sending notification: ${notification.toJson}")
    try {
      this.sendMessage(notification)
      notificationRepository ! SaveNotificationMessage(notification)
    } catch {
      case e:NotificationSenderException => log.error(e.cause, "Message not sent")
    }
  }

  def sendMessage(notification: Notification): Unit = {
    val system = context.system
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    import com.github.wkicior.helyeah.application.JsonProtocol._
    val response: Future[HttpResponse] = pipeline(Post(NotificationSender.MAIL_SERVICE_URL, notification))
    response.onComplete {
      case Success(msg) =>
        log.info(s"Notification successfully sent ${msg}")        
      case Failure(error) =>
        log.error(error, "Couldn't post message")
        throw new NotificationSenderException(error)
    }
  }

  def receive = {
    case msg:NotificationComposerMessage => composeMessage(msg)
    case _ => log.error("Unknown message")
  }
}
