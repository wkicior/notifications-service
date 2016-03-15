package com.github.wkicior.helyeah.service

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.MongoCollection
import com.github.wkicior.helyeah.model.Notification
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import com.github.wkicior.helyeah.model.NotificationPlan
import com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

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
   
   def findLastByPlan(plan:NotificationPlan):Option[Notification] =  {
     val q = MongoDBObject("plan.id" -> plan.id)
     val order = MongoDBObject("rating.startingFrom" -> -1)
     val notificationDbObj:Option[DBObject] = collection.findOne(q, orderBy = order)     
     return notificationDbObj.map(grater[Notification].asObject(_))
   }
  
}
object NotificationsMongoDAO extends NotificationsMongoDAO {
  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()
  val mongoClient = MongoClient("notifications-mongo", 27017)
  val db = mongoClient("notifications-db")
  val collection = db("notifications")  
}