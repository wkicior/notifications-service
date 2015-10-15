package com.github.wkicior.helyeah.service

import org.specs2._
import com.mongodb.casbah.MongoClient
import com.github.wkicior.helyeah.model.ForecastRating
import com.github.wkicior.helyeah.model.Forecast
import org.joda.time.DateTime
import com.github.wkicior.helyeah.model.Rating
import com.github.wkicior.helyeah.model.Notification
import com.github.wkicior.helyeah.model.NotificationPlan


class NotificationsMongoDAOSpecs extends Specification {
  object NotificationsMongoDAOTest extends NotificationsMongoDAO {
     val mongoClient =  MongoClient("notifications-mongo", 27017)
     val db = mongoClient("notifications-test-db")
     val collection = db("notifications")
  }
  
  val planx: NotificationPlan = NotificationPlan("test@localhost", "href")
  val rating: ForecastRating = ForecastRating(Rating.HIGH, DateTime.now)
  val forecast: Forecast = Forecast(Seq())      
  val notification = Notification(planx, "It's windy ;)", rating, forecast)
  
  def is = s2"""

 This is a specification to check the NotificationsRepository

 The NotificationsRepository should
   return an empty collection                                    $e1
   save new notification                                         $e2
   end with 'world'                                              $e3
                                                                 """
  val notificationsMongoDAO = NotificationsMongoDAOTest.collection
  
  //def e1 = notificationsRepository.count() mustEqual 0
  def e1 = NotificationsMongoDAOTest.count() mustEqual 0
  //def e1 = "Hello world" must have size(12)  
  NotificationsMongoDAOTest.save(notification)
  def e2 = NotificationsMongoDAOTest.count() mustEqual 1
  def e3 = "Hello world" must endWith("world")
}
  