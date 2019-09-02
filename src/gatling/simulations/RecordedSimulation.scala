import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RecordedSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://142.93.164.106")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:68.0) Gecko/20100101 Firefox/68.0")

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_2 = Map(
    "Accept-Encoding" -> "gzip, deflate",
    "Pragma" -> "no-cache")

  val headers_5 = Map(
    "Content-Type" -> "application/json",
    "authorization" -> "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiIzOWI2NGY1NS02NjhmLTRkYzUtYjE3Yy1kZWRkNzFiMDUxY2YiLCJleHAiOjE1NjcyOTk5ODUsIm5iZiI6MCwiaWF0IjoxNTY3MjYzOTg1LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYjIyOTg0MjMtNGMxMi00MjZlLTg3ZmItNmNmMWM1MGE0OGU4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiIxZTAxZDRiYy0yMjE5LTQ2YTUtOGNhYS00ODQzZWNjODgwYjUiLCJhdXRoX3RpbWUiOjE1NjcyNjM5ODUsInNlc3Npb25fc3RhdGUiOiJhOTM3MDBmMy0wNjc2LTQyNmMtODA5Yi03YzQwODFiOWQxMDgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvc3R1ZGVudHMiLCIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyIsIi9Ob24tVGVjaG5pY2FsIFB5dGhvbiBJbnRyb2R1Y3Rpb24vc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.JMa1zWKIG8oUC7cvfYivF1huH8sVK7UOXeYlIzoELd-E2tvVUem7IZ85L_11N0h5rDeWrQCBcxjCmiVPjw_3K9wT_hUDJB-cikMWg0yYW_bz8_JSfbYpM41U1qPgBcUaqNm95jz7nkdDrrHGyG2MPMlqKx7sg3sijaCfWFf0e4XJI6a-0HJuVIwgekXpfhQS5nb_ExqE9XOMrTkXjooMUGr_qnMPMI8TIOwCMzEHmXBPJl12C721oEhP8U0y9YntmnusqdBNgi92NViaY__hAkxMnVb3j7h-U5aBQfkiUmR3eWvo7bE5PxrdCIyb6lTVHQmVM7rIDNVa8JI3up3Nag")

  val headers_27 = Map(
    "Content-Type" -> "application/json",
    "Origin" -> "https://142.93.164.106",
    "authorization" -> "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiIzOWI2NGY1NS02NjhmLTRkYzUtYjE3Yy1kZWRkNzFiMDUxY2YiLCJleHAiOjE1NjcyOTk5ODUsIm5iZiI6MCwiaWF0IjoxNTY3MjYzOTg1LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYjIyOTg0MjMtNGMxMi00MjZlLTg3ZmItNmNmMWM1MGE0OGU4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiIxZTAxZDRiYy0yMjE5LTQ2YTUtOGNhYS00ODQzZWNjODgwYjUiLCJhdXRoX3RpbWUiOjE1NjcyNjM5ODUsInNlc3Npb25fc3RhdGUiOiJhOTM3MDBmMy0wNjc2LTQyNmMtODA5Yi03YzQwODFiOWQxMDgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvc3R1ZGVudHMiLCIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyIsIi9Ob24tVGVjaG5pY2FsIFB5dGhvbiBJbnRyb2R1Y3Rpb24vc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.JMa1zWKIG8oUC7cvfYivF1huH8sVK7UOXeYlIzoELd-E2tvVUem7IZ85L_11N0h5rDeWrQCBcxjCmiVPjw_3K9wT_hUDJB-cikMWg0yYW_bz8_JSfbYpM41U1qPgBcUaqNm95jz7nkdDrrHGyG2MPMlqKx7sg3sijaCfWFf0e4XJI6a-0HJuVIwgekXpfhQS5nb_ExqE9XOMrTkXjooMUGr_qnMPMI8TIOwCMzEHmXBPJl12C721oEhP8U0y9YntmnusqdBNgi92NViaY__hAkxMnVb3j7h-U5aBQfkiUmR3eWvo7bE5PxrdCIyb6lTVHQmVM7rIDNVa8JI3up3Nag")

  val uri1 = "http://detectportal.firefox.com/success.txt"

  val scn = scenario("RecordedSimulation2")
    .exec(http("request_0")
      .get("/auth/realms/dev/protocol/openid-connect/auth?client_id=access-frontend&redirect_uri=https%3A%2F%2F142.93.164.106%2Fcourses&state=6c7ca1b9-49b1-49f9-8dde-5f4d9a839185&response_mode=fragment&response_type=code&scope=openid&nonce=1e01d4bc-2219-46a5-8caa-4843ecc880b5")
      .headers(headers_0))
    .pause(1)
    .exec(http("request_1")
      .post("/auth/realms/dev/login-actions/authenticate?session_code=KI3fxqxtdjJKzF79fIMKO94CTy4-HPzMbbfwFqCGTIQ&execution=921f870c-cef4-47c6-97aa-e5256435830f&client_id=access-frontend&tab_id=WE81AaobYDc")
      .headers(headers_0)
      .formParam("username", "johann.schop@uzh.ch")
      .formParam("password", "test")
      .resources(http("request_2")
        .get(uri1 + "")
        .headers(headers_2),
        http("request_3")
          .get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html")
          .headers(headers_0),
        http("request_4")
          .post("/auth/realms/dev/protocol/openid-connect/token")
          .formParam("code", "316d5140-0cf6-449f-8d21-5f3f831adc0b.a93700f3-0676-426c-809b-7c4081b9d108.f94a434e-e42b-49e3-8cd2-1d72f8c3105d")
          .formParam("grant_type", "authorization_code")
          .formParam("client_id", "access-frontend")
          .formParam("redirect_uri", "https://142.93.164.106/courses"),
        http("request_5")
          .get("/api/courses")
          .headers(headers_5),
        http("request_6")
          .get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html/init?client_id=access-frontend&origin=https%3A%2F%2F142.93.164.106")))
    .pause(1)
    .exec(http("request_7")
      .get("/api/courses")
      .headers(headers_5)
      .resources(http("request_8")
        .get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
        .headers(headers_5)))
    .pause(1)
    .exec(http("request_9")
      .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
      .headers(headers_5)
      .resources(http("request_10")
        .get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
        .headers(headers_5)))
    .pause(1)
    .exec(http("request_11")
      .get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
      .headers(headers_5)
      .resources(http("request_12")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_5),
        http("request_13")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
          .headers(headers_5),
        http("request_14")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
          .headers(headers_5)))
    .pause(1)
    .exec(http("request_15")
      .get("/api/exercises/aa097709-e8a1-343e-9352-8ba412758379")
      .headers(headers_5)
      .resources(http("request_16")
        .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
        .headers(headers_5),
        http("request_17")
          .get("/api/submissions/exercises/aa097709-e8a1-343e-9352-8ba412758379")
          .headers(headers_5),
        http("request_18")
          .get("/api/submissions/exercises/aa097709-e8a1-343e-9352-8ba412758379/history")
          .headers(headers_5),
        http("request_19")
          .get("/api/exercises/ea1a481c-522f-3347-92e3-a604179ac82a")
          .headers(headers_5),
        http("request_20")
          .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
          .headers(headers_5),
        http("request_21")
          .get("/api/submissions/exercises/ea1a481c-522f-3347-92e3-a604179ac82a")
          .headers(headers_5),
        http("request_22")
          .get("/api/submissions/exercises/ea1a481c-522f-3347-92e3-a604179ac82a/history")
          .headers(headers_5),
        http("request_23")
          .get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
          .headers(headers_5),
        http("request_24")
          .get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
          .headers(headers_5),
        http("request_25")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
          .headers(headers_5),
        http("request_26")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
          .headers(headers_5)))
    .pause(2)
    .exec(http("request_27")
      .post("/api/submissions/exs/8e989eea-2a38-3147-a03f-d0c300f8c99d")
      .headers(headers_27)
      .body(RawFileBody("RecordedSimulation2_0027_request.txt")))
    .pause(1)
    .exec(http("request_28")
      .get("/api/submissions/evals/d0d4a10d-5292-4de7-828a-9d1ab0c57c03")
      .headers(headers_5)
      .resources(http("request_29")
        .get("/api/submissions/evals/d0d4a10d-5292-4de7-828a-9d1ab0c57c03")
        .headers(headers_5),
        http("request_30")
          .get("/api/submissions/5d6aa0b4ec57dc0001dcb0fa")
          .headers(headers_5),
        http("request_31")
          .get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
          .headers(headers_5)))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}