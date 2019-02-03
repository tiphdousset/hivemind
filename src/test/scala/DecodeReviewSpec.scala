import io.circe.parser.decode
import org.scalatest._
import review.Codecs._
import review._

class DecodeReviewSpec extends FunSuite {

  test("Happy case 1") {
    val r = decode[Review]("""{"reviewerID": "AN3YYDZAS3O1Y", "asin": "0700099867", "reviewerName": "Bob", "helpful": [10, 13], "reviewText": "Loved playing Dirt 2 and I thought the graphics were good. (...)", "overall": 5.0, "summary": "A step up from Dirt 2 and that is terrific!", "unixReviewTime": 1313280000, "reviewTime": "08 14, 2011"}""")
    assert(r.isRight, r.left)
    val review = r.right.get
    assert(review.reviewerID == "AN3YYDZAS3O1Y")
    assert(review.asin == "0700099867")
    assert(review.overall == 5.0)
    assert(review.unixReviewTime == 1313280000)
  }

  test("Happy case 2") {
    val r = decode[Review]("""{"reviewerID": "A2UMFJ6CJAY5GX", "asin": "9625990674", "helpful": [0, 1], "reviewText": "nice cheap made cover it doesnt fot very tight but it does the job if you want to extend the life of your controller this is it", "overall": 3.0, "summary": "cover", "unixReviewTime": 1320796800, "reviewTime": "11 9, 2011"}""")
    assert(r.isRight, r.left)
    val review = r.right.get
    assert(review.reviewerID == "A2UMFJ6CJAY5GX")
    assert(review.asin == "9625990674")
    assert(review.overall == 3.0)
    assert(review.unixReviewTime == 1320796800)
  }

  test("Happy case 3") {
    val r = decode[Review]("""{"reviewerID": "AHT34BRYFBFT1", "asin": "0700099867", "reviewerName": "hewimp", "helpful": [3, 5], "reviewText": "Dirt 3 on DVDi collect racing games so had to add this to my collectionSon wated one also", "overall": 5.0, "summary": "Cars", "unixReviewTime": 1388275200, "reviewTime": "12 29, 2013"}""")
    assert(r.isRight, r.left)
    val review = r.right.get
    assert(review.reviewerID == "AHT34BRYFBFT1")
    assert(review.asin == "0700099867")
    assert(review.overall == 5.0)
    assert(review.unixReviewTime == 1388275200)
  }

  test("Missing reviewerID field") {
    val r = decode[Review]("""{"asin": "0700099867", "reviewerName": "Bob", "helpful": [10, 13], "reviewText": "Loved playing Dirt 2 and I thought the graphics were good. (...)", "overall": 5.0, "summary": "A step up from Dirt 2 and that is terrific!", "unixReviewTime": 1313280000, "reviewTime": "08 14, 2011"}""")
    assert(r.isLeft)
  }

  test("Broken Json") {
    val r = decode[Review]("""{"asin": "0700099867, "reviewerName": "Bob", "helpful": [10, 13], "reviewText": "Loved playing Dirt 2 and I thought the graphics were good. (...)", "overall": 5.0, "summary": "A step up from Dirt 2 and that is terrific!", "unixReviewTime": 1313280000, "reviewTime": "08 14, 2011"}""")
    assert(r.isLeft)
  }

}


