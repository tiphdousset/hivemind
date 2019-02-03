import org.scalatest._
import review._
import Codecs._

class EncodeResponseSpec extends FunSuite {

  test("Happy case 1"){
    val response = encodeResponse(List(BestRatedArticle("9625990674", 4.0)))
    assert(response == """[{"asin":"9625990674","average_rating":4.0}]""")
  }

}


