import org.scalatestplus.play._
import play.api.test.Helpers._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.util.Success

import com.mamoreno.playseed.models.User

class UserSpec extends FlatSpec with Matchers {
  "A User" must "lowercase the username" in {
    val u = User("Name", "UserName", "email@example.com", "password")
    u.username shouldEqual "username"
  }

  "A User" must "not accept spaces in the username" in {
    val thrown = intercept[java.lang.AssertionError] {
      User("Name", "User Name", "email@example.com", "password")
    }
    thrown.getMessage shouldEqual "assertion failed: username must not contain spaces"
  }
}
