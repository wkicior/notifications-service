package com.github.wkicior.helyeah.service

import akka.actor.{ActorRef, Actor, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import com.github.wkicior.helyeah.model.{NotificationPlan, NotificationRequest}

import scala.concurrent.Await
import scala.concurrent.duration._

object NotificationPlanDispatcher {
  def props(): Props = Props(new NotificationPlanDispatcher(NotificationPlanRepository.props))
  def props(nrp: Props): Props = Props(new NotificationPlanDispatcher(nrp))
}

/**
 * Main service for handling the notification request.
 * It reads all the notification plans and dispatches them to the NotificationPlanExecutor children
 * Created by disorder on 21.02.15.
 */
class NotificationPlanDispatcher(notificationRepositoryProps: Props) extends Actor{
  val NotificationPlanRepository = context.actorOf(notificationRepositoryProps, "notification-repository")
  val log = Logging(context.system, this)

  def createNotificationExecutor(): ActorRef = {
    context.actorOf(NotificationPlanExecutor.props)
  }

  def processNotification(notificationRequest: NotificationRequest): Unit = {
    log.info(s"processing notification: ${notificationRequest.toString}")
    implicit val timeout = Timeout(5 seconds)
    val plansFuture = NotificationPlanRepository ? GetNotificationPlans
    val plans = Await.result(plansFuture, timeout.duration).asInstanceOf[Seq[NotificationPlan]]
    plans.foreach(plan => createNotificationExecutor ! NotificationPlanExecutorMessage(plan, notificationRequest.forecast))
  }

  def receive = {
    case notificationRequest : NotificationRequest => processNotification(notificationRequest)
    case _ => log.error("Unknown message")
  }

}
