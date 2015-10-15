package com.github.wkicior.helyeah.service

import com.github.wkicior.helyeah.model.NotificationPlan
import akka.actor.Props
import akka.actor.Actor
import akka.event.Logging
import com.github.wkicior.helyeah.model.Notification


/**
 * Message to be handled by NotificationRepository - query for the previous notification sent to the user
 * @param notificationPlan
 */
case class QueryLastNotificationMessage(notificationPlan: NotificationPlan)

/**
 * Message to be handled by NotifiactionRepository - saves the notification
 */
case class SaveNotificationMessage(notification: Notification)

object NotificationRepository {
  def props(dao:NotificationsMongoDAO): Props = Props(new NotificationRepository(dao))
  def props(): Props = Props(new NotificationRepository(NotificationsMongoDAO))

}
/**
 * @author disorder
 */
class NotificationRepository(dao:NotificationsMongoDAO) extends Actor {
  val log = Logging(context.system, this)
  val collection = NotificationsMongoDAO.collection
  def receive = {
    case s:SaveNotificationMessage =>
      log.info("saving " + s.notification)
      dao.save(s.notification)
    case _ => log.error("Unknown message")
  }
}