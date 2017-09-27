package auth

import javax.inject._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import business.UserManagement
import models.User

class AuthorizationHandlerImpl @Inject()(users: UserManagement)(implicit ec: ExecutionContext) extends AuthorizationHandler {
  override def onMustBeSignedIn(): Result = Redirect(controllers.routes.AuthController.signIn)
  override def onMustBeGuest(): Result = Redirect(controllers.routes.DashboardController.index)
  override def userFromRequest[A](request: Request[A]): Future[Option[User]] = {
    users.findByUsername(request.session.get("username"))
  }
  override def onSuccessfulSignUp(user: User): Result = {
    Redirect(controllers.routes.DashboardController.index)
    .withSession("username" -> user.username)
  }
  override def onFailedSignUp(msg: String): Result = {
    Redirect(controllers.routes.UserController.signUp)
    .flashing("error" -> msg)
  }
  override def onSuccessfulSignIn(user: User): Result = {
    Redirect(controllers.routes.DashboardController.index)
    .withSession("username" -> user.username)
  }
  override def onFailedSignIn(msg: String): Result = {
    Redirect(controllers.routes.AuthController.signIn)
    .flashing("error" -> msg)
  }
  override def onSuccessfulSignOut(): Result = {
    Redirect(controllers.routes.AuthController.signIn)
    .withNewSession
  }
}
