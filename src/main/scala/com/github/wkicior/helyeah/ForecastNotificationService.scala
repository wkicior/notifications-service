package com.github.wkicior.helyeah

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import spray.httpx._
import MediaTypes._

class ForecastNotificationServiceActor extends Actor with ForecastNotificationService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}
object ForecastJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val forecastFormat = jsonFormat2(Forecast)
}

trait ForecastNotificationService extends HttpService {
  import ForecastJsonProtocol._
  val myRoute =
    pathPrefix("notifications") {
      post {
        entity(as[Forecast]) { forecast =>
          complete(s"Person: ${forecast.name} - favorite number: ${forecast.favoriteNumber}")
        }
      }
    }
}