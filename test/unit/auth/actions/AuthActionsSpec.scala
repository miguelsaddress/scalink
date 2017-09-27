import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers.any

import akka.stream.Materializer

import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import play.api.Application
import play.api.test.FakeRequest
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ Future}
import scala.concurrent.ExecutionContext.Implicits.global

import models.User
import auth.AuthorizationHandler
import auth.RequestWithUser
import auth.actions.AuthActions
import business.UserManagement

import play.api.Logger

class AuthActionsSpec extends MockitoSugar with InvolvesDBSpecification {

  def authActions(implicit app: Application) = Application.instanceCache[AuthActions].apply(app)
      val USERNAME = "TheUsername"
      val USER = User("Name", USERNAME, "email@example.com", "password")

  "A MustHaveUserAction action" should {
    "return Ok when session contains a valid username" in new WithApplication() {
      await(userRepository.add(USER))
      val action = authActions.MustHaveUserAction { request =>
        Ok(request.user.username)
      }

      val request = FakeRequest(GET, "/dashboard").withSession("username" -> USERNAME)
      val result = call(action, request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual USERNAME
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
}