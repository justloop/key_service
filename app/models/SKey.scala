package models

import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * Created by gejun on 4/5/16.
 */
case class SKey(id: Long,
  key: String,
  inserttime: DateTime)

object SKey {
  import play.api.db._
  import play.api.Play.current
  import anorm._
  import anorm.{ Macro, RowParser }

  val parser = Macro.namedParser[SKey].asInstanceOf[RowParser[SKey]]

  def findById(id: Long): Future[Option[SKey]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM skeys WHERE id = {id}").on("id" -> id).as(parser.singleOpt)
    }
  }

  def insert(key: String): Future[Long] = Future.successful {
    val result: Option[Long] = DB.withConnection { implicit c =>
      SQL("INSERT INTO skeys(key,inserttime) VALUES({key},{inserttime})").on('key -> key, 'inserttime -> ((new DateTime())).toDate).executeInsert()
    }
    result.get
  }

  def basicUpdate(id: Long, key: String): Future[Boolean] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("UPDATE skeys SET key = {key} WHERE id = {id}").on('id -> id, 'key -> key).execute()
    }
  }

  def delete(id: Long): Unit = {
    DB.withConnection { implicit c => SQL("delete from skeys where id = {id}").on("id" -> id).executeUpdate() }
  }

  def list: Future[Seq[SKey]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM skeys ORDER BY id").as(parser.*)
    }
  }

}
