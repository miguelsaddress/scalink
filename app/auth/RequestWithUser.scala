package auth

import play.api.mvc.{Request, WrappedRequest}
import models.User

case class RequestWithUser[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

// class RequestWithOptionalUser[A](val maybeUser: Option[User], request: Request[A]) extends WrappedRequest[A](request) {}
