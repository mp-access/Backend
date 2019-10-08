package ch.uzh.ifi.access.config;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JwtAccessTokenCustomizerTest {

    /**
     * A real token generated from keycloak to use for testing.
     * Base64 decoded version of:
     * eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlTHpRbEdkalA1VnBEMDZLOXFtaTVtSDZTUFVuN3NMaHg1aU9EWnIzSW1jIn0.eyJqdGkiOiI4NGM3NWFkMy1mYjk4LTRiYmEtYTdiOS1mZWQ0ZTNkNzRkMmEiLCJleHAiOjE1NTg5NzM1NDIsIm5iZiI6MCwiaWF0IjoxNTU4OTczMjQyLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0Ojk5OTkvYXV0aC9yZWFsbXMvZGV2IiwiYXVkIjpbImNvdXJzZS1zZXJ2aWNlIiwiYWNjb3VudCJdLCJzdWIiOiJlMDk1YjY1Ni1mMjE5LTRlMTMtYmMzOS1jNjMyOWY3MjVjYzEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhY2Nlc3MtZnJvbnRlbmQiLCJhdXRoX3RpbWUiOjE1NTg5NzMyNDIsInNlc3Npb25fc3RhdGUiOiJlNDUxOGY5Zi1kODJkLTQ2MzQtOWVlZS1lZmJhYTlhNzk0ZTgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJjb3Vyc2Utc2VydmljZSI6eyJyb2xlcyI6WyJzdHVkZW50Il19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJ1c2VyX25hbWUiOiJjYXJsLWZyaWVkbGljaC5hYmVsQHV6aC5jaCIsImdyb3VwcyI6WyIvSW5mb3JtYXRpY3MgMi9zdHVkZW50cyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJjYXJsLWZyaWVkbGljaC5hYmVsQHV6aC5jaCIsImVtYWlsIjoiY2FybC1mcmllZGxpY2guYWJlbEB1emguY2gifQ.htKCjA-3529kpZijemLIASwZFC9QJkAvlBFHnwwDl06wXhHtfEg8_Ht00dDJH4nA_QRo7otKI_s75Gpj-7nmwMbH6XToDbAqljUQEbrd75wS7t0Yy8qtNFulfy_m_pMxd4YagwLWp8TGDncHFgwSgqcZanlO0mnFfa89XQwr0lpKzUwft9PzlKqmaeet_tP-dU3-fdEK-Ws_NSv3GEfyQ7XcERM7uVib6iH5J5-i3wQMc5Y_A7phaC-onpIA4dsBZ-V_GCQbII7z6CBP1QVxI8l-KfmEc6vrNaWCYa92JmHNDly5cQxw1Gb1jkPpK1vZnRA23boQbgfiTVN0iW0jhA
     */
    private final String testToken = "{" +
            "  \"jti\": \"84c75ad3-fb98-4bba-a7b9-fed4e3d74d2a\"," +
            "  \"exp\": 1558973542," +
            "  \"nbf\": 0," +
            "  \"iat\": 1558973242," +
            "  \"iss\": \"http://localhost:9999/auth/realms/dev\"," +
            "  \"aud\": [" +
            "    \"course-service\"," +
            "    \"account\"" +
            "  ]," +
            "  \"sub\": \"e095b656-f219-4e13-bc39-c6329f725cc1\"," +
            "  \"typ\": \"Bearer\"," +
            "  \"azp\": \"access-frontend\"," +
            "  \"auth_time\": 1558973242," +
            "  \"session_state\": \"e4518f9f-d82d-4634-9eee-efbaa9a794e8\"," +
            "  \"acr\": \"1\"," +
            "  \"allowed-origins\": [" +
            "    \"*\"" +
            "  ]," +
            "  \"realm_access\": {" +
            "    \"roles\": [" +
            "      \"offline_access\"," +
            "      \"uma_authorization\"" +
            "    ]" +
            "  }," +
            "  \"resource_access\": {" +
            "    \"course-service\": {" +
            "      \"roles\": [" +
            "        \"student\"" +
            "      ]" +
            "    }," +
            "    \"account\": {" +
            "      \"roles\": [" +
            "        \"manage-account\"," +
            "        \"manage-account-links\"," +
            "        \"view-profile\"" +
            "      ]" +
            "    }" +
            "  }," +
            "  \"scope\": \"openid email profile\"," +
            "  \"email_verified\": false," +
            "  \"user_name\": \"carl-friedlich.abel@uzh.ch\"," +
            "  \"groups\": [" +
            "    \"/b75be786-f1c1-32d3-99fc-8af4ff155ade/students\"" +
            "  ]," +
            "  \"preferred_username\": \"carl-friedlich.abel@uzh.ch\"," +
            "  \"email\": \"carl-friedlich.abel@uzh.ch\"" +
            "}";

    @Test
    public void extractAuthentication() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(mapper);
        Map<String, String> map = mapper.readValue(testToken, Map.class);

        CourseAuthentication authentication = (CourseAuthentication) tokenCustomizer.extractAuthentication(map);

        Assert.assertEquals(authentication.getUserId(), "e095b656-f219-4e13-bc39-c6329f725cc1");
        Assert.assertEquals(authentication.getName(), "carl-friedlich.abel@uzh.ch");
        Assert.assertEquals(authentication.getCourseAccesses(), Set.of(new GrantedCourseAccess("b75be786-f1c1-32d3-99fc-8af4ff155ade", true, false, false)));
    }

    @Test
    public void extractSubjectTest() throws IOException {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode token = mapper.readTree(testToken);
        String subject = tokenCustomizer.extractSubject(token);

        Assert.assertEquals("e095b656-f219-4e13-bc39-c6329f725cc1", subject);
    }

    @Test
    public void parseCourseAccessStudent() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("/b75be786-f1c1-32d3-99fc-8af4ff155ade/Informatics 1 - students");

        Assert.assertEquals("b75be786-f1c1-32d3-99fc-8af4ff155ade", grantedCourseAccess.getCourse());
        Assert.assertTrue(grantedCourseAccess.isStudent());
        Assert.assertFalse(grantedCourseAccess.isAssistant());
        Assert.assertFalse(grantedCourseAccess.isAdmin());
    }

    @Test
    public void parseCourseAccessAssistant() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("/b75be786-f1c1-32d3-99fc-8af4ff155ade/Informatics 1 - assistants");

        Assert.assertEquals("b75be786-f1c1-32d3-99fc-8af4ff155ade", grantedCourseAccess.getCourse());
        Assert.assertFalse(grantedCourseAccess.isStudent());
        Assert.assertTrue(grantedCourseAccess.isAssistant());
        Assert.assertFalse(grantedCourseAccess.isAdmin());
    }

    @Test
    public void parseCourseAccessAdmin() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("/b75be786-f1c1-32d3-99fc-8af4ff155ade/Informatics 1 - admins");

        Assert.assertEquals("b75be786-f1c1-32d3-99fc-8af4ff155ade", grantedCourseAccess.getCourse());
        Assert.assertFalse(grantedCourseAccess.isStudent());
        Assert.assertFalse(grantedCourseAccess.isAssistant());
        Assert.assertTrue(grantedCourseAccess.isAdmin());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseCourseAccessMissingSubgroup() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        tokenCustomizer.parseCourseAccess("/b75be786-f1c1-32d3-99fc-8af4ff155ade/");
    }

    @Test
    public void parseCourseAccessMissingEmptyStringAndNull() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("");
        Assert.assertEquals(grantedCourseAccess, GrantedCourseAccess.empty());

        grantedCourseAccess = tokenCustomizer.parseCourseAccess(null);
        Assert.assertEquals(grantedCourseAccess, GrantedCourseAccess.empty());
    }
}