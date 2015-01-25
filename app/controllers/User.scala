package Controller

case class User (
  puid: Long,
  studentNumber: Long,
  affliation: String,
  firstName: String,
  lastName: String,
  interests: List[Interest],
  groups: List[Groups]
)

object UserController extends Controller {

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

}
