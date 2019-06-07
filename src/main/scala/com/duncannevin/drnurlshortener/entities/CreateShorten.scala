package com.duncannevin.drnurlshortener.entities

case class CreateShorten(url: String) {
  def hashed: String = {
    scala.util.Random.alphanumeric.take(10).mkString
  }
}
