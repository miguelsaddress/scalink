package models

import play.api.libs.json._
import auth.Roles._

case class User(name: String, username: String, email: String, password: String, role: AuthRole, id: Long)

case object User {
  def apply(name: String, username: String, email: String, password: String, role: AuthRole = UserRole, id: Long = 0L): User = {
    assert( !username.contains(" "), "username must not contain spaces" )

    new User(name, username.toLowerCase, email.toLowerCase, password, role, id)
  }
}