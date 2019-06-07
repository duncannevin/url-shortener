package com.duncannevin.drnurlshortener.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.duncannevin.drnurlshortener.directives.{CustomDirectives, ValidatorDirectives}
import com.duncannevin.drnurlshortener.entities.{ApiSuccess, CreateShorten, Shortened}
import com.duncannevin.drnurlshortener.repository.Repository
import com.duncannevin.drnurlshortener.validation.CreateShortenValidator

class ShortenRouter(repository: Repository[Shortened]) extends Router with Directives with CustomDirectives with ValidatorDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathEndOrSingleSlash {
    post {
      entity(as[CreateShorten]) { createShorten =>
        validateWith(CreateShortenValidator)(createShorten) {
          val shortened = Shortened(createShorten)
          handle(repository.findOrCreate(shortened))(ApiSuccess.created) { success =>
            complete(success.statusCode, success.data)
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
