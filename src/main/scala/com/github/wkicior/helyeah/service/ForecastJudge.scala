package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.github.wkicior.helyeah.model.{Day, Rating, ForecastRating}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
 * Forecast Judge
 * Created by disorder on 24.02.15.
 */
object ForecastJudge {
  def props(): Props = Props[ForecastJudge]
}

class ForecastJudge extends Actor {
  val log = Logging(context.system, this)

  def getDailyRating(day: Day): ForecastRating = {
    val start = ForecastRating(Rating.NONE, getDateTime(day.date, "00:00"))
    val rating = day.conditionEntries.foldLeft(start) {
      (rating, entry) => {
        val dateTime = getDateTime(day.date, entry.hour)
        val entryRating:ForecastRating = entry.windSpeedKnots match {
          case knots if 0 until 6 contains knots => ForecastRating(Rating.NONE, dateTime)
          case knots if 6 until 12 contains knots => ForecastRating(Rating.POOR, dateTime)
          case knots if 12 until 16 contains knots => ForecastRating(Rating.PROMISING, dateTime)
          case knots if knots > 16 => ForecastRating(Rating.HIGH, dateTime)
          case broken => {
            log.warning(s"unmatched knots value: ${broken}")
            ForecastRating(Rating.NONE, dateTime)
          }
        }
        if (entryRating.rating > rating.rating) entryRating else rating
      }
    }
    log.info(s"Rating for ${day}: ${rating}")
    return rating
  }

  def getDateTime(day: String, hour: String): DateTime = {
    val formatter:DateTimeFormatter= DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
    formatter.parseDateTime(s"${day} ${hour}")
  }

  def judgeForecast(message: NotificationPlanExecutorMessage) {
    val start = ForecastRating(Rating.NONE, DateTime.now)
    val rating = message.forecast.days.foldLeft(start) {
      (rating, day) => {
        val dayRating = getDailyRating(day)
        if (dayRating.rating > rating.rating) dayRating else rating
      }
    }
    log.info(s"Overall rating: ${rating}")
    sender() ! rating
  }

  def receive = {
    case msg: NotificationPlanExecutorMessage => judgeForecast(msg)
    case _ => log.error("Unknown message")
  }
}
