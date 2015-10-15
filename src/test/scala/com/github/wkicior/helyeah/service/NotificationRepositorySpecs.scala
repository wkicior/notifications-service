package com.github.wkicior.helyeah.service


import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.github.wkicior.helyeah.model._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import com.mongodb.casbah.MongoClient

/**
 * @author disorder
 */
class NotificationRepositorySpecs (_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("NotificationSenderSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  
  object NotificationsMongoDAOTest extends NotificationsMongoDAO {
     val mongoClient =  MongoClient("notifications-mongo", 27017)
     val db = mongoClient("notifications-test-db")
     val collection = db("notifications")
  }

  val notificationRepository = system.actorOf( NotificationRepository.props(NotificationsMongoDAOTest))

  "An NotificationRepository actor" must {
    "reject other message than QueryLastNotificationMessage" in {
      EventFilter.error("Unknown message", occurrences = 1) intercept {
        notificationRepository ! "fail"
      }
    }
    "accept and save SaveNotificationMessage" in {
      val plan: NotificationPlan = NotificationPlan("test@localhost", "href")
      val rating: ForecastRating = ForecastRating(Rating.HIGH, DateTime.now)
      val forecast: Forecast = Forecast(Seq())      
      val notification = Notification(plan, "It's windy ;)", rating, forecast)           
      notificationRepository ! SaveNotificationMessage(notification)
    }
  }
}
