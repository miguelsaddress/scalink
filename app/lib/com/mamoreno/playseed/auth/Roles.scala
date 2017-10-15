package com.mamoreno.playseed.auth

import play.api.libs.json._

object Roles {
  sealed trait AuthRole
  case object AdminRole extends AuthRole
  case object UserRole extends AuthRole
}


