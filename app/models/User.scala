package models

case class User (
  id: Long,
  puid: String,
  studentNumber: Long,
  givenName: String,
  sn: String,
  interests: List[Long] = List[Long](),
  groups: List[Long] = List[Long]()
)
