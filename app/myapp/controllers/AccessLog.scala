package myapp.controllers

import scala.concurrent.Future

import play.api.Logger
import play.api.mvc._


object AccessLoggingFilter extends Filter {
  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val result = next(request)
    Logger.info(request + "\n\t => " + result)
    result
  }
}