package com.duncannevin.drnurlshortener.db

import org.mongodb.scala.{MongoClient, MongoDatabase}

trait DbConfig {
  def mongoDb: MongoDatabase = {
    val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017")
    mongoClient.getDatabase("drnurlshortener")
  }
}
