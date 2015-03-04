package controller

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import models.User

object UserController extends Controller {

  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsValue = Json.obj(
      "puid" -> user.puid,
      "studentNumber" -> user.studentNumber,
      "affiliation" -> user.affiliation,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "interests" -> user.interests,
      "groups" -> user.groups)
  }

  private def jsonToUser(json: JsValue) : User = {
    return User(
      (json \ "puid").as[Long],
      (json \ "studentNumber").as[Option[Long]],
      (json \ "affiliation").as[String],
      (json \ "firstName").as[String],
      (json \ "lastName").as[String],
      (json \ "interests").as[Option[List[Long]]],
      (json \ "groups").as[Option[List[Long]]]
      )
  }

  def list = Action {
    DB.withConnection { implicit c =>
      val results: List[User] = SQL("SELECT * FROM users")()
      .collect(matchUser)
      .toList
      Ok(Json.toJson(results))
    }
  }

  def create = Action(parse.json) { request =>
    val json: JsValue = request.body
    val user = jsonToUser(json)

    DB.withConnection { implicit c =>
      SQL("""
        INSERT INTO users(puid, studentNumber, affiliation, firstName, lastName)
        VALUES ({puid}, {studentNumber}, {affiliation}, {firstName}, {lastName})
        """)
      .on(
        "puid" -> user.puid,
        "studentNumber" -> user.studentNumber,
        "affiliation" -> user.affiliation,
        "firstName" -> user.firstName,
        "lastName" -> user.lastName).executeUpdate()
      for( a <- user.interests ){
        SQL("""
          INSERT INTO user_interests(puid, iid)
          VALUES ({puid}, {a})
          """)
        .on(
          "puid" -> user.puid,
          "a" -> a)
      }
    }
    Ok
  }

  def get(puid: Long) = Action {
    DB.withConnection { implicit c =>
      val result: User = SQL("SELECT * FROM users WHERE puid = {puid}")
      .on("puid" -> puid)()
      .collect(matchUser)
      .head
      Ok(Json.toJson(result))
    }
  }

  def update(puid: Long) = Action(parse.json) { request =>
    val json: JsValue = request.body
    val user = jsonToUser(json)

    DB.withConnection { implicit c =>
      SQL("""
        UPDATE users
        SET studentNumber=ISNULL({studentNumber}, studentNumber),
        affiliation=ISNULL({affiliation}, affiliation),
        firstName=ISNULL({firstName}, firstName),
        lastName=ISNULL({lastName}, lastName),
        WHERE puid={puid}
        """)
      .on(
        "puid" -> puid,
        "studentNumber" -> user.studentNumber,
        "affiliation" -> user.affiliation,
        "firstName" -> user.firstName,
        "lastName" -> user.lastName).executeUpdate()
    }
    Ok
  }

  def delete(puid: Long) = Action {
    DB.withConnection { implicit c =>
      SQL("""
        DELETE FROM users
        WHERE puid={puid}
        """)
      .on(
        "puid" -> puid
        ).executeUpdate()
    }
    Ok
  }

  def matchUser: PartialFunction[Row,User] = {
    case Row(puid: Long,
      studentNumber: Option[Long],
      affiliation: String,
      firstName: String,
      lastName: String,
      interests: Option[List[Long]],
      groups: Option[List[Long]]
      ) => User(puid, studentNumber, affiliation, firstName, lastName, interests, groups)
  }

}
