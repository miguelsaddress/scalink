package model
import java.net.URL
import scala.concurrent.{ Future, ExecutionContext }

case class Link(url: URL, title: String, description: String, userId: Long = 0L, id: Long = 0L) {

  def toMap() = Map(
    "url" -> url.toString,
    "title" -> title,
    "description" -> description,
    "id" -> id.toString
  )

}

case object Link {
  import play.api.libs.json._
  import scala.util.{ Try, Success, Failure }

  object Implicits {
    implicit def string2url(str: String) = {
      if (!str.startsWith("http")) new URL("http://" + str) else new URL(str)
    }
  }

  implicit object LinkWrites extends Writes[Link] {
    def writes(link: Link) = Json.toJson(link.toMap)
  }

  implicit object TryLinkWrites extends Writes[Try[Link]] {
    def writes(tryLink: Try[Link]) = tryLink match {
      case Success(link) => Json.toJson(link.toMap)
      case Failure(t) => Json.toJson(Map("status" -> "KO", "message" -> t.getMessage))
    }
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