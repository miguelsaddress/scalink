package business.forms

import play.api.data.Form
import play.api.data.Forms._
import business.adt.User.{ SignUpData, SignInData }

object User {
  lazy val signUpForm: Form[SignUpData] = Form {
    mapping(
      "name" -> nonEmptyText,
      "username" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(SignUpData.apply)(SignUpData.unapply)
  }

  lazy val signInForm: Form[SignInData] = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(SignInData.apply)(SignInData.unapply)
  }
}