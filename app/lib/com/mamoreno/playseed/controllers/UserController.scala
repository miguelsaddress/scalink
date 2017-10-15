package com.mamoreno.playseed.controllers

import javax.inject._
import org.webjars.play.WebJarsUtil

import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{ Try, Success, Failure }

import controllers.AssetsFinder
import com.mamoreno.playseed.auth.AuthorizationHandler
import com.mamoreno.playseed.auth.actions.AuthActions
import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.auth.actions.AuthFailures._
import com.mamoreno.playseed.forms.SignUpData

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
      Ok(lib.com.mamoreno.playseed.views.html.user.signup(signUpForm))
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
    def onFormSuccess(signUpData: SignUpData) = Future.successful {
      users.signUp(signUpData) flatMap { eitherUserOrError =>
        eitherUserOrError match {
          case Right(futureMaybeUser) => { futureMaybeUser map { maybeUser =>
            maybeUser match {
              case Some(user) => authHandler.onSuccessfulSignUp(user)
              case None => authHandler.onFailedSignUp(Messages(UnknownSignUpFailure.translationKey))
            }
          }}
          case Left(error) => Future.successful{ authHandler.onFailedSignUp(Messages(error.translationKey)) }
        }
      }
    }.flatten // Future[Future[Result]] .... :(

    def onFormError(errorForm: Form[SignUpData]) = Future.successful {
      BadRequest(lib.com.mamoreno.playseed.views.html.user.signup(errorForm))
    }

    signUpForm.bindFromRequest.fold(onFormError, onFormSuccess)
  }
}