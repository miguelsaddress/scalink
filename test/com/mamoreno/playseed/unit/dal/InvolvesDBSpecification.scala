import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import org.specs2.specification.BeforeAfterAll
import org.specs2.concurrent.ExecutionEnv

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.dal.UserRepository
import com.mamoreno.playseed.dal.Tables

/**
 * @see example https://github.com/knoldus/activator-play-slick-app/blob/master/src/main/g8/test/repo/EmployeeRepositorySpec.scala
 * @see https://github.com/playframework/playframework/blob/master/framework/src/play-specs2/src/main/scala/play/api/test/PlaySpecification.scala
 */

 // https://stackoverflow.com/q/41386320/1993756
trait InvolvesDBSpecification extends PlaySpecification with BeforeAfterAll {

  def await[T](v: Future[T]): T = Await.result(v, Duration.Inf)

  lazy val appBuilder = new GuiceApplicationBuilder()
  lazy val injector = appBuilder.injector()
  val tables = injector.instanceOf[Tables]

  //idea: import tables and make the schema creation at once
  // db.run(
  //   (tables.users.schema ++ tables.something.schema).create
  // )
  // 
  // instead of having the schema functions in the repository



  override def beforeAll() = {
    println("Running all the evolutions of default DB")
    await(tables.createSchema)
  }

  override def afterAll() = {
    println("Cleaning all the evolutions of default DB")
    await(tables.dropSchema)
  }

  def userRepository(implicit app: Application) = {
    Application.instanceCache[UserRepository].apply(app)
  }

  def userManagement(implicit app: Application) = {
    Application.instanceCache[UserManagement].apply(app)
  }

}