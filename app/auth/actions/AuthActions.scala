package auth.actions

import javax.inject._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import auth.Roles._
import auth.{ AuthorizationHandler, RequestWithUserInfo }
import business.adt.User.UserInfo

class AuthActions @Inject() (iab: IdentityActionBuilder, uac: UserActionConstructs)(implicit ec: ExecutionContext) {
  def MustHaveUserAction = (iab andThen uac.WithUserActionRefiner)
  def NoUserAction = (iab andThen uac.WithoutUserActionFilter)
  def UserWithRoleAction(role: AuthRole) = (iab andThen uac.WithUserActionRefiner andThen uac.WithUserRoleActionFilter(role))
}


/**************************
 * Building Blocks Needed *
 **************************/


// When Calling the Action Refiners, it seems that we need an ActionBuilder first in the Chain
// https://stackoverflow.com/a/40955537/1993756
class IdentityActionBuilder @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext) 
extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    block(request)
  }
}

class UserActionConstructs @Inject() (authorizationHandler: AuthorizationHandler)(implicit ec: ExecutionContext) {
  
  def WithUserActionRefiner()(implicit ec: ExecutionContext) = new ActionRefiner[Request, RequestWithUserInfo] {
    def executionContext = ec
    def refine[A](request: Request[A]): Future[Either[Result, RequestWithUserInfo[A]]] = {
      authorizationHandler.userFromRequest(request).map { maybeUser =>
        maybeUser.map { user =>
          Right(RequestWithUserInfo(UserInfo(user.username, user.role), request))
        }.getOrElse {
          Left(authorizationHandler.onMustBeSignedIn)
        }
      }
    }
  } //WithUserActionRefiner

  def WithoutUserActionFilter()(implicit ec: ExecutionContext) = new ActionFilter[Request] {
    def executionContext = ec
    def filter[A](request: Request[A])/*: Future[Option[Result]]*/ = {
      authorizationHandler.userFromRequest(request).map { maybeUser =>
        maybeUser.map { user =>
          Some(authorizationHandler.onMustBeGuest)
        }.getOrElse {
          None
        }
      }
    }
  } //WithoutUserActionFilter
  
  def WithUserRoleActionFilter(role: AuthRole)(implicit ec: ExecutionContext) = new ActionRefiner[RequestWithUserInfo, RequestWithUserInfo] {
    def executionContext = ec
    override protected def refine[A](request: RequestWithUserInfo[A]): Future[Either[Result, RequestWithUserInfo[A]]] = Future.successful {
      val user = request.user
      if (authorizationHandler.userHasRole(user, role)) {
        Right(request)
      } else {
        Left(authorizationHandler.onRoleFailure)
      }
    }
  } //WithUserRoleActionFilter
} //UserActionConstructs
