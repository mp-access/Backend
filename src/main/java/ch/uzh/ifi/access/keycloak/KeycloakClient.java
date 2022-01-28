package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.config.SecurityProperties;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.model.Course;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@EnableConfigurationProperties(SecurityProperties.class)
public class KeycloakClient {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakClient.class);

    private static final String ADMIN_CLIENT_ID = "admin-cli";

    private final SecurityProperties securityProperties;

    private final CourseServiceSetup.CourseProperties courseProperties;

    protected Keycloak keycloak;

    public KeycloakClient(SecurityProperties securityProperties, CourseServiceSetup.CourseProperties courseProperties) {
        this.securityProperties = securityProperties;
        this.courseProperties = courseProperties;
        this.keycloak = initKeycloak();
    }

    UsersResource getRealmUsers() {
        return keycloak.realm(securityProperties.getRealm()).users();
    }

    RolesResource getRealmRoles() {
        return keycloak.realm(securityProperties.getRealm()).roles();
    }

    public UserRepresentation getUserById(String userId) {
        return getRealmUsers().get(userId).toRepresentation();
    }

    public Roles enrollUsersInCourse(Course course) {
        logger.info("Enrolling users in course: {}, {}, {}", course.getTitle(), course.getId(), course.getGitURL());

        // A role is initialized based on the course title and semester
        Roles courseRoles = new Roles(course.getRoleName(), getRealmRoles());
        createOrValidateCourseRoles(courseRoles);
        Set<String> existingCourseUsers = getUsersByRole(course.getRoleName());

        logger.info("Checking accounts for students");
        createOrValidateUsers(existingCourseUsers, course.getStudents(), courseRoles.getStudentRolesForCourse());

        logger.info("Checking accounts for assistants");
        createOrValidateUsers(existingCourseUsers,course.getAssistants(), courseRoles.getAssistantRolesForCourse());

        logger.info("Checking accounts for admins");
        createOrValidateUsers(existingCourseUsers, course.getAdmins(), courseRoles.getAdminRolesForCourse());

        logger.info("Finished enrolling users in course: {}, {}, {}", course.getTitle(), course.getId(), course.getGitURL());
        return courseRoles;
    }

    void createOrValidateCourseRoles(Roles roles) {
        RolesResource rolesResource = getRealmRoles();
        List<RoleRepresentation> existingRoles = rolesResource.list(roles.getCourseRoleName(), false);
        if (existingRoles.isEmpty()) {
            roles.createCourseRoles();
            logger.info("Created new roles for the course: {}", roles.getCourseRoleName());
        } else
            logger.info("Found existing roles for the course: {}", roles.getCourseRoleName());
    }

    void createOrValidateUsers(Set<String> existingCourseUsers, List<String> emailAddresses, List<RoleRepresentation> rolesToAssign) {
        logger.info("Checking if all {} accounts already exist", emailAddresses.size());

        int newUsersCount = 0;
        int existingNonEnrolledUsersCount = 0;
        int existingEnrolledUsersCount = 0;
        List<String> failedToCreateUsers = new ArrayList<>();
        List<String> failedToAddCourseRolesUsers = new ArrayList<>();
        List<String> failedToSendEmailTo = new ArrayList<>();

        for (String emailAddress : emailAddresses) {
            if (existingCourseUsers.contains(emailAddress))
                existingEnrolledUsersCount++;
            else {
                Optional<UserRepresentation> user = findUserByEmail(emailAddress);
                if (user.isPresent()) {
                    try {
                        assignUserToRoles(user.get().getId(), rolesToAssign);
                        existingNonEnrolledUsersCount++;
                    }
                    catch (Exception e) {
                       failedToAddCourseRolesUsers.add(emailAddress);
                       logger.error("Failed to add course role to user {}", emailAddress, e);
                    }
                }
                else {
                    try {
                        String userId = createAndVerifyUser(emailAddress);
                        assignUserToRoles(userId, rolesToAssign);
                        newUsersCount++;
                    } catch (EmailSender.FailedToSendEmailException e) {
                        logger.error("Failed to send email to user {}", emailAddress, e);
                        failedToSendEmailTo.add(emailAddress);
                    } catch (Exception e) {
                        failedToCreateUsers.add(emailAddress);
                        logger.error("Failed to create user {}", emailAddress, e);
                    }
                }
            }
        }

        logger.info("Created and enrolled in course {} new accounts", newUsersCount);
        logger.info("Enrolled {} existing accounts in course", existingNonEnrolledUsersCount);
        logger.info("Found {} existing accounts which are already enrolled in course", existingEnrolledUsersCount);

        if (!failedToCreateUsers.isEmpty())
            logger.warn("Failed to create {} new accounts: {}",
                    failedToCreateUsers.size(), String.join(", ", failedToCreateUsers));


        if (!failedToAddCourseRolesUsers.isEmpty())
            logger.warn("Failed to add course roles to {} existing accounts: {}",
                    failedToAddCourseRolesUsers.size(), String.join(", ", failedToAddCourseRolesUsers));


        if (!failedToSendEmailTo.isEmpty())
            logger.warn("Failed to send an email to {} users: {}",
                    failedToSendEmailTo.size(), String.join(", ", failedToSendEmailTo));
    }

    public Set<String> getUsersByRole(String roleName) {
        return getRealmRoles().get(roleName).getRoleUserMembers().stream()
                .map(UserRepresentation::getEmail).collect(Collectors.toSet());
    }

    public Optional<UserRepresentation> findUserByEmail(String email) {
        List<UserRepresentation> usersByEmail = getRealmUsers().search(email, null, null, null, 0, 10);
        if (usersByEmail.isEmpty())
            return Optional.empty();

        if (usersByEmail.size() > 1)
            logger.warn(String.format("Found %d users for email address :%s", usersByEmail.size(), email));

        return Optional.of(usersByEmail.get(0));
    }

    private void assignUserToRoles(String userId, List<RoleRepresentation> roles) {
        getRealmUsers().get(userId).roles().realmLevel().add(roles);
    }

    /**
     * Creates a new user with the given email address. Sets the email address as the username.
     * Sends a verification email to the user after creation.
     *
     * @param email email address
     * @return the newly created user
     */
    String createAndVerifyUser(String email) {
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
        return Utils.getCreatedId(getRealmUsers().create(newUser));
    }

    Keycloak initKeycloak() {
        ResteasyClientBuilder restBuilder = new ResteasyClientBuilder().connectionPoolSize(10);
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(securityProperties.getAuthServer())
                .realm("master")
                .username(securityProperties.getKeycloakApiAdmin())
                .password(securityProperties.getKeycloakApiPassword())
                .clientId(ADMIN_CLIENT_ID)
                .resteasyClient(restBuilder.build())
                .build();
        logger.info("Keycloak instance info:");
        logger.info("Auth server: " + securityProperties.getAuthServer());
        logger.info("Realm: " + securityProperties.getRealm());
        return keycloak;
    }
}
