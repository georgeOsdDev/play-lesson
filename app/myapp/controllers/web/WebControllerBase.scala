package myapp.controllers.web

import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.mvc.Results.{Redirect, Unauthorized}
import play.filters.csrf._
import play.api.i18n.Lang

import myapp.Global
import myapp.exception.CSRFTokenException
import myapp.models.User
import myapp.views

class WebUIBaseController extends Controller {

  val lang = implicitly[Lang]
  // @FIXME this snippet is not work now
  // Help me
  def secureUserAction[A](block: (UserRequest[A]) => Result) = {
    CSRFFilter(errorHandler = CSRFErrorHandler) {
      UserAction { ur =>
        block(ur.asInstanceOf[UserRequest[A]])
      }
    }
  }

  // CSRFFilter only check header
  def csrfSecureAction = CSRFFilter(errorHandler = CSRFErrorHandler)
}

class UserRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

  // CSRFCheck check both header and body
object CSRFSecureAction {
  def apply[A](action: Action[A]) = {
    CSRFCheck(action, CSRFErrorHandler)
  }
}

object UserAction extends ActionBuilder[UserRequest] {
  def invokeBlock[A](request1: Request[A], block: (UserRequest[A]) => Future[Result]) = {
    implicit val request: Request[A] = request1
    implicit val lang    = Global.lang
    request.session.get(Security.username) match {
      case Some(uid) =>
        User.findById(uid) match {
          case Some(user) =>
            block(new UserRequest(user, request))
          case None =>
            Future.successful(Redirect(routes.AuthController.index)
            )
        }
      case None =>
        Future.successful(Redirect(routes.AuthController.index)
        )
    }
  }
}

object CSRFErrorHandler extends CSRF.ErrorHandler {
  def handle(request: RequestHeader, message: String) = throw new CSRFTokenException(message)
}