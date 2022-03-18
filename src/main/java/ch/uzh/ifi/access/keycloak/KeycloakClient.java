package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.config.AccessProperties;
import ch.uzh.ifi.access.course.model.Course;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
public class KeycloakClient {

    private static final String ADMIN_CLIENT_ID = "admin-cli";

    private AccessProperties accessProperties;

    private KeycloakSpringBootProperties keycloakProperties;

    protected Keycloak keycloak;

    public KeycloakClient(AccessProperties accessProperties, KeycloakSpringBootProperties keycloakProperties) {
        this.accessProperties = accessProperties;
        this.keycloakProperties = keycloakProperties;
        this.keycloak = initKeycloak();
    }

    UsersResource getRealmUsers() {
        return keycloak.realm(keycloakProperties.getRealm()).users();
    }

    RolesResource getRealmRoles() {
        return keycloak.realm(keycloakProperties.getRealm()).roles();
    }

    public UserRepresentation getUserById(String userId) {
        return getRealmUsers().get(userId).toRepresentation();
    }

    public Roles enrollUsersInCourse(Course course) {
        log.info("Enrolling users in course: {}, {}, {}", course.getTitle(), course.getId(), course.getGitURL());

        // A role is initialized based on the course title and semester
        Roles courseRoles = new Roles(course.getRoleName(), getRealmRoles());
        createOrValidateCourseRoles(courseRoles);
        Set<String> existingCourseUsers = getUsersByRole(course.getRoleName());
        log.info("Checking accounts for students");
        createOrValidateUsers(existingCourseUsers, course.getStudents(), courseRoles.getStudentRolesForCourse());
        log.info("Checking accounts for assistants");
        createOrValidateUsers(existingCourseUsers,course.getAssistants(), courseRoles.getAssistantRolesForCourse());
        log.info("Checking accounts for admins");
        createOrValidateUsers(existingCourseUsers, course.getAdmins(), courseRoles.getAdminRolesForCourse());

        log.info("Finished enrolling users in course: {}, {}", course.getTitle(), course.getGitURL());
        return courseRoles;
    }

    void createOrValidateCourseRoles(Roles roles) {
        RolesResource rolesResource = getRealmRoles();
        List<RoleRepresentation> existingRoles = rolesResource.list(roles.getCourseRoleName(), false);
        if (existingRoles.isEmpty()) {
            roles.createCourseRoles();
            log.info("Created new roles for the course: {}", roles.getCourseRoleName());
        } else
            log.info("Found existing roles for the course: {}", roles.getCourseRoleName());
    }

    void createOrValidateUsers(Set<String> existingCourseUsers, List<String> emailAddresses, List<RoleRepresentation> rolesToAssign) {
        List<String> newUsers = new ArrayList<>();
        List<String> existingUsers = new ArrayList<>();
        emailAddresses.stream().filter(Predicate.not(existingCourseUsers::contains)).forEach(emailAddress ->
            findUserByEmail(emailAddress).ifPresentOrElse(user -> {
                try {
                    assignUserToRoles(user.getId(), rolesToAssign);
                    existingUsers.add(emailAddress);
                }
                catch (Exception e) {
                    log.error("Failed to enroll in course {}", emailAddress, e);
                }
            }, () -> {
                try {
                    String userId = createAndVerifyUser(emailAddress);
                    assignUserToRoles(userId, rolesToAssign);
                    newUsers.add(emailAddress);
                } catch (Exception e) {
                    log.error("Failed to create new account for user {}", emailAddress, e);
                }
            })
        );
        if (!newUsers.isEmpty())
            log.info("Created {} new accounts", newUsers.size());
        if (!existingUsers.isEmpty())
            log.info("Enrolled {} existing accounts", existingUsers.size());
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
            log.warn(String.format("Found %d users for email address :%s", usersByEmail.size(), email));

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
    protected String createAndVerifyUser(String email) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setEnabled(true);
        newUser.setEmail(email);

        if (accessProperties.isUseDefaultPasswordForNewAccounts()) {
            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(accessProperties.getDefaultPassword());
            newUser.setCredentials(List.of(credentials));
        }
        return Utils.getCreatedId(getRealmUsers().create(newUser));
    }

    private Keycloak initKeycloak() {
        String realmName = keycloakProperties.getRealm();
        log.info("Initialising Keycloak for realm '{}' and server URL {}",
                realmName, keycloakProperties.getAuthServerUrl());
        ResteasyClientBuilder restBuilder = new ResteasyClientBuilder().connectionPoolSize(10);
        Keycloak keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm("master")
                .username(accessProperties.getAdminCLIUsername())
                .password(accessProperties.getAdminCLIPassword())
                .clientId(ADMIN_CLIENT_ID)
                .resteasyClient(restBuilder.build())
                .build();

        if (keycloakClient.realms().findAll().stream().noneMatch(realm -> realm.getRealm().equals(keycloakProperties.getRealm())))
            keycloakClient.realms().create(createAppRealm(realmName));

        return keycloakClient;
    }

    protected RealmRepresentation createAppRealm(String realmName) {
        log.info("Creating a new realm with the name '{}'...", realmName);
        RolesRepresentation basicUserRoles = new RolesRepresentation();
        basicUserRoles.setRealm(List.of(
                new RoleRepresentation(Roles.STUDENT_ROLE, "Basic student role", false),
                new RoleRepresentation(Roles.ASSISTANT_ROLE, "Basic assistant role", false),
                new RoleRepresentation(Roles.ADMIN_ROLE, "Basic admin role", false)));
        ClientRepresentation backendClient = new ClientRepresentation();
        backendClient.setId(realmName + "-backend");
        backendClient.setEnabled(true);
        backendClient.setBearerOnly(true);
        ClientRepresentation frontendClient = new ClientRepresentation();
        frontendClient.setId(realmName + "-frontend");
        frontendClient.setEnabled(true);
        frontendClient.setPublicClient(true);
        frontendClient.setRedirectUris(List.of("*"));
        frontendClient.setWebOrigins(List.of("*"));
        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(realmName);
        newRealm.setEnabled(true);
        newRealm.setRegistrationEmailAsUsername(true);
        newRealm.setRoles(basicUserRoles);
        newRealm.setClients(List.of(backendClient, frontendClient));
        return newRealm;
    }
}
