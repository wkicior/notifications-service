package com.github.wkicior.helyeah.service

import akka.actor.Actor
import akka.event.Logging
import com.github.wkicior.helyeah.model.NotificationPlan

object GetNotificationPlans

/**
 * NotificationPlanRepository for fetching the NotificationPlans
 * Created by disorder on 22.02.15.
 */
class NotificationPlanRepository extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case GetNotificationPlans =>
      log.info("GentNotificationPlans")
      val plans = Seq(new NotificationPlan("diso.junk@gmail.com"), NotificationPlan("hcsk8er@wp.pl"))
      sender ! plans
    case _ => log.error("Not implemented yet")
  }
}
