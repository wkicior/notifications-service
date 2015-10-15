package com.github.wkicior.helyeah.service

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.MongoCollection
import com.github.wkicior.helyeah.model.Notification
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import com.github.wkicior.helyeah.model.NotificationPlan

abstract class NotificationsMongoDAO {
  val collection:MongoCollection
  val mongoClient:MongoClient
  
   def count():Int = {
     collection.count()
   }
  
   def close() {
     mongoClient.close()
   }
   
   def save(notification:Notification) {
     val notificationObj = grater[Notification].asDBObject(notification)
     collection += notificationObj
   }
   
   def find(plan:NotificationPlan) {
     //collection.find
   }
  
}
object NotificationsMongoDAO extends NotificationsMongoDAO {
  val mongoClient = MongoClient("notifications-mongo", 27017)
  val db = mongoClient("notifications-db")
  val collection = db("notifications")  
}