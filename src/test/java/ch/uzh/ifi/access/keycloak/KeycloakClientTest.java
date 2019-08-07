package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.KeycloakClientTestConfiguration;
import ch.uzh.ifi.access.config.SecurityProperties;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.model.Course;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakClientTest {

    private static final String REALM_NAME = "testing";

    private KeycloakClient client;

    private RealmResource realmResource;

    private KeycloakClientTestConfiguration testConfiguration;

    @Before
    public void setUp() {
        this.testConfiguration = new KeycloakClientTestConfiguration();
        this.testConfiguration.createTestRealm();

        this.client = testConfiguration.testClient();
        realmResource = this.testConfiguration.getRealm();
    }

    @After
    public void tearDown() {
        this.testConfiguration.removeTestRealm();
    }

    private SecurityProperties properties() {
        SecurityProperties properties = new SecurityProperties();
        properties.setAuthServer("http://localhost:9999/auth");
        return properties;
    }

    @Test(expected = NotFoundException.class)
    public void getUserByIdNotFound() {
        client.getUserById("12");
    }

    @Test
    public void getUserById() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("test-user");
        userRepresentation.setFirstName("test");
        userRepresentation.setLastName("user");
        String userId = Utils.getCreatedId(realmResource.users().create(userRepresentation));

        UserRepresentation userById = client.getUserById(userId);

        Assert.assertNotNull(userById);
        Assert.assertEquals(userRepresentation.getUsername(), userById.getUsername());
        Assert.assertEquals(userRepresentation.getFirstName(), userById.getFirstName());
        Assert.assertEquals(userRepresentation.getLastName(), userById.getLastName());
    }

    @Test
    public void createUser() {
        final String email = "test@example.com";
        UserRepresentation user = client.createUser(email);
        Assert.assertEquals(user.getEmail(), email);
        Assert.assertEquals(user.getUsername(), email);

        CourseServiceSetup.CourseProperties courseProperties = testConfiguration.courseProperties();
        if (courseProperties.isUseDefaultPasswordForNewAccounts()) {
            Assert.assertEquals(user.getCredentials().size(), 1);
            Assert.assertEquals(user.getCredentials().get(0).getValue(), courseProperties.getDefaultPassword());
            Assert.assertEquals(user.getCredentials().get(0).getType(), CredentialRepresentation.PASSWORD);
        }
    }

    @Test
    public void createAndVerifyUser() {
        final String email = "test@example.com";
        UserRepresentation user = client.createAndVerifyUser(email);
        Assert.assertNotNull(user.getId());
        Assert.assertEquals(user.getEmail(), email);
        Assert.assertEquals(user.getUsername(), email);
    }

    @Test
    public void enrollUsersInCourse() {
        Course course = new Course();
        course.setTitle("Informatics 1");
        course.setStudents(List.of("alice@example.com", "bob@example.com"));
        course.setAssistants(List.of("ta@uzh.ch", "dr.prof@uzh.ch"));

        Group group = client.enrollUsersInCourse(course);

        List<String> studentEmails = group.getStudents().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());
        List<String> assistantsEmails = group.getAuthors().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());

        Assert.assertEquals(group.getName(), course.getTitle());
        Assert.assertEquals(Set.copyOf(studentEmails), Set.copyOf(course.getStudents()));
        Assert.assertEquals(Set.copyOf(assistantsEmails), Set.copyOf(course.getAssistants()));
    }

    @Test
    public void enrollUsersAlreadyEnrolledInAnotherCourse() {
        final String emailAddressStudentAndTa = "ta-student@uzh.ch";
        // Enroll users in a first course
        Course course = new Course();
        course.setTitle("Informatics 1");
        course.setStudents(List.of("alice@example.com", "bob@example.com"));
        course.setAssistants(List.of(emailAddressStudentAndTa, "dr.prof@uzh.ch"));

        Group info1 = client.enrollUsersInCourse(course);

        List<String> studentEmails = info1.getStudents().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());
        List<String> assistantsEmails = info1.getAuthors().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());

        Assert.assertEquals(info1.getName(), course.getTitle());
        Assert.assertEquals(Set.copyOf(studentEmails), Set.copyOf(course.getStudents()));
        Assert.assertEquals(Set.copyOf(assistantsEmails), Set.copyOf(course.getAssistants()));


        // Enrolling them in a second course should not remove them from the first one
        Course course2 = new Course();
        course2.setTitle("DBS");
        course2.setStudents(List.of("alice@example.com", "bob@example.com", emailAddressStudentAndTa));
        course2.setAssistants(List.of("dr.prof@uzh.ch"));
        Group dbs = client.enrollUsersInCourse(course2);

        studentEmails = dbs.getStudents().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());
        assistantsEmails = dbs.getAuthors().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());

        Assert.assertEquals(course2.getTitle(), dbs.getName());
        Assert.assertEquals(Set.copyOf(studentEmails), Set.copyOf(course2.getStudents()));
        Assert.assertEquals(Set.copyOf(assistantsEmails), Set.copyOf(course2.getAssistants()));

        // Get all students
        Set<UserRepresentation> info1Users = info1.getStudents().stream().collect(Collectors.toSet());
        Set<UserRepresentation> info1Authors = info1.getAuthors().stream().collect(Collectors.toSet());
        Set<UserRepresentation> dbsUsers = dbs.getStudents().stream().collect(Collectors.toSet());
        Set<UserRepresentation> dbsAuthors = dbs.getAuthors().stream().collect(Collectors.toSet());

        Set<UserRepresentation> users = new HashSet<>(info1Users);
        users.addAll(info1Authors);
        users.addAll(dbsUsers);
        users.addAll(dbsAuthors);

        // Get up-to-date version of users
        users = users.stream().map(user -> realmResource.users().get(user.getId()).toRepresentation()).collect(Collectors.toSet());

        for (UserRepresentation user : users) {
            List<GroupRepresentation> groups = realmResource.users().get(user.getId()).groups();
            Assert.assertEquals(groups.size(), 2);
        }

        // ta-student should be both student and author depending on the course
        UserRepresentation taStudent = users.stream().filter(u -> u.getEmail().equals(emailAddressStudentAndTa)).findFirst().orElseThrow();
        List<GroupRepresentation> groups = realmResource.users().get(taStudent.getId()).groups();
        for (GroupRepresentation group : groups) {
            if (group.getPath().contains(course.getTitle())) {
                Assert.assertEquals(String.format("ta-student should be an 'author' of course '%s'", course.getTitle()), "authors", group.getName());
            } else {
                Assert.assertEquals(String.format("ta-student should be a 'student' of course '%s'", course2.getTitle()), "students", group.getName());
            }
        }
    }

    @Test
    public void getUsersIfExistOrCreateUsers() {
        String existingEmail = "bob@example.com";
        List<String> userEmailAddresses = List.of("alice@example.com", existingEmail, "charlie@example.com");

        // Make sure all users are deleted
        Keycloak keycloak = KeycloakClient.keycloak(properties());
        UsersResource usersResource = keycloak.realm(REALM_NAME).users();
        usersResource.list().forEach(user -> usersResource.delete(user.getId()));

        // Create one just to test if method works
        UserRepresentation u1 = new UserRepresentation();
        u1.setEmail(existingEmail);
        u1.setUsername(existingEmail);
        usersResource.create(u1);

        Users users = client.getUsersIfExistOrCreateUsers(userEmailAddresses);

        Assert.assertEquals(2, users.getUsersCreated());
        Assert.assertEquals(users.size(), 3);
        Assert.assertEquals(Set.copyOf(users.emailAddresses()), Set.copyOf(userEmailAddresses));

    }
}