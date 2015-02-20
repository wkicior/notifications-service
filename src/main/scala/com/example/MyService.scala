package com.example

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import spray.httpx._
import MediaTypes._



// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

case class Person(name: String, favoriteNumber: Int)

object AdsJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val personFormat = jsonFormat2(Person)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {
  import AdsJsonProtocol._
  val myRoute =
    pathPrefix("ads") {
      get {
        complete {
          //TO getter
          case class Contact(email: String)
          case class User(id: Int, name: String, contact: Contact)
          case class Ad(id:Int, title: String, desc: String, owner:User)
          val ads = Ad(id=1, title="loremxxxxyyy33",desc="ipsum", owner=User(id=1, name="Dolor", contact = Contact(email="lorem@ipsum.pl")))::
            Ad(id=2, title="lorem",desc="ipsum", owner=User(id=1, name="Dolor", contact = Contact(email="lorem@ipsum.pl")))::
            Ad(id=3, title="lorem",desc="ipsum", owner=User(id=1, name="Dolor", contact = Contact(email="lorem@ipsum.pl")))::
            Nil
          ads.toString
        }
      } ~
      post {
        entity(as[Person]) { person =>
          complete(s"Person: ${person.name} - favorite number: ${person.favoriteNumber}")          
        }
      }
    }
}