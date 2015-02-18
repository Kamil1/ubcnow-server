package models

case class Blip(
    id: Long,
    gid: Long,
    name: String,
    summary: Option[String] = None,
    link: Option[String] = None,
    time: Option[String] = None,
    address: Option[String] = None,
    lat: Option[Double] = None,
    lng: Option[Double] = None
)
