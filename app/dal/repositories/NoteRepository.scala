package dal.repositories

import javax.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

import dal.Tables
import model.Note
import play.api.Logger

@Singleton
class NoteRepository @Inject() (val dbConfig: DatabaseConfig[JdbcProfile], val tables: Tables)(implicit ec: ExecutionContext) {
  import dbConfig.profile.api._
  val db = dbConfig.db

  val notes = tables.notes

  def add(note:Note): Future[Note] = db.run {
    val insertReturningNoteWithIdQuery = 
      notes returning notes.map(_.id) into ((note,id) => note.copy(id=id))
      (insertReturningNoteWithIdQuery += note)
  }
  
  def list(): Future[Seq[Note]] = db.run {
    notes.result
  }
}
