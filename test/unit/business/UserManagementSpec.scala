import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Application

import dal.UserRepository
import models.User
import business.UserManagement
import business.adt.SignUpData

import play.api.Logger

class UserManagementSpec extends RepositorySpec {

  //def usersRepo(implicit app: Application) = Application.instanceCache[UserRepository].apply(app)


  val signUpData = SignUpData(
    name = "Miguel", 
    username = "mamoreno", 
    email = "miguel@example.com", 
    password = "A password")
    
    "blablabla" in new WithApplication() {
        def users(implicit app: Application) = Application.instanceCache[UserManagement].apply(app)

      await(users.signUp(signUpData)) match {
          case Right(u) => Logger.debug(u.toString())
          case Left(msg:String) => Logger.debug(msg)
          case _ => println()
        }
    }

}
