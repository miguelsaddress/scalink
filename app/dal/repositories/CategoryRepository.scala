package dal.repositories

import javax.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

import dal.Tables
import model.Category
import play.api.Logger

@Singleton
class CategoryRepository @Inject() (val dbConfig: DatabaseConfig[JdbcProfile], val tables: Tables)(implicit ec: ExecutionContext) {
 
  import dbConfig.profile.api._
  val db = dbConfig.db

  val categories = tables.categories

  def add(category:Category): Future[Category] = db.run {
    val insertReturningCategoryWithIdQuery = 
      categories returning categories.map(_.id) into ((category,id) => category.copy(id=id))
      (insertReturningCategoryWithIdQuery += category)
  }

  def list(): Future[Seq[Category]] = db.run {
    categories.result
  }

}
