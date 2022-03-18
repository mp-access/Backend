package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.service.UserService.UserQueryResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {UserService.class})
public class UserServiceTest {

    Course course = TestObjectFactory.createCourseWithAssignmentAndExercises("Course 1");

    @MockBean
    private KeycloakClient keycloakClient;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(keycloakClient.findUserByEmail(anyString())).thenAnswer(invocation -> toUser(invocation.getArgument(0)));
        when(keycloakClient.getUserById(anyString())).thenAnswer(invocation -> toUserOrThrow(invocation.getArgument(0)));
    }

    private Optional<UserRepresentation> toUser(String email) {
        if (email.equals("not-found"))
            return Optional.empty();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(email.split("@")[0]);
        userRepresentation.setUsername(email);
        return Optional.of(userRepresentation);
    }

    private UserRepresentation toUserOrThrow(String id) {
        if (id.equals("not-found"))
            throw new NotFoundException();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(id + "@email.com");
        return userRepresentation;
    }

    @Test
    public void getCourseStudentsTest() {
        course.setStudents(List.of("student-1@email.com", "student-2@email.com", "not-found"));
        UserQueryResult testResult = new UserQueryResult(List.of("not-found"), List.of(
                new User("student-1", "student-1@email.com"),
                new User("student-2", "student-2@email.com")));
        Assertions.assertEquals(testResult, userService.getCourseStudents(course));
    }

    @Test
    public void getCourseAdminsTest() {
        course.setAssistants(List.of("admin@email.com", "not-found"));
        UserQueryResult testResult = new UserQueryResult(List.of("not-found"), List.of(
                new User("admin", "admin@email.com")));
        Assertions.assertEquals(testResult, userService.getCourseAdmins(course));
    }

    @Test
    public void getUsersByIdsTest() {
        UserQueryResult testResult = new UserQueryResult(List.of("not-found"), List.of(
                new User("student-1", "student-1@email.com")));
        Assertions.assertEquals(testResult, userService.getUsersByIds(List.of("student-1", "not-found")));
    }
}