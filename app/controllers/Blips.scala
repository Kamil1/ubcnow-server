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
            "title" -> blip.title,
            "summary" -> blip.summary,
            "link" -> blip.link,
            "time" -> blip.time,
            "address" -> blip.address,
            "lat" -> blip.lat,
            "lng" -> blip.lng)
    }

    def jsonToBlip(json: JsValue) : Blip = {
      return Blip(
        0,
        (json \ "gid").as[Long],
        (json \ "title").as[String],
        (json \ "summary").as[Option[String]],
        (json \ "link").as[Option[String]],
        (json \ "time").as[Option[String]],
        (json \ "address").as[Option[String]],
        (json \ "lat").as[Option[Double]],
        (json \ "lng").as[Option[Double]]
      )
  }

    def list = Action {
        DB.withConnection { implicit c =>
            val results: List[Blip] = SQL("SELECT * FROM blips")()
                .collect(matchBlip)
                .toList
            Ok(Json.toJson(results))
        }
    }

    def create() = Action(parse.json) { request =>
      val json: JsValue = request.body
      val blip = jsonToBlip(json)

      DB.withConnection { implicit c =>
      SQL("""
         INSERT INTO blips(gid, title, summary, link, time, address, lat, lng)
         VALUES ({gid}, {title}, {summary}, {link}, {time}, {address}, {lat}, {lng})
         """)
       .on(
         "gid" -> blip.gid,
         "title" -> blip.title,
         "summary" -> blip.summary,
         "link" -> blip.link,
         "time" -> blip.time,
         "address" -> blip.address,
         "lat" -> blip.lat,
         "lng" -> blip.lng).executeUpdate()
     }
    Ok
  }

    def get(id: Long) = Action {
        DB.withConnection { implicit c =>
            val result: Blip = SQL("SELECT * FROM blips WHERE id = {id}")
                .on("id" -> id)()
                .collect(matchBlip)
                .head
            Ok(Json.toJson(result))
        }
    }

    def update(id: Long) = Action(parse.json) { request =>
      val json: JsValue = request.body
      val blip = jsonToBlip(json)

      DB.withConnection { implicit c =>
      SQL("""
        UPDATE blips
        SET title={title}, summary={summary}, link={link}, time={time}, address={address}, lat={lat}, lng={lng}
        WHERE id={id}
        """)
    }
    Ok
  }


    def delete(id: Long) = Action {
      DB.withConnection { implicit c =>
      SQL("""
        DELETE FROM TABLE blips
        WHERE id={id}
        """)
    }
    Ok
  }

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
