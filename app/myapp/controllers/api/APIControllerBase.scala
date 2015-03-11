package myapp.controllers.api

import scala.collection.Seq
import scala.collection.JavaConverters._
import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json.{ JsArray, Json, JsObject }

import myapp.controllers.BaseController
import myapp.CustomeHeaders._
import myapp.ErrorCodes._
import myapp.RequestKeys._
import myapp.ResponseKeys._

class RequestWithKeys[A](
  val token: String,
  val appId: String,
  val appKey: String,
  request: Request[A]) extends WrappedRequest[A](request)

object APIAction extends ActionBuilder[RequestWithKeys] {
  def invokeBlock[A](request: Request[A], block: (RequestWithKeys[A]) => Future[Result]) = {

    if (!request.headers.get(TOKEN).isDefined) {
      Logger.debug(s"Invalid Request: ${request}")
      Future.successful(BadRequest(Json.obj(
        ERROR_CD -> MISSING_PARAM,
        MESSAGE -> s"Missing some custome header: ${TOKEN}")))
    } else if (!request.headers.get(APPID).isDefined) {
      Logger.debug(s"Invalid Request: ${request}")
      Future.successful(BadRequest(Json.obj(
        ERROR_CD -> MISSING_PARAM,
        MESSAGE -> s"Missing some custome header: ${APPID}")))
    } else if (!request.headers.get(APPKEY).isDefined) {
      Logger.debug(s"Invalid Request: ${request}")
      Future.successful(BadRequest(Json.obj(
        ERROR_CD -> MISSING_PARAM,
        MESSAGE -> s"Missing some custome header: ${APPKEY}")))
    } else {
      val token = request.headers.get(TOKEN).get
      val appId = request.headers.get(APPID).get
      val appKey = request.headers.get(APPKEY).get

      block(new RequestWithKeys(token, appId, appKey, request))
    }
  }
}