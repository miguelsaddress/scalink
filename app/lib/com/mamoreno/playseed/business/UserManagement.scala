package com.mamoreno.playseed.business

import scala.concurrent.{ExecutionContext, Future}

import javax.inject._
import play.api.data.Form
import play.api.data.Forms._

import com.mamoreno.playseed.auth.actions.AuthFailures._
import com.mamoreno.playseed.dal.UserRepository
import com.mamoreno.playseed.forms
import com.mamoreno.playseed.forms.{ SignUpData, SignInData }
import com.mamoreno.playseed.forms.validators.SignUpDataValidator
import com.mamoreno.playseed.models.User
import com.mamoreno.playseed.util.{ PasswordExtensions => Password }
import play.api.Logger

@Singleton
class UserManagement @Inject() (userRepository: UserRepository) {
    lazy val signUpForm: Form[SignUpData] = forms.User.signUpForm
    lazy val signInForm: Form[SignInData] = forms.User.signInForm
    lazy val signUpDataValidator = SignUpDataValidator(this)

    def signUp(data: SignUpData)(implicit ec: ExecutionContext): Future[Either[SignUpFailure, Future[Option[User]]]] = {

      signUpDataValidator.validate(data).map { seq =>
        val errors = seq.flatten

        errors match {
          case failure::Nil => Left(failure)
          case hd::fls => Left(hd) // just the head
          case Nil => {
            val user = User(data.name, data.username, data.email, Password(data.password).hash)
            Right(userRepository.add(user))
          }
        }
      }
    }

    def signIn(data: SignInData)(implicit ec: ExecutionContext): Future[Option[User]] = {
      userRepository.findByEmail(data.email) map { mayBeUser => 
        mayBeUser match {
          case Some(u) if (Password(data.password).matches(u.password)) => Some(u)
          case _ => None
        }
      }
    }

    def findByUsername(mayBeUsername: Option[String]): Future[Option[User]] = {
      mayBeUsername match {
        case Some(username) => userRepository.findByUsername(username)
        case _ => Future.successful(None)
      }
    }

    def findByUsername(username: String): Future[Option[User]] = {
      userRepository.findByUsername(username)
    }

    def findByEmail(email: String): Future[Option[User]] = {
      userRepository.findByEmail(email)
    }

    def fullList()(implicit ec: ExecutionContext): Future[Seq[User]] = {
      userRepository.list()
    }
}
