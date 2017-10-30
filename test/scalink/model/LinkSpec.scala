import org.scalatestplus.play._
import play.api.test.Helpers._
import org.scalatest._

import model.Link
import model.Link.Implicits._

class LinkSpec extends FlatSpec with Matchers {
  "A Link" must "convert string to URL" in {
    val link = Link("http://www.miguel.es", "test", "No desc", 1)
    link.url shouldBe a [java.net.URL]
  }

  "A newly create Link" must "have an id of 0L" in {
    val link = Link("http://www.miguel.es", "test", "No desc", 1)
    link.id should be (0L)
  }
}
