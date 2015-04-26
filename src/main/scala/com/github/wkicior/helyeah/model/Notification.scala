package com.github.wkicior.helyeah.model

/**
 * The model notification plan for every user.
 * Currently contains only e-mail address of the user to be notified
 * Created by disorder on 22.02.15.
 */
case class NotificationPlan(email: String, href: String)

/**
 * The actual notification that is send to the user
 * @param plan - notification plan on basis this notification being send
 * @param message - the notification literal message
 * @param rating - the rating on which this notification being send
 * @param forecast - the forecast of weather conditions
 */
case class Notification(plan: NotificationPlan, message: String, rating: ForecastRating, forecast: Forecast)
