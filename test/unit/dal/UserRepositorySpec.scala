import play.api.Application
import play.api.test.WithApplication

import business.UserManagement
import business.adt.User.SignUpData
import models.User
import dal.UserRepository
import dal.UserRepository.Failures._


/**
 * @see example https://github.com/knoldus/activator-play-slick-app/blob/master/src/main/g8/test/repo/EmployeeRepositorySpec.scala
 */
class UserRepositorySpec() extends InvolvesDBSpecification {


  "Adding a new User" should {

    val userOk = User(name = "Miguel", username = "mamoreno", email = "miguel@example.com", password = "A password")
    val userUsernameTaken = User(name = "Miguel", username = "mamoreno", email = "miguel2@example.com", password = "A password")
    val userEmailTaken = User(name = "Miguel", username = "other", email = "miguel@example.com", password = "A password")

    "Return newly created user after successfull creation" in new WithApplication() {

      // it should have been inserted
      await(userRepository.add(userOk)) match {
        case Right(user) => {
          user.name === userOk.name
          user.username === userOk.username
          user.email === userOk.email
          user.password === userOk.password
        }
        case Left(failure) => {
          //forcing test to fail if we reach here
          true === false
        }
      }

    }

    "Return a UsernameTaken when creating a user with an existing username" in new WithApplication() {
      // it should have been failed due to duplicated usernam
      await(userRepository.add(userUsernameTaken)) match {
        case Right(user) => {
          //forcing test to fail if we reach here
          true === false
        }
        case Left(failure) => {
          //forcing test to fail if we reach here
          failure shouldEqual UsernameTaken
        }
      }
    }

    "Return a EmailTaken when creating a user with an existing email" in new WithApplication() {
      // it should have been failed due to duplicated email. I replace the usernam
      await(userRepository.add(userEmailTaken)) match {
        case Right(user) => {
          //forcing test to fail if we reach here
          true === false
        }
        case Left(failure) => {
          //forcing test to fail if we reach here
          failure shouldEqual EmailTaken
        }
      }
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
      foundUser.getClass.getName === "models.User"

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
      foundUser.getClass.getName === "models.User"

      foundUser.name === user.name
      foundUser.username === user.username
      foundUser.email === user.email
      foundUser.password == user.password
    }

    "Return the None if the Username DOES NOT exist" in new WithApplication() {
      await(userRepository.findByUsername("invented")) === None
    }
  }


}
