package model
import java.net.URL
import scala.concurrent.{ Future, ExecutionContext }

case class Link(url: URL, title: String, description: String, userId: Long = 0L, id: Long = 0L) {

  def toMap() = Map(
    "id" -> id.toString,
    "url" -> url.toString,
    "title" -> title,
    "description" -> description
  )

}

case object Link {
  import play.api.libs.json._

  object Implicits {
    implicit def string2url(str: String) = {
      if (!str.startsWith("http")) new URL("http://" + str) else new URL(str)
    }
  }

  implicit object LinkWrites extends Writes[Link] {
    def writes(link: Link) = Json.toJson(link.toMap)
  }

  implicit object LinkReads extends Reads[Link] {
    def reads(json: JsValue): JsResult[Link] = {
      import Link.Implicits._

      val url = (json \ "url").as[String]
      val title = (json \ "title").as[String]
      val description = (json \ "description").as[String]

      JsSuccess(Link(url, title, description))
    }
  }

}