package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json.JsValueWrapper
import scala.collection.mutable.ArrayBuffer
import models.Group

object GroupController extends Controller {

    implicit val groupWrites = new Writes[Group] {
      def writes(group: Group): JsValue = Json.obj(
        "gid" -> group.gid,
        "name" -> group.name,
        "interests" -> group.interests,
        "concrete" -> group.concrete)
    }

    implicit val groupReads : Reads[Group] = (
        (__ \ "gid").read[Long] and
        (__ \ "name").read[String] and
        (__ \ "concrete").read[Boolean] and
        (__ \ "interests").read[ArrayBuffer[Long]]
        )(Group)

    def list = Action {
      DB.withConnection { implicit c =>
          val results: List[Group] = SQL(
            """SELECT * FROM groups
            LEFT OUTER JOIN group_interests
            ON groups.id = group_interests.gid""")()
            .foldLeft(List[Group]()) { (base, row) =>
                row match {
                    case Row(gid: Int, name: String, concrete: Boolean, _, _, iid: Int) => {
                        if (base.length > 0 && base.last.gid == gid) {
                            // Merge interests with last group.
                            // FIXME: a bit hackish? neat though.
                            base.last.interests += iid
                            base
                        } else {
                            Group(gid, name, concrete, ArrayBuffer(iid)) :: base
                        }
                    }
                    case _ => base
                }
            }
          Ok(Json.toJson(results))
      }
    }
    

    def create = Action(parse.json) { request =>
        val json: JsValue = request.body
        val group = json.as[Group]

        DB.withTransaction { implicit c =>
            SQL("""
                INSERT INTO groups(name, concrete)
                VALUES ({name}, {concrete})
                """)
            .on(
                "name" -> group.name,
                "concrete" -> group.name)
            for (interest <- group.interests) {
                SQL("""
                    INSERT INTO group_interests(gid, iid)
                    VALUES ({gid}, {interest})
                    """)
                .on(
                    "gid" -> group.gid,
                    "iid" -> interest)
            }
        }
        Ok
    }

    def get(gid: Long) = Action {
        DB.withConnection { implicit c =>
            val results: Group = SQL(
            """SELECT * FROM groups
            WHERE gid = {gid}
            RIGHT JOIN group_interests
            ON groups.id = group_interests.gid
            """)()
            .foldLeft(Group) { (base, row) =>
                row match {
                    case Row(gid: Int, name: String, concrete: Boolean, iid: Int) => {
                        if (base.gid || base.gid == gid) {
                            base.interests += iid
                            base
                        } else {
                            Group(gid, name, concrete, ArrayBuffer(iid)) :: base
                        }
                    }
                    case _ => base
                }
            }
            Ok(Json.toJson(results))
        }
    }
    
    def update(gid: Long) = Action(parse.json) { request =>
        val json: JsValue = request.body
        val group = json.as[Group]

        DB.withConnection { implicit c =>
            SQL("""
                UPDATE groups
                SET name=ISNULL({name}, name),
                concrete = ISNULL({concrete}, concrete)
                WHERE gid={gid}
                """)
            .on(
                "name" -> group.name,
                "concrete" -> group.concrete,
                "gid" -> group.gid)
            SQL("""
                DELETE FROM group_interests
                WHERE gid={gid}
                """)
            .on("gid" -> group.gid)
            for ( interest <- group.interests)
            SQL("""
                INSERT INTO group_interests(gid ,iid)
                VALES ({gid}, {interest})
                """)
            .on(
                "gid" -> group.gid,
                "interest" -> interest)
        }
        Ok
    }
    
    def delete(gid: Long) = TODO

    def search = TODO
}
