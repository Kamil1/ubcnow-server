package controllers

import controllers.Interest

case class Group(
  gid: Long,
  name: String, 
  interests: Array[Interest],
  concrete: Boolean
)

// TODO - METHODS
