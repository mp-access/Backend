import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RecordedSimulation extends Simulation {

  val headers_0 = Map("Pragma" -> "no-cache")

  val headers_1 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val json_headers = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6"
  )

  val headers_3 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "Origin" -> "http://localhost:8080",
    "Upgrade-Insecure-Requests" -> "1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6",
    "Cache-Control" -> "max-age=0",
    "Connection" -> "keep-alive"
  )

  val httpProtocol = http
    .baseUrl("https://142.93.164.106")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:68.0) Gecko/20100101 Firefox/68.0")
    .disableFollowRedirect

  val headers_2 = Map(
    "Accept-Encoding" -> "gzip, deflate",
    "Pragma" -> "no-cache")

  val headers_5 = Map(
    "Content-Type" -> "application/json",
    "authorization" -> "Bearer ${bearerToken}")

  val headers_27 = Map(
    "Content-Type" -> "application/json",
    "Origin" -> "https://142.93.164.106",
    "authorization" -> "Bearer ${bearerToken}")

  val client_id = "access-frontend"
  val realm = "dev"

  val authUri = "https://142.93.164.106/auth/realms/" + realm + "/protocol/openid-connect/auth"

  val tokenUri = "https://142.93.164.106/auth/realms/" + realm + "/protocol/openid-connect/token"

  val uri3 = "https://142.93.164.106"

  val scn = scenario("RecordedSimulation2")
    .exec(http("GET login page")
      .get(authUri)
      .queryParam("client_id", client_id)
      .queryParam("redirect_uri", uri3)
      .queryParam("state", UUID.randomUUID().toString())
      .queryParam("nonce", UUID.randomUUID().toString())
      .queryParam("response_mode", "fragment")
      .queryParam("response_type", "code")
      .queryParam("scope", "openid")
      .headers(headers_3)
      .check(status.is(200))
      .check(css("#kc-form-login")
        .ofType[Node]
        .transform(variabe => {
          variabe.getAttribute("action")
        })
        .saveAs("loginUrl"))
    )
    .exec(http("POST login")
      .post("${loginUrl}")
      .headers(headers_3)
      .formParam("username", "johann.schop@uzh.ch")
      .formParam("password", "test")
      .check(status.is(302))
      .check(header("Location")
        .transform(t => {
          t.substring(t.indexOf("code=") + 5, t.length())
        })
        .saveAs("authorizationCode"))
      .check(header("Location").saveAs("nextPage"))
    )
    .exec(http("POST fetch token")
      .post(tokenUri)
      .headers(json_headers)
      .header("Referer", "uri3")
      .formParam("code", "${authorizationCode}")
      .formParam("grant_type", "authorization_code")
      .formParam("client_id", client_id)
      .formParam("redirect_uri", uri3)
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("bearerToken"))
    )
    .pause(1)
    .exec(http("GET courses")
      .get("/api/courses")
      .headers(headers_5)
      .resources(http("Get results for course")
        .get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
        .headers(headers_5)))
    .pause(1)
    .exec(http("GET assignment")
      .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
      .headers(headers_5)
      .resources(http("GET results for course")
        .get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
        .headers(headers_5)))
    .pause(1)
    .exec(http("GET exercise 1")
      .get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
      .headers(headers_5)
      .resources(http("GET all exercises in assignment 1")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_5),
        http("GET last submission for exercise 1")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
          .headers(headers_5),
        http("GET submission history 1")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
          .headers(headers_5)))
    .pause(1)
    .exec(http("GET exercise 2")
      .get("/api/exercises/aa097709-e8a1-343e-9352-8ba412758379")
      .headers(headers_5)
      .resources(http("GET all exercises in assignment 2")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_5),
        http("GET last submission for exercise 2")
          .get("/api/submissions/exercises/aa097709-e8a1-343e-9352-8ba412758379")
          .headers(headers_5),
        http("GET submission history 2")
          .get("/api/submissions/exercises/aa097709-e8a1-343e-9352-8ba412758379/history")
          .headers(headers_5),
        http("GET exercise 3")
          .get("/api/exercises/ea1a481c-522f-3347-92e3-a604179ac82a")
          .headers(headers_5),
        http("GET all exercises in assignment 3")
          .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
          .headers(headers_5),
        http("GET last submission for exercise 3")
          .get("/api/submissions/exercises/ea1a481c-522f-3347-92e3-a604179ac82a")
          .headers(headers_5),
        http("GET submission history 3")
          .get("/api/submissions/exercises/ea1a481c-522f-3347-92e3-a604179ac82a/history")
          .headers(headers_5),
        http("GET exercise 4")
          .get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
          .headers(headers_5),
        http("GET all exercises in assignment 4")
          .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
          .headers(headers_5),
        http("GET last submission for exercise 4")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
          .headers(headers_5),
        http("GET submission history 4")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
          .headers(headers_5)))
    .pause(2)
    .exec(http("POST submission")
      .post("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
      .headers(headers_27)
      .body(RawFileBody("RecordedSimulation2_0027_request.txt"))
      .check(jsonPath("$.evalId").saveAs("evalId"))
    )
  // remove polling from the test scenario
//    .pause(1)
//    .asLongAs(session => !session("submissionCompleted").asOption[String].getOrElse("").equals("ok")) {
//      exec(http("Poll for evaluation")
//        .get("/api/submissions/evals/${evalId}")
//        .headers(headers_5)
//        .check(bodyString.exists)
//        .check(jsonPath("$.status").saveAs("submissionCompleted")))
//        .pause(1)
//    }

  setUp(scn.inject(atOnceUsers(1000))).protocols(httpProtocol)
}