import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Application

import dal.UserRepository
import models.User
import business.UserManagement
import business.adt.SignUpData
import util.{ PasswordExtensions => Password }

import play.api.Logger

class UserManagementSpec extends InvolvesDBSpecification {

  val signUpDataOk = SignUpData(name = "Miguel", username = "mamoreno", email = "miguel@example.com", password = "A password")
  val signUpDataEmailTaken = SignUpData(name = "Miguel", username = "mamoreno+1", email = "miguel@example.com", password = "A password")
  val signUpDataUsernameTaken = SignUpData(name = "Miguel", username = "mamoreno", email = "miguel+2@example.com", password = "A password")

  def users(implicit app: Application) = Application.instanceCache[UserManagement].apply(app)
    
  "Return newly created user after successfull signup" in new WithApplication() {

    await(users.signUp(signUpDataOk)) match {
      case Right(user) => {
        user.name === signUpDataOk.name
        user.username === signUpDataOk.username
        user.email === signUpDataOk.email
        user.password !== signUpDataOk.password
        Password(signUpDataOk.password).matches(user.password)
      }
      case Left(msg:String) => {
        //forcing test to fail if we reach here
        true === false
      }
    }
  }

  "Return a 'usernameTaken' when creating a user with an existing username" in new WithApplication() {

    // it should have been failed due to duplicated usernam
    await(users.signUp(signUpDataUsernameTaken)) match {
      case Right(user) => {
        //forcing test to fail if we reach here
        true === false
      }
      case Left(failure) => {
        failure shouldEqual "usernameTaken"
      }
    }
  }

  "Return a 'emailTaken' when creating a user with an existing email" in new WithApplication() {

    // it should have been failed due to duplicated email. I replace the usernam
    await(users.signUp(signUpDataEmailTaken)) match {
      case Right(user) => {
        //forcing test to fail if we reach here
        true === false
      }
      case Left(failure) => {
        failure shouldEqual "emailTaken"
      }
    }
  }
}
