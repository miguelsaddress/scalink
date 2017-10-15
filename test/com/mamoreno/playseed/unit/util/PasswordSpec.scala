import org.scalatestplus.play._
import play.api.test.Helpers._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.util.Success

import com.mamoreno.playseed.util.PasswordExtensions

class PasswordSpec extends FlatSpec with Matchers {
  "A hashed password" must "differ to original Password" in {
    val pswd = PasswordExtensions("My Cool Password")
    pswd.hash should not be "My Cool Password"
  }

  "A hashed password" must "be always hashed with the same result" in {
    val myPass = "My Cool Password"
    val password = PasswordExtensions(myPass)
    val itMatches = password matches (password.hash)

    itMatches shouldEqual (true)
  }
}
