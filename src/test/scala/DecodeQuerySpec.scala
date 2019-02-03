import org.scalatest._
import review._
import Codecs._

class DecodeQuerySpec extends FunSuite {

  test("Happy case 1"){
    val q = decodeQuery("""{"start": "15.10.2011", "end": "01.08.2013", "limit": 2, "min_number_reviews": 2 }""")
    assert(q.isRight, q.left)
    val query = q.right.get
    assert(query.start == "15.10.2011")
    assert(query.end == "01.08.2013")
    assert(query.limit == 2)
    assert(query.min_number_reviews == 2)
  }

  test("Missing start field"){
    val q = decodeQuery("""{"end": "01.08.2013", "limit": 2, "min_number_reviews": 2 }""")
    assert(q.isLeft)
  }

  test("Broken Json"){
    val q = decodeQuery("""{"start": 15.10.2011", "end": "01.08.2013", "limit": 2, "min_number_reviews": 2 }""")
    assert(q.isLeft)
  }

}


