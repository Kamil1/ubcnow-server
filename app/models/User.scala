package models

case class User (
  puid: Long,
  studentNumber: Long,
  affiliation: String,
  firstName: String,
  lastName: String,
  interests: List[Long] = List[Long](),
  groups: List[Long] = List[Long]()
)
