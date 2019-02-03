import io.circe.literal._
import io.circe.syntax._
import org.scalatest._
import review.Codecs._
import review._

class EncodeResponseSpec extends FunSuite {

  test("Happy case 1") {
    val response = List(BestRatedArticle("9625990674", 4.0)).asJson
    assert(response == json"""[{"asin":"9625990674","average_rating":4.0}]""")
  }

}


