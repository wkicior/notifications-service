package com.github.wkicior.helyeah.service

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.MongoCollection

abstract class NotificationsRepository {
  val collection:MongoCollection
  val mongoClient:MongoClient
  
   def count():Int = {
     collection.count()
   }
  
   def close() {
     mongoClient.close()
   }
  
}
object NotificationsRepository extends NotificationsRepository {
  val mongoClient = MongoClient("notifications-mongo", 27017)
  val db = mongoClient("notifications-db")
  val collection = db("notifications")    
}