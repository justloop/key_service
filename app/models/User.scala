package models

import org.joda.time.DateTime
import anorm.JodaParameterMetaData._
import scala.concurrent.Future

case class User(
  id: Long,
  email: String,
  password: String,
  name: String,
  emailconfirmed: Boolean,
  active: Boolean)

object User {
  import play.api.db._
  import play.api.Play.current
  import anorm._
  import anorm.{ Macro, RowParser }

  val parser = Macro.namedParser[User].asInstanceOf[RowParser[User]]

  def findById(id: Long): Future[Option[User]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM users WHERE id = {id}").on("id" -> id).as(parser.singleOpt)
    }
  }
  def findByEmail(email: String): Future[Option[User]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM users WHERE email = {email}").on("email" -> email).as(parser.singleOpt)
    }
  }

  def insert(email: String, password: String, name: String): Future[Long] = Future.successful {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("INSERT INTO users(email,password,name,emailconfirmed,active) VALUES({email},{password},{name},{emailconfirmed},{active})").on('email -> email, 'password -> password, 'name -> name, 'emailconfirmed -> true, 'active -> true).executeInsert()
    }
    id.get
  }

  def update(id: Long, name: String): Future[Boolean] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("UPDATE users SET name = {name} WHERE id = {id}").on('id -> id, 'name -> name).execute()
    }
  }

  def confirmEmail(id: Long): Future[Boolean] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("UPDATE users SET emailconfirmed=true and active=true WHERE id = {id}").on('id -> id).execute()
    }
  }

  def updatePassword(id: Long, password: String): Future[Boolean] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("UPDATE users SET password = {password} WHERE id = {id}").on('id -> id, 'password -> password).execute()
    }
  }

  def delete(id: Long): Future[Unit] = Future.successful {
    DB.withConnection { implicit c => SQL("delete from users where id = {id}").on("id" -> id).executeUpdate() }
  }

  def list: Future[Seq[User]] = Future.successful {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM users ORDER BY id").as(parser.*)
    }
  }

}
