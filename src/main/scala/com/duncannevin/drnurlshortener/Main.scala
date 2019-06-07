package com.duncannevin.drnurlshortener

import akka.http.scaladsl.server.Directives.reject
import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.duncannevin.drnurlshortener.db.DbConfig
import com.duncannevin.drnurlshortener.entities.Shortened
import com.duncannevin.drnurlshortener.logging.ShortenLogger
import com.duncannevin.drnurlshortener.repository.{Repository, ShortenRepository}
import com.duncannevin.drnurlshortener.routes.{Router, ShortenRouter}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}


object Main extends App with ShortenLogger with DbConfig {
  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todo-api")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val shortenRepository: Repository[Shortened] = new ShortenRepository(mongoDb)
  val shortenRouter: Router = new ShortenRouter(shortenRepository)

  lazy val routes: Route = Seq(shortenRouter).foldRight[Route](reject) {
    (partial, builder) =>
      partial.route ~ builder
  }

  lazy val router: Flow[HttpRequest, HttpResponse, NotUsed] =
    Route.handlerFlow(routes)

  val server: Server = new Server(router, host, port)

  val binding = server.bind()

  binding.onComplete {
    case Success(_) => serverListening(port)
    case Failure(error) => startFailure(port, error.getMessage)
  }
  Await.result(binding, FiniteDuration(3, "seconds"))
}
