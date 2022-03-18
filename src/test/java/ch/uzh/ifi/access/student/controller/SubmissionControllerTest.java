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
class SubmissionControllerTest {

    Course course = TestObjectFactory.createCourseWithAssignmentAndExercises("Course 1");
    Exercise exercise = course.getExercises().get(0);
    final String userId = "test-user";
    final String testProcessId = "test-process";
    ObjectMapper mapper = new ObjectMapper();
    CodeSubmission submission = TestObjectFactory.createCodeAnswer(userId, exercise.getId());

    @MockBean
    private StudentSubmissionService studentSubmissionService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private EvalProcessService processService;

    @Autowired
    private SubmissionController submissionController;

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void getSubmissionByIdTest() {
        doReturn(submission).when(studentSubmissionService).getSubmissionWithPermission(submission.getId());
        ResponseEntity<StudentSubmission> returnedSubmission = submissionController
                .getSubmissionById(exercise.getId(), submission.getId());
        Assertions.assertEquals(200, returnedSubmission.getStatusCodeValue());
        Assertions.assertEquals(submission, returnedSubmission.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void getLatestSubmissionByExerciseTest() {
        doReturn(Optional.of(submission)).when(studentSubmissionService)
                .findLatestExerciseSubmission(exercise.getId(), userId);
        doReturn(submission).when(studentSubmissionService).getSubmissionWithPermission(submission.getId());
        ResponseEntity<StudentSubmission> returnedSubmission = submissionController
                .getLatestSubmissionByExercise(exercise.getId(), userId);
        Assertions.assertEquals(200, returnedSubmission.getStatusCodeValue());
        Assertions.assertEquals(submission, returnedSubmission.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void getLatestSubmissionByExerciseNotFoundTest() {
        doReturn(Optional.empty()).when(studentSubmissionService)
                .findLatestExerciseSubmission(exercise.getId(), userId);
        ResponseEntity<StudentSubmission> returnedSubmission = submissionController
                .getLatestSubmissionByExercise(exercise.getId(), userId);
        Assertions.assertEquals(204, returnedSubmission.getStatusCodeValue());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void submitTest() {
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(submission, JsonNode.class));
        doReturn(false).when(studentSubmissionService).isUserRateLimited(userId);
        doReturn(exercise).when(courseService).getExerciseWithPermission(exercise.getId(), true);
        doReturn(List.of()).when(studentSubmissionService).filterValidSubmissionsByPermission(exercise.getId(), userId);
        doReturn(testProcessId).when(processService).initEvalProcess(any());
        ResponseEntity<?> returnedResponse = submissionController.submit(testSolution, exercise.getId(), userId);
        Assertions.assertEquals(200, returnedResponse.getStatusCodeValue());
        Assertions.assertEquals(testProcessId, returnedResponse.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void submitAsRateLimitedUserTest() {
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(submission, JsonNode.class));
        doReturn(true).when(studentSubmissionService).isUserRateLimited(userId);
        ResponseEntity<?> returnedSubmission = submissionController.submit(testSolution, exercise.getId(), userId);
        Assertions.assertEquals(429, returnedSubmission.getStatusCodeValue());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void testRunWithoutRemainingSubmissionsTest() {
        submission.setGraded(false);
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(submission, JsonNode.class));
        doReturn(false).when(studentSubmissionService).isUserRateLimited(userId);
        doReturn(exercise).when(courseService).getExerciseWithPermission(exercise.getId(), false);
        doReturn(Collections.nCopies(exercise.getMaxSubmits(), submission))
                .when(studentSubmissionService).filterValidSubmissionsByPermission(exercise.getId(), userId);
        doReturn(testProcessId).when(processService).initEvalProcess(any());
        ResponseEntity<?> returnedResponse = submissionController.submit(testSolution, exercise.getId(), userId);
        Assertions.assertEquals(200, returnedResponse.getStatusCodeValue());
        Assertions.assertEquals(testProcessId, returnedResponse.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void submitForGradingWithoutRemainingSubmissionsTest() {
        StudentAnswerDTO testSolution = new StudentAnswerDTO(ExerciseType.code, mapper.convertValue(submission, JsonNode.class));
        doReturn(false).when(studentSubmissionService).isUserRateLimited(userId);
        doReturn(exercise).when(courseService).getExerciseWithPermission(exercise.getId(), true);
        doReturn(Collections.nCopies(exercise.getMaxSubmits(), submission))
                .when(studentSubmissionService).filterValidSubmissionsByPermission(exercise.getId(), userId);
        doReturn(testProcessId).when(processService).initEvalProcess(any());
        ResponseEntity<?> returnedResponse = submissionController.submit(testSolution, exercise.getId(), userId);
        Assertions.assertEquals(429, returnedResponse.getStatusCodeValue());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    void getAllSubmissionsForExerciseTest() {
        doReturn(List.of()).when(studentSubmissionService).findAllTestRuns(exercise.getId(), userId);
        doReturn(List.of(submission)).when(studentSubmissionService).findAllGradedSubmissions(exercise.getId(), userId);
        doReturn(exercise).when(courseService).getExerciseWithViewPermission(exercise.getId());
        doReturn(Collections.nCopies(exercise.getMaxSubmits(), submission))
                .when(studentSubmissionService).filterValidSubmissionsByPermission(exercise.getId(), userId);
        SubmissionCount testCount = new SubmissionCount(exercise.getMaxSubmits(), exercise.getMaxSubmits());
        SubmissionHistoryDTO expectedHistory = new SubmissionHistoryDTO(List.of(submission), List.of(), testCount);
        SubmissionHistoryDTO returnedHistory = submissionController.getSubmissionHistoryForExercise(exercise.getId(), userId);
        Assertions.assertEquals(expectedHistory, returnedHistory);
    }
}
