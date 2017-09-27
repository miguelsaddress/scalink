package auth

import play.api.mvc._
import scala.concurrent.Future
import models.User

trait AuthorizationHandler {
  def onMustBeSignedIn(): Result
  def onMustBeGuest(): Result
  def userFromRequest[A](request: Request[A]): Future[Option[User]]
  def onSuccessfulSignUp(user: User): Result
  def onFailedSignUp(msg: String): Result
  def onSuccessfulSignIn(user: User): Result
  def onFailedSignIn(msg: String): Result
  def onSuccessfulSignOut(): Result
}
