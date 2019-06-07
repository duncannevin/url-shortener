package com.duncannevin.drnurlshortener.repository

import akka.http.scaladsl.util.FastFuture
import com.duncannevin.drnurlshortener.entities.Shortened
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ShortenRepository(mongoDatabase: MongoDatabase) extends Repository[Shortened] with RepositoryConfig[Shortened] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "shortened_urls"
  override val codecRegistry: CodecRegistry = Shortened.codecRegistry
  override val collection: MongoCollection[Shortened] =  database
    .getCollection[Shortened](collectionName)
    .withCodecRegistry(codecRegistry)

  override def findOrCreate(shortened: Shortened): Future[Shortened] =
    for {
      shortenedOpt <- collection
        .find(equal("hash", shortened.hash))
        .first()
        .toFutureOption()
      savedOrFound <- shortenedOpt match {
        case Some(originalShortened) => FastFuture.successful(originalShortened)
        case None => collection.insertOne(shortened).toFuture().map(_ => shortened)
      }
    } yield savedOrFound

  override def find(hash: String): Future[Option[Shortened]] =
    collection.find(equal("hash", hash)).first().toFutureOption()

  createIndex("hash")
}
