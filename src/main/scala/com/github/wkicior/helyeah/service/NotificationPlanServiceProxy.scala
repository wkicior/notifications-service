package com.github.wkicior.helyeah.service

import akka.actor.{Props, Actor}
import akka.event.Logging
import com.github.wkicior.helyeah.model.NotificationPlan
import spray.client.pipelining._
import spray.http._
import akka.util.Timeout
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.Await
import scala.concurrent.duration._
import com.github.wkicior.helyeah.application.JsonProtocol

object GetNotificationPlans

object NotificationPlanServiceProxy {  
  def props(): Props = Props[NotificationPlanServiceProxy]
  val NOTIFICATION_PLANS_SERVICE_URL = "http://notification-plans/notification-plans/"
}

/**
 * NotificationPlanRepository for fetching the NotificationPlans
 * Created by disorder on 22.02.15.
 */
class NotificationPlanServiceProxy extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case GetNotificationPlans =>
      log.info("GentNotificationPlans")
      val plansFuture = callGetNotificationPlansService()
      sender ! plansFuture
    case _ => log.error("Unknown message")
  }
  
  def callGetNotificationPlansService():Future[Iterable[NotificationPlan]] = {
    val system = context.system
    import system.dispatcher
    import com.github.wkicior.helyeah.application.JsonProtocol._
    val pipeline: HttpRequest => Future[Iterable[NotificationPlan]] = ( 
      sendReceive
      ~> unmarshal[Iterable[NotificationPlan]]
    )
    val response: Future[Iterable[NotificationPlan]] = pipeline(Get(NotificationPlanServiceProxy.NOTIFICATION_PLANS_SERVICE_URL))
    return response
  }
}
