package myapp;

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json.Json
import play.filters.gzip.GzipFilter
import play.i18n.Lang
import scala.concurrent.Future

import myapp.controllers.AccessLoggingFilter
import myapp.controllers.web.routes
import myapp.exception._
import myapp.models.User

object Global  extends WithFilters(
    AccessLoggingFilter,
    new GzipFilter(shouldGzip = (request, response) =>
      response.headers.get("Content-Type").exists(_.startsWith("text/html"))
    )
  ) with GlobalSettings {

  implicit val lang: Lang = Lang.forCode("ja")

  override def onStart(app: Application) {
    if (play.api.Play.isDev(play.api.Play.current)) {
      User.initializeRoot()
    }
    super.onStart(app);
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    super.onStop(app);
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    Logger.trace("executed before every request:" + request.toString)
    super.onRouteRequest(request)
  }

  override def onError(request1: RequestHeader, ex: Throwable) = {
    Logger.error(s"Unexpected Error: request=${request1.headers}", ex)
    implicit val request = request1

    Future.successful(
      if (request1.headers.keys.contains(CustomeHeaders.TOKEN)) {
        InternalServerError(Json.obj(
            ResponseKeys.ERROR_CD -> ErrorCodes.UNEXPECTED_ERROR,
            ResponseKeys.MESSAGE    -> "Unkonwn error"))
      } else {
        ex.getCause match {
          case ex: CSRFTokenException =>
            // signout
            Redirect(routes.AuthController.index)
          case ex: DataNotFoundException =>
            InternalServerError(views.html.error(""))
          case _ =>
            InternalServerError(views.html.error(""))
        }
      }
    )
  }

  override def onHandlerNotFound(request1: RequestHeader) = {
    implicit val request = request1
    Future.successful(
      if (request1.headers.keys.contains(CustomeHeaders.TOKEN))
        NotFound(Json.obj(
            ResponseKeys.ERROR_CD -> ErrorCodes.INVALID_URL,
            ResponseKeys.MESSAGE    -> "Not found"))
      else
        NotFound(views.html.notfound(""))
    )
  }
}
