package com.duncannevin.drnurlshortener.routes

import akka.http.scaladsl.server.Route

trait Router {
  def route: Route
}
