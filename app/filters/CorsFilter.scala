package filters

import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class CorsFilter extends EssentialFilter {
  def apply(next: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {
      next(requestHeader).map { result =>
        result.withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Allow" -> "*",
          "Access-Control-Allow-Methods" -> "POST, GET, PUT, DELETE, OPTIONS",
          "Access-Control-Allow-Headers" -> "Origin, X-Requested-With, Content-Type, Accept, Referrer, User-Agent")
      }
    }
  }
}
