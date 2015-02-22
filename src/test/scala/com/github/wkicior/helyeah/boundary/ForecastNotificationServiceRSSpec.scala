package com.github.wkicior.helyeah.boundary

import com.github.wkicior.helyeah.model.{Forecast, Day, ConditionEntry, NotificationRequest}
import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.http._
import spray.json._
import spray.testkit.Specs2RouteTest


class ForecastNotificationServiceRSSpec extends Specification with Specs2RouteTest with ForecastNotificationServiceRS {
  def actorRefFactory = system
  
  "MyService" should {

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for GET requests to the /notifications path" in {
      Get("/notifications") ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: POST"
      }
    }

    "accept a Forecast JSON object post to the /notifications spath" in {
      import ForecastJsonProtocol._
      val conditionEntriesD1 = List(ConditionEntry(12, 13, 14, 15))
      val days = List(Day(conditionEntriesD1, "2010-12-12"), Day(conditionEntriesD1, "2010-12-12"))
      val forecast = Forecast(days)
      val notificationRequest = NotificationRequest(forecast)
      println(notificationRequest.toJson)
      Post("/notifications", notificationRequest) ~> sealRoute(myRoute) ~> check {
        status mustEqual OK
        responseAs[String] must contain("2010-12-12")
      }
    }
  }
}
