package business.adt.User

import scala.concurrent.{ExecutionContext, Future}

import auth.Roles._
import auth.actions.AuthFailures._
import models.User

case class SignUpData(name: String, username: String, email: String, password: String, passwordConf: String)
case class SignInData(email: String, password: String)
case class UserInfo(username: String, role: AuthRole)
