package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._

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

object Blips extends Controller {

    implicit val blipWrites = new Writes[Blip] {
        def writes(blip: Blip): JsValue = Json.obj(
            "id" -> blip.id,
            "gid" -> blip.gid,
            "name" -> blip.name,
            "summary" -> blip.summary,
            "link" -> blip.link,
            "time" -> blip.time,
            "address" -> blip.address,
            "lat" -> blip.lat,
            "lng" -> blip.lng)
    }

    def list = Action {
        DB.withConnection { implicit c =>
            val results: List[Blip] = SQL("select * from blips")().collect {
                case Row(id: Int, gid: Int, title: String, summary: Option[String], link: Option[String], time: Option[String], address: Option[String], lat: Option[Double], lng: Option[Double]) => Blip(id, gid, title, summary, link, time, address, lat, lng)
            }.toList
            Ok(Json.toJson(results))
        }
    }

    def create = TODO

    def get(id: Long) = Action {
        DB.withConnection { implicit c =>
            val result: Blip = SQL("select * from blips where id = {id}").on("id" -> id)().collect {
                case Row(id: Int, gid: Int, title: String, summary: Option[String], link: Option[String], time: Option[String], address: Option[String], lat: Option[Double], lng: Option[Double]) => Blip(id, gid, title, summary, link, time, address, lat, lng)
            }.head
            Ok(Json.toJson(result))
        }
    }

    def update(id: Long) = TODO

    def delete(id: Long) = TODO

}