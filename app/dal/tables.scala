package dal

import javax.inject.{ Inject, Singleton }
import java.sql.Timestamp
import java.time.LocalDateTime
import java.net.URL
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import com.mamoreno.playseed.dal.{ Tables => SeedTables }

import model.{ Link, AccessLog, Note, Category, CategoryLink }
import dal.ColumnMappingFunctions._

@Singleton
class Tables @Inject() (override val dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) 
extends SeedTables(dbConfig)(ec) {
  
  import dbConfig.profile.api._
  lazy val dbApi = dbConfig.profile.api

  implicit val urlColumnType = MappedColumnType.base[URL, String](url2string, string2url)
  implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
    localDateTime2timestamp, timestamp2localeDateTime
  )

  class LinkTable(tag: Tag) extends Table[Link](tag, "link") {

    def url = column[URL]("url")
    def name = column[String]("name")
    def description = column[String]("description")
    def userId = column[Long]("user_id")
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def idx_unique_url = index("idx_unique_url", (url), unique = true)
    def idx_unique_ame = index("idx_unique_ame", (name), unique = true)
    def users_fk = foreignKey("users_fk", userId, users)(_.id)

    def * = (url, name, description, userId, id).mapTo[Link]
  }

  class AccessLogTable(tag: Tag) extends Table[AccessLog](tag, "access_log") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def linkId = column[Long]("link_id")
    def accessedAt = column[LocalDateTime]("accessed_at")

    def links_fk = foreignKey("links_fk", linkId, links)(_.id)
    def * = (linkId, accessedAt, id).mapTo[AccessLog]
  }

  class NoteTable(tag: Tag) extends Table[Note](tag, "note") {

    def text = column[String]("text")
    def createdAt = column[LocalDateTime]("created_at")
    def linkId = column[Long]("link_id")
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def links_fk = foreignKey("links_fk", linkId, links)(_.id)
    def * = (text, createdAt,linkId, id).mapTo[Note]
  }

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {

    def name = column[String]("name")
    def description = column[Option[String]]("description")
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (name, description, id).mapTo[Category]
  }

  class CategoryLinkTable(tag: Tag) extends Table[CategoryLink](tag, "category_link") {

    def categoryId = column[Long]("category_id")
    def linkId = column[Long]("link_id")

    def categories_fk = foreignKey("categories_fk", categoryId, categories)(_.id)
    def links_fk = foreignKey("links_fk", linkId, links)(_.id)

    def * = (categoryId, linkId).mapTo[CategoryLink]
  }

  val links = TableQuery[LinkTable]
  val accessLogs = TableQuery[AccessLogTable]
  val notes = TableQuery[NoteTable]
  val categories = TableQuery[CategoryTable]
  val categoryLinkPivot = TableQuery[CategoryLinkTable]

  override def createSchema() = db.run {
    (users.schema ++ links.schema).create
  }

  override def dropSchema() = db.run {
    (users.schema ++ links.schema).drop
  }
}