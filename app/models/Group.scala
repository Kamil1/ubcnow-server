package models

import scala.collection.mutable.MutableList

case class Group(
    gid: Int,
    name: String, 
    concrete: Boolean,
    interests: List[Long] = List[Long]()
)
