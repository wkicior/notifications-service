package com.github.wkicior.helyeah.model

import com.github.wkicior.helyeah.model.Rating.Rating
import org.joda.time.DateTime

object Rating extends Enumeration {
  type Rating = Value
  val NONE, POOR, PROMISING, HIGH = Value
}


/**
 * Forecast rating return by ForecastJudge
 * Created by disorder on 25.02.15.
 */
case class ForecastRating(rating:Rating, startingFrom:DateTime)


