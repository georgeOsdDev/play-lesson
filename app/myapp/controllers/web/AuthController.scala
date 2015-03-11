package myapp.controllers.web

import play.api._
import play.api.mvc._
import play.api.mvc.Results.Redirect
import play.api.http.Status.UNAUTHORIZED
import play.api.data._
import play.api.data.Forms._
import play.filters.csrf._
import play.api.i18n.Messages

import myapp.controllers.BaseController
import myapp.models.User
import myapp.views

case class LoginForm(email: String, password: String)

object AuthController extends WebUIBaseController {

  val loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> text
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def index() = CSRFAddToken { Action { implicit request =>
    Ok(views.html.loginForm(request.flash.get("message").getOrElse(Messages("Welcome!")), loginForm)).withNewSession
  }}

  def login() = CSRFAddToken { Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Redirect(routes.AuthController.index)
        .flashing("message" -> Messages("invalid request"))
        .withNewSession
      },
      loginReq => {
        User.authenticate(loginReq.email, loginReq.password) match {
          case Some(user: User) =>
            Ok(views.html.index(Messages("This is dashBoard"), user))
              .withSession(Security.username -> user.id.toString)
          case None =>
            Redirect(routes.AuthController.index)
            .flashing("message" -> Messages("invalid email or password"))
            .withNewSession
        }
      }
    )
  }}

  def logout() = CSRFSecureAction { UserAction { implicit request =>
    Redirect(routes.AuthController.index)
    .flashing("message" -> Messages("Bye"))
  }}
}