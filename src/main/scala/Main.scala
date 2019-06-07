import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import entities.Shortened
import logging.TodoLogger
import repository.{ShortenRepository, ShortenedRepository}
import routes.{Router, ShortenRouter}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object Main extends App with TodoLogger {
  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todo-api")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val shortenRepository: ShortenedRepository[Shortened] = new ShortenRepository
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
