package com.github.wkicior.helyeah.service

import org.specs2._
import com.mongodb.casbah.MongoClient

class NotificationsRepositorySpecs extends Specification {
  object NotificationsRepositoryTest extends NotificationsRepository {
     val mongoClient =  MongoClient("notifications-mongo", 27017)
     val db = mongoClient("notifications-test-db")
     val collection = db("notifications")
  }
  
  def is = s2"""

 This is a specification to check the NotificationsRepository

 The NotificationsRepository should
   return a collection                                           $e1
   start with 'Hello'                                            $e2
   end with 'world'                                              $e3
                                                                 """
  val notificationsRepository = NotificationsRepositoryTest.collection
  
  //def e1 = notificationsRepository.count() mustEqual 0
  def e1 = NotificationsRepositoryTest.count() mustEqual 0
  //def e1 = "Hello world" must have size(12)
  def e2 = "Hello world" must startWith("Hello")
  def e3 = "Hello world" must endWith("world")
}
  