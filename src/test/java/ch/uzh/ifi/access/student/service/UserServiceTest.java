package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.KeycloakClientTestConfiguration;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.student.model.User;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.UUID;

public class UserServiceTest {

    private RealmResource realmResource;

    private UserService userService;

    @Before
    public void setUp() {
        KeycloakClientTestConfiguration testConfiguration = new KeycloakClientTestConfiguration();
        testConfiguration.createTestRealm();
        this.realmResource = testConfiguration.getRealm();

        this.userService = new UserService(testConfiguration.testClient());
    }

    @Test
    public void getUsersByEmailAddresses() {
        final String email1 = "test1@email.com";
        final String email2 = "test2@email.com";
        final String adminEmail = "admin@email.com";
        createUserWithEmail(email1);
        createUserWithEmail(email2);
        createUserWithEmail(adminEmail);

        Course course = new Course("");
        course.setStudents(List.of(email1, email2));
        course.setAssistants(List.of(adminEmail));
        List<User> users = userService.getCourseStudents(course);
        Assertions.assertThat(users).hasSize(2);
        Assertions.assertThat(users.get(0).getEmailAddress()).isEqualTo(email1);
        Assertions.assertThat(users.get(1).getEmailAddress()).isEqualTo(email2);
    }

    @Test
    public void getAssistantsByEmailAddresses() {
        final String email1 = "test1@email.com";
        final String email2 = "test2@email.com";
        final String adminEmail = "admin@email.com";
        createUserWithEmail(email1);
        createUserWithEmail(email2);
        createUserWithEmail(adminEmail);

        Course course = new Course("");
        course.setStudents(List.of(email1, email2));
        course.setAssistants(List.of(adminEmail));
        List<User> users = userService.getCourseAdmins(course);
        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users.get(0).getEmailAddress()).isEqualTo(adminEmail);
    }

    private void createUserWithEmail(String email) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(UUID.randomUUID().toString());
        realmResource.users().create(user);
    }

    @Test(expected = RuntimeException.class)
    public void noUserFoundByEmail() {
        // Make sure no users exist
        realmResource
                .users()
                .list()
                .forEach(user -> realmResource.users().delete(user.getId()));

        final String nonExistingEmail = "test@email.com";
        Course course = new Course("");
        course.setStudents(List.of(nonExistingEmail));
        userService.getCourseStudents(course);
    }
}