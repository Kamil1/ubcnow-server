package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import anorm._
import models.Blip

object BlipController extends Controller {

    implicit val blipWrites: Writes[Blip] = (
        (__ \ "id").writeNullable[Long] and
        (__ \ "gid").writeNullable[Long] and
        (__ \ "title").writeNullable[String] and
        (__ \ "summary").writeNullable[String] and
        (__ \ "link").writeNullable[String] and
        (__ \ "startTime").writeNullable[String] and
        (__ \ "endTime").writeNullable[String] and
        (__ \ "address").writeNullable[String] and
        (__ \ "lat").writeNullable[Double] and
        (__ \ "lng").writeNullable[Double]
    )(unlift(Blip.unapply))


    implicit val blipReads: Reads[Blip] = (
        (__ \ "id").readNullable[Long] and
        (__ \ "gid").readNullable[Long] and
        (__ \ "title").readNullable[String] and
        (__ \ "summary").readNullable[String] and
        (__ \ "link").readNullable[String] and
        (__ \ "startTime").readNullable[String] and
        (__ \ "endTime").readNullable[String] and
        (__ \ "address").readNullable[String] and
        (__ \ "lat").readNullable[Double] and
        (__ \ "lng").readNullable[Double]
    )(Blip.apply _)

    def list = Action {
        DB.withConnection { implicit c =>
            val results: List[Blip] = SQL("SELECT * FROM blips")()
                .collect(matchBlip)
                .toList
            Ok(Json.toJson(results))
        }
    }

    def create() = Action(parse.json) { request =>
      request.body.validate[Blip](blipReads) match {
        case s: JsSuccess[Blip] => {
          // Parsing was successful, commit to DB.
          val blip: Blip = s.get
          DB.withConnection { implicit c =>
          SQL("""
             INSERT INTO blips(gid, title, summary, link, startTime, endTime, address, lat, lng)
             VALUES ({gid}, {title}, {summary}, {link}, {startTime}, {endTime}, {address}, {lat}, {lng})
             """)
           .on(
             "gid" -> blip.gid,
             "title" -> blip.title,
             "summary" -> blip.summary,
             "link" -> blip.link,
             "startTime" -> blip.startTime,
             "endTime" -> blip.endTime,
             "address" -> blip.address,
             "lat" -> blip.lat,
             "lng" -> blip.lng).executeUpdate()
         }
         Ok
        }
        case e: JsError => BadRequest(e.toString)
      }
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
      request.body.validate[Blip] match {
        case s: JsSuccess[Blip] => {
            val blip: Blip = s.get
            DB.withConnection { implicit c =>
              SQL("""
                UPDATE blips
                SET title=ISNULL({title}, title), 
                    summary=ISNULL({summary},summary),
                    link=ISNULL({link}, link),
                    startTime=ISNULL({startTime}, startTime),
                    endTime=ISNULL({endTime}, endTime),
                    address=ISNULL({address}, address),
                    lat=ISNULL({lat}, lat),
                    lng=ISNULL({lng}, lng)
                WHERE id={id}
                """)
              .on(
                 "id" -> id,
                 "gid" -> blip.gid,
                 "title" -> blip.title,
                 "summary" -> blip.summary,
                 "link" -> blip.link,
                 "startTime" -> blip.startTime,
                 "endTime" -> blip.endTime,
                 "address" -> blip.address,
                 "lat" -> blip.lat,
                 "lng" -> blip.lng).executeUpdate()
            }
            Ok
        }
        case e: JsError => BadRequest(e.toString)
      }

  }


    def delete(id: Long) = Action {
      DB.withConnection { implicit c =>
      SQL("""
        DELETE FROM blips
        WHERE id={id}
        """)
      .on(
        "id" -> id
      ).executeUpdate()
    }
    Ok
  }

    def matchBlip: PartialFunction[Row,Blip] = {
        case Row(id: Int,
          gid: Int,
          title: String,
          summary: Option[String],
          link: Option[String],
          startTime: Option[String],
          endTime: Option[String],
          address: Option[String],
          lat: Option[Double],
          lng: Option[Double]) => Blip(Some[Long](id), Some[Long](gid),
                                       Some[String](title), summary, link,
                                       startTime, endTime, address, lat, lng)
    }

}