package controllers.api

import javax.inject._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import org.webjars.play.WebJarsUtil
import java.net.URL

import scala.concurrent.{ExecutionContext, Future}
import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.auth.actions.AuthActions

import model.Link
import model.Link.Implicits._
import dal.repositories.LinkRepository
import play.Logger

import scala.util.{ Try, Success, Failure }

import play.api.libs.json.Json
import play.api.libs.json._


class LinksController @Inject()(
  authActions: AuthActions,
  users: UserManagement,
  linkRepo: LinkRepository,
  cc: ControllerComponents
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: controllers.AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(cc) with I18nSupport {

  private def getSeeds(uid: Long) = Seq(
    Link("http://www.google.com", "Google", "A search engine with a lot of future", uid),
    Link("http://www.mamoreno.com", "My Website", "My own website", uid),
    Link("http://www.udemy.com", "Udemy", "An online school", uid),
    Link(
      url = new URL("https://stackoverflow.com/questions/38757621/how-do-i-implement-kafka-consumer-in-scala"),
      title = "How to implement a kafka consumer in Scala",
      description = "How to implement a kafka consumer in Scala",
      userId = uid
    ),
    Link(
      url = new URL("https://www.tutorialspoint.com/apache_kafka/"),
      title = "Apache Kafka Tutorial",
      description = "A tutorial about Apache Kafka and how to start with it",
      userId = uid
    ),
    Link(
      url = new URL("https://www.cakesolutions.net/teamblogs/getting-started-with-kafka-using-scala-kafka-client-and-akka"),
      title = "Getting Started with Kafka using Scala client and Akka",
      description = "Getting Started with Kafka using Scala client and Akka",
      userId = uid
    ),
    Link(
      url = new URL("https://www.udemy.com/apache-kafka-series-kafka-from-beginner-to-intermediate/"),
      title = "Apache Kafka Series: Kafka from Beginner to intermediate",
      description = "A course belonging to a series of courses in Udemy to understand Kafka",
      userId = uid
    ),
    Link(
      url = new URL("https://www.packtpub.com/packt/offers/free-learning"),
      title = "Packt Free Learning",
      description = "Every day there is a free eBook to download",
      userId = uid
    )
  )

  def seed = authActions.MustHaveUserAction.async { implicit request => 
    val links = getSeeds(request.user.id)
    linkRepo.addMany(links) map { optionInt =>
      val res = optionInt map { i =>
        val added = i match {
          case Some(number) => number
          case None => 0
        }
        val jsonData = Json.toJson(
          Map("added" -> added)
        )
        makeSuccessJsonResponse(jsonData)
      } getOrElse {
        makeErrorJsonResponse("Something was wrong while seeding")
      }
      Ok(Json.toJson(res))
    }
  }

  def create = authActions.MustHaveUserAction.async { implicit request => 
    val json = request.body.asJson.get
    val link = json.as[Link].copy(userId = request.user.id)

    linkRepo.add(link) map { tryLink =>
      Ok(Json.toJson(makeJsonResponse(tryLink)))
    }
  }

  def list = authActions.MustHaveUserAction.async { implicit request => 
    linkRepo.getAllForUserWithId(request.user.id) map { links => 
      Ok(makeJsonResponse(links))
    }
  }

  def delete(id: Long) = authActions.MustHaveUserAction.async { implicit request =>
    linkRepo.deleteForUserById(request.user.id, id) map { maybeLink =>
      val res = maybeLink match {
        case Some(link) => makeSuccessJsonResponse(Json.toJson(link))
        case None =>  makeErrorJsonResponse(s"Something was wrong while deleting item with id = $id")
      }
      Ok(Json.toJson(res))
    }
  }

  def get(id: Long) = authActions.MustHaveUserAction.async { implicit request =>
    linkRepo.findForUserById(request.user.id, id) map { maybeLink =>
      val res = maybeLink match {
        case Some(link) => makeSuccessJsonResponse(Json.toJson(link))
        case None =>  makeErrorJsonResponse(s"Something was wrong while fetching item with id = $id")
      }
      Ok(Json.toJson(res))
    }
  }

  private def makeJsonResponse(links: Seq[Link]) = Json.toJson(
    Map (
      "status" -> JsString("OK"),
      "count" -> JsString(links.length.toString),
      "links" -> Json.toJson(links)
    )
  )

  private def makeErrorJsonResponse(msg: String) = Map(
    "status" -> JsString("KO"),
    "message" -> JsString(msg)
  )

  private def makeSuccessJsonResponse(data: JsValue) = Map(
    "status" -> JsString("OK"),
    "data" -> data
  )

  private def makeJsonResponse(tryLink: Try[Link]) = Json.toJson(
    tryLink match {
      case Success(link) => makeSuccessJsonResponse(Json.toJson(link))
      case Failure(t) => makeErrorJsonResponse(t.getMessage)
    }
  )

}

