package models

import api.ApiRequestHeader
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import anorm.JodaParameterMetaData._
import play.api.libs.json._
import play.api.mvc.RequestHeader

import scala.concurrent.Future

/*
* Stores all the information of a request. Specially used for store the errors in the DB.
*/
case class ApiLog(
    id: Long,
    dateinsert: DateTime,
    ip: String,
    apikey: Option[String],
    token: Option[String],
    method: String,
    uri: String,
    requestbody: Option[String],
    responsestatus: Int,
    responsebody: Option[String]) {
  def dateStr: String = ApiLog.dtf.print(dateinsert)
}

object ApiLog {

  import play.api.db._
  import play.api.Play.current
  import anorm._
  import anorm.{ Macro, RowParser }

  val parser = Macro.namedParser[ApiLog].asInstanceOf[RowParser[ApiLog]]

  private val dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:ss:mm")

  def findById(id: Long): Future[Option[ApiLog]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM apilogs WHERE id = {id}").on("id" -> id).as(parser.singleOpt)
    }
  }

  def insert[R <: RequestHeader](request: ApiRequestHeader[R], status: Int, json: JsValue): Future[Long] = Future.successful {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("INSERT INTO apilogs(dateinsert,ip,apikey,token,method,uri,requestbody,responsestatus,responsebody) VALUES ({dateinsert},{ip},{apikey},{token},{method},{uri},{requestbody},{responsestatus},{responsebody})").on('dateinsert -> request.dateOrNow, 'ip -> request.remoteAddress, 'apikey -> request.apiKeyOpt, 'token -> request.tokenOpt, 'method -> request.method, 'uri -> request.uri, 'requestbody -> request.maybeBody, 'responsestatus -> status, 'responsebody -> (if (json == JsNull) None else Some(Json.prettyPrint(json)))).executeInsert()
    }
    id.get
  }

  def delete(id: Long): Future[Unit] = Future.successful {
    DB.withConnection { implicit c => SQL("delete from apilogs where id = {id}").on("id" -> id).executeUpdate() }
  }

}