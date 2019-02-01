import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import org.http4s.dsl.io._
import org.http4s._
import org.http4s.circe._
import org.http4s.server.blaze._

import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import fs2.Stream

import java.text.SimpleDateFormat
import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect._
import scala.io._

case class Review(reviewerID: String, asin: String, reviewerName: Option[String], overall: Double, unixReviewTime: Long, reviewTime: String)
case class PostBody(start: String, end: String, limit: Int, min_number_reviews: Int)
case class PostResponse(asin: String, average_rating: Double)

object Backend extends StreamApp[IO] {

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {

    //Read the file that contains the reviews and store the content in an iterator
    def createListOfReviews(fileName: String): Iterator[Review] = {
      Source.fromFile(fileName).getLines().map {
        line => decode[Review](line)
      }.collect { //we do not need the lines of the file that do not match the type Review
        case Right(review) => review
      }
    }

    //Create a map that associate an article id to his list of reviews
    def filterPerAsin(listOfReviews: Iterator[Review]): Map[String, Iterable[Review]] = {
      listOfReviews.toIterable.groupBy(review => review.asin)
    }

    //Convert String time into a unix time (seconds)
    def getEpocheTime(date: String) : Long = {
      val DATE_FORMAT = "dd.MM.yyyy"
      val dateFormat = new SimpleDateFormat(DATE_FORMAT)
      dateFormat.parse(date).getTime()/1000
    }

    //Calculate the rating of an article according to some of the reviews
    //The reviews have been previously filtered according to the POST request recieved
    def calculateRate(reviews: Iterable[Review]): Double = {
      reviews.foldLeft(0.0)((sum, review) => sum+review.overall)/reviews.size
    }

    //Create a list of PostResponse
    def calculateRatingOfProducts(filteredList : Map[String, Iterable[Review]]): Iterable[PostResponse] = {
      filteredList.map{ case (asin, value) => PostResponse(asin, calculateRate(value)) }
    }

    //Main method to get the best rated reviews according to the criteria of the POST request
    def filterReviews(fileName: String, startDate: String, endDate: String, limit: Int, min: Int) : Iterable[PostResponse] = {
      val listOfReviews = filterPerAsin(createListOfReviews(fileName))
      // ("123", [reviews])
      val startDate_epoche = getEpocheTime(startDate)
      val endDate_epoche = getEpocheTime(endDate)

      val filteredList = listOfReviews
                                      .filter{ case(key, reviews) => reviews.size >= min}
                                      .mapValues{ reviews => reviews.filter(review => startDate_epoche <= review.unixReviewTime
                                                                      && review.unixReviewTime <= endDate_epoche)
                                                }
      
      val bestRatedProducts = calculateRatingOfProducts(filteredList)

      bestRatedProducts.take(limit)
    }

    val bestRatedReviewService = HttpService[IO] {
      case req@POST -> Root / "amazon" / "best-rated" =>
          req.decodeJson[PostBody] flatMap{ (p: PostBody) =>
              Ok(filterReviews("video_game_reviews_example.json", p.start, p.end, p.limit, p.min_number_reviews).asJson)
          }
    }

    BlazeBuilder[IO].bindHttp(8080, "localhost").mountService(bestRatedReviewService, "/").serve

  }

}


