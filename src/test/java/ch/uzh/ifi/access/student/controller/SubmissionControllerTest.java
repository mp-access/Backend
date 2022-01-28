package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.student.dto.SubmissionCount;
import ch.uzh.ifi.access.student.dto.SubmissionHistoryDTO;
import ch.uzh.ifi.access.student.evaluation.EvalProcessService;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {SubmissionController.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SubmissionControllerTest {

    Course testCourse = TestObjectFactory.createCourseWithAssignmentAndExercises("Course 1");
    Exercise testExercise = testCourse.getExercises().get(0);
    final String testUserId = "test-user";
    final String testProcessId = "test-process";
    ObjectMapper mapper = new ObjectMapper();
    CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer(testUserId, testExercise.getId());

    @MockBean
    private StudentSubmissionService studentSubmissionService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private EvalProcessService processService;

    @Autowired
    private SubmissionController submissionController;

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getSubmissionByIdTest() {
        doReturn(testSubmission).when(studentSubmissionService).getSubmissionWithPermission(testSubmission.getId());
        ResponseEntity<StudentSubmission> returnedSubmission = submissionController
                .getSubmissionById(testExercise.getId(), testSubmission.getId());
        Assertions.assertEquals(200, returnedSubmission.getStatusCodeValue());
        Assertions.assertEquals(testSubmission, returnedSubmission.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getLatestSubmissionByExerciseTest() {
        doReturn(Optional.of(testSubmission)).when(studentSubmissionService)
                .findLatestExerciseSubmission(testExercise.getId(), testUserId);
        doReturn(testSubmission).when(studentSubmissionService).getSubmissionWithPermission(testSubmission.getId());
        ResponseEntity<StudentSubmission> returnedSubmission = submissionController
                .getLatestSubmissionByExercise(testExercise.getId(), testUserId);
        Assertions.assertEquals(200, returnedSubmission.getStatusCodeValue());
        Assertions.assertEquals(testSubmission, returnedSubmission.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getLatestSubmissionByExerciseNotFoundTest() {
        doReturn(Optional.empty()).when(studentSubmissionService)
                .findLatestExerciseSubmission(testExercise.getId(), testUserId);
        ResponseEntity<StudentSubmission> returnedSubmission = submissionController
                .getLatestSubmissionByExercise(testExercise.getId(), testUserId);
        Assertions.assertEquals(204, returnedSubmission.getStatusCodeValue());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void submitTest() {
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(testSubmission, JsonNode.class));
        doReturn(false).when(studentSubmissionService).isUserRateLimited(testUserId);
        doReturn(testExercise).when(courseService).getExerciseWithPermission(testExercise.getId(), true);
        doReturn(List.of()).when(studentSubmissionService).filterValidSubmissionsByPermission(testExercise.getId(), testUserId);
        doReturn(testProcessId).when(processService).initEvalProcess(any());
        ResponseEntity<?> returnedResponse = submissionController.submit(testSolution, testExercise.getId(), testUserId);
        Assertions.assertEquals(200, returnedResponse.getStatusCodeValue());
        Assertions.assertEquals(new AbstractMap.SimpleEntry<>("evalId", testProcessId), returnedResponse.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void submitAsRateLimitedUserTest() {
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(testSubmission, JsonNode.class));
        doReturn(true).when(studentSubmissionService).isUserRateLimited(testUserId);
        ResponseEntity<?> returnedSubmission = submissionController.submit(testSolution, testExercise.getId(), testUserId);
        Assertions.assertEquals(429, returnedSubmission.getStatusCodeValue());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void testRunWithoutRemainingSubmissionsTest() {
        testSubmission.setGraded(false);
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(testSubmission, JsonNode.class));
        doReturn(false).when(studentSubmissionService).isUserRateLimited(testUserId);
        doReturn(testExercise).when(courseService).getExerciseWithPermission(testExercise.getId(), false);
        doReturn(Collections.nCopies(testExercise.getMaxSubmits(), testSubmission))
                .when(studentSubmissionService).filterValidSubmissionsByPermission(testExercise.getId(), testUserId);
        doReturn(testProcessId).when(processService).initEvalProcess(any());
        ResponseEntity<?> returnedResponse = submissionController.submit(testSolution, testExercise.getId(), testUserId);
        Assertions.assertEquals(200, returnedResponse.getStatusCodeValue());
        Assertions.assertEquals(new AbstractMap.SimpleEntry<>("evalId", testProcessId), returnedResponse.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void submitForGradingWithoutRemainingSubmissionsTest() {
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(testSubmission, JsonNode.class));
        doReturn(false).when(studentSubmissionService).isUserRateLimited(testUserId);
        doReturn(testExercise).when(courseService).getExerciseWithPermission(testExercise.getId(), true);
        doReturn(Collections.nCopies(testExercise.getMaxSubmits(), testSubmission))
                .when(studentSubmissionService).filterValidSubmissionsByPermission(testExercise.getId(), testUserId);
        doReturn(testProcessId).when(processService).initEvalProcess(any());
        ResponseEntity<?> returnedResponse = submissionController.submit(testSolution, testExercise.getId(), testUserId);
        Assertions.assertEquals(429, returnedResponse.getStatusCodeValue());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getAllSubmissionsForExerciseTest() {
        doReturn(List.of()).when(studentSubmissionService).findAllTestRuns(testExercise.getId(), testUserId);
        doReturn(List.of(testSubmission)).when(studentSubmissionService).findAllGradedSubmissions(testExercise.getId(), testUserId);
        doReturn(testExercise).when(courseService).getExerciseWithViewPermission(testExercise.getId());
        doReturn(Collections.nCopies(testExercise.getMaxSubmits(), testSubmission))
                .when(studentSubmissionService).filterValidSubmissionsByPermission(testExercise.getId(), testUserId);
        SubmissionCount testCount = new SubmissionCount(testExercise.getMaxSubmits(), testExercise.getMaxSubmits());
        SubmissionHistoryDTO returnedHistory = submissionController.getSubmissionHistoryForExercise(testExercise.getId(), testUserId);
        Assertions.assertEquals(new SubmissionHistoryDTO(List.of(testSubmission), List.of(), testCount), returnedHistory);
    }
}
