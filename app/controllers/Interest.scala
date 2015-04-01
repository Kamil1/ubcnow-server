package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import anorm._
import play.api.libs.functional.syntax._
import java.sql.Connection
import play.api.libs.json.Json.JsValueWrapper
import anorm.SqlParser._

case class Interest(
  iid: Long,
  name: String
)


object InterestController extends Controller {
  
  implicit val interestWrites = new Writes[Interest] {
    def writes(interest: Interest): JsValue = Json.obj(
      "iid" -> interest.iid,
      "name" -> interest.name)
  }

  implicit val interestReads : Reads[Interest] = (
    (__ \ "iid").read[Long] and
    (__ \ "name").read[String]
    )(Interest)

  val interestRowParser = int("id") ~ str("name") map {
    case id~name => Interest(id, name)
  }
  
  def list = Action {
    DB.withTransaction { implicit c =>
    val results: List[Interest] = SQL("SELECT * FROM interests")
    .as(interestRowParser *)
    Ok(Json.toJson(results))
  }
}

  def create = Action(parse.json) { request =>
    val json: JsValue = request.body
    val interest = json.as[Interest]

    DB.withTransaction { implicit c =>
      SQL("""
        INSERT INTO interests(name)
        VALUES ({name})
        """)
      .on("name" -> interest.name)()
    }
    Ok //It worked trust me TODO
  }

  def get(iid: Long) = Action{
    DB.withConnection { implicit c =>
    val result: Interest = SQL("SELECT * FROM interest WHERE iid = {iid}")().collect {
      case Row(iid: Long, name: String) => Interest(iid, name)
    }.head
    Ok(Json.toJson(result))
  }
}

  def update(iid: Long) = TODO

  def delete(iid: Long) = TODO

}

