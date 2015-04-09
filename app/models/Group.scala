package models

import scala.collection.mutable.MutableList

case class Group(
    gid: Long,
    name: String,
    concrete: Boolean,
    interests: List[Long] = List[Long]()
)
