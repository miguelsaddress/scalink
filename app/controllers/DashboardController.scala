package controllers

import javax.inject._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import org.webjars.play.WebJarsUtil

import scala.concurrent.ExecutionContext

import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.auth._
import com.mamoreno.playseed.auth.actions.AuthActions

import play.Logger

class DashboardController @Inject()(
  users: UserManagement,
  authActions: AuthActions,
  cc: ControllerComponents
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(cc) with I18nSupport {

  def index = authActions.MustHaveUserAction.async { implicit request => 
    users.fullList.map { users => 
      Ok(views.html.dashboard.index(request.user, users))
    }
  }
}