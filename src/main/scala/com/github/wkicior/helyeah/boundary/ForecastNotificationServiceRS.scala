package com.github.wkicior.helyeah.boundary

import akka.actor.Actor
import com.github.wkicior.helyeah.model.NotificationRequest
import com.github.wkicior.helyeah.service.NotificationPlanDispatcher
import spray.routing._

class ForecastNotificationServiceRSActor extends Actor with ForecastNotificationServiceRS {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}


trait ForecastNotificationServiceRS extends HttpService {
  import com.github.wkicior.helyeah.application.JsonProtocol._
  val forecastNotificationService = actorRefFactory.actorOf(NotificationPlanDispatcher.props)
  val myRoute =
    pathPrefix("notifications") {
      post {
        entity(as[NotificationRequest]) { notificationRequest: NotificationRequest =>
          forecastNotificationService ! notificationRequest
          complete(s"Forecast: $notificationRequest")
        }
      }
    }
}