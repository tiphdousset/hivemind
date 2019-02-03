package review

import io.circe._
import io.circe.generic.semiauto._

object Codecs {
  implicit val queryDecoder: Decoder[QueryParameters] = deriveDecoder[QueryParameters]
  implicit val reviewDecoder: Decoder[Review] = deriveDecoder[Review]
  implicit val bestRatedArticleEncoder: Encoder[BestRatedArticle] = deriveEncoder[BestRatedArticle]
}
