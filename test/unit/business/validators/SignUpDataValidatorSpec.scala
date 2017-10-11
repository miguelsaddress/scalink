import org.scalatestplus.play._
import play.api.test.Helpers._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.util.Success
import org.scalatest.mockito.MockitoSugar
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.test.WithApplication

import auth.actions.AuthFailures.{ PasswordMissmatch, InvalidUsername }
import business.adt.User.SignUpData
import business.validators.SignUpDataValidator

import play.api.Logger

class SignUpDataValidatorSpec extends InvolvesDBSpecification with MockitoSugar {

  "A SignUpDataValidator" should {

    "return None when there are no spaces in the username and the password matches the password confirmation" in new WithApplication() {
      val validator = SignUpDataValidator(userManagement)

      val data = SignUpData("Name", "username", "email@example.com", "password", "password")
      val error = await(validator.validate(data)).headOption.flatten

      error.isDefined === false
      error equals None
    }

    "fail when there are spaces in the username" in new WithApplication() {
      val validator = SignUpDataValidator(userManagement)

      val data = SignUpData("Name", "user name", "email@example.com", "password", "password")
      val error = await(validator.validate(data)).headOption.flatten

      error.isDefined === true
      error.get === InvalidUsername
    }

    "fail when there password does not match the password confirmations" in new WithApplication() {
      val validator = SignUpDataValidator(userManagement)

      val data = SignUpData("Name", "username", "email@example.com", "password1", "password2")
      val error = await(validator.validate(data)).headOption.flatten
      Logger.debug(s"${error}")
      
      error.isDefined === true
      error.get === PasswordMissmatch
    }
  }
}
