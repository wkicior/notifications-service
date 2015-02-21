package com.github.wkicior.helyeah

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import ForecastJsonProtocol._

class ForecastNotificationServiceSpec extends Specification with Specs2RouteTest with ForecastNotificationService {
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

    "accept a Forecast JSON object post to the /notifications path" in {

      Post("/notifications", Forecast("nameValue", 12)) ~> sealRoute(myRoute) ~> check {
        status mustEqual OK
        responseAs[String] must contain("nameValue")
        responseAs[String] must contain("12")
      }
    }
  }
}
