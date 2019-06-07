package com.duncannevin.drnurlshortener.repository

import com.duncannevin.drnurlshortener.logging.ShortenLogger
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, _}
import org.mongodb.scala.model.Indexes.ascending

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait RepositoryConfig[T] extends ShortenLogger {
  val database: MongoDatabase
  val collectionName: String
  val codecRegistry: CodecRegistry
  val collection: MongoCollection[T]

  def createIndex(key: String): Unit = {
    collection.createIndex(ascending(key)).toFuture().onComplete {
      case Success(_) => indexCreated(collectionName, key)
      case Failure(_) => indexFailure(collectionName, key)
    }
  }
}
