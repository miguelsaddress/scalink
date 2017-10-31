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

  def addMany(linkSeq: Seq[Link]): Future[Try[Option[Int]]] = db.run {
    (links ++= linkSeq) asTry
  }

  def deleteForUserById(uid: Long, id: Long): Future[Option[Link]] = db.run {
    val findForUserByIdAction = links
      .filter(_.id === id)
      .filter(_.userId === uid)

    for {
      link <- findForUserByIdAction.result
      _    <- findForUserByIdAction.delete
    } yield {
      link.headOption
    }
  }

  def findForUserById(uid: Long, id: Long): Future[Option[Link]] = db.run {
    links
      .filter(_.id === id)
      .filter(_.userId === uid)
      .result.headOption
  }

  def getAllForUserWithId(uid: Long): Future[Seq[Link]] = db.run {
    links.filter(_.userId === uid).result
  }

  def linksCountForUser(uid: Long) = db.run {
    links.filter(_.userId === uid).length.result
  }
}
