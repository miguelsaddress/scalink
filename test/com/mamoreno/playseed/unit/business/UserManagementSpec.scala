import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Application
import org.scalatest.EitherValues

import com.mamoreno.playseed.dal.UserRepository
import com.mamoreno.playseed.models.User
import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.forms.{ SignUpData, SignInData }
import com.mamoreno.playseed.util.{ PasswordExtensions => Password }
import com.mamoreno.playseed.auth.actions.AuthFailures._

import play.api.Logger

class UserManagementSpec extends InvolvesDBSpecification with EitherValues {

  //def userManagement(implicit app: Application) = Application.instanceCache[UserManagement].apply(app)
  val r = scala.util.Random

  "Signup " should {

    val signUpDataOk = SignUpData(name = "Miguel", username = "mamoreno", email = "miguel@example.com", password = "A password", passwordConf = "A password")
    val signUpDataEmailTaken = SignUpData(name = "Miguel", username = "mamoreno+1", email = "miguel@example.com", password = "A password", passwordConf = "A password")
    val signUpDataUsernameTaken = SignUpData(name = "Miguel", username = "mamoreno", email = "miguel+2@example.com", password = "A password", passwordConf = "A password")
    val signUpDataPaswordMismatch = SignUpData(name = "Miguel", username = "mamoreno+3", email = "miguel+3@example.com", password = "A password", passwordConf = "A password missmatch")
    val signUpDataUsernameWithSpaces = SignUpData(name = "Miguel", username = "mamo reno", email = "miguel+4@example.com", password = "A password", passwordConf = "A password")
    val signUpDataUsernameMustBeLowercased = SignUpData(name = "Miguel", username = "MaMoReNo4", email = "miguel+5@example.com", password = "A password", passwordConf = "A password")

    "Return newly created user after successfull signup" in new WithApplication() {
      val user = await(await(userManagement.signUp(signUpDataOk)).right.value).get

      user.name === signUpDataOk.name
      user.username === signUpDataOk.username
      user.email === signUpDataOk.email
      user.password !== signUpDataOk.password
      Password(signUpDataOk.password).matches(user.password) === true
    }

    "Return a 'usernameTaken' when creating a user with an existing username" in new WithApplication() {
      // it should have been failed due to duplicated username
      await(userManagement.signUp(signUpDataUsernameTaken)).left.value shouldEqual UsernameTaken
    }

    "Return a 'emailTaken' when creating a user with an existing email" in new WithApplication() {
      // it should have been failed due to duplicated email. I replace the usernam
      await(userManagement.signUp(signUpDataEmailTaken)).left.value shouldEqual EmailTaken
    }

    "Fail if password and passwordConf do not match" in new WithApplication() {
      val either = await(userManagement.signUp(signUpDataPaswordMismatch))
      either.left.value shouldEqual PasswordMissmatch
    }

    "Fail if username provided contains spaces" in new WithApplication() {
      val either = await(userManagement.signUp(signUpDataUsernameWithSpaces))
      either.left.value shouldEqual InvalidUsername
    }

    "Username will be lowercased during signup process" in new WithApplication() {
      val either = await(userManagement.signUp(signUpDataUsernameMustBeLowercased))
      await(either.right.value).get.username shouldEqual signUpDataUsernameMustBeLowercased.username.toLowerCase
    }
  }

  "SignIn" should {
    val signUpDataOk = SignUpData(
      name = "Miguel",
      username = "sign_in_ok",
      email = "sign_in_ok@example.com",
      password = "A password",
      passwordConf = "A password")
    
    "Successfully signIn for existing email and password" in new WithApplication() {
      val signInDataOk = SignInData(emailOrUsername = "sign_in_ok@example.com", password = "A password")
      // create the user
      await(userManagement.signUp(signUpDataOk)).right.value should not be None
      //sign it in
      val maybeUser = await(userManagement.signIn(signInDataOk))
      maybeUser.isDefined === true
     
      val user = maybeUser.get
      user.name === signUpDataOk.name
      user.username === signUpDataOk.username
      user.email === signUpDataOk.email
      user.password !== signUpDataOk.email
    }

    "Successfully signIn for existing username and password" in new WithApplication() {

      val signInDataOk = SignInData(emailOrUsername = signUpDataOk.username, password = signUpDataOk.password)
      // create the user in case it does not exist
      await(userManagement.signUp(signUpDataOk))

      //sign it in
      val maybeUser = await(userManagement.signIn(signInDataOk))
      maybeUser.isDefined === true
     
      val user = maybeUser.get
      user.name === signUpDataOk.name
      user.username === signUpDataOk.username
      user.email === signUpDataOk.email
      user.password !== signUpDataOk.email
    }

    "Return None when trying a signIn for a NON existing email and password" in new WithApplication() {
      val signInDataOk = SignInData(emailOrUsername = "I_Dont_Exist@example.com", password = "A password")
      //sign it in and see it fail
      await(userManagement.signIn(signInDataOk)) shouldEqual None
    }

    "Return None when trying a signIn for a existing email and WRONG password" in new WithApplication() {
      val signInDataOk = SignInData(emailOrUsername = "signInOk2@example.com", password = "a WRONG password")
      val signUpDataOk = SignUpData(name = "Miguel", username = "signInOk2", email = "signInOk2@example.com", password = "A password", passwordConf = "A password")
      // create the user
      await(userManagement.signUp(signUpDataOk)).right.value should not be None
      //sign it in and see it fail
      await(userManagement.signIn(signInDataOk)) shouldEqual None
    }
  }

  "findByEmail" should {
    "return the expected existing user" in new WithApplication() {
      val randomInt = r.nextInt(100)
      val username = s"mamoreno_${randomInt}"
      val email = s"miguel+${randomInt}@example.com"

      val signUpDataOk = SignUpData(
        name = "Miguel",
        username = username,
        email = email,
        password = "A password",
        passwordConf = "A password")

      await(userManagement.signUp(signUpDataOk))

      val maybeUser = await(userManagement.findByEmail(signUpDataOk.email))
      maybeUser.isDefined === true

      val user = maybeUser.get
      user.email === email
    }
  }

  "findByUsername" should {
    "return the expected existing user" in new WithApplication() {
      val randomInt = r.nextInt(100)
      val username = s"mamoreno_${randomInt}"
      val email = s"miguel+${randomInt}@example.com"

      val signUpDataOk = SignUpData(
        name = "Miguel",
        username = username,
        email = email,
        password = "A password",
        passwordConf = "A password")

      await(userManagement.signUp(signUpDataOk))

      val maybeUser = await(userManagement.findByUsername(signUpDataOk.username))
      maybeUser.isDefined === true

      val user = maybeUser.get
      user.username === username
    }
  }

}
