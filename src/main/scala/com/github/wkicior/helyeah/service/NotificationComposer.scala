package com.github.wkicior.helyeah.service

import akka.actor.{Actor, Props}
import akka.event.Logging
import com.github.wkicior.helyeah.model.{ForecastRating, NotificationPlan}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class NotificationComposerMessage(plan: NotificationPlan, forecastRating: ForecastRating)

/**
 * Created by disorder on 25.02.15.
 */
object NotificationComposer {
  def props(): Props = Props(new NotificationComposer())
}

class NotificationComposer extends Actor {
  val log = Logging(context.system, this)

  def composeMessage(message: NotificationComposerMessage) = {
    val system = context.system
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    //TODO: that returns timeout on tests
    val response: Future[HttpResponse] = pipeline(Get("http://wp.pl"))
    response.onComplete {
      case Success(somethingUnexpected) =>
        log.warning("The Google API call was successful but returned something unexpected: '{}'.", somethingUnexpected)
      case Failure(error) =>
        log.error(error, "Couldn't get elevation")
    }
  }

  def receive = {
    case msg:NotificationComposerMessage => composeMessage(msg)
    case _ => log.error("Unknown message")
  }
}
