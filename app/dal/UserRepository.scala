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

  import UserRepository.Failures._

  val users = tables.users

  def add(user:User): Future[Either[UserRepositoryFailure, User]] = db.run {
    val insertReturningUserWithIdQuery = 
      users returning users.map(_.id) into ((user,id) => user.copy(id=id))
      (insertReturningUserWithIdQuery += user).asTry 
    } map { res => 
      res match {
        case Success(user) => Right(user)
        case Failure(e: Exception) => {
          val msg = e.getMessage()
          msg match {
            case _ if msg.contains("idx_unique_username") => Left(UsernameTaken)
            case _ if msg.contains("idx_unique_email") => Left(EmailTaken)
            case _ => Left(DBFailure)
          }
        }
        case Failure(_) => Left(DBFailure)
      }
    } 

  def findByEmail(email: String): Future[Option[User]] = db.run {
    users.filter(_.email === email).result.headOption
  }

  def findByUsername(username: String): Future[Option[User]] = db.run {
    users.filter(_.username === username).result.headOption
  }

  /**
   * List all the users in the database.
   */
  def list(): Future[Seq[User]] = db.run {
    users.result
  }

}

object UserRepository {
  object Failures {
    sealed trait UserRepositoryFailure
    object DBFailure extends UserRepositoryFailure
    object UsernameTaken extends UserRepositoryFailure  
    object EmailTaken extends UserRepositoryFailure
  }
}
