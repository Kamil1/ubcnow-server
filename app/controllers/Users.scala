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
import anorm.SqlParser._

object UserController extends Controller {

  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsValue = Json.obj(
      "id" -> user.id,
      "puid" -> user.puid,
      "studentNumber" -> user.studentNumber,
      "givenName" -> user.firstName,
      "sn" -> user.lastName,
      "interests" -> user.interests,
      "groups" -> user.groups)
  }

    implicit val userReads : Reads[User] = (
      (__\ "id").read[Long] and
      (__ \ "puid").read[String] and
      (__ \ "studentNumber").read[Long] and
      (__ \ "givenName").read[String] and
      (__ \ "sn").read[String] and
      (__ \ "interests").read[List[Long]] and
      (__ \ "groups").read[List[Long]]
  )(User)

  val userRowParser = int("id") ~ str("puid") ~ int("studentNumber") ~ str("givenName") ~ str("sn") map {
    case id~puid~studentNumber~givenName~sn => User(puid, studentNumber, givenName, sn)
  }

  def list = Action {
    DB.withConnection { implicit c =>
      val results: List[User] = SQL("SELECT * FROM users")
      .as(userRowParser *)
      .map { case User(id, puid, studentNumber, givenName, sn, _, _) =>
        User(id, puid, studentNumber, givenName, sn, userInterests(puid).as(scalar[String] *), userGroups(puid).as(scalar[Long] *))
      }
      Ok(Json.toJson(results))
    }
  }

  def create = Action(parse.json) { request =>
    val json: JsValue = request.body
    val user = json.as[User]

    DB.withTransaction { implicit c =>
      SQL("""
        INSERT INTO users(id, puid, studentNumber, givenName, sn)
        VALUES ({id}, {puid}, {studentNumber}, {givenName}, {sn})
        """)
      .on(
        "id" -> user.id,
        "puid" -> user.puid,
        "studentNumber" -> user.studentNumber,
        "givenName" -> user.givenName,
        "sn" -> user.sn)
      for( interest <- user.interests ) {
        SQL("""
          INSERT INTO user_interests(id, iid)
          VALUES ({id}, {interest})
          """)
        .on(
          "id" -> user.id,
          "interest" -> interest)
      }
      for ( group <- user.groups ) {
        SQL("""
          INSERT INTO user_groups(id, gid)
          VALUES ({id}, {group})
          """)
        .on(
          "id" -> user.id,
          "group" -> group)
      }
    }
    Ok
  }

  def get(id: Long) = Action {
    DB.withTransaction { implicit c =>
      SQL("SELECT * FROM users WHERE id = {id}")
      .on("id" -> puid)
      .as(userRowParser singleOpt)
      match {
        case Some(User(id, puid, studentNumber, givenName, sn, _, _)) => {
          Ok(Json.toJson(User(id, puid, studentNumber, givenName, sn, userInterests(puid).as(scalar[Long] *), userGroups(puid).as(scalar[Long] *))))
        }
        case None => NotFound
      }
    }
  }

  def update(id: Long) = Action(parse.json) { request =>
    val json: JsValue = request.body
    val user = json.as[User]

    DB.withTransaction { implicit c =>
      SQL("""
        UPDATE users
        SET studentNumber=ISNULL({studentNumber}, studentNumber),
        givenName=ISNULL({givenName}, givenName),
        sn=ISNULL({sn}, sn),
        WHERE id={id}
        """)
      .on(
        "id" -> id,
        "puid" -> user.puid,
        "studentNumber" -> user.studentNumber,
        "givenName" -> user.givenName,
        "sn" -> user.sn).executeUpdate()
      SQL("""
        DELETE FROM user_interests
        WHERE id = {id}
        """)
      .on("id" -> id)
      for( interest <- user.interests ) {
        SQL("""
          INSERT INTO user_interests(id, iid)
          VALUES ({id}, {interest})
          """)
        .on(
          "id" -> user.id,
          "interest" -> interest)
      }
      SQL("""
        DELETE FROM user_groups
        WHERE id = {id}
        """)
      for ( group <- user.groups ) {
        SQL("""
          INSERT INTO user_groups(id, gid)
          VALUES ({id}, {group})
          """)
        .on(
          "id" -> user.id,
          "group" -> group)
      }
    }
    Ok
  }

  def delete(id: Long) = Action {
    DB.withTransaction { implicit c =>
      SQL("""
        DELETE FROM users
        WHERE id={id}
        """)
      .on("id" -> id)
      SQL("""
        DELETE FROM user_interests
        WHERE id={id}
        """)
      .on("id" -> id)
      SQL("""
        DELETE FROM user_groups
        WHERE id={id}
        """)
      .on("id" -> id)
    }
    Ok
  }

  def userInterests = { id: Long =>
    SQL("""
      SELECT users.id FROM user_interests
      RIGHT JOIN interests
      ON user_interests.iid = interests.id
      WHERE user_interests.id = {id}
      """)
    .on("id" -> id)
  }

  def userGroups = { id: Long =>
    SQL("""
      SELECT users.id FROM user_groups
      RIGHT JOIN groups
      ON user_groups.gid = groups.id
      WHERE user_groups.id = {id}
      """)
    .on("id" -> id)
  }

}
