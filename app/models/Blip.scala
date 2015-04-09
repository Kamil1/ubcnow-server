package models
import scala.util.parsing.json
import java.util.Date

case class Blip(
    id: Option[Long],
    gid: Option[Long],
    title: Option[String],
    summary: Option[String],
    link: Option[String],
    startTime: Option[Date],
    endTime: Option[Date],
    address: Option[String],
    lat: Option[Double],
    lng: Option[Double]
)
