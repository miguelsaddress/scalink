package dal

import javax.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.util.{ Try, Success, Failure }
import models.User
import scala.concurrent.{ Future, ExecutionContext }
import org.postgresql.util.PSQLException

import play.api.Logger


/**
 * A repository for users.
 *
 */
@Singleton
class UserRepository @Inject() (val dbConfig: DatabaseConfig[JdbcProfile], val tables: Tables)(implicit ec: ExecutionContext) {
  
  import dbConfig.profile.api._
  val db = dbConfig.db

  val users = tables.users

  def add(user:User): Future[Option[User]] = db.run {
    val insertReturningUserWithIdQuery = 
      users returning users.map(_.id) into ((user,id) => user.copy(id=id))
      (insertReturningUserWithIdQuery += user).asTry 
    } map { res => 
      res match {
        case Success(user) => Some(user)
        case Failure(_) => None
      }
    } 

  def findByEmail(email: String): Future[Option[User]] = db.run {
    users.filter(_.email === email.toLowerCase).result.headOption
  }

  def findByUsername(username: String): Future[Option[User]] = db.run {
    users.filter(_.username === username.toLowerCase).result.headOption
  }

  /**
   * List all the users in the database.
   */
  def list(): Future[Seq[User]] = db.run {
    users.sortBy(_.id.asc).result
  }
}
