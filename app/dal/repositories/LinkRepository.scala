package dal.repositories

import javax.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

import dal.Tables
import model.Link
import play.api.Logger

import scala.util.Try

@Singleton
class LinkRepository @Inject() (val dbConfig: DatabaseConfig[JdbcProfile], val tables: Tables)(implicit ec: ExecutionContext) {
  import dbConfig.profile.api._
  val db = dbConfig.db

  val links = tables.links

  def add(link:Link): Future[Try[Link]] = db.run {
    val insertReturningLinkWithIdQuery = 
      links returning links.map(_.id) into ((link,id) => link.copy(id=id))
      (insertReturningLinkWithIdQuery += link) asTry
  }

  def list(): Future[Seq[Link]] = db.run {
    links.result
  }
}
