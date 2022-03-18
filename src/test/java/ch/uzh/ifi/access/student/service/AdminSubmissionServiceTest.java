package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import ch.uzh.ifi.access.student.SubmissionProperties;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation.Points;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@DataMongoTest
public class AdminSubmissionServiceTest {

    Course course = TestObjectFactory.createCourseWithAssignmentAndExercises("Course 1");
    Assignment assignment = course.getAssignments().get(0);
    Exercise exercise = assignment.getExercises().get(0);

    @Mock
    private KeycloakClient keycloakClient;

    @Mock
    private SubmissionProperties submissionProperties;

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    private StudentSubmissionService studentSubmissionService;

    private AdminSubmissionService adminSubmissionService;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(keycloakClient);
        studentSubmissionService = new StudentSubmissionService(studentSubmissionRepository, submissionProperties);
        adminSubmissionService = new AdminSubmissionService(studentSubmissionService, userService, null);
    }

    @AfterEach
    public void tearDown() {
        studentSubmissionRepository.deleteAll();
    }

    @Test
    public void generateAssignmentReportTest() {
        UserRepresentation user1 = new UserRepresentation();
        user1.setId("test-user-1");
        user1.setUsername("test-user-1@example.com");
        UserRepresentation user2 = new UserRepresentation();
        user2.setId("test-user-2");
        user2.setUsername("test-user-2@example.com");
        UserRepresentation user3 = new UserRepresentation();
        user3.setId("test-user-3");
        user3.setUsername("test-user-3@example.com");
        course.setStudents(List.of(user1.getUsername(), user2.getUsername(), user3.getUsername()));

        CodeSubmission lastSubmissionUser1 = TestObjectFactory.createCodeAnswer(user1.getId(), exercise.getId());
        lastSubmissionUser1.setResult(SubmissionEvaluation.builder().points(new Points(5, 10))
                .maxScore(exercise.getMaxScore()).timestamp(Instant.now()).build());

        CodeSubmission lastSubmissionUser2 = TestObjectFactory.createCodeAnswer(user2.getId(), exercise.getId());
        lastSubmissionUser2.setResult(SubmissionEvaluation.builder().points(new Points(10, 10))
                .maxScore(exercise.getMaxScore()).timestamp(Instant.now()).build());

        studentSubmissionService.initSubmission(TestObjectFactory.createCodeAnswer(user1.getId(), exercise.getId()));
        studentSubmissionService.initSubmission(TestObjectFactory.createCodeAnswer(user2.getId(), exercise.getId()));
        studentSubmissionService.initSubmission(lastSubmissionUser1);
        studentSubmissionService.initSubmission(lastSubmissionUser2);

        List<User> testUsersFound = List.of(User.of(user1), User.of(user2), User.of(user3));
        Map<User, List<StudentSubmission>> submissionsByStudent = Map.of(
                testUsersFound.get(0), studentSubmissionService.findLatestGradedSubmissionsByAssignment(assignment, user1.getId()),
                testUsersFound.get(1), studentSubmissionService.findLatestGradedSubmissionsByAssignment(assignment, user2.getId()),
                testUsersFound.get(2), List.of());
        AssignmentReport testReport = new AssignmentReport(assignment, testUsersFound, submissionsByStudent, List.of());

        doReturn(Optional.of(user1)).when(keycloakClient).findUserByEmail(user1.getUsername());
        doReturn(Optional.of(user2)).when(keycloakClient).findUserByEmail(user2.getUsername());
        doReturn(Optional.of(user3)).when(keycloakClient).findUserByEmail(user3.getUsername());
        AssignmentReport returnedReport = adminSubmissionService.generateAssignmentReport(course, assignment);
        Assertions.assertEquals(testReport, returnedReport);
    }

    @Test
    public void invalidateSubmissionsByExerciseAndUserTest() {
        String user1 = "test-user-1";
        String user2 = "test-user-2";
        CodeSubmission submissionUser1A = TestObjectFactory.createCodeAnswer(user1, exercise.getId());
        CodeSubmission submissionUser1B = TestObjectFactory.createCodeAnswer(user1, exercise.getId());
        CodeSubmission submissionUser2 = TestObjectFactory.createCodeAnswer(user2, exercise.getId());
        studentSubmissionService.initSubmission(submissionUser1A);
        Assertions.assertFalse(submissionUser1A.isInvalid());
        studentSubmissionService.initSubmission(submissionUser1B);
        Assertions.assertFalse(submissionUser1B.isInvalid());
        studentSubmissionService.initSubmission(submissionUser2);
        Assertions.assertFalse(submissionUser2.isInvalid());
        adminSubmissionService.invalidateSubmissionsByExerciseAndUser(exercise.getId(), user1);
        List<StudentSubmission> submissionsUser1 = studentSubmissionService.findAllGradedSubmissions(exercise.getId(), user1);
        submissionsUser1.forEach(submission -> Assertions.assertTrue(submission.isInvalid()));
        List<StudentSubmission> submissionsUser2 = studentSubmissionService.findAllGradedSubmissions(exercise.getId(), user2);
        submissionsUser2.forEach(submission -> Assertions.assertFalse(submission.isInvalid()));
    }
}