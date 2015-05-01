package com.github.wkicior.helyeah.service

import com.github.wkicior.helyeah.model.NotificationPlan


/**
 * Message to be handled by NotificationRepository - query for the previous notification sent to the user
 * @param notificationPlan
 */
case class QueryLastNotificationMessage(notificationPlan: NotificationPlan)

/**
 * @author disorder
 */
class NotificationRepository {
  
}