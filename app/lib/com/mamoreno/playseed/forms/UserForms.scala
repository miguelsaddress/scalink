package com.mamoreno.playseed.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.emailAddress

case class SignUpData(name: String, username: String, email: String, password: String, passwordConf: String)
case class SignInData(emailOrUsername: String, password: String)

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
      "emailOrUsername" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(SignInData.apply)(SignInData.unapply)
  }
}