# Best rated articles on Amazon

This API allows you to search for the best rated products on Amazon within a certain period of time.
The rating of a product is determined by taking the average number of stars.
The number of results will be at most the number specified by the `limit` field.
Additionally, only products with a minimum number of reviews will be considered.

The `POST` requests will be made to `localhost:8080/amazon/best-rated` and should contain a body like this:

```json
{
"start": "15.10.2011",
"end": "01.08.2013",
"limit": 2,
"min_number_reviews": 2
}
```

# How to start
Go to the root of the project and start the service with the following command: 

```bash
sbt "run video_game_reviews_example.json"
```

"video_game_reviews_example.json" is an example (present in this project) please enter the absolute path to the Amazon's reviews file you want to analyse.

Finally you can try the service via Postman or Command line:

```bash
curl -d '{"start": "15.10.2011","end": "01.08.2013","limit": 2,"min_number_reviews": 2}' localhost:8080/amazon/best-rated
```

# Test
If you want to run the Unit/Integration tests of this project use the following command from the root of the project:

```bash
sbt test
```

