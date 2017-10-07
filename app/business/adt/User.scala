package business.adt.User

import auth.Roles._

case class SignUpData(name: String, username: String, email: String, password: String, passwordConf: String)
case class SignInData(email: String, password: String)
case class UserInfo(username: String, role: AuthRole)
