package repository

import akka.http.scaladsl.util.FastFuture
import entities.Shortened

import scala.concurrent.Future

class ShortenRepository extends ShortenedRepository[Shortened] {
  private var repository: Seq[Shortened] = Seq.empty[Shortened]

  override def save(shortened: Shortened): Future[Option[Shortened]] =
    FastFuture.successful {
      if (repository.exists(_.hash == shortened.hash)) None
      else {
        repository = repository :+ shortened
        Some(shortened)
      }
    }

  override def find(hash: String): Future[Option[Shortened]] =
    FastFuture.successful(repository.find(_.hash == hash))
}
