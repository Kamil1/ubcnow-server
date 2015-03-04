package models

case class User (
  puid: Long,
  studentNumber: Option[Long] = None,
  affiliation: String,
  firstName: String,
  lastName: String,
  interests: Option[List[Long]],
  groups: Option[List[Long]]
)
