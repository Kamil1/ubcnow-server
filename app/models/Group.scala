package models

import scala.collection.mutable.ArrayBuffer

case class Group(
    gid: Int,
    name: String, 
    concrete: Boolean,
    interests: ArrayBuffer[Int]
)
