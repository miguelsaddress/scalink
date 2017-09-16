import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
//import play.api.db.DBApi
import play.api.inject.guice.GuiceApplicationBuilder


import org.specs2.specification.BeforeAfterAll
import org.specs2.concurrent.ExecutionEnv


import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

/**
 * @see example https://github.com/knoldus/activator-play-slick-app/blob/master/src/main/g8/test/repo/EmployeeRepositorySpec.scala
 */

 // https://stackoverflow.com/q/41386320/1993756
trait RepositorySpec extends PlaySpecification with BeforeAfterAll {

  def await[T](v: Future[T]): T = Await.result(v, Duration.Inf)

  lazy val appBuilder = new GuiceApplicationBuilder()
  lazy val injector = appBuilder.injector()
  val users = injector.instanceOf[dal.UserRepository]

  override def beforeAll() = {
    await(users.createSchema)
    println("Running all the evolutions of default DB")
  }

  override def afterAll() = {
    await(users.dropSchema)
    println("Cleaning all the evolutions of default DB")
  }
}