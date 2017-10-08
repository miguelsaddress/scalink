package business

import scala.concurrent.{ExecutionContext, Future}

import javax.inject._
import play.api.data.Form
import play.api.data.Forms._

import business.adt.User.{ SignUpData, SignInData }
import business.validators.SignUpDataValidator
import auth.actions.AuthFailures._
import dal.UserRepository
import dal.UserRepository.Failures.UserRepositoryFailure
import dal.UserRepository.Failures.{
  EmailTaken => EmailTakenRepoFailure, 
  UsernameTaken => UsernameTakenRepoFailure
}
import models.User
import util.{ PasswordExtensions => Password }
import play.api.Logger

@Singleton
class UserManagement @Inject() (userRepository: UserRepository) {
    lazy val signUpForm: Form[SignUpData] = business.forms.User.signUpForm
    lazy val signInForm: Form[SignInData] = business.forms.User.signInForm
    lazy val signUpDataValidator = SignUpDataValidator(this)

    def signUp(data: SignUpData)(implicit ec: ExecutionContext): Future[Either[SignUpFailure, User]] = {

      signUpDataValidator.validate(data) match {
        case Some(failure) => Future.successful { Left(failure) }
        case None => {
          val user = User(data.name, data.username, data.email, Password(data.password).hash)
          userRepository.add(user) map (handleUserAddition)
        }
      }
    }

    private def handleUserAddition(eitherUserOrFailure: Either[UserRepositoryFailure, User]) = eitherUserOrFailure match {
      case Right(user) => Right(user)
      case Left(failure) => failure match {
        case EmailTakenRepoFailure => Left(EmailTaken)
        case UsernameTakenRepoFailure => Left(UsernameTaken)
        case _ => Left(UnknownSignUpFailure)
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
