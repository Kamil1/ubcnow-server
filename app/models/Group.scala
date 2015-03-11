package models

import scala.collection.mutable.ArrayBuffer

case class Group(
    gid: Long,
    name: String, 
    concrete: Boolean,
    interests: ArrayBuffer[Long]
)
