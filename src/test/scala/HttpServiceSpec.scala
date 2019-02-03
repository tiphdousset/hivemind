import io.circe._
import io.circe.literal._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.scalatest._
import review._

class HttpServiceSpec extends FunSuite {

  test("Example") {
    val service = Backend.bestRatedReviewHttpService("video_game_reviews_example.json")
    val post = POST(
      uri("/amazon/best-rated"),
      """{"start": "15.10.2011","end": "01.08.2013","limit": 2,"min_number_reviews": 2}"""
    ).unsafeRunSync()

    val response = service.run(post).value.unsafeRunSync()

    val body = response.get.as[Json].unsafeRunSync()

    assert(body == (json"""[ {"asin": "9625990674", "average_rating": 4.0},
                         {"asin": "1700099867", "average_rating": 3.5}
                        ]"""))

  }

}
