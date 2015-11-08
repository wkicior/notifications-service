package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import com.github.wkicior.helyeah.model.{Rating, ForecastRating, Forecast, NotificationPlan}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import com.github.wkicior.helyeah.model.Notification

object NotificationPlanExecutor {
  def props(): Props = Props(new NotificationPlanExecutor(ForecastJudge.props, NotificationSender.props, NotificationRepository.props))
  def props(fjep: Props, nsp:Props, nrp:Props): Props = Props(new NotificationPlanExecutor(fjep, nsp, nrp))
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
class NotificationPlanExecutor(forecastJudgeProps: Props, 
    notificationComposerProps: Props,
    notificationRepositoryProps: Props) extends Actor {
  val log = Logging(context.system, this)
  val forecastJudge = context.actorOf(forecastJudgeProps)
  val notificationRepository = context.actorOf(notificationRepositoryProps)

  def executeNotificationPlan(message: NotificationPlanExecutorMessage) {
    log.info(s"executing message: ${message}")
    implicit val timeout = Timeout(3000 milliseconds)
    val forecastRatingFuture = forecastJudge ? message
 		val previousNotificationFuture = notificationRepository ? QueryLastNotificationMessage(message.notificationPlan)
    val forecastRating = Await.result(forecastRatingFuture, timeout.duration).asInstanceOf[ForecastRating]
    forecastRating.rating match {
      case Rating.NONE | Rating.POOR => log.info("No wind this time")
      case Rating.PROMISING | Rating.HIGH =>
        if (noSuchNotificationSent(previousNotificationFuture, forecastRating, message)) { 
          sendNotificationToComposer(message, forecastRating)
        } else {
          log.info("notification already sent, skipping")
        }
      case default => log.error(s"Unrecognized rating ${default}")
    }
  }
  
  /**
   * Checks whether such notification candidate was already sent
   */
  def noSuchNotificationSent(previousNotificationFuture:Future[Any], forecastRating:ForecastRating, message:NotificationPlanExecutorMessage): Boolean = {
    implicit val timeout = Timeout(3000 milliseconds)
    val notificationOpt = Await.result(previousNotificationFuture, timeout.duration).asInstanceOf[Option[Notification]]
    return notificationOpt match {
      case None => true
      case Some(x) => !x.rating.startingFrom.equals(forecastRating.startingFrom) 
    }
 
  }

  def sendNotificationToComposer(message:NotificationPlanExecutorMessage, forecastRating:ForecastRating) = {
    log.debug("It's a surf time")
    val notificationComposer = context.actorOf(notificationComposerProps)
    notificationComposer ! NotificationComposerMessage(message.notificationPlan, forecastRating, message.forecast)
  }

  def receive = {
    case npeMsg: NotificationPlanExecutorMessage => executeNotificationPlan(npeMsg)
    case _ => log.error("Unknown message")
  }
}
