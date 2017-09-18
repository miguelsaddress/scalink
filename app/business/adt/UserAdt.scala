package business.adt

case class SignUpData(name: String, username: String, email: String, password: String)
case class SignInData(email: String, password: String)