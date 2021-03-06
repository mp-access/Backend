import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder

import scala.concurrent.duration._

class CodeExecutionSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://info1-exercises.ifi.uzh.ch")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:68.0) Gecko/20100101 Firefox/68.0")

  val headers_0 = Map(
    "Accept-Encoding" -> "gzip, deflate",
    "Pragma" -> "no-cache")

  val headers_1 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_4 = Map("Accept" -> "image/webp,*/*")

  val headers_5 = Map("Accept" -> "application/json")

  val bearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXZUZPRHh2N3RtMUhFQk9oWXFrN09NanhSMWxJTnd2djZrNTc2SldNbm5ZIn0.eyJqdGkiOiJlZDRiOTI1ZC1lYTEzLTRlMzItYjNmOS0zMjBjODdlNDQzZTkiLCJleHAiOjE1Njg1ODgwMDAsIm5iZiI6MCwiaWF0IjoxNTY4NTUyMDAwLCJpc3MiOiJodHRwczovL2luZm8xLWV4ZXJjaXNlcy5pZmkudXpoLmNoL2F1dGgvcmVhbG1zL2FjY2VzcyIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiZGY1MzFhOWUtZDkwZi00NDMwLTk2MDAtOGM4ZGQyZDYwYzBhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiJkYmZjODEzZC05YTgzLTQ2YTktOWFiYS1jYmQwOTc5N2VlMWYiLCJhdXRoX3RpbWUiOjE1Njg1NTIwMDAsInNlc3Npb25fc3RhdGUiOiIzOWQ1NDhmNC1kNTE1LTRhMjUtODE3Yy1kNjNkZjc1NmMyZmMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImluZm8xLWV4ZXJjaXNlcy5pZmkudXpoLmNoLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL2I3NWJlNzg2LWYxYzEtMzJkMy05OWZjLThhZjRmZjE1NWFkZS9JbmZvcm1hdGljcyAxIC0gc3R1ZGVudHMiLCIvZGM0NGZkM2YtMjIxMi0zMjVlLTkxMzktNzJmY2YxOWViNmIzL0luZm9ybWF0aWNzIDIgLSBzdHVkZW50cyIsIi9iMzkzYWFlNy0zMDEwLTNkMjgtYTI3Ni1lNDZjYWE5YTQzMDAvTm9uLVRlY2huaWNhbCBQeXRob24gSW50cm9kdWN0aW9uIC0gc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.VFWG1NnF7raGL65SjVdFGNGfTUE6yTsviAHk8ERILhe0xNShg3FtS9iaVS7zZLEuZ1p2z-uZHMQTMCGS--fA_lhLcRd9HzlnY7h3wTKy68G7PvzbpV-216_AcDv80DtqRDKiRauZlZ-6GPxMudcU9gY8kbmTOJM6IpoXAFDjdVxh3ZHSGg1pgTK6_EN7RpTZF_TTUUYYPY8OlCXIm8n-kcdQRuO73M8lKU8oGAAk0xaMV_gi6ubuBOY-EP_jgpMVkjHCxiLrJV-Yp_s8Orf5o5wuaMXU12RIgDZr6tE_lzc-BuZGuSj4-XeW1DrvnI4Yxgb9x0jkDkdIgBQGw2WHDQ"
  val headers_15 = Map(
    "Content-Type" -> "application/json",
    "authorization" -> s"Bearer ${bearerToken}")

  val headers_26 = Map("Accept" -> "application/font-woff2;q=1.0,application/font-woff;q=0.9,*/*;q=0.8")

  val headers_50 = Map(
    "Accept" -> "application/json",
    "authorization" -> s"bearer ${bearerToken}")

  val headers_27 = Map(
    "Content-Type" -> "application/json",
    "Origin" -> "https://info1-exercises.ifi.uzh.ch",
    "authorization" -> s"Bearer ${bearerToken}")

  val uri1 = "http://detectportal.firefox.com/success.txt"

  val scn = scenario("RecordedSimulation")
    .exec(getGroup("Execute code group",
      exec(http("POST submission")
        .post("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
        .headers(headers_27)
        .body(RawFileBody("RecordedSimulation2_0027_request.txt"))
        .check(status.is(200))
        .check(jsonPath("$.evalId").saveAs("evalId"))
      )
        // remove polling from the test scenario
        .pause(1)
        .asLongAs(session => session.contains("evalId") && !session("submissionCompleted").asOption[String].getOrElse("").equals("ok")) {
          exec(http("Poll for evaluation")
            .get("/api/submissions/evals/${evalId}")
            .headers(headers_27)
            .check(bodyString.exists)
            .check(status.is(200))
            .check(jsonPath("$.status").saveAs("submissionCompleted")))
            .pause(1000 milliseconds)
        }
    ))

  private def getGroup(groupName: String, chain: ChainBuilder) = {
    group(groupName)(chain)
  }

  //      setUp(scn.inject(atOnceUsers(25))).protocols(httpProtocol)
  setUp(
    scn.inject(
      atOnceUsers(25),
      rampUsers(100) during (10 seconds),
      nothingFor(10 seconds),
      rampUsers(300) during (10 seconds),
//      nothingFor(25 seconds),
//      rampUsers(400) during (10 seconds)
    ).protocols(httpProtocol))
}