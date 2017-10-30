import play.api.test.WithApplication
import org.scalatest.EitherValues
import org.scalatest.TryValues._
import scala.concurrent.ExecutionContext.Implicits.global

import dal.repositories.LinkRepository
import model.Link
import model.Link.Implicits._
import play.api.Logger

class LinkRepositorySpec() extends UsesDB with EitherValues {

  "Adding new Link" should {
    "Return newly created link with an id other than zero" in new WithApplication() {
      val link = Link("http://www.miguel.es", "test", "No desc", 1)
      val tryLink = await(linkRepository.add(link))
      val createdLink = tryLink.success.value
      createdLink.id !== 0L
      link.title === createdLink.title
      link.description === createdLink.description
      link.userId === createdLink.userId
    }

    "Admit same url for different users but not duplicates for a user" in new WithApplication() {
      val linkUser: Link = Link("http://www.miguel2.es", "test2", "No desc")
      val userIds = List(1, 2)
      userIds.foreach { uid =>
        val link = linkUser.copy(userId = uid)
        val tryLink = await(linkRepository.add(link))
        val createdLink: Link = tryLink.success.value
        createdLink.id !== 0L
        link.userId === uid
      }
    }

    "Fails when adding a duplicated url" in new WithApplication {
      val link = Link("http://www.miguelin3.es", "test3", "No desc", 1)
      var tryLink = await(linkRepository.add(link))
      tryLink.success.value

      tryLink = await(linkRepository.add(link))
      //if its not a failure, it will throw an exception and break the test
      tryLink.failed.get

      // But another user id must be ok
      tryLink = await(linkRepository.add(link.copy(userId = 2)))
      tryLink.success.value
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