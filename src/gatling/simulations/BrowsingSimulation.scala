import io.gatling.core.Predef.{constantUsersPerSec, _}
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
    "authorization" -> "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiI4MDI5MmU0YS1hNGMxLTQ2NTgtOWIwMy0yN2U3OGUxOTBhNzgiLCJleHAiOjE1Njc5MDg2NjgsIm5iZiI6MCwiaWF0IjoxNTY3ODcyNjY5LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYTg5ZjAxNGQtZTVkMS00ZWRlLWI4NGItYjEzYjc2YzY2NGQyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiIwNDEyODllMi0wYTZjLTRlMjItOGRlZi1kZmFiMzBlNGE4NzMiLCJhdXRoX3RpbWUiOjE1Njc4NzI2NjgsInNlc3Npb25fc3RhdGUiOiI1Yzc4NDI5My0zOTgxLTQ0MmEtOGVmZS04MjhmZWMyZDAxM2MiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvYXV0aG9ycyIsIi9JbmZvcm1hdGljcyAyL2F1dGhvcnMiLCIvTm9uLVRlY2huaWNhbCBQeXRob24gSW50cm9kdWN0aW9uL2F1dGhvcnMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoibHVkd2lnLmFiZWlsbGVAdXpoLmNoIiwiZW1haWwiOiJsdWR3aWcuYWJlaWxsZUB1emguY2gifQ.M9HtfjGaavKBEfGKnhNWueU6nGhpT4Xb8v7iPU-fwGZ8O4inY_7DCv5rKdaO6A7JAts3yegPBRtYNLiLz7IoFXz4YzWFLo40S4hRiv9PEHfZEa52JlUlJRwa_X9uzBnJkfgcI8fF5dihZbBkC9921Iqu_h-Gayb5G0cbcj2zIctEA2xCvD2c0sdrQzI7HrhROGzeXPjy6YmkJlcffqeVHAIqLd7NBOrmRYYF8vIJe-iQWjfInvgDUp3lvtJ36OUs8zuQHurrOCNhXjhDN5XH-ECg5UKXrFDtQDXHce_kw8_K1aYadv7q7__Oys4X5GtKLAR-5YQh16KELeJvFPRwZg")

  val headers_26 = Map("Accept" -> "application/font-woff2;q=1.0,application/font-woff;q=0.9,*/*;q=0.8")

  val headers_50 = Map(
    "Accept" -> "application/json",
    "authorization" -> "bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiI4MDI5MmU0YS1hNGMxLTQ2NTgtOWIwMy0yN2U3OGUxOTBhNzgiLCJleHAiOjE1Njc5MDg2NjgsIm5iZiI6MCwiaWF0IjoxNTY3ODcyNjY5LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYTg5ZjAxNGQtZTVkMS00ZWRlLWI4NGItYjEzYjc2YzY2NGQyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiIwNDEyODllMi0wYTZjLTRlMjItOGRlZi1kZmFiMzBlNGE4NzMiLCJhdXRoX3RpbWUiOjE1Njc4NzI2NjgsInNlc3Npb25fc3RhdGUiOiI1Yzc4NDI5My0zOTgxLTQ0MmEtOGVmZS04MjhmZWMyZDAxM2MiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvYXV0aG9ycyIsIi9JbmZvcm1hdGljcyAyL2F1dGhvcnMiLCIvTm9uLVRlY2huaWNhbCBQeXRob24gSW50cm9kdWN0aW9uL2F1dGhvcnMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoibHVkd2lnLmFiZWlsbGVAdXpoLmNoIiwiZW1haWwiOiJsdWR3aWcuYWJlaWxsZUB1emguY2gifQ.M9HtfjGaavKBEfGKnhNWueU6nGhpT4Xb8v7iPU-fwGZ8O4inY_7DCv5rKdaO6A7JAts3yegPBRtYNLiLz7IoFXz4YzWFLo40S4hRiv9PEHfZEa52JlUlJRwa_X9uzBnJkfgcI8fF5dihZbBkC9921Iqu_h-Gayb5G0cbcj2zIctEA2xCvD2c0sdrQzI7HrhROGzeXPjy6YmkJlcffqeVHAIqLd7NBOrmRYYF8vIJe-iQWjfInvgDUp3lvtJ36OUs8zuQHurrOCNhXjhDN5XH-ECg5UKXrFDtQDXHce_kw8_K1aYadv7q7__Oys4X5GtKLAR-5YQh16KELeJvFPRwZg")

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
    .pause(10)
    .exec(http("GET profile")
      .get("/auth/realms/dev/protocol/openid-connect/userinfo")
      .headers(headers_50))


  //  setUp(scn.inject(atOnceUsers(500))).protocols(httpProtocol)
  setUp(
    scn.inject(
      atOnceUsers(25),
      rampUsers(100) during (10 seconds),
      nothingFor(10 seconds),
      atOnceUsers(25),
      rampUsers(100) during (10 seconds),
      rampUsers(400) during (10 seconds),
      nothingFor(10 seconds),
      rampUsers(400) during (5 seconds)
//      constantUsersPerSec(100) during (5 seconds)
      //      constantUsersPerSec(100) during (15 seconds) randomized,
      //      rampUsersPerSec(20) to 20 during (5 minutes),
      //      heavisideUsers(5000) during (60 seconds)
    ).protocols(httpProtocol)
  )
}