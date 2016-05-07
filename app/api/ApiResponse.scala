package api

import api.Api._
import play.api.libs.json._

/*
* Successful response for an ApiRequest.
*/
case class ApiResponse(status: Int, json: JsValue, headers: Seq[(String, String)]) extends ApiResult

object ApiResponse {

  //////////////////////////////////////////////////////////////////////
  // Status Codes

  final val STATUS_OK = 200
  final val STATUS_CREATED = 201
  final val STATUS_ACCEPTED = 202
  final val STATUS_NOCONTENT = 204

  //////////////////////////////////////////////////////////////////////
  // Predefined responses

  val CORS_HEADERS = Seq(
    "Access-Control-Allow-Origin" -> "*",
    "Access-Control-Allow-Methods" -> "GET, POST, PATCH, PUT, DELETE, OPTIONS",
    "Access-Control-Allow-Headers" -> "Origin")

  def ok(json: JsValue, headers: (String, String)*) = apply(STATUS_OK, json, headers ++ CORS_HEADERS)
  def ok[A](json: JsValue, page: Page[A], headers: (String, String)*) = apply(STATUS_OK, json, headers ++ Seq(
    HEADER_PAGE -> page.page.toString,
    HEADER_PAGE_FROM -> page.offset.toString,
    HEADER_PAGE_SIZE -> page.size.toString,
    HEADER_PAGE_TOTAL -> page.total.toString
  ) ++ CORS_HEADERS)
  def created(json: JsValue, headers: (String, String)*) = apply(STATUS_CREATED, json, headers ++ CORS_HEADERS)
  def created(headers: (String, String)*) = apply(STATUS_CREATED, JsNull, headers ++ CORS_HEADERS)
  def accepted(json: JsValue, headers: (String, String)*) = apply(STATUS_ACCEPTED, json, headers ++ CORS_HEADERS)
  def accepted(headers: (String, String)*) = apply(STATUS_ACCEPTED, JsNull, headers ++ CORS_HEADERS)
  def noContent(headers: (String, String)*) = apply(STATUS_NOCONTENT, JsNull, headers ++ CORS_HEADERS)

}