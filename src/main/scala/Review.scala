package review

import java.io.FileNotFoundException
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat

import cats.effect._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import io.circe.parser.decode
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import review.Codecs._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io._

//The class Review describes only the needed fields of a Json Review for the exercice
case class Review(reviewerID: String, asin: String, overall: Double, unixReviewTime: Long)

case class QueryParameters(start: String, end: String, limit: Int, min_number_reviews: Int)

case class BestRatedArticle(asin: String, average_rating: Double)

object Backend extends StreamApp[IO] {

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    args match {
      case List(path) if Files.exists(Paths.get(path)) =>
        BlazeBuilder[IO]
          .bindHttp(8080, "localhost")
          .mountService(bestRatedReviewHttpService(path), "/")
          .serve

      case _ => Stream.raiseError(new FileNotFoundException("Path given is not valid"))
    }
  }

  def bestRatedReviewHttpService(path: String) = {
    HttpService[IO] {
      case req@POST -> Root / "amazon" / "best-rated" =>
        req.as[QueryParameters].flatMap { queryParams =>
          Ok(filterReviews(path, queryParams).asJson)
        }
    }
  }

  //Main method to get the best rated reviews according to the criteria of the POST request
  def filterReviews(fileName: String, queryParams: QueryParameters): Iterable[BestRatedArticle] = {
    val listOfReviews = filterPerAsin(createListOfReviews(fileName))
    val startDate_epoch = getEpochTime(queryParams.start)
    val endDate_epoch = getEpochTime(queryParams.end)

    //Filter by min_number_reviews and date range
    val filteredList = listOfReviews.flatMap {
      case (asin, reviews) if reviews.size >= queryParams.min_number_reviews =>
        val filteredReviews = reviews.filter(review => startDate_epoch <= review.unixReviewTime
          && review.unixReviewTime <= endDate_epoch)
        if (filteredReviews.isEmpty)
          None
        else
          Some((asin, filteredReviews))
      case _ => None
    }

    val bestRatedProducts = calculateRatingOfProducts(filteredList)

    bestRatedProducts.take(queryParams.limit)
  }

  //Create a map that associates an article id to his list of reviews
  def filterPerAsin(listOfReviews: Iterator[Review]): Map[String, Iterable[Review]] = {
    listOfReviews.toIterable.groupBy(review => review.asin)
  }

  //Read the file that contains the reviews and store the content in an iterator
  def createListOfReviews(fileName: String): Iterator[Review] = {
    Source.fromFile(fileName).getLines().map(decode[Review]).collect {
      //we do not need the lines of the file that do not match the type Review
      case Right(review) => review
    }
  }

  //Convert String time into a unix time (seconds)
  def getEpochTime(date: String): Long = {
    val DATE_FORMAT = "dd.MM.yyyy"
    val dateFormat = new SimpleDateFormat(DATE_FORMAT)
    dateFormat.parse(date).getTime / 1000
  }

  //Create a list of PostResponse
  def calculateRatingOfProducts(filteredList: Map[String, Iterable[Review]]): Iterable[BestRatedArticle] = {
    filteredList.map { case (asin, value) => BestRatedArticle(asin, calculateRate(value)) }
  }

  //Calculate the rating of an article according to some of the reviews
  //The reviews have been previously filtered according to the POST request received
  def calculateRate(reviews: Iterable[Review]): Double = {
    reviews.foldLeft(0.0)((sum, review) => sum + review.overall) / reviews.size
  }

}


