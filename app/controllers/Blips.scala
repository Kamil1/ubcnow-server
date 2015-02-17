package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import models.Blip

object BlipController extends Controller {

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
            val results: List[Blip] = SQL("SELECT * FROM blips")()
                .collect(matchBlip)
                .toList
            Ok(Json.toJson(results))
        }
    }

    def create = TODO

    def get(id: Long) = Action {
        DB.withConnection { implicit c =>
            val result: Blip = SQL("SELECT * FROM blips WHERE id = {id}")
                .on("id" -> id)()
                .collect(matchBlip)
                .head
            Ok(Json.toJson(result))
        }
    }

    def update(id: Long) = TODO

    def delete(id: Long) = TODO

    def matchBlip: PartialFunction[Row,Blip] = {
        case Row(id: Int,
          gid: Int,
          title: String,
          summary: Option[String],
          link: Option[String],
          time: Option[String],
          address: Option[String],
          lat: Option[Double],
          lng: Option[Double]) => Blip(id, gid, title, summary, link, time,
                                       address, lat, lng)
    }

}
