package model
import java.time.LocalDateTime

case class Note(
  text: String,
  createdAt: LocalDateTime = LocalDateTime.now(),
  linkId: Long,
  id: Long = 0L)