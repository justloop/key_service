package models

import scala.concurrent.Future

/*
* Stores the Api Key information
*/
case class ApiKey(
  apikey: String,
  name: String,
  active: Boolean)

object ApiKey {

  import play.api.db._
  import play.api.Play.current
  import anorm._
  import anorm.{ Macro, RowParser }

  val parser: RowParser[ApiKey] = Macro.namedParser[ApiKey].asInstanceOf[RowParser[ApiKey]]

  def isActive(apikey: String): Future[Option[Boolean]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM apikeys WHERE apikey={apikey}").on("apikey" -> apikey).as(parser.singleOpt).map(_.active)
    }
  }
}
