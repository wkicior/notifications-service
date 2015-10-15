package com.github.wkicior.helyeah.model

import com.github.wkicior.helyeah.model.Rating.Rating
import org.joda.time.DateTime
import com.novus.salat.annotations.raw.EnumAs
import com.novus.salat.annotations.raw.Ignore

object Rating extends Enumeration {
  type Rating = Value
  val NONE, POOR, PROMISING, HIGH = Value
}


/**
 * Forecast rating return by ForecastJudge
 * Created by disorder on 25.02.15.
 */
case class ForecastRating(
    @Ignore rating:Rating, startingFrom:DateTime)


    