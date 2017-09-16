import play.api.Application
import play.api.test.WithApplication

import business.UserManagement
import business.adt.SignUpData
import models.User
import dal.UserRepository
import dal.UserRepository.Failures._


/**
 * @see example https://github.com/knoldus/activator-play-slick-app/blob/master/src/main/g8/test/repo/EmployeeRepositorySpec.scala
 */
class UserRepositorySpec() extends RepositorySpec {


  "Adding a new User" should {

    val userOk = User(name = "Miguel", username = "mamoreno", email = "miguel@example.com", password = "A password")
    val userUsernameTaken = User(name = "Miguel", username = "mamoreno", email = "miguel2@example.com", password = "A password")
    val userEmailTaken = User(name = "Miguel", username = "other", email = "miguel@example.com", password = "A password")

    "Return newly created user after successfull creation" in new WithApplication() {

      // it should have been inserted
      await(users.add(userOk)) match {
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
      val app2Repo = Application.instanceCache[UserRepository]
      val users: UserRepository = app2Repo(app)

      // it should have been failed due to duplicated usernam
      await(users.add(userUsernameTaken)) match {
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
            val app2Repo = Application.instanceCache[UserRepository]
      val users: UserRepository = app2Repo(app)

      // it should have been failed due to duplicated email. I replace the usernam
      await(users.add(userEmailTaken)) match {
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
}
