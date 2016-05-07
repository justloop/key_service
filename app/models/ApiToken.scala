package models

import java.util.UUID

import org.joda.time.DateTime

import scala.concurrent.Future

/*
* Stores the Auth Token information. Each token belongs to a Api Key and user
*/
case class ApiToken(
    token: String, // UUID 36 digits
    apiKey: String,
    expirationtime: DateTime,
    userid: Long) {
  def isExpired = expirationtime.isBeforeNow
}

object ApiToken {
  import play.api.db._
  import play.api.Play.current
  import anorm._
  import anorm.{ Macro, RowParser }

  val parser = Macro.namedParser[ApiToken].asInstanceOf[RowParser[ApiToken]]

  def findByTokenAndApiKey(token: String, apikey: String): Future[Option[ApiToken]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM apitokens WHERE token = {token} and apikey = {apikey}").on("token" -> token, "apikey" -> apikey).as(parser.singleOpt)
    }
  }

  // for user
  def create(apikey: String, userid: Long): Future[String] = Future.successful {
    // Be sure the uuid is not already taken for another token
    def newUUID: String = {
      val uuid = UUID.randomUUID().toString
      val dbResult = DB.withConnection { implicit c =>
        SQL("SELECT * FROM apitokens WHERE token = {token}").on("token" -> uuid).as(parser.singleOpt)
      }
      if (!dbResult.isDefined) uuid else newUUID
    }
    val token = newUUID
    DB.withConnection { implicit c =>
      SQL("DELETE FROM apitokens WHERE userid = {userid}").on("userid" -> userid).executeUpdate()
    }

    DB.withConnection { implicit c =>
      SQL("INSERT INTO apitokens(token,apikey,expirationtime,userid) VALUES ({token},{apikey},{expirationtime},{userid})").on("token" -> token, 'apikey -> apikey, 'expirationtime -> ((new DateTime()) plusDays 1).toDate, 'userid -> userid).executeInsert()
    }
    token
  }

  // for app
  def create(apikey: String): Future[String] = Future.successful {
    // Be sure the uuid is not already taken for another token
    def newUUID: String = {
      val uuid = UUID.randomUUID().toString
      val dbResult = DB.withConnection { implicit c =>
        SQL("SELECT * FROM apitokens WHERE token = {token}").on("token" -> uuid).as(parser.singleOpt)
      }
      if (!dbResult.isDefined) uuid else newUUID
    }
    val token = newUUID
    DB.withConnection { implicit c =>
      SQL("DELETE FROM apitokens WHERE apikey = {apikey}").on("apikey" -> apikey).executeUpdate()
    }
    DB.withConnection { implicit c =>
      SQL("INSERT INTO apitokens(token,apikey,expirationtime,userid) VALUES ({token},{apikey},{expirationtime},{userid})").on("token" -> token, 'apikey -> apikey, 'expirationtime -> ((new DateTime()) plusYears 100).toDate, 'userid -> 0L).executeInsert()
    }
    token
  }

  def delete(token: String): Future[Unit] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM apitokens WHERE token = {token}").on("token" -> token).executeUpdate()
    }
  }

  def deleteByApiKey(apikey: String): Future[Unit] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM apitokens WHERE apikey = {apikey}").on("apikey" -> apikey).executeUpdate()
    }
  }

  def list: Future[Seq[ApiToken]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM apitokens WHERE userid = 0").as(parser.*)
    }
  }
}
