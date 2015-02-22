package com.github.wkicior.helyeah.boundary

import akka.actor.{Props, Actor}
import com.github.wkicior.helyeah.model.{NotificationRequest, Day, Forecast, ConditionEntry}
import com.github.wkicior.helyeah.service.ForecastNotificationService
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
  import ForecastJsonProtocol._
  val forecastNotificationService = actorRefFactory.actorOf(Props[ForecastNotificationService])
  val myRoute =
    pathPrefix("notifications") {
      post {
        entity(as[NotificationRequest]) { notificationRequest: NotificationRequest =>
          forecastNotificationService ! notificationRequest
          complete(s"Forecast: ${notificationRequest}")
        }
      }
    }
}