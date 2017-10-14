import play.api.test.WithApplication
import org.scalatest.EitherValues

import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.forms.SignUpData
import com.mamoreno.playseed.models.User
import com.mamoreno.playseed.dal.UserRepository


/**
 * @see example https://github.com/knoldus/activator-play-slick-app/blob/master/src/main/g8/test/repo/EmployeeRepositorySpec.scala
 */
class UserRepositorySpec() extends InvolvesDBSpecification with EitherValues {


  "Adding a new User" should {

    val userOk = User(name = "Miguel", username = "mamoreno", email = "miguel@example.com", password = "A password")
    val userUsernameTaken = User(name = "Miguel", username = "mamoreno", email = "miguel2@example.com", password = "A password")
    val userEmailTaken = User(name = "Miguel", username = "other", email = "miguel@example.com", password = "A password")

    "Return newly created user after successfull creation" in new WithApplication() {
      // it should have been inserted
      val user = await(userRepository.add(userOk)).get
      user.name === userOk.name
      user.username === userOk.username
      user.email === userOk.email
      user.password === userOk.password
    }

    "Return a None when creating a user with an existing username" in new WithApplication() {
      // it should have been failed due to duplicated username
      val maybeUSer = await(userRepository.add(userUsernameTaken))
      maybeUSer shouldEqual None
    }

    "Return a None when creating a user with an existing email" in new WithApplication() {
      // it should have been failed due to duplicated email. I replace the username
      val maybeUser = await(userRepository.add(userEmailTaken))
      maybeUser shouldEqual None
    }
  }

  "Finding a user by Email" should {
    "Return the Option[User] if the Email exists" in new WithApplication() {
      val user = User(
        name = "My Name",
        username = "finding_by_username",
        email = "finding_by_username@example.com",
        password = "A password")

      await(userRepository.add(user))

      val foundUser: User = await(userRepository.findByEmail(user.email)).get
      foundUser.getClass.getName === "com.mamoreno.playseed.models.User"

      foundUser.name === user.name
      foundUser.username === user.username
      foundUser.email === user.email
      foundUser.password == user.password
    }

    "Return the None if the Email DOES NOT exist" in new WithApplication() {
      await(userRepository.findByEmail("invented")) === None
    }
  }

  "Finding a user by Username" should {
    val user = User(
      name = "My Name",
      username = "finding_by_username",
      email = "finding_by_username@example.com",
      password = "A password")

    "Return the Option[User] if the Username exists" in new WithApplication() {
      await(userRepository.add(user))

      val foundUser: User = await(userRepository.findByUsername(user.username)).get
      foundUser.getClass.getName === "com.mamoreno.playseed.models.User"

      foundUser.name === user.name
      foundUser.username === user.username
      foundUser.email === user.email
      foundUser.password == user.password
    }

    "Return the None if the Username DOES NOT exist" in new WithApplication() {
      await(userRepository.findByUsername("invented")) === None
    }
  }

  "Finding a user by Email Or Username" should {
    "Return the Option[User] if the Email or Username exists" in new WithApplication() {
      val user = User(
        name = "My Name",
        username = "finding_by_email_or_username",
        email = "finding_by_email_or_username@example.com",
        password = "A password")

      await(userRepository.add(user))

      val foundUserByEmail: User = await(userRepository.findByEmailOrUsername(user.email)).get
      foundUserByEmail.getClass.getName === "com.mamoreno.playseed.models.User"

      foundUserByEmail.name === user.name
      foundUserByEmail.username === user.username
      foundUserByEmail.email === user.email
      foundUserByEmail.password == user.password

      val foundUserByUsername: User = await(userRepository.findByEmailOrUsername(user.username)).get
      foundUserByUsername.getClass.getName === "com.mamoreno.playseed.models.User"

      foundUserByUsername.name === user.name
      foundUserByUsername.username === user.username
      foundUserByUsername.email === user.email
      foundUserByUsername.password == user.password
    }

    "Return the None if the Email AND the Username DOES NOT exist" in new WithApplication() {
      await(userRepository.findByEmailOrUsername("invented")) === None
    }
  }


}
