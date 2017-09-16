package business

import scala.concurrent.{ExecutionContext, Future}

import javax.inject._
import play.api.data.Form
import play.api.data.Forms._

import dal.UserRepository
import dal.UserRepository.Failures._
import models.User
import business.adt.SignUpData
import util.{ PasswordExtensions => Password }
import play.api.Logger

@Singleton
class UserManagement @Inject() (userRepository: UserRepository) {
    lazy val signUpForm: Form[SignUpData] = Form {
      mapping(
        "name" -> nonEmptyText,
        "username" -> nonEmptyText,
        "email" -> nonEmptyText,
        "password" -> nonEmptyText,
      )(SignUpData.apply)(SignUpData.unapply)
    }

    def signUp(data: SignUpData)(implicit ec: ExecutionContext): Future[Either[String, User]] = {
        val user = User(data.name, data.username, data.email, Password(data.password).hash)
        userRepository.add(user) map { eitherUserOrFailure => eitherUserOrFailure match {
          case Right(user) => Right(user)
          case Left(failure) => failure match {
            case EmailTaken => Left("emailTaken")
            case UsernameTaken => Left("usernameTaken")
            case _ => Left("unknownError")
          } 
        }
      }
    }

    def fullList()(implicit ec: ExecutionContext): Future[Seq[User]] = {
        userRepository.list()
    }
}