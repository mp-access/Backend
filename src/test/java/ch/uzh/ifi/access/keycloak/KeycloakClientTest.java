package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.config.AccessProperties;
import ch.uzh.ifi.access.course.model.Course;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(properties = {"keycloak.realm=test"}, classes = {KeycloakClient.class, AccessProperties.class})
class KeycloakClientTest {

    @Autowired
    private AccessProperties accessProperties;

    @Autowired
    private KeycloakClient keycloakClient;

    @Test
    void initKeycloakTest() {
        List<String> returnedRealms = keycloakClient.getRealmRoles().list().stream()
                        .map(RoleRepresentation::getName).collect(Collectors.toList());
        Assertions.assertTrue(returnedRealms.contains(Roles.STUDENT_ROLE));
        Assertions.assertTrue(returnedRealms.contains(Roles.ASSISTANT_ROLE));
        Assertions.assertTrue(returnedRealms.contains(Roles.ADMIN_ROLE));
    }

    @Test
    void getUserByIdTest() {
        UserRepresentation expectedUser = new UserRepresentation();
        expectedUser.setEmail("test-user");
        expectedUser.setFirstName("test");
        expectedUser.setLastName("user");
        String expectedUserId = Utils.getCreatedId(keycloakClient.getRealmUsers().create(expectedUser));

        UserRepresentation returnedUser = keycloakClient.getUserById(expectedUserId);
        Assertions.assertNotNull(returnedUser);
        Assertions.assertEquals(expectedUser.getEmail(), returnedUser.getUsername());
        Assertions.assertEquals(expectedUser.getFirstName(), returnedUser.getFirstName());
        Assertions.assertEquals(expectedUser.getLastName(), returnedUser.getLastName());
        keycloakClient.getRealmUsers().get(expectedUserId).remove();
    }

    @Test
    void getUserByIdNotFoundTest() {
        Assertions.assertThrows(NotFoundException.class, () -> keycloakClient.getUserById("12"));
    }

    @Test
    void createAndVerifyUserTest() {
        String email = "test@example.com";
        String newUserId = keycloakClient.createAndVerifyUser(email);
        UserRepresentation newUser = keycloakClient.getUserById(newUserId);
        Assertions.assertNotNull(newUser.getId());
        Assertions.assertEquals(email, newUser.getEmail());
        Assertions.assertEquals(email, newUser.getUsername());

        // Fetch the credentials directly to receive updated data (newUser.getCredentials() might be null)
        List<CredentialRepresentation> newUserCredentials = keycloakClient.getRealmUsers().get(newUserId).credentials();
        if (accessProperties.isUseDefaultPasswordForNewAccounts()) {
            Assertions.assertEquals(1, newUserCredentials.size());
            Assertions.assertEquals(CredentialRepresentation.PASSWORD, newUserCredentials.get(0).getType());
        }
        keycloakClient.getRealmUsers().get(newUserId).remove();
    }

    @Test
    void enrollUsersInCourseTest() {
        Course course = new Course("Informatics 1");
        course.setTitle("Informatics 1");
        course.setStudents(List.of("alice@example.com", "bob@example.com"));
        course.setAssistants(List.of("ta@uzh.ch", "dr.prof@uzh.ch"));
        course.setAdmins(List.of("admin@uzh.ch"));
        Roles courseRoles = keycloakClient.enrollUsersInCourse(course);
        Set<String> studentEmails = keycloakClient.getUsersByRole(courseRoles.getUserRoleNameForCourse(Roles.STUDENT_ROLE));
        Set<String> assistantsEmails = keycloakClient.getUsersByRole(courseRoles.getUserRoleNameForCourse(Roles.ASSISTANT_ROLE));
        Set<String> adminEmails = keycloakClient.getUsersByRole(courseRoles.getUserRoleNameForCourse(Roles.ADMIN_ROLE));

        Assertions.assertEquals(Set.copyOf(course.getStudents()), studentEmails);
        Assertions.assertEquals(Sets.union(Set.copyOf(course.getAssistants()), Set.copyOf(course.getAdmins())), assistantsEmails);
        Assertions.assertEquals(Set.copyOf(course.getAdmins()), adminEmails);
    }

    @Test
    void enrollUsersAlreadyEnrolledInAnotherCourse() {
        final String emailAddressStudentAndTa = "ta-student@uzh.ch";

        Course course1 = new Course("Course 1");
        course1.setTitle("Course 1");
        course1.setAssistants(List.of(emailAddressStudentAndTa));
        Roles course1Roles = keycloakClient.enrollUsersInCourse(course1);

        Course course2 = new Course("Course 2");
        course2.setTitle("Course 2");
        course2.setStudents(List.of(emailAddressStudentAndTa));
        Roles course2Roles = keycloakClient.enrollUsersInCourse(course2);

        // Enrolling a user in another course should not remove or change their roles in the first course
        Optional<UserRepresentation> studentAndTaUser = keycloakClient.findUserByEmail(emailAddressStudentAndTa);
        Assertions.assertTrue(studentAndTaUser.isPresent());
        List<String> studentAndTaUserRoles = keycloakClient.getRealmUsers().get(studentAndTaUser.get().getId()).roles()
                .realmLevel().listAll().stream().map(RoleRepresentation::getName).collect(Collectors.toList());
        Assertions.assertTrue(studentAndTaUserRoles.contains(course1Roles.getUserRoleNameForCourse(Roles.ASSISTANT_ROLE)));
        Assertions.assertTrue(studentAndTaUserRoles.contains(course2Roles.getUserRoleNameForCourse(Roles.STUDENT_ROLE)));
        Assertions.assertFalse(studentAndTaUserRoles.contains(course2Roles.getUserRoleNameForCourse(Roles.ASSISTANT_ROLE)));
    }
}