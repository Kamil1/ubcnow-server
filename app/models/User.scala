package models

import scala.collection.mutable.ArrayBuffer

case class User (
  puid: Long,
  studentNumber: Option[Long] = None,
  affiliation: String,
  firstName: String,
  lastName: String,
  interests: ArrayBuffer[Long],
  groups: ArrayBuffer[Long]
)
