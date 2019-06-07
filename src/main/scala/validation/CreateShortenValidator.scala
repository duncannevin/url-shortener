package validation

import entities.{ApiError, CreateShorten}

object CreateShortenValidator extends Validator[CreateShorten] {
  // todo -> write function for validating url
  override def validate(createShorten: CreateShorten): Option[ApiError] = {
    if (!createShorten.url.contains('/')) {
      Some(ApiError.invalidEmail)
    } else None
  }
}
