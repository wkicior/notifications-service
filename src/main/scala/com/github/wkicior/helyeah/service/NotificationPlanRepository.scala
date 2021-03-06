package com.github.wkicior.helyeah.service

import akka.actor.{Props, Actor}
import akka.event.Logging
import com.github.wkicior.helyeah.model.NotificationPlan

object GetNotificationPlans

object NotificationPlanRepository {
  def props(): Props = Props[NotificationPlanRepository]
}

/**
 * NotificationPlanRepository for fetching the NotificationPlans
 * Created by disorder on 22.02.15.
 */
class NotificationPlanRepository extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case GetNotificationPlans =>
      log.info("GentNotificationPlans")
      val plans = Seq(NotificationPlan("diso.junk@gmail.com", "http://notification-plans/plan/1"), NotificationPlan("hcsk8er@wp.pl", "http://notification-plans/plan/2"))
      sender ! plans
    case _ => log.error("Not implemented yet")
  }
}
