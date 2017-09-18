package controllers

import javax.inject._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import org.webjars.play.WebJarsUtil

import business.UserManagement
import business.adt.SignUpData

import play.api.Logger

class AuthController @Inject()(
  users: UserManagement,
  cc: ControllerComponents
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(cc) with I18nSupport {

  val signInForm = users.signInForm

  /**
   * GET signup view.
   */
  def signIn = Action { implicit request =>
    Ok(views.html.user.signin(signInForm))
  }

  /**
   * The add user action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on UserManagement layerº.
   */
  def validateCredentials = Action.async { implicit request =>
    signInForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.user.signin(errorForm)))
      },
      signInData => {
        users.signIn(signInData) map { r => 
          r match {
          case Some(user) => Redirect(routes.DashboardController.index)
          case None => Redirect(routes.AuthController.signIn).flashing("error" -> Messages("forms.signIn.error"))
        }}
      }
    )
  }
}