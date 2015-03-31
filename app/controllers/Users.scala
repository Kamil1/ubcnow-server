package controller

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError
import scala.collection.mutable.ArrayBuffer
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

    implicit val userReads : Reads[User] = (
      (__ \ "puid").read[Long] and
      (__ \ "studentNumber").read[Option[Long]] and
      (__ \ "affiliation").read[String] and
      (__ \ "firstName").read[String] and
      (__ \ "lastName").read[String] and
      (__ \ "interests").read[ArrayBuffer[Long]] and
      (__ \ "groups").read[ArrayBuffer[Long]]
  )(User)

  val userRowParser = int("puid") ~ int("studentNumber") ~ str("affiliation") ~ str("firstName") ~ str("lastName") map {
    case puid~studentNumber~affiliation~firstName~lastName => User(puid, studentNumber, affiliation, firstName, lastName)
  }

  def list = Action {
    DB.withConnection { implicit c =>
      val results: List[User] = SQL("SELECT * FROM users")
      .as(userRowParser *)
      .map { case User(puid, studentNumber, affiliation, firstName, lastName, _, _) =>
        User(puid, studentNumber, affiliation, firstName, lastName, userInterests(puid).as(scalar[Long] *), userGroups(puid).as(scalar[Long] *))
      }
      Ok(Json.toJson(results))
    }
  }

  def create = Action(parse.json) { request =>
    val json: JsValue = request.body
    val user = json.as[User]

    DB.withTransaction { implicit c =>
      SQL("""
        INSERT INTO users(puid, studentNumber, affiliation, firstName, lastName)
        VALUES ({puid}, {studentNumber}, {affiliation}, {firstName}, {lastName})
        """)
      .on(
        "puid" -> user.puid,
        "studentNumber" -> user.studentNumber,
        "affiliation" -> user.affiliation,
        "firstName" -> user.firstName,
        "lastName" -> user.lastName)
      for( interest <- user.interests ) {
        SQL("""
          INSERT INTO user_interests(puid, iid)
          VALUES ({puid}, {interest})
          """)
        .on(
          "puid" -> user.puid,
          "interest" -> interest)
      }
      for ( group <- user.groups ) {
        SQL("""
          INSERT INTO user_groups(puid, gid)
          VALUES ({puid}, {group})
          """)
        .on(
          "puid" -> user.puid,
          "group" -> group)
      }
    }
    Ok
  }

  def get(puid: Long) = Action {
    DB.withTransaction { implicit c =>
      val result: User = SQL("SELECT * FROM users WHERE puid = {puid}")
      .on("puid" -> puid)()
      .collect(matchUser)
      .head
      Ok(Json.toJson(result))
    }
  }

  def update(puid: Long) = Action(parse.json) { request =>
    val json: JsValue = request.body
    val user = json.as[User]

    DB.withTransaction { implicit c =>
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
      SQL("""
        DELETE FROM user_interests
        WHERE puid = {puid}
        """)
      .on("puid" -> puid)
      for( interest <- user.interests ) {
        SQL("""
          INSERT INTO user_interests(puid, iid)
          VALUES ({puid}, {interest})
          """)
        .on(
          "puid" -> user.puid,
          "interest" -> interest)
      }
      SQL("""
        DELETE FROM user_groups
        WHERE puid = {puid}
        """)
      for ( group <- user.groups ) {
        SQL("""
          INSERT INTO user_groups(puid, gid)
          VALUES ({puid}, {group})
          """)
        .on(
          "puid" -> user.puid,
          "group" -> group)
      }
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
        )
    }
    Ok
  }

  def matchUser: PartialFunction[Row,User] = {
    case Row(puid: Long,
      studentNumber: Option[Long],
      affiliation: String,
      firstName: String,
      lastName: String,
      interests: ArrayBuffer[Long],
      groups: ArrayBuffer[Long]
      ) => User(puid, studentNumber, affiliation, firstName, lastName, interests, groups)
  }

  def userInterests = { puid: Long =>
    SQL("""
      SELECT users.puid FROM user_interests
      RIGHT JOIN interests
      ON user_interests.iid = interests.id
      WHERE user_interests.puid = {puid}
      """)
    .on("puid" -> puid)
  }

  def userGroups = { puid: Long =>
    SQL("""
      SELECT users.puid FROM user_groups
      RIGHT JOIN groups
      ON user_groups.gid = groups.id
      WHERE user_groups.puid = {puid}
      """)
    .on("puid" -> puid)
  }

}
