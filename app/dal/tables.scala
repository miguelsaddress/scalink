package dal

import javax.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import models.User
import scala.concurrent.{ Future, ExecutionContext }


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


    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the User object.
     *
     * In this case, we are simply passing the User parameters to the User case classes
     * apply and unapply methods.
     */
    def * = (name, username, email, password, id) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[UsersTable]
}
