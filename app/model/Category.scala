package model

case class Category(
  name: String,
  description: Option[String] = None,
  id: Long = 0L
)