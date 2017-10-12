package com.mamoreno.playseed.dal
import com.mamoreno.playseed.auth.Roles._

object ColumnMappingFunctions {
  implicit def role2Int(role: AuthRole) = role match {
    case AdminRole => 0
    case UserRole  => 1
  }

  implicit def int2Role(i: Int) = i match {
    case 0 => AdminRole
    case 1 => UserRole
  }
}