import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder

import scala.concurrent.duration._

class BrowsingSimulation extends Simulation {

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

  val headers_15 = Map(
    "Content-Type" -> "application/json",
    "authorization" -> "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiIwNjAwMzgyZC1hZjFjLTRhZGEtOWJjZi04MjY4MTgxOTI2NWMiLCJleHAiOjE1NjgwOTg0NzcsIm5iZiI6MCwiaWF0IjoxNTY4MDYyNDc5LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYjIyOTg0MjMtNGMxMi00MjZlLTg3ZmItNmNmMWM1MGE0OGU4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiJjYzQxZDNkYy0yZjVlLTQ5YmUtYjcwYy0yNzViOWE5NWYzMTIiLCJhdXRoX3RpbWUiOjE1NjgwNjI0NzcsInNlc3Npb25fc3RhdGUiOiI4MDQ5YTUyMy0wNDAxLTQyOTctOGNlYS03ZDAwMzZhYmUyMTYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvc3R1ZGVudHMiLCIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyIsIi9Ob24tVGVjaG5pY2FsIFB5dGhvbiBJbnRyb2R1Y3Rpb24vc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.OYTG6SJ0wsBEZEKWJ7D9SGWXDEAmOc4v3-PlgFsfzvvQhNZhgI01daNJnQZuJ3h1DrLPwuxryz_6v1RXtCG46L-vqWmfxGMX7uNcKt5Ma6xe4ZEQQH82hp3DEoyNTFfbamtrwPym6FKWpWnMfZW_snQttxolZ1aFwANmJKxaTjQS6lFLbknHPpNy2RtMhhXL4NvcIGVu8tyUNVtNkyzQC6GzqPpJLRomBQwYraLKOtNHSTN1-U2f-3yHZRXs1KN-PuMpJu9XRbnX-mrsXOvYUfkOEAxzytJSB_iwTuTUHlZd7G2VIOI2yB0VfoZMng7Z3CRR1guzsby4af22VpBFqw")

  val headers_26 = Map("Accept" -> "application/font-woff2;q=1.0,application/font-woff;q=0.9,*/*;q=0.8")

  val headers_50 = Map(
    "Accept" -> "application/json",
    "authorization" -> "bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiIwNjAwMzgyZC1hZjFjLTRhZGEtOWJjZi04MjY4MTgxOTI2NWMiLCJleHAiOjE1NjgwOTg0NzcsIm5iZiI6MCwiaWF0IjoxNTY4MDYyNDc5LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYjIyOTg0MjMtNGMxMi00MjZlLTg3ZmItNmNmMWM1MGE0OGU4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiJjYzQxZDNkYy0yZjVlLTQ5YmUtYjcwYy0yNzViOWE5NWYzMTIiLCJhdXRoX3RpbWUiOjE1NjgwNjI0NzcsInNlc3Npb25fc3RhdGUiOiI4MDQ5YTUyMy0wNDAxLTQyOTctOGNlYS03ZDAwMzZhYmUyMTYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvc3R1ZGVudHMiLCIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyIsIi9Ob24tVGVjaG5pY2FsIFB5dGhvbiBJbnRyb2R1Y3Rpb24vc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.OYTG6SJ0wsBEZEKWJ7D9SGWXDEAmOc4v3-PlgFsfzvvQhNZhgI01daNJnQZuJ3h1DrLPwuxryz_6v1RXtCG46L-vqWmfxGMX7uNcKt5Ma6xe4ZEQQH82hp3DEoyNTFfbamtrwPym6FKWpWnMfZW_snQttxolZ1aFwANmJKxaTjQS6lFLbknHPpNy2RtMhhXL4NvcIGVu8tyUNVtNkyzQC6GzqPpJLRomBQwYraLKOtNHSTN1-U2f-3yHZRXs1KN-PuMpJu9XRbnX-mrsXOvYUfkOEAxzytJSB_iwTuTUHlZd7G2VIOI2yB0VfoZMng7Z3CRR1guzsby4af22VpBFqw")

  val uri1 = "http://detectportal.firefox.com/success.txt"

