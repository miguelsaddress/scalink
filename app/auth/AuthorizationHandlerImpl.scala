package auth

import javax.inject._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.ExecutionContext

import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.models.User
import com.mamoreno.playseed.auth.{AuthorizationHandlerImpl => SeedAuthorizationHandlerImpl}

class AuthorizationHandlerImpl @Inject()(users: UserManagement)(implicit ec: ExecutionContext) extends SeedAuthorizationHandlerImpl(users) {
  override def onMustBeGuest(): Result = Redirect(controllers.routes.DashboardController.index)

  override def onSuccessfulSignUp(user: User): Result = {
    Redirect(controllers.routes.DashboardController.index)
    .withSession("username" -> user.username)
  }
  
  override def onSuccessfulSignIn(user: User): Result = {
    Redirect(controllers.routes.DashboardController.index)
    .withSession("username" -> user.username)
  }
}
