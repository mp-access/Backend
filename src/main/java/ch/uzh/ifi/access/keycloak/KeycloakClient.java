package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.config.SecurityProperties;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.model.Course;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


@Component
public class KeycloakClient {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakClient.class);

    private static final List<String> emailActionsAfterCreation = List.of("VERIFY_EMAIL", "UPDATE_PASSWORD", "UPDATE_PROFILE");

    private static final String ADMIN_CLIENT_ID = "admin-cli";

    private static final String DEFAULT_REALM = "dev";

    private final RealmResource realmResource;

    private final CourseServiceSetup.CourseProperties courseProperties;

    @Autowired
    public KeycloakClient(SecurityProperties securityProperties, CourseServiceSetup.CourseProperties courseProperties) {
        this.courseProperties = courseProperties;
        Keycloak keycloak = KeycloakClient.keycloak(securityProperties);
        realmResource = keycloak.realm(securityProperties.getRealm());
    }

    public KeycloakClient(SecurityProperties securityProperties, String realm, CourseServiceSetup.CourseProperties courseProperties) {
        this.courseProperties = courseProperties;
        Keycloak keycloak = KeycloakClient.keycloak(securityProperties);
        realmResource = keycloak.realm(realm);
    }

    public UserRepresentation getUserById(String id) {
        UserResource userResource = realmResource.users().get(id);
        return userResource.toRepresentation();
    }

    public Group enrollUsersInCourse(Course course) {
        Users students = getUsersIfExistOrCreateUsers(course.getStudents());
        Users assistants = getUsersIfExistOrCreateUsers(course.getAssistants());

        if (course.getTitle() != null && !course.getTitle().isEmpty()) {
            // A group can only be initialized with a title
            Group courseGroup = removeIfExistsAndCreateGroup(course.getId(), course.getTitle());

            UsersResource usersResource = realmResource.users();
            students.enrollUsersInGroup(courseGroup.getStudentsGroupId(), usersResource);
            assistants.enrollUsersInGroup(courseGroup.getAuthorsGroupId(), usersResource);

            return courseGroup;
        } else {
            logger.warn("Cannot enroll users in course {} yet, as the course does not have a title with which to name the groups", course.getGitURL());
        }
        return null;
    }

    private Group removeIfExistsAndCreateGroup(final String courseId, String title) {
        try {
            GroupRepresentation groupRepresentation = realmResource.getGroupByPath(courseId);
            logger.info(String.format("Found existing group: %s.\nRemoving it and creating it anew", groupRepresentation.getName()));
            realmResource.groups().group(groupRepresentation.getId()).remove();
        } catch (NotFoundException e) {
            logger.debug("Did not find any groups. Creating new group...", e);
        }

        return Group.create(courseId, title, realmResource.groups());
    }

    Users getUsersIfExistOrCreateUsers(List<String> emailAddresses) {
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> existingUsers = new ArrayList<>();
        List<UserRepresentation> createdUsers = new ArrayList<>();

        Consumer<UserRepresentation> addToExistingUsers = existingUsers::add;
        for (String emailAddress : emailAddresses) {
            Optional<UserRepresentation> user = findUserByEmail(emailAddress, usersResource);

            user.ifPresentOrElse(addToExistingUsers, () -> {
                UserRepresentation newUser = createAndVerifyUser(emailAddress);
                createdUsers.add(newUser);
            });
        }

        logger.info(String.format("Created %d new accounts", createdUsers.size()));
        return new Users(existingUsers, createdUsers);
    }

    public Optional<UserRepresentation> findUsersByEmail(String emailAddress) {
        UsersResource usersResource = realmResource.users();
        return findUserByEmail(emailAddress, usersResource);
    }

    private Optional<UserRepresentation> findUserByEmail(final String email, UsersResource usersResource) {
        List<UserRepresentation> usersByEmail = usersResource.search(null, null, null, email, 0, 10);
        if (usersByEmail.isEmpty()) {
            return Optional.empty();
        }

        if (usersByEmail.size() > 1) {
            logger.warn(String.format("Found %d users for email address :%s", usersByEmail.size(), email));
        }

        return Optional.of(usersByEmail.get(0));
    }

    /**
     * Creates a new user with the given email address. Sets the email address as the username.
     * Sends a verification email to the user after creation.
     *
     * @param email email address
     * @return the newly created user
     */
    UserRepresentation createAndVerifyUser(final String email) {
        UsersResource usersResource = realmResource.users();

        String createdId = Utils.getCreatedId(usersResource.create(createUser(email)));
        UserResource userResource = usersResource.get(createdId);

        Map<String, String> smtpConfig = realmResource.toRepresentation().getSmtpServer();
        if (!smtpConfig.isEmpty()) {
            try {
                userResource.executeActionsEmail(emailActionsAfterCreation);
            } catch (Exception e) {
                logger.error("Failed to send email", e);
            }
        } else {
            logger.warn("No SMTP server configured. Cannot send out verification emails.");
        }
        return userResource.toRepresentation();
    }

    UserRepresentation createUser(final String email) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(email);
        newUser.setEmail(email);
        newUser.setEnabled(true);

        if (courseProperties.isUseDefaultPasswordForNewAccounts()) {
            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(courseProperties.getDefaultPassword());
            newUser.setCredentials(List.of(credentials));
        }
        return newUser;
    }

    public static Keycloak keycloak(SecurityProperties securityProperties) {
        ResteasyClientBuilder builder = new ResteasyClientBuilder().connectionPoolSize(10).disableTrustManager();
        return KeycloakBuilder.builder()
                .serverUrl(securityProperties.getAuthServer())
                .realm("master")
                .username(securityProperties.getKeycloakApiAdmin())
                .password(securityProperties.getKeycloakApiPassword())
                .clientId(ADMIN_CLIENT_ID)
                .resteasyClient(
                        builder.build()
                ).build();
    }
}
