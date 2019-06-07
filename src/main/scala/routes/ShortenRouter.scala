package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import directives.{CustomDirectives, ValidatorDirectives}
import entities.{ApiSuccess, CreateShorten, Shortened}
import repository.ShortenedRepository
import validation.CreateShortenValidator

class ShortenRouter(repository: ShortenedRepository[Shortened]) extends Router with Directives with CustomDirectives with ValidatorDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathEndOrSingleSlash {
    post {
      entity(as[CreateShorten]) { createShorten =>
        validateWith(CreateShortenValidator)(createShorten) {
          val shortened = Shortened(createShorten)
          handle(repository.save(shortened))(ApiSuccess.created) { success =>
            complete(success.statusCode, success.data.getOrElse(shortened))
          }
        }
      }
    }
  } ~ pathPrefix(Segment) { url =>
    get {
      handleOption(repository.find(url), "find")(ApiSuccess.redirect) { success =>
        redirect(success.data.url, StatusCodes.PermanentRedirect)
      }
    }
  }
}
