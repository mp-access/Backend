package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import ch.uzh.ifi.access.student.model.User;
import lombok.Value;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
     */
    @PreAuthorize("@coursePermissionEvaluator.hasAdminAccessToCourse(authentication, #course)")
    public UserQueryResult getCourseStudents(Course course) {
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
    public UserQueryResult getCourseAdmins(Course course) {
        List<String> emailAddresses = course.getAssistants();
        return getUsersByEmailAddresses(emailAddresses);
    }

    private UserQueryResult getUsersByEmailAddresses(List<String> emailAddresses) {
        List<User> users = new ArrayList<>(emailAddresses.size());
        List<String> accountsNotFound = new ArrayList<>(emailAddresses.size());
        for (String emailAddress : emailAddresses) {
            Optional<UserRepresentation> userRepresentation = keycloakClient
                    .findUsersByEmail(emailAddress);

            if (userRepresentation.isPresent()) {
                users.add(User.of(userRepresentation.get()));
            } else {
                logger.error("Could not find account matching email address {}", emailAddress);
                accountsNotFound.add(emailAddress);
            }

        }
        return new UserQueryResult(accountsNotFound, users);
    }

    @Value
    public static class UserQueryResult {

        private List<String> accountsNotFound;

        private List<User> usersFound;
    }
}
