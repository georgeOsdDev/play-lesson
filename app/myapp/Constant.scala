package myapp;

object ErrorCodes {
  val SUCCESS = 0
  val MISSING_PARAM = 101
  val INVALID_PARAM = 102
  val INVALID_URL   = 103

  val UNEXPECTED_ERROR = 900

}

object CustomeHeaders {
  val TOKEN  = "X-MY-TOKEN"
  val APPID  = "X-MY-APP-ID"
  val APPKEY = "X-MY-APP-KEY"
}

object ResponseKeys {
  val ERROR_CD     = "error_cd"
  val MESSAGE      = "message"
}

object RequestKeys {
}