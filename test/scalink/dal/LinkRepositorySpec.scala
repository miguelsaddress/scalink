import play.api.test.WithApplication
import org.scalatest.EitherValues
import scala.concurrent.ExecutionContext.Implicits.global

import dal.Implicits._
import dal.repositories.LinkRepository
import model.Link
import play.api.Logger

class LinkRepositorySpec() extends UsesDB with EitherValues {

  "Adding new Link" should {

    "Return newly created link with id" in new WithApplication() {
      val link = await(linkRepository.add(Link("http://www.miguel.es", "test", "No desc", 1)))
      link.id !== 0L
      link.name === "test"
    }
  }

  "Listing links" should {
    "return all the links" in new WithApplication() {
      val list = await(linkRepository.list)
      list.length !== 0
      list.length >= Seeds.links.length
    }
  }
}