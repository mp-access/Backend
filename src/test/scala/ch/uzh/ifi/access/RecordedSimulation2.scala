package ch.minimal

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		//    .baseUrl("https://142.93.164.106")
		.baseUrl("http://localhost:8080")
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

	val headers_2 = Map("Accept" -> "text/css,*/*;q=0.1")

	val headers_8 = Map("Accept" -> "image/webp,*/*")

	val headers_9 = Map("Accept" -> "application/json")

	val headers_18 = Map(
		"Content-Type" -> "application/json",
		"authorization" -> "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiI4ZTc5YzZiOS0xNDA0LTQzZjktYTY0Zi0wNDFmYzA0MDlhNTgiLCJleHAiOjE1NjcyOTE1MDksIm5iZiI6MCwiaWF0IjoxNTY3MjU1ODE2LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYjIyOTg0MjMtNGMxMi00MjZlLTg3ZmItNmNmMWM1MGE0OGU4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiI4YThkODNjNC04NzMxLTQ3NmYtOGM3ZC1hNDY0YWZiNjVhMWUiLCJhdXRoX3RpbWUiOjE1NjcyNTU1MDksInNlc3Npb25fc3RhdGUiOiJlNDZjNjg1ZC0zNDEzLTQzYjYtOWUzYi0zNjljZGIxOTBjMDEiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvc3R1ZGVudHMiLCIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyIsIi9Ob24tVGVjaG5pY2FsIFB5dGhvbiBJbnRyb2R1Y3Rpb24vc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.lesqsSpdtZn1x7VcvuqyyXVzNNt45a8VWVF2YvRkXMywmACNK9fOWQYgkuoUMtaLZSMvjvEDyVdGE_UYWA4G0UXp57ns_nEXunKjeLVDTBN74vyeFgp5yNAunPG-L264EJ0chZ_xKcUAxyRCw29nhYRCRe8u1OcnJwscrXsuRa98wGSLelP3oOjlL4rIV-vAD3bfiND3ZZa0Vbh5IPbdLlRuX84guw8XR9d-7vHgrc9ZzuOUBF4rLqqcGSQ_kzwQJ4UfGWQd90yrg1rQZW-CrcSsLpZh1R1zhMdmHshDKs8dsXbkObieceMP2E4u1pocPHctrtH8mUKLOQwmx7NoLw")

	val headers_28 = Map(
		"Content-Type" -> "application/json",
		"Origin" -> "https://142.93.164.106",
		"authorization" -> "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoS244M2ptd2pnNEtVLVpDZlRnbTVTaC0wS05IdHBTSTdDbTZWdXRfUk40In0.eyJqdGkiOiI4ZTc5YzZiOS0xNDA0LTQzZjktYTY0Zi0wNDFmYzA0MDlhNTgiLCJleHAiOjE1NjcyOTE1MDksIm5iZiI6MCwiaWF0IjoxNTY3MjU1ODE2LCJpc3MiOiJodHRwczovLzE0Mi45My4xNjQuMTA2L2F1dGgvcmVhbG1zL2RldiIsImF1ZCI6WyJjb3Vyc2Utc2VydmljZSIsImFjY291bnQiXSwic3ViIjoiYjIyOTg0MjMtNGMxMi00MjZlLTg3ZmItNmNmMWM1MGE0OGU4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWNjZXNzLWZyb250ZW5kIiwibm9uY2UiOiI4YThkODNjNC04NzMxLTQ3NmYtOGM3ZC1hNDY0YWZiNjVhMWUiLCJhdXRoX3RpbWUiOjE1NjcyNTU1MDksInNlc3Npb25fc3RhdGUiOiJlNDZjNjg1ZC0zNDEzLTQzYjYtOWUzYi0zNjljZGIxOTBjMDEiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbIjE0Mi45My4xNjQuMTA2LyoiLCJodHRwOi8vbG9jYWxob3N0OjMwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiL0luZm9ybWF0aWNzIDEvc3R1ZGVudHMiLCIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyIsIi9Ob24tVGVjaG5pY2FsIFB5dGhvbiBJbnRyb2R1Y3Rpb24vc3R1ZGVudHMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9oYW5uLnNjaG9wQHV6aC5jaCIsImVtYWlsIjoiam9oYW5uLnNjaG9wQHV6aC5jaCJ9.lesqsSpdtZn1x7VcvuqyyXVzNNt45a8VWVF2YvRkXMywmACNK9fOWQYgkuoUMtaLZSMvjvEDyVdGE_UYWA4G0UXp57ns_nEXunKjeLVDTBN74vyeFgp5yNAunPG-L264EJ0chZ_xKcUAxyRCw29nhYRCRe8u1OcnJwscrXsuRa98wGSLelP3oOjlL4rIV-vAD3bfiND3ZZa0Vbh5IPbdLlRuX84guw8XR9d-7vHgrc9ZzuOUBF4rLqqcGSQ_kzwQJ4UfGWQd90yrg1rQZW-CrcSsLpZh1R1zhMdmHshDKs8dsXbkObieceMP2E4u1pocPHctrtH8mUKLOQwmx7NoLw")

	val uri1 = "http://detectportal.firefox.com/success.txt"

	var jwt_token = ""
	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get(uri1 + "")
			.headers(headers_0))
		.pause(3)
		.exec(http("request_1")
			.get("/")
			.headers(headers_1)
			.resources(http("request_2")
				.get("/static/css/3.bc810aa9.chunk.css")
				.headers(headers_2),
				http("request_3")
					.get("/static/js/3.5028c6f8.chunk.js"),
				http("request_4")
					.get(uri1 + "")
					.headers(headers_0),
				http("request_5")
					.get("/static/css/main.75c01003.chunk.css")
					.headers(headers_2),
				http("request_6")
					.get(uri1 + "")
					.headers(headers_0),
				http("request_7")
					.get("/static/js/main.176a771b.chunk.js"),
				http("request_8")
					.get("/logo.png")
					.headers(headers_8),
				http("request_9")
					.get("/keycloak-prod.json")
					.headers(headers_9),
				http("request_10")
					.get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html")
					.headers(headers_1),
				http("request_11")
					.get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html/init?client_id=access-frontend&origin=https%3A%2F%2F142.93.164.106")))
		.pause(1)
		.exec(http("request_12")
			.get("/auth/realms/dev/protocol/openid-connect/auth?client_id=access-frontend&redirect_uri=https%3A%2F%2F142.93.164.106%2Fcourses&state=f705647a-8dc1-42d6-be17-821018112060&response_mode=fragment&response_type=code&scope=openid&nonce=bd50b2f3-dd00-4170-8a83-446067583c56")
			.headers(headers_1))
		.pause(1)
		.exec(http("request_13")
			.post("/auth/realms/dev/login-actions/authenticate?session_code=Is4kAzX112FoiGHj2UOhwgV48AKCltjjKRxkYuUjGTE&execution=afebdda4-93f7-488c-9c3d-96950be998c1&client_id=access-frontend&tab_id=27XJVVdHUUs")
			.headers(headers_1)
			.formParam("username", "johann.schop@uzh.ch")
			.formParam("password", "test")
			.check(headerRegex("Location", ".*&code=(.*)").saveAs("jwt_token"))
			.resources(http("request_14")
				.get(uri1 + "")
				.headers(headers_0),
				http("request_15")
					.get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html")
					.headers(headers_1),
				http("request_16")
					.post("/auth/realms/dev/protocol/openid-connect/token")
					.formParam("code", s"${jwt_token}")
					.formParam("grant_type", "authorization_code")
					.formParam("client_id", "access-frontend")
					.formParam("redirect_uri", "https://142.93.164.106/courses"),
				http("request_17")
					.get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html/init?client_id=access-frontend&origin=https%3A%2F%2F142.93.164.106"),
				http("request_18")
					.get("/api/courses")
					.headers(headers_18)))
		.pause(2)
		.exec(http("request_19")
			.get("/api/courses")
			.headers(headers_18)
			.resources(http("request_20")
				.get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
				.headers(headers_18)))
		.pause(1)
		.exec(http("request_21")
			.get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
			.headers(headers_18)
			.resources(http("request_22")
				.get("/api/students/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/results")
				.headers(headers_18),
				http("request_23")
					.get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
					.headers(headers_18),
				http("request_24")
					.get("/api/courses/b75be786-f1c1-32d3-99fc-8af4ff155ade/assignments/4aeef65e-fb99-3dcf-bd91-1e7a0ba6fec1")
					.headers(headers_18),
				http("request_25")
					.get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d")
					.headers(headers_18),
				http("request_26")
					.get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
					.headers(headers_18)))
		.pause(8)
		.exec(http("request_27")
			.get("/api/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/files/7")
			.headers(headers_18))
		.pause(2)
		.exec(http("request_28")
			.post("/api/submissions/exs/8e989eea-2a38-3147-a03f-d0c300f8c99d")
			.headers(headers_28)
			.body(RawFileBody("user-files/resources/RecordedSimulation_0028_request.txt")))
		.pause(1)
		.exec(http("request_29")
			.get("/api/submissions/evals/b2b55288-a2f9-46d9-926d-39ac6723a8d3")
			.headers(headers_18)
			.resources(http("request_30")
				.get("/api/submissions/evals/b2b55288-a2f9-46d9-926d-39ac6723a8d3")
				.headers(headers_18),
				http("request_31")
					.get("/api/submissions/5d69934ddaa4d400018723b5")
					.headers(headers_18),
				http("request_32")
					.get("/api/submissions/exercises/8e989eea-2a38-3147-a03f-d0c300f8c99d/history")
					.headers(headers_18)))
		.pause(4)
		.exec(http("request_33")
			.get("/auth/realms/dev/protocol/openid-connect/logout?redirect_uri=https%3A%2F%2F142.93.164.106%2F")
			.headers(headers_1)
			.resources(http("request_34")
				.get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html")
				.headers(headers_1),
				http("request_35")
					.get("/auth/realms/dev/protocol/openid-connect/login-status-iframe.html/init?client_id=access-frontend&origin=https%3A%2F%2F142.93.164.106")))

	setUp(scn.inject(atOnceUsers(10)))
		.protocols(httpProtocol)
}