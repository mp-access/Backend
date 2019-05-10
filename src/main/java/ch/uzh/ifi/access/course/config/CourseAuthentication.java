package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.Model.security.GrantedCourseAccess;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Set;

public class CourseAuthentication extends OAuth2Authentication {

    private final Set<GrantedCourseAccess> courseAccesses;

    /**
     * Construct an OAuth 2 authentication. Since some grant types don't require user authentication, the user
     * authentication may be null.
     *
     * @param storedRequest      The authorization request (must not be null).
     * @param userAuthentication The user authentication (possibly null).
     */
    public CourseAuthentication(OAuth2Request storedRequest, Authentication userAuthentication, Set<GrantedCourseAccess> courseAccesses) {
        super(storedRequest, userAuthentication);
        this.courseAccesses = Set.copyOf(courseAccesses);
    }

    public Set<GrantedCourseAccess> getCourseAccesses() {
        return courseAccesses;
    }
}