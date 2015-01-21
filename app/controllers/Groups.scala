package controllers

import controllers.Interest
import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import play.api.libs.json.Json.JsValueWrapper

case class Group(
  gid: Long,
  name: String, 
  interests: Long,
  concrete: Boolean
)

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
    val results: List[Group] = SQL("select * from groups")().collect {
      case Row(gid: Long,
        name: String,
        interests: Long,
        concrete: Boolean) => Group(gid, name, interests, concrete)
    }.toList
    Ok(Json.toJson(results))
  }
}

  def create = TODO

  def get(gid: Long) = Action {
    DB.withConnection { implicit c =>
    val result: Group = SQL("select * from groups where gid = {gid}").on("gid"->gid)().collect {
      case Row(gid: Long,
        name: String,
        interests: Long,
        concrete: Boolean) => Group(gid, name, interests, concrete)
    }.head
    Ok(Json.toJson(result))
  }
}
  
  def update(gid: Long) = TODO
  
  def delete(gid: Long) = TODO
}
