package controllers

import api._
import api.ApiError._
import api.JsonCombinators._
import models.SKey
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import play.api.i18n.{ MessagesApi }

/**
 * Created by gejun on 4/5/16.
 */
class KeyController @Inject() (val messagesApi: MessagesApi) extends api.ApiController {

  // Returns the Location header, but not the folder information within the content body.
  def insert = SecuredApiActionWithBody { implicit request =>
    readFromRequest[SKey] { skey =>
      SKey.insert(skey.key).flatMap {
        case id => created(Api.locationHeader(routes.KeyController.info(id)))
      }
    }
  }

  def info(id: Long) = UserAwareApiAction { implicit request =>
    maybeItem(SKey.findById(id))
  }

  def update(id: Long) = SecuredApiActionWithBody { implicit request =>
    readFromRequest[SKey] { skey =>
      SKey.basicUpdate(id, skey.key).flatMap { failed =>
        if (!failed) noContent() else errorInternal
      }
    }
  }

  def delete(id: Long) = SecuredApiAction { implicit request =>
    SKey.delete(id)
    noContent()
  }

  def list = UserAwareApiAction { implicit request =>
    SKey.list.flatMap { result => ok(result) }
  }

}
