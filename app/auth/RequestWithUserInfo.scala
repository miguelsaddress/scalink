package auth

import play.api.mvc.{Request, WrappedRequest}
import business.adt.User.UserInfo

case class RequestWithUserInfo[A](val user: UserInfo, request: Request[A]) extends WrappedRequest[A](request)

// class RequestWithOptionalUser[A](val maybeUser: Option[User], request: Request[A]) extends WrappedRequest[A](request) {}
