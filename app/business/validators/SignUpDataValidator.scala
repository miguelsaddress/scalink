package business.validators

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ExecutionContext, Future}

import auth.actions.AuthFailures._
import business.UserManagement
import business.adt.User.SignUpData
import models.User

@Singleton
case class SignUpDataValidator @Inject() (users: UserManagement) {

  def validate(data: SignUpData): Option[SignUpFailure] = {
    data match {
        case _ if data.password != data.passwordConf => Some(PasswordMissmatch)
        case _ if data.username.trim.contains(" ") => Some(InvalidUsername)
        case _ => None
    }
  }

  // def validateWIP(data: SignUpData)(implicit ec: ExecutionContext): Seq[Option[SignUpFailure]] = {
  //   val userByEmail: Future[Option[User]] = users.findByEmail(data.email)
  //   val userByUsername: Future[Option[User]] = users.findByUsername(data.username)
    
  //   hacer una sequencia de futuros y devolverla????

  //   validateCurrentData(data) match {
  //     case Some(f) => Some(f)
  //     case None => {
  //       validateUsername(userByUsername) match {
  //         case Some(f) => Some(f)
  //         case None => {
  //           validateEmail(userByEmail) match {
  //             case f => Some(f)
  //             case None => None
  //           }
  //         }
  //       }
  //     }
  //   }
  //   Seq[Option[SignUpFailure]](None)
  // }

  // private def validateCurrentData(data: SignUpData): Future[Option[SignUpFailure]] = Future.successful {
  //   if (data.password != data.passwordConf) {
  //     Some(PasswordMissmatch)
  //   } else if (data.username.trim.contains(" ")) {
  //     Some(InvalidUsername)
  //   } else {
  //     None
  //   }
  // }
  
  // private def validateUsername(futureOptionUser: Future[Option[User]])(implicit ec: ExecutionContext) = futureOptionUser map { maybeUser => 
  //   maybeUser match {
  //     case Some(u) => Some(UsernameTaken)
  //     case None => None
  //   }
  // }

  // private def validateEmail(futureOptionUser: Future[Option[User]])(implicit ec: ExecutionContext) = futureOptionUser map { maybeUser => 
  //   maybeUser match {
  //     case Some(u) => Some(EmailTaken)
  //     case None => None
  //   }
  // }
}
