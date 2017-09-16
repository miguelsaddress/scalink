package models

import play.api.libs.json._

case class User(name: String, username: String, email: String, password: String, id: Long = 0L)

object User {
  implicit val personFormat = Json.format[User]
}
