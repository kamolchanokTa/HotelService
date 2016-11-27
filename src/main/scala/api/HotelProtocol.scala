package api

import api.HotelProtocol.Hotel.jsonFormat4

import scala.collection.mutable.ListBuffer

/**
  * Created by Miruku on 11/26/2016.
  */
object HotelProtocol {
  import spray.json._

  case class Hotel(city:String,hotelId: Int,room: String,price:Double)

  case class Hotels(hotels:Vector[Hotel])

  case object ListHotelByCity

  case object HotelNotFound

  case class SortHotel(sortType: String)

  case object HotelSortAsc

  case object HotelSortDesc

  object Hotel extends DefaultJsonProtocol {
    implicit val format = jsonFormat4(Hotel.apply)
  }

  object Hotels extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(Hotels.apply)
  }

}

