package controllers

import javax.inject._
import org.webjars.play.WebJarsUtil

import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

import auth.AuthorizationHandler
import auth.actions.AuthActions
import business.UserManagement
import auth.actions.AuthFailures._
import business.adt.User.SignUpData

class UserController @Inject()(
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

  val signUpForm = users.signUpForm
  val isSignUpEnabled = config.get[Boolean]("auth.signUp.enabled")

  /**
   * GET signup view.
   */
  def signUp = authActions.NoUserAction { implicit request =>
    if (isSignUpEnabled) {
      Ok(views.html.user.signup(signUpForm))
    } else {
      NotFound
    }
  }

  /**
   * The add user action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on UserManagement layer.
   */
  def addUser = authActions.NoUserAction.async { implicit request =>

    /**
     * TODO: Investigate why it returns an Object and I need the map
     */

    def onFormSuccess(signUpData: SignUpData) = {
      users.signUp(signUpData) map { eitherUserOrError =>
        eitherUserOrError match {
          case Right(futureMaybeUser) => { futureMaybeUser map { maybeUser =>
            maybeUser match {
              case Some(user) => authHandler.onSuccessfulSignUp(user)
              case None => authHandler.onFailedSignUp(Messages(UnknownSignUpFailure.translationKey))
            }
          }}
          case Left(error) => authHandler.onFailedSignUp(Messages(error.translationKey))
        }
      } map { 
        _: Object => authHandler.onFailedSignUp(Messages(UnknownSignUpFailure.translationKey))
      }
    }

    def onFormError(errorForm: Form[SignUpData]) = Future.successful {
      BadRequest(views.html.user.signup(errorForm))
    }

    signUpForm.bindFromRequest.fold(onFormError, onFormSuccess)
  }
}