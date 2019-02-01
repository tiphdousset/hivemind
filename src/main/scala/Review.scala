
import io.circe.generic.auto._
import io.circe.parser._

import scala.io._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s.server.blaze._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import cats.effect._
import fs2.Stream
import org.http4s._




case class Review(reviewerID: String, asin: String, reviewerName: Option[String], overall: Double, unixReviewTime: Long, reviewTime: String)
case class PostBody(start: String, end: String, limit: Int, min_number_of_reviews: Int)
case class PostResponse(asin: String, average_rating: Double)

object Backend extends StreamApp[IO] {

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {

    def createListOfReviews(fileName: String): Iterator[Review] = {
      Source.fromFile(fileName).getLines().map {
        line => decode[Review](line)
      }.collect {
        case Right(review) => review
      }
    }

    def filterPerAisn(listOfReview: Iterator[Review]): Map[String, Iterable[Review]] = {
      listOfReview.toIterable.groupBy(review => review.asin)
    }


    val helloWorldService = HttpService[IO] {
      case req @ POST -> Root / "amazon" / "best-rated" =>
        println(req)
        //val reviewsFiltered = filterPerAisn(start, end...)
        //Ok(req.body).map(_.putHeaders(`Content-Type`(MediaType.text.plain)))
        Ok("Everything went well")
    }
    BlazeBuilder[IO].bindHttp(8080, "localhost").mountService(helloWorldService, "/").serve
    // println(filterPerAisn(createListOfReviews("video_game_reviews_example.json")).mkString("\n"))
    // println(createListOfReviews("video_game_reviews_example.json").size)
  }

}


