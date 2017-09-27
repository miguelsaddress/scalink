package controllers

import javax.inject._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}
import org.webjars.play.WebJarsUtil

import auth.AuthorizationHandler
import auth.actions.AuthActions
import business.UserManagement
import auth.actions.AuthFailures.WrongCredentials
import business.adt.User.SignInData

class AuthController @Inject()(
  users: UserManagement,
  authActions: AuthActions,
  cc: ControllerComponents,
  config: play.api.Configuration,
  authHandler: AuthorizationHandler
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(cc) with I18nSupport {

  val signInForm = users.signInForm
  val isSignUpEnabled = config.get[Boolean]("auth.signUp.enabled")

  /**
   * GET signup view.
   */
  def signIn = authActions.NoUserAction { implicit request =>
    Ok(views.html.user.signin(signInForm, isSignUpEnabled))
  }

  def signOut = Action { authHandler.onSuccessfulSignOut }
  
  /**
   * The add user action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on UserManagement layerº.
   */
  def validateCredentials = Action.async { implicit request =>
    def onFormError(errorForm: Form[SignInData]) = Future.successful {
      Ok(views.html.user.signin(errorForm, isSignUpEnabled))
    }

    def onFormSuccess(signInData: SignInData) = {
      users.signIn(signInData) map { maybeUSer => 
        maybeUSer match {
          case Some(user) => authHandler.onSuccessfulSignIn(user)
          case None => authHandler.onFailedSignIn(Messages(WrongCredentials.translationKey))
        }
      }
    }
    signInForm.bindFromRequest.fold(onFormError, onFormSuccess)
  }
}