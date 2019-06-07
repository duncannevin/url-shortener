package com.duncannevin.drnurlshortener.entities

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}

object Shortened {
  def apply(createShorten: CreateShorten): Shortened = {
    val hash = createShorten.hashed
    new Shortened(
      hash,
      shortenedUrl = s"http://localhost:9000/$hash",
      url = createShorten.url,
    )
  }

  def codecRegistry: CodecRegistry = {
    fromRegistries(
      fromProviders(
        Macros.createCodecProvider[Shortened]()
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}

case class Shortened(hash: String, shortenedUrl: String, url: String)
