package business.adt.User

case class SignUpData(name: String, username: String, email: String, password: String, passwordConf: String)
case class SignInData(email: String, password: String)
