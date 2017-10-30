package controllers

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
import dal.repositories.LinkRepository
import play.Logger

import scala.util.{ Success, Failure }

class LinksController @Inject()(
  authActions: AuthActions,
  users: UserManagement,
  linkRepo: LinkRepository,
  cc: ControllerComponents
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ec: ExecutionContext
) extends AbstractController(cc) with I18nSupport {

  def create = authActions.MustHaveUserAction.async { implicit request => 
    val json = request.body.asJson.get
    val link = json.as[Link].copy(userId = request.user.id)
    Logger.debug(s"$link")

    linkRepo.add(link) map { link =>
      Ok(Json.toJson(link))
    }
  }
}