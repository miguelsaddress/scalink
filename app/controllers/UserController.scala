package controllers

import javax.inject._

import business.UserManagement

import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import business.adt.User.Failures._

import org.webjars.play.WebJarsUtil

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
      case UnknownSignUpFailure => "error.signUp.unkown"
    }
  }

}