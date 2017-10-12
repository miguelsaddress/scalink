package com.mamoreno.playseed.dal

import javax.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

import com.mamoreno.playseed.auth.Roles._
import com.mamoreno.playseed.models.User
import com.mamoreno.playseed.dal.ColumnMappingFunctions._

/**
 * A repository for users.
 *
 */
@Singleton
class Tables @Inject() (val dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {
  
  import dbConfig.profile.api._
  val db = dbConfig.db

  implicit val authRoleColumnType = MappedColumnType.base[AuthRole, Int](role2Int, int2Role)

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")
    def username = column[String]("username")
    def email = column[String]("email")
    def password = column[String]("password")
    def role = column[AuthRole]("role")

    def idx_unique_email = index("idx_unique_email", (email), unique = true)
    def idx_unique_username = index("idx_unique_username", (username), unique = true)

    def * = (name, username, email, password, role, id) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[UsersTable]

  def createSchema() = db.run {
    users.schema.create
  }

  def dropSchema() = db.run {
    users.schema.drop
  }
  
}
