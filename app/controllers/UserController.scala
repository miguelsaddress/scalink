package controllers

import javax.inject._
import org.webjars.play.WebJarsUtil

import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import business.adt.User.Failures._
import business.UserManagement

import play.api.Logger

class UserController @Inject()(
  users: UserManagement,
  cc: ControllerComponents
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(cc) with I18nSupport {

  val signUpForm = users.signUpForm

  /**
   * GET signup view.
   */
  def signUp = Action { implicit request =>
    Ok(views.html.user.signup(signUpForm))
  }

  /**
   * The add user action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on UserManagement layerº.
   */
  def addUser = Action.async { implicit request =>
    signUpForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.user.signup(errorForm)))
      },
      signUpData => {
        Logger.debug(signUpData.password)
        Logger.debug(signUpData.passwordConf)

        if (signUpData.password != signUpData.passwordConf) {
          val errorMessage = Messages("error.signUp.passwordMissmatch")
          Redirect(routes.UserController.signUp).flashing("error" -> errorMessage)
        } 

        users.signUp(signUpData) map { _ match {
          case Right(user) => Redirect(routes.DashboardController.index)
          case Left(error) => {
            val errorMessage = Messages(addUserErrorToMessage(error))
            Redirect(routes.UserController.signUp).flashing("error" -> errorMessage)
          }
        }}
      }
    )
  }

  private def addUserErrorToMessage(error: SignUpFailure): String = {
    error match {
      case EmailTaken => "error.signUp.emailTaken"
      case UsernameTaken => "error.signUp.usernameTaken"
      case PasswordMissmatch => "error.signUp.passwordMissmatch"
      case UnknownSignUpFailure => "error.signUp.unkown"
    }
  }

}