package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
<<<<<<< HEAD
import play.api.libs.functional.syntax._
=======
import anorm.SqlParser._
>>>>>>> upstream/master
import play.api.libs.json.Json.JsValueWrapper
import java.sql.Connection
import models.Group

object GroupController extends Controller {

    implicit val groupWrites = new Writes[Group] {
      def writes(group: Group): JsValue = Json.obj(
        "gid" -> group.gid,
        "name" -> group.name,
        "interests" -> group.interests,
        "concrete" -> group.concrete)
    }
    val groupRowParser = int("id") ~ str("name") ~ bool("concrete") map {
        case id~name~concrete => Group(id, name, concrete)
    }

    def list = Action {
      DB.withConnection { implicit c =>
          val results: List[Group] = SQL("SELECT * FROM groups")
            .as(groupRowParser *)
            .map { case Group(gid, name, concrete, _) =>
                Group(gid, name, concrete, groupInterests(gid).as(scalar[Long] *))
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
        DB.withTransaction { implicit c =>
            SQL("SELECT * FROM groups WHERE id = {id}")
                .on("id" -> gid)
                .as(groupRowParser singleOpt)
                match {
                    case Some(Group(gid, name, concrete, _)) => {
                        Ok(Json.toJson(Group(gid, name, concrete, groupInterests(gid).as(scalar[Long] *))))
                    }
                    case None => NotFound
                }
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

    def groupInterests = { gid: Long =>
        SQL("""SELECT interests.id FROM group_interests
               RIGHT JOIN interests
               ON group_interests.iid = interests.id
               WHERE group_interests.gid = {gid}""")
        .on("gid" -> gid)
    }
}
