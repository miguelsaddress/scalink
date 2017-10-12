package com.mamoreno.playseed.forms.validators

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ExecutionContext, Future}

import com.mamoreno.playseed.auth.actions.AuthFailures._
import com.mamoreno.playseed.business.UserManagement
import com.mamoreno.playseed.forms.SignUpData
import com.mamoreno.playseed.models.User

import play.api.Logger

@Singleton
case class SignUpDataValidator @Inject() (users: UserManagement) {

  // def validateBasic(data: SignUpData): Option[SignUpFailure] = {
  //   data match {
  //       case _ if data.password != data.passwordConf => Some(PasswordMissmatch)
  //       case _ if data.username.trim.contains(" ") => Some(InvalidUsername)
  //       case _ => None
  //   }
  // }

  def validate(data: SignUpData)(implicit ec: ExecutionContext): Future[Seq[Option[SignUpFailure]]] = {
    val currentDataValidation: Future[Option[SignUpFailure]] = validateCurrentData(data) 
    val userByUsername: Future[Option[SignUpFailure]] = users.findByUsername(data.username).map(validateUsername)
    val userByEmail: Future[Option[SignUpFailure]] = users.findByEmail(data.email).map(validateEmail)

    val futuresList: Seq[Future[Option[SignUpFailure]]] = List(currentDataValidation, userByUsername, userByEmail)

    Future.sequence(futuresList)
  }

  private def validateCurrentData(data: SignUpData): Future[Option[SignUpFailure]] = Future.successful {
    if (data.password != data.passwordConf) {
      Some(PasswordMissmatch)
    } else if (data.username.trim.contains(" ")) {
      Some(InvalidUsername)
    } else {
      None
    }
  }
  
  private def validateUsername(maybeUser: Option[User])(implicit ec: ExecutionContext) =  
    maybeUser match {
      case Some(u) => Some(UsernameTaken)
      case None => None
    }
  

  private def validateEmail(maybeUser: Option[User])(implicit ec: ExecutionContext) = 
    maybeUser match {
      case Some(u) => Some(EmailTaken)
      case None => None
    }
  
}
