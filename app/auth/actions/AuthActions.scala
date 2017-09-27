package auth.actions

import javax.inject._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import auth.{ AuthorizationHandler, RequestWithUser }

class AuthActions @Inject() (iab: IdentityActionBuilder, uac: UserActionConstructs)(implicit ec: ExecutionContext) {
  def MustHaveUserAction = (iab andThen uac.WithUserActionRefiner)
  def NoUserAction = (iab andThen uac.WithoutUserActionFilter)
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
  
  def WithUserActionRefiner(implicit ec: ExecutionContext) = new ActionRefiner[Request, RequestWithUser] {
    def executionContext = ec
    def refine[A](request: Request[A]) = {
      authorizationHandler.userFromRequest(request).map { maybeUser =>
        maybeUser.map { user =>
          Right(RequestWithUser(user, request))
        }.getOrElse {
          Left(authorizationHandler.onMustBeSignedIn)
        }
      }
    }
  } //WithUserActionRefiner

  def WithoutUserActionFilter(implicit ec: ExecutionContext) = new ActionFilter[Request] {
    def executionContext = ec
    def filter[A](request: Request[A]) = {
      authorizationHandler.userFromRequest(request).map { maybeUser =>
        maybeUser.map { user =>
          Some(authorizationHandler.onMustBeGuest)
        }.getOrElse {
          None
        }
      }
    }
  } //WithoutUserActionFilter
} //UserActionConstructs
