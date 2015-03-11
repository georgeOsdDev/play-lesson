package myapp.controllers.web

import play.api._
import play.api.mvc._
import play.filters.csrf._

import myapp.views

object IndexController extends WebUIBaseController {
  def index = UserAction { implicit request =>
    Ok(views.html.index("This is dashBoard", request.user))
  }
}