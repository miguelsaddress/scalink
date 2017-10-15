package com.mamoreno.playseed.auth

import play.api.mvc.{Request, WrappedRequest}

import com.mamoreno.playseed.auth.Roles._

case class UserInfo(username: String, role: AuthRole)
case class RequestWithUserInfo[A](val user: UserInfo, request: Request[A]) extends WrappedRequest[A](request)
