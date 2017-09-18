import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Application

import dal.UserRepository
import models.User
import business.UserManagement
import business.adt.{ SignUpData, SignInData }
import util.{ PasswordExtensions => Password }

import play.api.Logger

class UserManagementSpec extends InvolvesDBSpecification {

  def users(implicit app: Application) = Application.instanceCache[UserManagement].apply(app)
  
  "Signup " should {

    val signUpDataOk = SignUpData(name = "Miguel", username = "mamoreno", email = "miguel@example.com", password = "A password")
    val signUpDataEmailTaken = SignUpData(name = "Miguel", username = "mamoreno+1", email = "miguel@example.com", password = "A password")
    val signUpDataUsernameTaken = SignUpData(name = "Miguel", username = "mamoreno", email = "miguel+2@example.com", password = "A password")

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

  "signIn" should {
    val signUpDataOk = SignUpData(name = "Miguel", username = "signInOk", email = "signInOk@example.com", password = "A password")
    
    "Successfully signIn for existing email and password" in new WithApplication() {
      val signInDataOk = SignInData(email = "signInOk@example.com", password = "A password")
      // create the user
      await(users.signUp(signUpDataOk)) !== None
      
      //sign it in
      await(users.signIn(signInDataOk)) match {
        case Some(user) => {
          user.name === signUpDataOk.name
          user.username === signUpDataOk.username
          user.email === signUpDataOk.email
          user.password !== signUpDataOk.email
        }
        case None => {
          // force failure if we reach here
          true === false
        }
      }
    }

    "Return None when trying a signIn for a NON existing email and password" in new WithApplication() {
      val signInDataOk = SignInData(email = "I_Dont_Exist@example.com", password = "A password")

      //sign it in and see it fail
      await(users.signIn(signInDataOk)) shouldEqual None
    }

    "Return None when trying a signIn for a existing email and WRONG password" in new WithApplication() {
      val signInDataOk = SignInData(email = "signInOk@example.com", password = "a WRONG password")

      // create the user
      await(users.signUp(signUpDataOk)) !== None

      //sign it in and see it fail
      await(users.signIn(signInDataOk)) shouldEqual None
    }
  }
}
