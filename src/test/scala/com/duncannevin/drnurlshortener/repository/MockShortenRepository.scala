package com.duncannevin.drnurlshortener.repository

import akka.http.scaladsl.util.FastFuture
import com.duncannevin.drnurlshortener.entities.Shortened

import scala.concurrent.Future

class MockShortenRepository extends Repository[Shortened] {
  private var collection = Seq.empty[Shortened]

  override def findOrCreate(shortened: Shortened): Future[Shortened] = FastFuture.successful {
    collection.find(_.hash == shortened.hash).getOrElse {
      collection = collection :+ shortened
      shortened
    }
  }

  override def find(hash: String): Future[Option[Shortened]] = FastFuture.successful {
    collection.find(_.hash == hash)
  }
}
