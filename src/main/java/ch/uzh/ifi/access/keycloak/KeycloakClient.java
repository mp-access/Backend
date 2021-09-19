package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.config.SecurityProperties;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.model.Course;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


@Slf4j
@Component
public class KeycloakClient {

    private static final String ADMIN_CLIENT_ID = "admin-cli";

    private final RealmResource realmResource;

    private final CourseServiceSetup.CourseProperties courseProperties;

    private final SecurityProperties securityProperties;

    private final EmailSender emailSender;

    @Autowired
    public KeycloakClient(SecurityProperties securityProperties, CourseServiceSetup.CourseProperties courseProperties, EmailSender emailSender) {
        this.courseProperties = courseProperties;
        this.securityProperties = securityProperties;
        this.emailSender = emailSender;
        Keycloak keycloak = KeycloakClient.keycloak(securityProperties);

        log.info("Keycloak instance info:");
        log.info("Auth server: " + securityProperties.getAuthServer());
        log.info("Realm: " + securityProperties.getRealm());

        realmResource = keycloak.realm(securityProperties.getRealm());
    }

    public KeycloakClient(SecurityProperties securityProperties, String realm, CourseServiceSetup.CourseProperties courseProperties, EmailSender emailSender) {
        this.courseProperties = courseProperties;
        this.securityProperties = securityProperties;
        this.emailSender = emailSender;
        Keycloak keycloak = KeycloakClient.keycloak(securityProperties);
        realmResource = keycloak.realm(realm);

    }

    public UserRepresentation getUserById(String id) {
        UserResource userResource = realmResource.users().get(id);
        return userResource.toRepresentation();
    }

    public Group enrollUsersInCourse(Course course) {
        log.info("Enrolling users in course: {}, {}, {}", course.getTitle(), course.getId(), course.getGitURL());

        log.info("Checking accounts for students");
        Users students = getUsersIfExistOrCreateUsers(course.getStudents());

        log.info("Checking accounts for assistants");
        Users assistants = getUsersIfExistOrCreateUsers(course.getAssistants());

        log.info("Checking accounts for admins");
        Users admins = getUsersIfExistOrCreateUsers(course.getAdmins());

        if (course.getTitle() != null && !course.getTitle().isEmpty()) {
            // A group can only be initialized with a title
            Group courseGroup = removeIfExistsAndCreateGroup(course.getId(), course.getTitle());

            UsersResource usersResource = realmResource.users();
            students.enrollUsersInGroup(courseGroup.getStudentsGroupId(), usersResource);
            assistants.enrollUsersInGroup(courseGroup.getAssistantsGroupId(), usersResource);
            admins.enrollUsersInGroup(courseGroup.getAdminsGroupId(), usersResource);

            log.info("Finished enrolling users in course: {}, {}, {}", course.getTitle(), course.getId(), course.getGitURL());
            return courseGroup;
        } else {
            log.warn("Cannot enroll users in course {} yet, as the course does not have a title with which to name the groups", course.getGitURL());
        }
        return null;
    }

    private Group removeIfExistsAndCreateGroup(final String courseId, String title) {
        try {
            GroupRepresentation groupRepresentation = realmResource.getGroupByPath(courseId);
            log.info("Found existing group for course '{}': {}. Removing it and creating it anew", title, groupRepresentation.getName());
            realmResource.groups().group(groupRepresentation.getId()).remove();
        } catch (NotFoundException e) {
            log.debug("Did not find any groups. Creating new group...", e);
        }

        Group group = Group.create(courseId, title, realmResource);
        log.info("Created group for course '{}': {}", title, group.getName());
        return group;
    }

    Users getUsersIfExistOrCreateUsers(List<String> emailAddresses) {

        log.info("Checking if all {} accounts already exist", emailAddresses.size());
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> existingUsers = new ArrayList<>();
        List<UserRepresentation> createdUsers = new ArrayList<>();
        List<String> failedToCreateUsers = new ArrayList<>();
        List<String> failedToSendEmailTo = new ArrayList<>();

        Consumer<UserRepresentation> addToExistingUsers = existingUsers::add;
        for (String emailAddress : emailAddresses) {
            Optional<UserRepresentation> user = findUserByEmail(emailAddress, usersResource);

            user.ifPresentOrElse(addToExistingUsers, () -> {
                try {
                    UserRepresentation newUser = createAndVerifyUser(emailAddress);
                    createdUsers.add(newUser);
                } catch (EmailSender.FailedToSendEmailException e) {
                    log.error("Failed to send email to user '{}'", emailAddress, e);
                    failedToSendEmailTo.add(emailAddress);
                } catch (Exception e) {
                    failedToCreateUsers.add(emailAddress);
                    log.error("Failed to create user {}", emailAddress, e);
                }
            });
        }

        log.info("Created {} new accounts", createdUsers.size());
        log.info("Found {} accounts which already exist", existingUsers.size());


        if (!failedToCreateUsers.isEmpty()) {
            log.warn("Failed to create {} new accounts", failedToCreateUsers.size());
            log.warn("Failed to create account for users: {}", String.join(", ", failedToCreateUsers));
        }

        if (!failedToSendEmailTo.isEmpty()) {
            log.warn("Failed to send {} emails", failedToSendEmailTo.size());
            log.warn("Failed to send emails to users: {}", String.join(", ", failedToSendEmailTo));
        }

        return new Users(existingUsers, createdUsers);
    }

    public Optional<UserRepresentation> findUsersByEmail(String emailAddress) {
        UsersResource usersResource = realmResource.users();
        return findUserByEmail(emailAddress, usersResource);
    }

    private Optional<UserRepresentation> findUserByEmail(final String email, UsersResource usersResource) {
        List<UserRepresentation> usersByEmail = usersResource.search(email, null, null, null, 0, 10);
        if (usersByEmail.isEmpty()) {
            return Optional.empty();
        }

        if (usersByEmail.size() > 1) {
            log.warn(String.format("Found %d users for email address :%s", usersByEmail.size(), email));
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
            emailSender.sendEmailToUser(userResource, securityProperties);
        } else {
            log.warn("No SMTP server configured. Cannot send out verification emails.");
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
