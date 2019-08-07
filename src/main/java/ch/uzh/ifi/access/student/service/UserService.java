package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import ch.uzh.ifi.access.student.model.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final KeycloakClient keycloakClient;

    public UserService(KeycloakClient keycloakClient) {
        this.keycloakClient = keycloakClient;
    }

    /**
     * Gets a list of users for the given course.
     * Since user data is sensitive, we enforce that only course assistants have access to this information.
     *
     * @param course course containing student email addresses
     * @return list of user entities
     * @see ch.uzh.ifi.access.course.config.CoursePermissionEvaluator#hasAdminAccessToCourse(CourseAuthentication, Course)
     */
    @PreAuthorize("@coursePermissionEvaluator.hasAdminAccessToCourse(authentication, #course)")
    public List<User> getCourseStudents(Course course) {
        List<String> emailAddresses = course.getStudents();
        return getUsersByEmailAddresses(emailAddresses);
    }

    /**
     * Gets a list of assistants for the given course.
     * This data is not as sensitive and is usually also published elsewhere, so anyone can read this.
     *
     * @param course course containing assistant email addresses
     * @return list of user entities
     */
    public List<User> getCourseAdmins(Course course) {
        List<String> emailAddresses = course.getAssistants();
        return getUsersByEmailAddresses(emailAddresses);
    }

    private List<User> getUsersByEmailAddresses(List<String> emailAddresses) {
        List<User> users = new ArrayList<>(emailAddresses.size());
        for (String emailAddress : emailAddresses) {
            UserRepresentation userRepresentation = keycloakClient
                    .findUsersByEmail(emailAddress)
                    .orElseThrow(() -> new NoUserFoundForEmail(emailAddress));

            users.add(User.of(userRepresentation));
        }
        return users;
    }

    private static class NoUserFoundForEmail extends RuntimeException {

        private NoUserFoundForEmail(String emailAddress) {
            super(String.format("No user found with email: %s", emailAddress));
        }
    }
}
