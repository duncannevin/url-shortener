package com.duncannevin.drnurlshortener.validation

import com.duncannevin.drnurlshortener.entities.ApiError

trait Validator[T] {
  def validate(t: T): Option[ApiError]
}
