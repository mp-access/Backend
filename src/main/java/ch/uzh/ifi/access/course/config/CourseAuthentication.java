package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Set;

public class CourseAuthentication extends OAuth2Authentication {

    private final Set<GrantedCourseAccess> courseAccesses;

    private final String userId;

    /**
     * Construct an OAuth 2 authentication. Since some grant types don't require user authentication, the user
     * authentication may be null.
     *
     * @param storedRequest      The authorization request (must not be null).
     * @param userAuthentication The user authentication (possibly null).
     */
    public CourseAuthentication(OAuth2Request storedRequest, Authentication userAuthentication, Set<GrantedCourseAccess> courseAccesses, String userId) {
        super(storedRequest, userAuthentication);
        this.courseAccesses = Set.copyOf(courseAccesses);
        this.userId = userId;
    }

    public Set<GrantedCourseAccess> getCourseAccesses() {
        return courseAccesses;
    }

    public boolean hasAccess(String courseId) {
        return courseAccesses.stream().anyMatch(access -> access.evaluateAccess(courseId));
    }

    public boolean hasAdminAccess(String courseId) {
        return courseAccesses.stream().anyMatch(access -> access.evaluateAdminAccess(courseId));
    }

    public String getUserId() {
        return userId;
    }

    /**
     * Creates a new authentication object for the given user and sets it to the security context.
     * If the user is not allowed to impersonate a user, does nothing and returns null.
     *
     * @param userId   the user to impersonate
     * @param courseId the course for which the calling user has admin access
     * @return impersonated authentication object if admin, null otherwise.
     */
    public CourseAuthentication impersonateUser(String userId, String courseId) {
        if (hasAdminAccess(courseId)) {
            CourseAuthentication impersonatedAuth = new CourseAuthentication(getOAuth2Request(), this, courseAccesses, userId);
            SecurityContextHolder.getContext().setAuthentication(impersonatedAuth);
            return impersonatedAuth;
        }

        return null;
    }
}