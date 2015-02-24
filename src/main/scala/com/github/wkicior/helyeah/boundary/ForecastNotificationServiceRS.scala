package com.github.wkicior.helyeah.boundary

import akka.actor.Actor
import com.github.wkicior.helyeah.model.{ConditionEntry, Day, Forecast, NotificationRequest}
import com.github.wkicior.helyeah.service.NotificationPlanDispatcher
import spray.httpx._
import spray.json._
import spray.routing._

class ForecastNotificationServiceRSActor extends Actor with ForecastNotificationServiceRS {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}
object ForecastJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val conditionEntryFormat = jsonFormat4(ConditionEntry)
  implicit val dayFormat = jsonFormat2(Day)
  implicit val forecastFormat = jsonFormat1(Forecast)
  implicit val notificationRequestFormat = jsonFormat1(NotificationRequest)
}

trait ForecastNotificationServiceRS extends HttpService {
  import com.github.wkicior.helyeah.boundary.ForecastJsonProtocol._
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