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

object APIController extends BaseController {

  def test = APIAction { request =>

    val token = request.token
    val appId = request.appId
    val body = request.body.asFormUrlEncoded

    respondSuccessJson()
  }
}