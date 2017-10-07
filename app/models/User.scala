package models

import play.api.libs.json._
import auth.Roles._

case class User (
  name: String, 
  username: String, 
  email: String, 
  password: String, 
  role: AuthRole = UserRole,
  id: Long = 0L)
