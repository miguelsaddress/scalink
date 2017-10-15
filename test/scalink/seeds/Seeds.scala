import model.Link
import com.mamoreno.playseed.models.User

object Seeds {
  import dal.Implicits._

  def users: Seq[User] = Seq(
    User("Admin", "admin", "admin@email.com", "admin"),
    User("Miguel Angel", "mamoreno", "my@email.com", "password"),
  )
  
  def links: Seq[Link] = Seq(
    Link("http://www.google.com", "Google", "A search engine with a lot of future", 1),
    Link("http://www.mamoreno.com", "My Website", "My own website", 1),
    Link("http://www.udemy.com", "Udemy", "An online school", 1)
  )
}