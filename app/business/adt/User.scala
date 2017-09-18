package business.adt.User

case class SignUpData(name: String, username: String, email: String, password: String)
case class SignInData(email: String, password: String)

object Failures {
  sealed trait SignUpFailure
  case object EmailTaken extends SignUpFailure
  case object UsernameTaken extends SignUpFailure
  case object UnknownSignUpFailure extends SignUpFailure
}
