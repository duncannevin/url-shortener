package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities.{CreateShorten, Shortened}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import repository.ShortenRepository

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class ShortenRouterSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val repository = new ShortenRepository()
  private val router = new ShortenRouter(repository)

  val timeout = FiniteDuration(500, "milliseconds")
  val createShorten = CreateShorten("https://alvinalexander.com/scala/creating-random-strings-in-scala")
  val createShorten2 = CreateShorten("https://alvinalexander.com/scala/creating-random-strings-in-scala")
  var testHash = ""

  override def beforeAll(): Unit = {
    testHash = Await.result(repository.save(Shortened(createShorten2)), timeout).get.hash
  }

  "A ShortenRouter" should {
    "shorten a url" in {
      Post("/", createShorten) ~> router.route ~> check {
        status shouldBe StatusCodes.Created
        val resp = responseAs[Shortened]
        resp.hash.isInstanceOf[String] shouldBe true
      }
    }

    "respond with a url even if it already exists" in {
      Post("/", createShorten) ~> router.route ~> check {
        status shouldBe StatusCodes.Created
        val resp = responseAs[Shortened]
        resp.hash.isInstanceOf[String] shouldBe true
      }
    }

    "respond with redirect for a valid url" in {
      Get(s"/$testHash") ~> router.route ~> check {
        status shouldBe StatusCodes.PermanentRedirect
      }
    }

    "respond with bad request for none existent hash" in {
      Get("/notanyhash") ~> router.route ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
