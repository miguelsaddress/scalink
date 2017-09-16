package controllers

import javax.inject._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import org.webjars.play.WebJarsUtil

import scala.concurrent.{ExecutionContext}

import business.UserManagement

class DashboardController @Inject()(
  users: UserManagement,
  cc: ControllerComponents
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(cc) with I18nSupport {

  def index = Action.async { implicit request =>
    //it will be an endpoint for logged in users only
    users.fullList.map { users => 
        Ok(views.html.dashboard.index(users))
    }
  }
}