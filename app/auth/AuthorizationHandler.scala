package auth

import play.api.mvc._
import scala.concurrent.Future
import models.User
import auth.Roles.AuthRole
import business.adt.User.UserInfo

trait AuthorizationHandler {
  def onMustBeSignedIn(): Result
  def onMustBeGuest(): Result
  def userFromRequest[A](request: Request[A]): Future[Option[User]]
  def userHasRole(user: UserInfo, role: AuthRole): Boolean
  def onRoleFailure(): Result
  def onSuccessfulSignUp(user: User): Result
  def onFailedSignUp(msg: String): Result
  def onSuccessfulSignIn(user: User): Result
  def onFailedSignIn(msg: String): Result
  def onSuccessfulSignOut(): Result
}
