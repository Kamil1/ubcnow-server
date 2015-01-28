package controller

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import play.api.libs.json.Json.JsValueWrapper

case class User (
  puid: Long,
  studentNumber: Option[Long] = None,
  affliation: String,
  firstName: String,
  lastName: String,
  interests: List[Long],
  groups: List[Long]
)

object UserController extends Controller {

  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsValue = Json.obj(
      "puid" -> user.puid,
      "studentNumber" -> user.studentNumber,
      "affiliation" -> user.affliation,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "interests" -> user.interests,
      "groups" -> user.groups)
  }

  def list = Action {
    DB.withConnection { implicit c =>
      val results: List[User] = SQL("SELECT * FROM users")()
        .collect(matchUser)
        .toList
      Ok(Json.toJson(results))
    }
  }

  def create = TODO

  def get(puid: Long) = Action {
    DB.withConnection { implicit c =>
      val result: User = SQL("SELECT * FROM users WHERE puid = {puid}")
        .on("puid" -> puid)()
        .collect(matchUser)
        .head
      Ok(Json.toJson(result))
    }
  }

  def update(puid: Long) = TODO

  def delete(puid: Long) = TODO

  def matchUser: PartialFunction[Row,User] = {
    case Row(puid: Long,
      studentNumber: Option[Long],
      affiliation: String,
      firstName: String,
      lastName: String,
      interests: Long,
      groups: Long
      ) => User(puid, studentNumber, affiliation, firstName, lastName, interests, groups)
  }

}
