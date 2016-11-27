package api

/**
  * Created by Miruku on 11/26/2016.
  */
import akka.actor._
import akka.util.Timeout
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing._
import spray.json._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.language.postfixOps

class RestInterface extends HttpServiceActor
  with RestApi {

  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging { actor: Actor =>
  import api.HotelProtocol._

  implicit val timeout = Timeout(10 seconds)

  var hotels = getHotelsCSV()

  def routes: Route =

    pathPrefix("sort") {
      path(Segment) { sortType =>
        get { requestContext =>
          val responder = createResponder(requestContext)
          sortHotels(sortType).toList.map(responder ! _)
        }
      }
    } ~
      pathPrefix("hotels") {
        pathEnd {
          get { requestContext =>
            val responder = createResponder(requestContext)
            getHotels(null).map(responder ! _)
          }
        } ~
          path(Segment) { city =>
            get { requestContext =>
              val responder = createResponder(requestContext)
              getHotels(city).map(responder ! _)
            }
          }
      } ~
      pathPrefix("hotel") {
          path(Segment) { id =>
            get { requestContext =>
              val responder = createResponder(requestContext)
              getHotelById(id.toInt).map(responder ! _)
            }
          }
      }


  private def createResponder(requestContext:RequestContext) = {
    context.actorOf(Props(new Responder(requestContext)))
  }

  private def getHotels(city: String): Option[Hotels] = {
    val doesNotExist = hotels.isEmpty
    if (!doesNotExist) {
      val hotelsList = hotels.filter(_.city == city)
      val hotelList = new Hotels(hotelsList)
      return Some(hotelList)
    }
    else
      null
  }

  private def getHotelById(id: Int): Option[Hotel] = {
    val doesNotExist = hotels.isEmpty
    if (!doesNotExist) {
      val hotelsList = hotels.find(_.hotelId == id)
      return hotelsList
    }
    else
      null
  }

  private def sortHotels(sortType: String): Option[Hotels] = {
    sortType match {
      case "ASC" => Some(new Hotels(hotels.sortWith(_.price < _.price)))
      case "DESC" => Some(new Hotels(hotels.sortWith(_.price > _.price)))
    }

  }

  def getCurrentDirectory = new java.io.File(".").getCanonicalPath

  def readCSV() : Vector[Array[String]]= {
    val home = getCurrentDirectory
    io.Source.fromFile(s"${home}/src/main/resources/hoteldb.csv")
      .getLines().drop(1)
      .map(_.split(",").map(_.trim))
      .toVector
  }

  def getHotelsCSV() : Vector[Hotel] = {
    var hotels = new ListBuffer[Hotel]()
    val readData = readCSV()
    for(collection <- readData )
      {
        val hotel = new Hotel(collection(0),collection(1).toInt,collection(2),collection(3).toDouble)
        hotels += hotel
      }
    return hotels.toVector
  }
}

class Responder(requestContext:RequestContext) extends Actor with ActorLogging {

  import api.HotelProtocol._
  def receive = {

    case HotelNotFound =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself
    case hotel : Hotel =>
      requestContext.complete(StatusCodes.OK,hotel)
      killYourself
    case hotels : Hotels =>
      requestContext.complete(StatusCodes.OK,hotels)
      killYourself
  }

  private def killYourself = self ! PoisonPill

}
