package business.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.emailAddress

import business.adt.User.{ SignUpData, SignInData }

object User {

  lazy val signUpForm: Form[SignUpData] = Form {
    mapping(
      "name" -> text(minLength=1, maxLength=100),
      "username" -> text(minLength=5, maxLength=100),
      "email" -> email.verifying(emailAddress),
      "password" -> text(minLength=6, maxLength=160),
      "passwordConf" -> text(minLength=6, maxLength=160),
    )(SignUpData.apply)(SignUpData.unapply)
  }

  lazy val signInForm: Form[SignInData] = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(SignInData.apply)(SignInData.unapply)
  }
}