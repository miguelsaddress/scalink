import model.Link
import model.Link.Implicits._
import com.mamoreno.playseed.models.User
import java.net.URL

object Seeds {

  def users: Seq[User] = Seq(
    User("Admin", "admin", "admin@email.com", "admin"),
    User("Miguel Angel", "mamoreno", "my@email.com", "password"),
  )
  
  def links: Seq[Link] = Seq(
    Link("http://www.google.com", "Google", "A search engine with a lot of future", 1L),
    Link("http://www.mamoreno.com", "My Website", "My own website", 1L),
    Link("http://www.udemy.com", "Udemy", "An online school", 1L),
    Link(
      url = new URL("https://stackoverflow.com/questions/38757621/how-do-i-implement-kafka-consumer-in-scala"),
      title = "How to implement a kafka consumer in Scala",
      description = "How to implement a kafka consumer in Scala",
      userId = 1L
    ),
    Link(
      url = new URL("https://www.tutorialspoint.com/apache_kafka/"),
      title = "Apache Kafka Tutorial",
      description = "A tutorial about Apache Kafka and how to start with it",
      userId = 1L
    ),
    Link(
      url = new URL("https://www.cakesolutions.net/teamblogs/getting-started-with-kafka-using-scala-kafka-client-and-akka"),
      title = "Getting Started with Kafka using Scala client and Akka",
      description = "Getting Started with Kafka using Scala client and Akka",
      userId = 1L
    ),
    Link(
      url = new URL("https://www.udemy.com/apache-kafka-series-kafka-from-beginner-to-intermediate/"),
      title = "Apache Kafka Series: Kafka from Beginner to intermediate",
      description = "A course belonging to a series of courses in Udemy to understand Kafka",
      userId = 1L
    ),
    Link(
      url = new URL("https://www.packtpub.com/packt/offers/free-learning"),
      title = "Packt Free Learning",
      description = "Every day there is a free eBook to download",
      userId = 1L
    )
  )
}