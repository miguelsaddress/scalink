package model
import com.mamoreno.playseed.models.{ User => SeedUser }
import com.mamoreno.playseed.auth.Roles._

class User(
    name: String,
    username: String,
    email: String,
    password: String,
    role: AuthRole,
    id: Long) 
extends SeedUser(name, username, email, password, role, id)

object User {
  def apply(name: String, username: String, email: String, password: String, role: AuthRole = UserRole, id: Long = 0L): User = {
    assert( !username.contains(" "), "username must not contain spaces" )
    new User(name, username.toLowerCase, email.toLowerCase, password, role, id)
  }
}