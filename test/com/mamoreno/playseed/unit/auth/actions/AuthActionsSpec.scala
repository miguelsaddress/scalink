import org.scalatestplus.play._

import akka.stream.Materializer

import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import play.api.Application
import play.api.test.FakeRequest
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ Future}
import scala.concurrent.ExecutionContext.Implicits.global

import com.mamoreno.playseed.models.User
import com.mamoreno.playseed.auth.AuthorizationHandler
import com.mamoreno.playseed.auth.RequestWithUserInfo
import com.mamoreno.playseed.auth.actions.AuthActions
import com.mamoreno.playseed.auth.Roles._
import com.mamoreno.playseed.business.UserManagement

import play.api.Logger

class AuthActionsSpec extends InvolvesDBSpecification {
  
  val USERNAME = "the_username"
  val USER = User("Name", USERNAME, "email@example.com", "password")

  val ADMIN_USERNAME = "the_admin_username"
  val ADMIN_USER = User("Admin", ADMIN_USERNAME, "admin_email@example.com", "password", AdminRole)

  def authActions(implicit app: Application) = Application.instanceCache[AuthActions].apply(app)

  "A MustHaveUserAction action" should {
    "return Ok when session contains a valid username with role UserRole" in new WithApplication() {
      await(userRepository.add(USER))
      val action = authActions.MustHaveUserAction { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/dashboard").withSession("username" -> USERNAME)
      val result = call(action, request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual USERNAME
    }

    "return Ok when session contains a valid username with role AdminRole" in new WithApplication() {
      await(userRepository.add(ADMIN_USER))
      val action = authActions.MustHaveUserAction { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/dashboard").withSession("username" -> ADMIN_USERNAME)
      val result = call(action, request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual ADMIN_USERNAME
    }
  
    "Redirect to /signIn route when session does not contain a valid username" in new WithApplication() {
      val action = authActions.MustHaveUserAction { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/dashboard").withSession("username" -> "invalid")
      val result = call(action, request)

      status(result) mustEqual 303 // Redirect
      header("location", result) mustEqual Some("/signIn")
    }

    "Redirect to /signIn route when session does not contain a username" in new WithApplication() {

      val action = authActions.MustHaveUserAction { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/dashboard")
      val result = call(action, request)

      status(result) mustEqual 303 // Redirect
      header("location", result) mustEqual Some("/signIn")
    }
  }

  "A NoUserAction action" should {
    "Reach endpoint when session does not contain a username" in new WithApplication() {
      val action = authActions.NoUserAction { request =>
        Ok("reached")
      }

      val request = FakeRequest(GET, "/signIn")
      val result = call(action, request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual "reached"
    }

    "Redirect to /dashboard when session contains a username" in new WithApplication() {
      await(userRepository.add(USER))
      val action = authActions.NoUserAction { request =>
        Ok("reached")
      }

      val request = FakeRequest(GET, "/signIn").withSession("username" -> USERNAME)
      val result = call(action, request)

      status(result) mustEqual 303 // Redirect
      header("location", result) mustEqual Some("/dashboard")
    }
  }

  "A UserWithRoleAction action" should {
    "return Ok when session contains a user with the requested Role" in new WithApplication() {
      await(userRepository.add(ADMIN_USER))

      val action = authActions.UserWithRoleAction(AdminRole) { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/").withSession("username" -> ADMIN_USERNAME)
      val result = call(action, request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual ADMIN_USERNAME
    }

    "return Ok when session does not contain a user with the requested Role but an Admin" in new WithApplication() {
      await(userRepository.add(USER))
      await(userRepository.add(ADMIN_USER))

      val action = authActions.UserWithRoleAction(UserRole) { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/").withSession("username" -> ADMIN_USERNAME)
      val result = call(action, request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual ADMIN_USERNAME
    }

    "return Forbidden when session contains a user without the requested Role" in new WithApplication() {
      await(userRepository.add(USER))

      val action = authActions.UserWithRoleAction(AdminRole) { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/").withSession("username" -> USERNAME)
      val result = call(action, request)

      status(result) mustEqual FORBIDDEN
    }

  }
}