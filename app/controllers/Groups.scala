package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
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
    

    def create = TODO

    def get(gid: Long) = TODO   
    
    def update(gid: Long) = TODO
    
    def delete(gid: Long) = TODO

    def search = TODO
}
