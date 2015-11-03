package com.github.wkicior.helyeah.service

import org.specs2._
import com.mongodb.casbah.MongoClient
import com.github.wkicior.helyeah.model.ForecastRating
import com.github.wkicior.helyeah.model.Forecast
import org.joda.time.DateTime
import com.github.wkicior.helyeah.model.Rating
import com.github.wkicior.helyeah.model.Notification
import com.github.wkicior.helyeah.model.NotificationPlan
import org.specs2.mutable.Before



class NotificationsMongoDAOSpecs extends Specification with Before {
  object NotificationsMongoDAOTest extends NotificationsMongoDAO {
     val mongoClient =  MongoClient("notifications-mongo", 27017)
     val db = mongoClient("notifications-test-db")
     val collection = db("notifications")
  }
  
  val planx: NotificationPlan = NotificationPlan("test@localhost", "href")
  val rating: ForecastRating = ForecastRating(Rating.HIGH, DateTime.now)
  val forecast: Forecast = Forecast(Seq())      
  val notification = Notification(planx, "It's windy ;)", rating, forecast)
  
  def is = sequential ^ s2"""

 This is a specification to check the NotificationsRepository

 The NotificationsRepository should
   return an empty collection                                    $e1
   save new notification                                         $create
                                                                 """
  val notificationsMongoDAO = NotificationsMongoDAOTest.collection
  
  
  def e1 = NotificationsMongoDAOTest.count() mustEqual 0
  
  def create = {
    NotificationsMongoDAOTest.save(notification) 
    notificationsMongoDAO.find().count() mustEqual 1
  }
  def before = NotificationsMongoDAOTest.db.dropDatabase()
}
  