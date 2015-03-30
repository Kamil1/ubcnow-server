package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import anorm.SqlParser._
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
    

    def create = TODO

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
    
    def update(gid: Long) = TODO
    
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
