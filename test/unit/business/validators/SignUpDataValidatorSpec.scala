import org.scalatestplus.play._
import play.api.test.Helpers._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.util.Success
import org.scalatest.mockito.MockitoSugar

import auth.actions.AuthFailures.{ PasswordMissmatch, InvalidUsername }
import business.UserManagement
import business.adt.User.SignUpData
import business.validators.SignUpDataValidator

class SignUpDataValidatorSpec extends FlatSpec with Matchers with MockitoSugar {

  val userManagementMock = mock[UserManagement]
  val validator = SignUpDataValidator(userManagementMock)

  "A SignUpDataValidator" must "return NONE when there are no spaces in the username and the password matches the password confirmation" in {
    val data = SignUpData("Name", "username", "email@example.com", "password", "password")
    val error = validator.validate(data)
    
    error.isDefined === false
    error === None
  }


  "A SignUpDataValidator" must "fail when there are spaces in the username" in {
    val data = SignUpData("Name", "user name", "email@example.com", "password", "password")
    val error = validator.validate(data)
    
    error.isDefined === true
    error.get === PasswordMissmatch
  }

  "A SignUpDataValidator" must "fail when there password does not match the password confirmations" in {
    val data = SignUpData("Name", "username", "email@example.com", "password1", "password2")
    val error = validator.validate(data)
    
    error.isDefined === true
    error.get === InvalidUsername
  }
}
