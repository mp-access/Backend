package ch.uzh.ifi.access.course.keycloak;

import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.config.SecurityProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakClientTest {

    private KeycloakClient client;

    private RealmResource realmResource;

    @Before
    public void setUp() throws Exception {
        SecurityProperties properties = new SecurityProperties();
        properties.setAuthServer("http://localhost:9999/auth");
        client = new KeycloakClient(properties, "Testing");

        // Make sure all users and groups are deleted
        Keycloak keycloak = client.keycloak();
        this.realmResource = keycloak.realm("Testing");

        UsersResource usersResource = realmResource.users();
        usersResource.list().forEach(user -> usersResource.delete(user.getId()));

        GroupsResource groupsResource = realmResource.groups();
        groupsResource.groups().forEach(g -> groupsResource.group(g.getId()).remove());
    }

    @Test
    public void enrollUsersInCourse() {
        Course course = new Course();
        course.title = "Informatics 1";
        course.students = List.of("alice@example.com", "bob@example.com");
        course.assistants = List.of("ta@uzh.ch", "dr.prof@uzh.ch");

        Group group = client.enrollUsersInCourse(course);

        List<String> studentEmails = group.getStudents().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());
        List<String> assistantsEmails = group.getAuthors().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());

        Assert.assertEquals(group.getName(), course.title);
        Assert.assertEquals(Set.copyOf(studentEmails), Set.copyOf(course.students));
        Assert.assertEquals(Set.copyOf(assistantsEmails), Set.copyOf(course.assistants));
    }

    @Test
    public void enrollUsersAlreadyEnrolledInAnotherCourse() {
        final String emailAddressStudentAndTa = "ta-student@uzh.ch";
        // Enroll users in a first course
        Course course = new Course();
        course.title = "Informatics 1";
        course.students = List.of("alice@example.com", "bob@example.com");
        course.assistants = List.of(emailAddressStudentAndTa, "dr.prof@uzh.ch");

        Group info1 = client.enrollUsersInCourse(course);

        List<String> studentEmails = info1.getStudents().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());
        List<String> assistantsEmails = info1.getAuthors().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());

        Assert.assertEquals(info1.getName(), course.title);
        Assert.assertEquals(Set.copyOf(studentEmails), Set.copyOf(course.students));
        Assert.assertEquals(Set.copyOf(assistantsEmails), Set.copyOf(course.assistants));


        // Enrolling them in a second course should not remove them from the first one
        Course course2 = new Course();
        course2.title = "DBS";
        course2.students = List.of("alice@example.com", "bob@example.com", emailAddressStudentAndTa);
        course2.assistants = List.of("dr.prof@uzh.ch");
        Group dbs = client.enrollUsersInCourse(course2);

        studentEmails = dbs.getStudents().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());
        assistantsEmails = dbs.getAuthors().stream().map(UserRepresentation::getEmail).collect(Collectors.toList());

        Assert.assertEquals(course2.title, dbs.getName());
        Assert.assertEquals(Set.copyOf(studentEmails), Set.copyOf(course2.students));
        Assert.assertEquals(Set.copyOf(assistantsEmails), Set.copyOf(course2.assistants));

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
        UserRepresentation taStudent = users.stream().filter(u -> u.getEmail().equals(emailAddressStudentAndTa)).findFirst().get();
        List<GroupRepresentation> groups = realmResource.users().get(taStudent.getId()).groups();
        for (GroupRepresentation group : groups) {
            if (group.getPath().contains(course.title)) {
                Assert.assertEquals(String.format("ta-student should be an 'author' of course '%s'", course.title), "authors", group.getName());
            } else {
                Assert.assertEquals(String.format("ta-student should be a 'student' of course '%s'", course2.title), "students", group.getName());
            }
        }
    }

    @Test
    public void getUsersIfExistOrCreateUsers() {
        String existingEmail = "bob@example.com";
        List<String> userEmailAddresses = List.of("alice@example.com", existingEmail, "charlie@example.com");

        // Make sure all users are deleted
        Keycloak keycloak = client.keycloak();
        UsersResource usersResource = keycloak.realm("Testing").users();
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