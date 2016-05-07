package controllers

import javax.inject.Inject

import api.ApiError._
import api.JsonCombinators._
import play.api.mvc._
import scala.concurrent.Future
import models.{ ApiToken, User }
import play.api.Play.current
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Akka
import play.api.libs.functional.syntax._
import play.api.libs.json._
import models.Encryption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class Auth @Inject() (val messagesApi: MessagesApi) extends api.ApiController {

  implicit val loginInfoReads: Reads[Tuple2[String, String]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String] tupled
  )

  def signIn = ApiActionWithBody { implicit request =>
    readFromRequest[Tuple2[String, String]] {
      case (email, pwd) =>
        User.findByEmail(email).flatMap {
          case None => errorUserNotFound
          case Some(user) => {
            if (user.password != Encryption.encrypt(Encryption.ENCRIPTION_KEY, pwd)) errorUserNotFound
            else if (!user.emailconfirmed) errorUserEmailUnconfirmed
            else if (!user.active) errorUserInactive
            else ApiToken.create(request.apiKeyOpt.get, user.id).flatMap { token =>
              ok(Json.obj(
                "token" -> token,
                "minutes" -> 10
              ))
            }
          }
        }
    }
  }

  def signOut = SecuredApiAction { implicit request =>
    ApiToken.delete(request.token).flatMap { _ =>
      noContent()
    }
  }

  implicit val signUpInfoReads: Reads[Tuple3[String, String, User]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String](Reads.minLength[String](6)) and
      (__ \ "user").read[User] tupled
  )

  def signUp = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Tuple3[String, String, User]] {
      case (email, password, user) =>
        User.findByEmail(email).flatMap {
          case Some(anotherUser) => errorCustom("api.error.signup.email.exists")
          case None => {
            User.insert(email, Encryption.encrypt(Encryption.ENCRIPTION_KEY, password), user.name)
            ApiToken.create(user.name, user.id).flatMap { token =>
              ok(Json.obj(
                "apiKey" -> user.name,
                "token" -> token,
                "minutes" -> 10
              ))
            }
          }
        }
    }
  }

}