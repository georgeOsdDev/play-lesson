package myapp.controllers.web

import play.api._
import play.api.mvc._
import myapp.views
import myapp.models.{User, Role}

object UserController extends WebUIBaseController {

  def index = UserAction { implicit request =>
    Ok(views.html.index("This is GET /web/users", request.user))
  }

  def edit = csrfSecureAction { UserAction { implicit request =>
    Ok(views.html.index("This is POST /web/users/new", request.user))
  }}

  def create = csrfSecureAction { UserAction { implicit request =>
    Ok(views.html.index("This is POST /web/users/new", request.user))
  }}

  def show(userId: String) = UserAction { implicit request =>
    Ok(views.html.index("This is GET /web/users/:userId", request.user))
  }

  def update(userId: String) = csrfSecureAction { UserAction { implicit request =>
    Ok(views.html.index("This is PUT /web/users/:userId", request.user))
  }}

  def delete(userId: String) = csrfSecureAction { UserAction { implicit request =>
    Ok(views.html.index("This is DELETE /web/users/:userId", request.user))
  }}

}