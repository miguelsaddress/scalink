package com.mamoreno.playseed.auth

import javax.inject._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import com.mamoreno.playseed.auth.Roles._
import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.models.User

abstract class AuthorizationHandlerImpl @Inject()(users: UserManagement)(implicit ec: ExecutionContext) extends AuthorizationHandler {
  override def onMustBeSignedIn(): Result = Redirect(com.mamoreno.playseed.controllers.routes.AuthController.signIn)
  override def userFromRequest[A](request: Request[A]): Future[Option[User]] = {
    users.findByUsername(request.session.get("username"))
  }

  override def userHasRole(user: UserInfo, role: AuthRole): Boolean = {
    // admin has access to everything
    user.role == role || user.role == AdminRole
  }

  def onRoleFailure(): Result = {
    Forbidden
  }

  override def onFailedSignUp(msg: String): Result = {
    Redirect(com.mamoreno.playseed.controllers.routes.UserController.signUp)
    .flashing("error" -> msg)
  }  
  
  override def onFailedSignIn(msg: String): Result = {
    Redirect(com.mamoreno.playseed.controllers.routes.AuthController.signIn)
    .flashing("error" -> msg)
  }
  
  override def onSuccessfulSignOut(): Result = {
    Redirect(com.mamoreno.playseed.controllers.routes.AuthController.signIn)
    .withNewSession
  }


  // override def onMustBeGuest(): Result = ???
  // override def onSuccessfulSignUp(user: User): Result = ???
  // override def onSuccessfulSignIn(user: User): Result = ???

}
