package com.github.wkicior.helyeah.application

import com.github.wkicior.helyeah.model.Rating
import com.github.wkicior.helyeah.model._
import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport
import spray.json._

/**
 * Protocols for model JSON support
 * Created by disorder on 06.03.15.
 */
object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val conditionEntryFormat = jsonFormat4(ConditionEntry)
  implicit val dayFormat = jsonFormat2(Day)
  implicit val forecastFormat = jsonFormat1(Forecast)
  implicit val notificationRequestFormat = jsonFormat1(NotificationRequest)

  implicit object RatingJsonFormat extends RootJsonFormat[Rating.Value] {
    def write(r: Rating.Value) = JsString(r.toString)
    def read(value: JsValue) = {
      value.asJsObject.toString() match {
        case str:String => Rating.withName(str)
        case _ => throw new DeserializationException("Rating expected")
      }
    }
  }

  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {
    def write(d: DateTime) = JsString(d.toString)
    def read(value: JsValue) = {
      value.asJsObject.toString() match {
        case str:String => new DateTime(str)
        case _ => throw new DeserializationException("DateTime expected")
      }
    }
  }
  implicit val forecastRatingFormat = jsonFormat2(ForecastRating)
  implicit val notificationPlanFormat = jsonFormat2(NotificationPlan)
  implicit val notificationFormat = jsonFormat4(Notification)
}

