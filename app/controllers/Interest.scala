package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._

case class Interest(
  iid: Long,
  name: String,
  groups: Long
)


object InterestController extends Controller {
  
  implicit val interestWrites = new Writes[Interest] {
    def writes(interest: Interest): JsValue = Json.obj(
      "iid" -> interest.iid,
      "name" -> interest.name,
      "groups" -> interest.groups)
  }
  
  def list = Action {
    DB.withConnection { implicit c =>
    val result: List[Interest] = SQL("select * from interest")().collect {
      case Row(iid: Long,
        name: String,
        groups: Long) => Interest(iid, name, groups)
    }.toList
    Ok(Json.toJson(result))
  }
}

  def create = TODO

  def get(iid: Long) = Action{
    DB.withConnection { implicit c =>
    val result: Interest = SQL("select * from interest where iid = {iid}")().collect {
      case Row(iid: Long,
        name: String,
        groups: Long) => Interest(iid, name, groups)
    }.head
    Ok(Json.toJson(result))
  }
}

  def update(iid: Long) = TODO

  def delete(iid: Long) = TODO

}

