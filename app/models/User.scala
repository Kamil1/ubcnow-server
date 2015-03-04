package models

case class User (
  puid: Long,
  studentNumber: Option[Long] = None,
  affiliation: String,
  firstName: String,
  lastName: String,
  interests: Option[List[Int]],
  groups: Option[List[Int]]
)