  val scn = scenario("RecordedSimulation")
    .exec(http("GET courses")
      .get("/api/courses")
      .headers(headers_15)
      .resources(http("GET course results")
        .get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
        .headers(headers_15)))
    .pause(2)
    .exec(http("GET assignment")
      .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
      .headers(headers_15)
      .resources(http("GET course results")
        .get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
        .headers(headers_15)))
    .pause(3)
    .exec(http("GET exercise 1")
      .get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
      .headers(headers_15)
      .resources(http("GET assignment")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_15),
        http("GET exercise 1 submissions")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
          .headers(headers_15),
        http("GET exercise 1 submissision history")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
          .headers(headers_15),
        http("GET exercise 1 resource")
          .get("/static/media/octicons.de59a972.woff2")
          .headers(headers_26),
        http("GET exercise 1 resource")
          .get("/static/media/mfixx.0a32a802.woff2")
          .headers(headers_26)))
    .pause(6)
    .exec(http("GET exercise 1 resource")
      .get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/files/7")
      .headers(headers_15)
      .resources(http("request_29")
        .get(uri1 + "")
        .headers(headers_0)))
    .pause(1)
    .exec(http("request_30")
      .get("/static/js/0.4e013a45.chunk.js")
      .resources(http("request_31")
        .get("/editor.worker.js")))
    .pause(2)
    .exec(http("GET exercise 2")
      .get("/api/exercises/aa097709-e8a1-343e-9352-8ba412758379")
      .headers(headers_15)
      .resources(http("GET assignment")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_15),
        http("GET last submission of exercise 2")
          .get("/api/submissions/exercises/aa097709-e8a1-343e-9352-8ba412758379")
          .headers(headers_15),
        http("GET exercise 2 submission history")
          .get("/api/submissions/exercises/aa097709-e8a1-343e-9352-8ba412758379/history")
          .headers(headers_15)))
    .pause(2)
    .exec(http("GET exercise 3")
      .get("/api/exercises/ea1a481c-522f-3347-92e3-a604179ac82a")
      .headers(headers_15)
      .resources(http("GET assignment")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_15),
        http("GET last submission exercise 3")
          .get("/api/submissions/exercises/ea1a481c-522f-3347-92e3-a604179ac82a")
          .headers(headers_15),
        http("GET exercise 3 submission history")
          .get("/api/submissions/exercises/ea1a481c-522f-3347-92e3-a604179ac82a/history")
          .headers(headers_15)))
    .pause(3)
    .exec(http("GET exercise 4")
      .get("/api/exercises/47ed12ee-8337-3080-945e-a8ba195e1c7b")
      .headers(headers_15)
      .resources(http("GET assignment")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_15),
        http("GET last submission exercise 4")
          .get("/api/submissions/exercises/47ed12ee-8337-3080-945e-a8ba195e1c7b")
          .headers(headers_15),
        http("request_43")
          .get("/static/js/33.95b441ae.chunk.js"),
        http("GET exercise 4 submission history")
          .get("/api/submissions/exercises/47ed12ee-8337-3080-945e-a8ba195e1c7b/history")
          .headers(headers_15)))
    .pause(3)
    .exec(http("GET exercise 5")
      .get("/api/exercises/3e36a76e-7450-33bf-b568-8f485c9c2cd4")
      .headers(headers_15)
      .resources(http("GET assignment")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_15),
        http("GET last submission exercise 5")
          .get("/api/submissions/exercises/3e36a76e-7450-33bf-b568-8f485c9c2cd4")
          .headers(headers_15),
        http("GET exercise 5 submission history")
          .get("/api/submissions/exercises/3e36a76e-7450-33bf-b568-8f485c9c2cd4/history")
          .headers(headers_15)))
    .pause(4)
    .exec(http("GET courses")
      .get("/api/courses")
      .headers(headers_15))


  //    setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(
    scn.inject(
      atOnceUsers(25),
      rampUsers(100) during (10 seconds),
      nothingFor(10 seconds),
      rampUsers(200) during (10 seconds),
      nothingFor(25 seconds),
      rampUsers(500) during (10 seconds),
      nothingFor(10 seconds),
      rampUsers(600) during (10 seconds)
    ).protocols(httpProtocol))
}