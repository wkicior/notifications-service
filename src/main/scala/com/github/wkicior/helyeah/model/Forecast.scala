 package com.github.wkicior.helyeah.model

 /**
 * Created by disorder on 20.02.15.
 */

case class ConditionEntry(hour: Int, windDirDegree: Int, windGustKnots: Int, windSpeedKnots: Int)
case class Day(conditionEntries: Seq[ConditionEntry], date: String)
case class Forecast(days: Seq[Day])
case class NotificationRequest(forecast: Forecast)
