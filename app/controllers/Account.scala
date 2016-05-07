package controllers

import javax.inject.Inject

import api.ApiError._
import api.JsonCombinators._
import play.api.mvc._

import scala.concurrent.Future
import models.{ ApiToken, Encryption, User }
import play.api.i18n.MessagesApi
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

class Account @Inject() (val messagesApi: MessagesApi) extends api.ApiController {

  def info = SecuredApiAction { implicit request =>
    maybeItem(User.findById(request.userId))
  }

  def update = SecuredApiActionWithBody { implicit request =>
    readFromRequest[User] { user =>
      User.update(request.userId, user.name).flatMap { failed =>
        if (!failed) noContent() else errorInternal
      }
    }
  }

  implicit val pwdsReads: Reads[Tuple2[String, String]] = (
    (__ \ "old").read[String](Reads.minLength[String](1)) and
      (__ \ "new").read[String](Reads.minLength[String](6)) tupled
  )

  def updatePassword = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Tuple2[String, String]] {
      case (oldPwd, newPwd) =>
        User.findById(request.userId).flatMap {
          case None => errorUserNotFound
          case Some(user) if (Encryption.encrypt(Encryption.ENCRIPTION_KEY, oldPwd) != user.password) => errorCustom("api.error.reset.pwd.old.incorrect")
          case Some(user) => User.updatePassword(request.userId, Encryption.encrypt(Encryption.ENCRIPTION_KEY, newPwd)).flatMap { failed =>
            if (!failed) noContent() else errorInternal
          }
        }
    }
  }

  def delete(id: Long) = SecuredApiAction { implicit request =>
    User.delete(id).flatMap { _ =>
      noContent()
    }
  }

  def list = SecuredApiAction { implicit request =>
    User.list.flatMap { result => ok(result) }
  }

}