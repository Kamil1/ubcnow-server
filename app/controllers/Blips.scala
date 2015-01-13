package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._

class Blip(blipId: Long, groupId: Long, blipName: String) {
    var id: Long = blipId
    var gid: Long = groupId
    var name: String = blipName
    var summary: Option[String] = None
    var link: Option[String] = None
    var time: Option[String] = None
    var address: Option[String] = None
    var lat: Option[Double] = None
    var lng: Option[Double] = None
}

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

    def list = TODO

    def create = TODO

    def get(id: Long) = Action {
        // TODO (static placeholder)
        val blip = new Blip(1, 1, "test blip")
        blip.summary = Some("sample description")
        val json = Json.toJson(blip)
        Ok(json)
    }

    def update(id: Long) = TODO

    def delete(id: Long) = TODO
}
