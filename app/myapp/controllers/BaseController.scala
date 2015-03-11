package myapp.controllers

import scala.collection.Seq
import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json.{Json, JsObject}

import myapp.ErrorCodes._
import myapp.ResponseKeys._

trait BaseController extends Controller {

  def paramo(req: Request[Map[String, Seq[String]]], key: String): Option[String] = {
    req.body.get(key) map {
      case Seq(s) => s
    }
  }

  def paramo(body: Option[Map[String, Seq[String]]], key: String): Option[String] = {
    body match {
      case Some(b) =>
        b.get(key) map {
          case Seq(s) => s
        }
      case None => None
    }
  }

  def respondSuccessJson() = {
    Ok(Json.obj(
        ERROR_CD -> SUCCESS,
        MESSAGE    -> ""))
  }

  def respondSuccessJson(jsonObj: JsObject) = {
    Ok(Json.obj(
        ERROR_CD -> SUCCESS,
        MESSAGE    -> "") ++ jsonObj)
  }

  def respondClientErrorJson(errorCd: Int, message: String) = {
    BadRequest(Json.obj(
        ERROR_CD -> errorCd,
        MESSAGE    -> message))
  }

  def respondServerErrorJson(errorCd: Int, message: String) = {
    InternalServerError(Json.obj(
        ERROR_CD -> errorCd,
        MESSAGE    -> message))
  }


}