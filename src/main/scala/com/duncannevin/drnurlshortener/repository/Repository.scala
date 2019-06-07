package com.duncannevin.drnurlshortener.repository

import com.duncannevin.drnurlshortener.logging.ShortenLogger

import scala.concurrent.Future

trait Repository[T] extends ShortenLogger {
  def findOrCreate(shortened: T): Future[T]
  def find(hash: String): Future[Option[T]]
}
