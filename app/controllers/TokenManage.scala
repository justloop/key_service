package controllers

import javax.inject.Inject

import api.ApiError._
import api.JsonCombinators._
import play.api.mvc._

import scala.concurrent.Future
import models.{ ApiKey, ApiToken }
import play.api.i18n.MessagesApi
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by gejun on 5/5/16.
 */
class TokenManage @Inject() (val messagesApi: MessagesApi) extends api.ApiController {

  def createToken = SecuredApiActionWithBody { implicit request =>
    readFromRequest[ApiKey] {
      case apiKey => ApiToken.create(apiKey.apikey).flatMap { token =>
        ok(Json.obj(
          "apiKey" -> apiKey.apikey,
          "token" -> token,
          "years" -> 100
        ))
      }
    }
  }

  def deleteToken = SecuredApiActionWithBody { implicit request =>
    readFromRequest[ApiKey] {
      case apiKey => ApiToken.deleteByApiKey(apiKey.apikey).flatMap { _ =>
        noContent()
      }
    }
  }

  def listToken = SecuredApiAction { implicit request =>
    ApiToken.list.flatMap {
      list =>
        ok(list.map(a => Json.obj("apiKey" -> a.apiKey, "token" -> a.token, "expirationTime" -> a.expirationtime)))
    }
  }
}
