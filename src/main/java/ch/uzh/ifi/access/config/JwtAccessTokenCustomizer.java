package ch.uzh.ifi.access.config;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JwtAccessTokenCustomizer extends DefaultAccessTokenConverter
        implements JwtAccessTokenConverterConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(JwtAccessTokenCustomizer.class);

    private static final String CLIENT_NAME_ELEMENT_IN_JWT = "resource_access";

    private static final String AUD_ELEMENT_IN_JWT = "aud";

    private static final String ROLE_ELEMENT_IN_JWT = "roles";

    private static final String COURSES_ELEMENT_IN_JWT = "groups";

    private ObjectMapper mapper;

    public JwtAccessTokenCustomizer(ObjectMapper mapper) {
        this.mapper = mapper;
        logger.info("Initialized {}", JwtAccessTokenCustomizer.class.getSimpleName());
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
        logger.info("Configured {}", JwtAccessTokenConverter.class.getSimpleName());
    }

    /**
     * Spring oauth2 expects roles under authorities element in tokenMap,
     * but keycloak provides it under resource_access. Hence extractAuthentication
     * method is overriden to extract roles from resource_access.
     *
     * @return OAuth2Authentication with authorities for given application
     */
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> tokenMap) {
        logger.debug("Begin extractAuthentication: tokenMap = {}", tokenMap);
        JsonNode token = mapper.convertValue(tokenMap, JsonNode.class);

        String subject = extractSubject(token);
        String username = extractUsername(token);
        Set<String> audienceList = extractAudiences(token);
        List<GrantedAuthority> authorities = extractRoles(token);
        Set<GrantedCourseAccess> courseAccesses = extractCourses(token);

        OAuth2Authentication authentication = super.extractAuthentication(tokenMap);
        OAuth2Request oAuth2Request = authentication.getOAuth2Request();

        OAuth2Request request =
                new OAuth2Request(oAuth2Request.getRequestParameters(),
                        oAuth2Request.getClientId(),
                        authorities, true,
                        oAuth2Request.getScope(),
                        audienceList, null, null, null);

        Authentication usernamePasswordAuthentication =
                new UsernamePasswordAuthenticationToken(username,
                        "N/A", authorities);

        logger.debug("End extractAuthentication");
        return new CourseAuthentication(request, usernamePasswordAuthentication, courseAccesses, subject);
    }

    String extractSubject(JsonNode jwt) {
        if (jwt.has("sub")) {
            return jwt.get("sub").asText();
        }
        return null;
    }
    String extractUsername(JsonNode jwt) {
        if (jwt.has("preferred_username")) {
            return jwt.get("preferred_username").asText();
        }
        return null;
    }

    private List<GrantedAuthority> extractRoles(JsonNode jwt) {
        logger.debug("Begin extractRoles: jwt = {}", jwt);
        Set<String> rolesWithPrefix = new HashSet<>();

        jwt.path(CLIENT_NAME_ELEMENT_IN_JWT)
                .elements()
                .forEachRemaining(e -> e.path(ROLE_ELEMENT_IN_JWT)
                        .elements()
                        .forEachRemaining(r -> rolesWithPrefix.add(r.asText())));

        final List<GrantedAuthority> authorityList =
                AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));

        logger.debug("End extractRoles: roles = {}", authorityList);
        return authorityList;
    }

    private Set<String> extractAudiences(JsonNode jwt) {
        logger.debug("Begin extractAudiences: jwt = {}", jwt);

        if (!jwt.has(AUD_ELEMENT_IN_JWT)) {
            throw new IllegalArgumentException("Expected element " +
                    CLIENT_NAME_ELEMENT_IN_JWT + " not found in token");
        }

        final Set<String> audiences = new HashSet<>();

        JsonNode aud = jwt.path(AUD_ELEMENT_IN_JWT);
        aud.elements().forEachRemaining(el -> audiences.add(el.textValue()));

        logger.debug("End extractCourses: clients = {}", audiences);
        return audiences;
    }

    private Set<GrantedCourseAccess> extractCourses(JsonNode jwt) {
        logger.debug("Begin extractCourses: jwt = {}", jwt);

        final HashSet<GrantedCourseAccess> courses = new HashSet<>();
        if (jwt.has(COURSES_ELEMENT_IN_JWT)) {
            JsonNode coursesNode = jwt.path(COURSES_ELEMENT_IN_JWT);
            if (coursesNode.isArray()) {
                for (JsonNode element : coursesNode) {
                    courses.add(parseCourseAccess(element.textValue()));
                }
            }
        }
        logger.debug("End extractCourses: clients = {}", courses);
        return courses;
    }

    GrantedCourseAccess parseCourseAccess(String courseElement) {
        if (StringUtils.isEmpty(courseElement)) {
            return GrantedCourseAccess.empty();
        }

        List<String> group = Stream.of(courseElement.split("/")).filter(str -> !StringUtils.isEmpty(str)).collect(Collectors.toList());
        if (group.size() != 2) {
            throw new IllegalArgumentException(String.format("Cannot parse group to GrantedCourseAccess for string %s", courseElement));
        }
        String courseKey = group.get(0);
        boolean isStudent = group.get(1).toLowerCase().contains("students");
        boolean isAuthor = group.get(1).toLowerCase().contains("authors");

        return new GrantedCourseAccess(courseKey, isStudent, isAuthor);
    }
}