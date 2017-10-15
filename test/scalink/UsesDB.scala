import play.api.test.Helpers._
import play.api.test.{WithApplication, PlaySpecification}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import org.specs2.specification.BeforeAfterAll
//import org.specs2.concurrent.ExecutionEnv

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

import com.mamoreno.playseed.dal.UserRepository
import dal.Tables
import dal.repositories.LinkRepository

trait UsesDB extends PlaySpecification with BeforeAfterAll {

  def await[T](v: Future[T]): T = Await.result(v, Duration.Inf)

  lazy val appBuilder = new GuiceApplicationBuilder()
  lazy val injector = appBuilder.injector()
  val tables = injector.instanceOf[Tables]

  import tables.db
  import tables.dbApi._

  def seed() = db.run {
    (tables.users ++= Seeds.users) andThen
    (tables.links ++= Seeds.links)
  }
    
  override def beforeAll() = {
    println("Running all the evolutions of default DB")
    await(tables.createSchema)
    await(seed)
  }

  override def afterAll() = {
    println("Cleaning all the evolutions of default DB")
    await(tables.dropSchema)
  }

  def userRepository(implicit app: Application) = {
    Application.instanceCache[UserRepository].apply(app)
  }

  def linkRepository(implicit app: Application) = {
    Application.instanceCache[LinkRepository].apply(app)
  }

}