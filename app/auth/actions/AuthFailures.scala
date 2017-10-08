package auth.actions

object AuthFailures {
  sealed trait UserAuthFailure {
    def translationKey: String
  }

  sealed trait SignInFailure extends UserAuthFailure {
    def translationKey: String
  }

  case object WrongCredentials extends SignInFailure {
    def translationKey = "error.signIn.wrongCredentials"
  }

  sealed trait SignUpFailure extends UserAuthFailure {
    def translationKey: String
  }

  case object EmailTaken extends SignUpFailure {
    def translationKey = "error.signUp.emailTaken"
  }
  
  case object UsernameTaken extends SignUpFailure {
    def translationKey = "error.signUp.usernameTaken"
  }

  case object InvalidUsername extends SignUpFailure {
    def translationKey = "error.signUp.invalidUsername"
  }
  
  case object PasswordMissmatch extends SignUpFailure {
    def translationKey = "error.signUp.passwordMissmatch"
  }
  
  case object UnknownSignUpFailure extends SignUpFailure {
    def translationKey = "error.signUp.unkown"
  }
}
