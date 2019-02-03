package review
import cats.effect._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.http4s.EntityDecoder

object Codecs{

  val queryDecoder = EntityDecoder.text[IO].map(decodeQuery)

  def decodeReview(json : String): Either[io.circe.Error, review.Review] = {
    decode[Review](json)
  }

  def decodeQuery(json : String): Either[io.circe.Error, review.QueryParameters] = {
    decode[QueryParameters](json)
  }

  def encodeResponse(bestRatedArticles : Iterable[BestRatedArticle]): String = {
    bestRatedArticles.asJson.noSpaces
  }
}
