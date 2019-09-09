import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder

import scala.concurrent.duration._

class CodeExecutionSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://142.93.164.106")
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

  val bearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiIxZDEzMTdiNi04ZjIzLTRmMTQtYTQ0Ny04ZjY5NWM5NmYwNmYiLCJleHAiOjE1NjgwODIyMDEsIm5iZiI6MCwiaWF0IjoxNTY4MDQ2ODAwLCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYjIyOTg0MjMtNGMxMi00MjZlLTg3ZmItNmNmMWM1MGE0OGU4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiIyY2U0MzVhMy1lZGViLTQwMzktODQ4NS01MzFjYzRhYTQ2NWIiLCJhdXRoX3RpbWUiOjE1NjgwNDYyMDEsInNlc3Npb25fc3RhdGUiOiIxOTgxYzM0Yi01NjRhLTQ0MGYtYTUyNS0xMmQ1YWE5OTZmMjUiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvc3R1ZGVudHMiLCIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyIsIi9Ob24tVGVjaG5pY2FsIFB5dGhvbiBJbnRyb2R1Y3Rpb24vc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.NDJ9j-hRmxm1szN_aaL2CZNavHpCYjfUeNpD6Otf18iWjAYjZWukESwjTL7OB881Hj97ZAFNExT2WE3XK2IgoLszYn429KIquu-wyGr_T79uwqYA5Io-OyBUYP2ypH_-aLtuYJfIL6__kEPk8Hv6Mh8bkU9WlUdeWGyeUaC_ul1Pq9EVIu5uB-Uc-B3aoCdV40PRtXQWPiIhpdip5ZcBiioXI-48_E-8yVBBDcFDuAaSbCo3qhfjeqtchCRK4hO9NA27KjNXuV1SU-TCMuAQOnLfSnLLEKV4qkhR_1I6r1tFOf6ovHvX4K--fhJxW1py2ZCiigrZuwA5FLHQbZh_Hg"
  val headers_15 = Map(
    "Content-Type" -> "application/json",
    "authorization" -> s"Bearer ${bearerToken}")

  val headers_26 = Map("Accept" -> "application/font-woff2;q=1.0,application/font-woff;q=0.9,*/*;q=0.8")

  val headers_50 = Map(
    "Accept" -> "application/json",
    "authorization" -> s"bearer ${bearerToken}")

  val headers_27 = Map(
    "Content-Type" -> "application/json",
    "Origin" -> "https://142.93.164.106",
    "authorization" -> s"Bearer ${bearerToken}")

  val uri1 = "http://detectportal.firefox.com/success.txt"

  val scn = scenario("RecordedSimulation")
    .exec(http("POST submission")
      .post("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
      .headers(headers_27)
      .body(RawFileBody("RecordedSimulation2_0027_request.txt"))
      .check(jsonPath("$.evalId").saveAs("evalId"))
      .check(status.is(200))
    )
  // remove polling from the test scenario
      .pause(1)
      .asLongAs(session => !session("submissionCompleted").asOption[String].getOrElse("").equals("ok")) {
        exec(http("Poll for evaluation")
          .get("/api/submissions/evals/${evalId}")
          .headers(headers_27)
          .check(bodyString.exists)
          .check(jsonPath("$.status").saveAs("submissionCompleted")))
          .pause(1)
      }




//    setUp(scn.inject(atOnceUsers(25))).protocols(httpProtocol)
  setUp(
    scn.inject(
      atOnceUsers(25),
      rampUsers(100) during (10 seconds),
      nothingFor(10 seconds),
      atOnceUsers(25),
      rampUsers(200) during (10 seconds),
      nothingFor(10 seconds),
      rampUsers(500) during (25 seconds)

    ).protocols(httpProtocol))
}