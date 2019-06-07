package com.duncannevin.drnurlshortener.repository

import scala.concurrent.Future

trait ShortenedRepository[T] {
  def save(shortened: T): Future[Option[T]]
  def find(hash: String): Future[Option[T]]
}
