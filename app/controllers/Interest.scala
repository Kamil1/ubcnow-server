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
  groups: List[Long]
)


object InterestController extends Controller {
  
  implicit val interestWrites = new Writes[Interest] {
    def writes(interest: Interest): JsValue = Json.obj(
      "iid" -> interest.iid,
      "name" -> interest.name,
      "groups" -> interest.groups)
  }

  val interestRowParser = int("id") ~ str("name") map {
    case id~name => Interest(id, name)
  }
  
  def list = Action {
    DB.withTransaction { implicit c =>
    val results: List[Interest] = SQL("SELECT * FROM interests")
    .as(interestRowParser *)
    .map { case Interest(id, name, _) => 
      Interest(id, name, interestGroups(id).as(scalar[Long] *))
    }
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
      .on("name" -> interest.name)
      for ( group <- interest.groups) {
        SQL("""
          INSERT INTO group_interests(gid, iid)
          VALUES ({gid}, {iid})
          """)
        .on("gid" -> group,
            "iid" -> interest.iid)
      }
    }
  }

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

  def interestGroups = { iid: Long =>
    SQL("""
      SELECT groups.id FROM group_interests
      RIGHT JOIN groups
      ON group_interests.gid = groups.id
      WHERE group_interests.iid = {iid}
      """)
    .on("iid" -> iid)
  }
}

