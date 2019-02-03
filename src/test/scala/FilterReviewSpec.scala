import org.scalatest._
import review._

class FilterReviewSpec extends FunSuite {

  def testCase(queryParams: QueryParameters, bestRatedArticles: List[BestRatedArticle]): Unit = {
    val file = "video_game_reviews_example.json"
    val response = Backend.filterReviews(file, queryParams)
    assert(response == bestRatedArticles)
  }

  test("Example of the coding challenge") {
    val queryParams = QueryParameters("15.10.2011", "01.08.2013", 1000, 3)
    val bestRatedArticles = List(
      BestRatedArticle("9625990674", 4.0),
      BestRatedArticle("0700099867", 2.0)
    )
    testCase(queryParams, bestRatedArticles)
  }

  test("Date outside date range") {
    val queryParams = QueryParameters("15.10.2001", "20.09.2004", 1000, 3)
    val bestRatedArticles = List()
    testCase(queryParams, bestRatedArticles)
  }

  test("Specific date range") {
    val queryParams = QueryParameters("14.06.2011", "29.10.2011", 1000, 0)
    val bestRatedArticles = List(
      BestRatedArticle("B00005BOSF", 5.0),
      BestRatedArticle("0700099867", 4.5)
    )
    testCase(queryParams, bestRatedArticles)
  }

}
