import org.scalatest._
import review._
import Codecs._

class DecodeQuerySpec extends FunSuite {

  test("decode happy case 1"){
    val q = decodeQuery("""{"start": "15.10.2011", "end": "01.08.2013", "limit": 2, "min_number_reviews": 2 }""")
    assert(q.isRight, q.left)
    val query = q.right.get
    assert(query.start == "15.10.2011")
    assert(query.end == "01.08.2013")
    assert(query.limit == 2)
    assert(query.min_number_reviews == 2)
  }

  test("decode missing start field"){
    val q = decodeQuery("""{"end": "01.08.2013", "limit": 2, "min_number_reviews": 2 }""")
    assert(q.isLeft)
  }

  test("decode broken Json"){
    val q = decodeQuery("""{"start": 15.10.2011", "end": "01.08.2013", "limit": 2, "min_number_reviews": 2 }""")
    assert(q.isLeft)
  }

}


