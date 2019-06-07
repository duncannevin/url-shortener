package directives

import akka.http.scaladsl.server.{Directive1, Directives}
import entities.{ApiError, ApiSuccess}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait CustomDirectives extends Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def handle[T](f: Future[T])(success: T => ApiSuccess[T]): Directive1[ApiSuccess[T]] = onComplete(f) flatMap {
    case Success(t) => provide(success(t))
    case Failure(e) =>
      complete(ApiError.generic.statusCode, ApiError.generic.data.copy(msg = e.getMessage))
  }

  def handleOption[T](f: Future[Option[T]], `type`: String)(success: T => ApiSuccess[T]): Directive1[ApiSuccess[T]] = onComplete(f) flatMap {
    case Success(tOpt) =>
      tOpt match {
        case Some(t) => provide(success(t))
        case None =>
          if (`type` == "find") {
            complete(ApiError.notFound.statusCode, ApiError.notFound.data)
          } else if (`type` == "save") {
            complete(ApiError.conflict.statusCode, ApiError.conflict.data)
          } else {
            complete(ApiError.generic.statusCode, ApiError.generic.data)
          }
      }
    case Failure(e) =>
      complete(ApiError.generic.statusCode, ApiError.generic.data.copy(msg = e.getMessage))
  }
}
