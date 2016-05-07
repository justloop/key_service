package api

import java.util.Date

import models._
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{ DefaultDateReads => _, _ }
import play.api.libs.json._

/*
* Set of every Writes[A] and Reads[A] for render and parse JSON objects
*/
object JsonCombinators {

  implicit val dateWrites = Writes.dateWrites("dd-MM-yyyy HH:mm:ss")
  implicit val dateReads = Reads.dateReads("dd-MM-yyyy HH:mm:ss")

  implicit val userWrites = new Writes[User] {
    def writes(u: User) = Json.obj(
      "id" -> u.id,
      "email" -> u.email,
      "name" -> u.name
    )
  }
  implicit val userReads: Reads[User] =
    (__ \ "name").read[String](minLength[String](1)).map(name => User(0L, null, null, name, false, false))

  implicit val apiKeyWrites = new Writes[ApiKey] {
    def writes(a: ApiKey) = Json.obj(
      "apiKey" -> a.apikey,
      "name" -> a.name,
      "active" -> a.active
    )
  }

  implicit val apiKeyReads: Reads[ApiKey] =
    (__ \ "apiKey").read[String](minLength[String](1)).map(apiKey => ApiKey(apiKey, "", false))

  implicit val apiTokenWrites = new Writes[ApiToken] {
    def writes(a: ApiToken) = Json.obj(
      "token" -> a.token, // UUID 36 digits
      "apiKey" -> a.apiKey,
      "expirationTime" -> a.expirationtime,
      "userId" -> a.userid
    )
  }

  implicit val apiTokenReads: Reads[ApiToken] =
    ((__ \ "token").read[String](minLength[String](1)) and
      (__ \ "apiKey").read[String](minLength[String](1)))((token, apiKey) => ApiToken(token, apiKey, new DateTime(), 0L))

  implicit val skeyWrites = new Writes[SKey] {
    def writes(s: SKey) = Json.obj(
      "id" -> s.id,
      "key" -> Encryption.decrypt(Encryption.ENCRIPTION_KEY, s.key),
      "insertTime" -> s.inserttime
    )
  }

  implicit val skeyReads: Reads[SKey] =
    (__ \ "key").read[String](minLength[String](1)).map(key => SKey(0L, Encryption.encrypt(Encryption.ENCRIPTION_KEY, key), new DateTime()))
}