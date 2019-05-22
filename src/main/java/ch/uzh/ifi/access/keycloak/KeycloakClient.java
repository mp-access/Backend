package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.config.SecurityProperties;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
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

    private SecurityProperties securityProperties;

    private static final List<String> emailActionsAfterCreation = List.of("VERIFY_EMAIL", "UPDATE_PASSWORD", "UPDATE_PROFILE");

    private static final String ADMIN_CLIENT_ID = "admin-cli";

    private static final String DEFAULT_REALM = "dev";

    private final RealmResource realmResource;

    @Autowired
    public KeycloakClient(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;

        Keycloak keycloak = KeycloakClient.keycloak(securityProperties);
        realmResource = keycloak.realm(DEFAULT_REALM);
    }

    public KeycloakClient(SecurityProperties securityProperties, String realm) {
        this.securityProperties = securityProperties;
        Keycloak keycloak = KeycloakClient.keycloak(securityProperties);
        realmResource = keycloak.realm(realm);
    }

    public Group enrollUsersInCourse(Course course) {
        Users students = getUsersIfExistOrCreateUsers(course.getStudents());
        Users assistants = getUsersIfExistOrCreateUsers(course.getAssistants());

        Group courseGroup = removeIfExistsAndCreateGroup(course.getTitle());

        UsersResource usersResource = realmResource.users();
        students.enrollUsersInGroup(courseGroup.getStudentsGroupId(), usersResource);
        assistants.enrollUsersInGroup(courseGroup.getAuthorsGroupId(), usersResource);

        return courseGroup;
    }

    private Group removeIfExistsAndCreateGroup(final String title) {
        try {
            GroupRepresentation groupRepresentation = realmResource.getGroupByPath(title);
            logger.info(String.format("Found existing group: %s.\nRemoving it...", groupRepresentation.getName()));
            realmResource.groups().group(groupRepresentation.getId()).remove();
        } catch (NotFoundException e) {
            logger.info("Did not find any groups. Creating new group...", e);
        }

        return Group.create(title, realmResource.groups());
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
    private UserRepresentation createAndVerifyUser(final String email) {
        UsersResource usersResource = realmResource.users();

        String createdId = Utils.getCreatedId(usersResource.create(createUser(email)));
        UserResource userResource = usersResource.get(createdId);

        Map<String, String> smtpConfig = realmResource.toRepresentation().getSmtpServer();
        if (!smtpConfig.isEmpty()) {
            userResource.executeActionsEmail(emailActionsAfterCreation);
        } else {
            logger.warn("No smtp server configured. Cannot send out verification emails.");
        }
        return userResource.toRepresentation();
    }

    private UserRepresentation createUser(final String email) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(email);
        newUser.setEmail(email);
        newUser.setEnabled(true);
        return newUser;
    }

    static Keycloak keycloak(SecurityProperties securityProperties) {
        return KeycloakBuilder.builder()
                .serverUrl(securityProperties.getAuthServer())
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId(ADMIN_CLIENT_ID)
                .resteasyClient(
                        new ResteasyClientBuilder().connectionPoolSize(10).build()
                ).build();
    }
}
