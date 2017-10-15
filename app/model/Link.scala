package model
import java.net.URL

case class Link(
    url: URL,
    name: String,
    description: String,
    userId: Long,
    id: Long = 0L)
