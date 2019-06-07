package entities

object Shortened {
  def apply(createShorten: CreateShorten): Shortened = {
    val hash = createShorten.hashed
    new Shortened(
      hash,
      shortenedUrl = s"http://localhost:9000/$hash",
      url = createShorten.url,
    )
  }
}

case class Shortened(hash: String, shortenedUrl: String, url: String)
