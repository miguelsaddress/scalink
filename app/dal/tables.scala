package dal

import javax.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

import models.User

/**
 * A repository for users.
 *
 */
@Singleton
class Tables @Inject() (val dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {
  
  import dbConfig.profile.api._
  val db = dbConfig.db

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")
    def username = column[String]("username")
    def email = column[String]("email")
    def password = column[String]("password")

    def idx_unique_email = index("idx_unique_email", (email), unique = true)
    def idx_unique_username = index("idx_unique_username", (username), unique = true)

    def * = (name, username, email, password, id) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[UsersTable]

  def createSchema() = db.run(
    users.schema.create
  )

  def dropSchema() = db.run (
    users.schema.drop
  )
  
}
