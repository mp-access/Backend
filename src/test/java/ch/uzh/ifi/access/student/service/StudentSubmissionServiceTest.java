package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.SubmissionProperties;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {StudentSubmissionService.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class StudentSubmissionServiceTest {

    Assignment assignment = TestObjectFactory.createAssignmentWithExercises(true, false);
    Exercise codeExercise = assignment.getExercises().get(0);
    Exercise textExercise = assignment.getExercises().get(1);
    Exercise MCExercise = assignment.getExercises().get(2);
    final String userId = "test-user";
    final String assistantId = "test-assistant";
    List<StudentSubmission> tempDB;

    @MockBean
    private StudentSubmissionRepository submissionRepository;

    @MockBean
    private SubmissionProperties submissionProperties;

    @Autowired
    private StudentSubmissionService submissionService;

    @BeforeEach
    void setUpTempDB() {
        tempDB = new ArrayList<>();
        doAnswer(this::saveSubmissionInTempDB).when(submissionRepository).save(any(StudentSubmission.class));
    }

    private Answer<Void> saveSubmissionInTempDB(InvocationOnMock invocation) {
        tempDB.add(invocation.getArgument(0));
        return null;
    }

    @Test
    public void findByIdTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer("", "");
        doReturn(Optional.of(submission)).when(submissionRepository).findById(submission.getId());
        Optional<StudentSubmission> returnedSubmission = submissionService.findById(submission.getId());
        Assertions.assertTrue(returnedSubmission.isPresent());
        Assertions.assertEquals(submission, returnedSubmission.get());
    }

    @Test
    public void findByIdWithoutValueTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> submissionService.findById(null));
    }

    @Test
    public void saveSubmissionWithoutSubmissionTest() {
        Assertions.assertThrows(IllegalArgumentException.class,() -> submissionService.saveSubmission(null));
    }

    @Test
    public void saveSubmissionWithoutUserIdTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> submissionService.saveSubmission(
                TestObjectFactory.createCodeAnswer(null, "exercise")));
    }

    @Test
    public void saveSubmissionWithoutExerciseIdTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> submissionService.saveSubmission(
                TestObjectFactory.createCodeAnswer("user", null)));
    }

    @Test
    public void saveSubmissionTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        doReturn(submission).when(submissionRepository).save(submission);
        CodeSubmission returnedSubmission = submissionService.saveSubmission(submission);
        Assertions.assertEquals(submission, returnedSubmission);
    }

    @Test
    public void findAllSubmissionsTest() {
        CodeSubmission codeSubmission = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        TextSubmission textSubmission = TestObjectFactory.createTextAnswer(userId, textExercise.getId());
        MultipleChoiceSubmission MCSubmission = TestObjectFactory.createMultipleChoiceAnswer(userId, MCExercise.getId());
        submissionService.saveSubmission(codeSubmission);
        submissionService.saveSubmission(textSubmission);
        submissionService.saveSubmission(MCSubmission);
        doReturn(tempDB).when(submissionRepository).findAll();
        List<StudentSubmission> returnedSubmissions = submissionService.findAll();
        Assertions.assertEquals(List.of(codeSubmission, textSubmission, MCSubmission), returnedSubmissions);
    }

    @Test
    public void findAllSubmissionsByGradingTest() {
        CodeSubmission submission1 = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        CodeSubmission submission2 = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        submission2.setGraded(false);
        doReturn(Optional.of(submission1)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), userId);
        submissionService.initSubmission(submission1);
        Assertions.assertEquals(1, submission1.getVersion());
        Assertions.assertTrue(submission1.isGraded());
        submissionService.initSubmission(submission2);
        Assertions.assertEquals(2, submission2.getVersion());
        Assertions.assertFalse(submission2.isGraded());
        when(submissionRepository.findAllByExerciseIdAndUserIdAndIsGradedOrderByVersionDesc(anyString(), anyString(), anyBoolean()))
                .thenAnswer(invocation -> tempDB.stream()
                        .filter(submission -> submission.isGraded() == (boolean) invocation.getArgument(2))
                        .collect(Collectors.toList()));
        Assertions.assertEquals(List.of(submission1), submissionService.findAllGradedSubmissions(codeExercise.getId(), userId));
        Assertions.assertEquals(List.of(submission2), submissionService.findAllTestRuns(codeExercise.getId(), userId));
    }

    @Test
    public void findLatestExerciseSubmissionNoSubmissionsYetTest() {
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), userId);
        Optional<StudentSubmission> latestSubmission = submissionService.findLatestExerciseSubmission(codeExercise.getId(), userId);
        Assertions.assertFalse(latestSubmission.isPresent());
    }

    @Test
    public void findLatestSubmissionsByAssignmentTest() {
        CodeSubmission codeSubmission1 = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        TextSubmission textSubmission = TestObjectFactory.createTextAnswer(userId, textExercise.getId());
        submissionService.initSubmission(codeSubmission1);
        submissionService.initSubmission(textSubmission);
        submissionService.initSubmission(codeSubmission2);
        List<StudentSubmission> submissions = List.of(codeSubmission2, textSubmission);
        doReturn(Optional.of(codeSubmission2)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), userId);
        doReturn(Optional.of(textSubmission)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(textExercise.getId(), userId);
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(MCExercise.getId(), userId);
        List<StudentSubmission> returnedSubmissions = submissionService.findLatestGradedSubmissionsByAssignment(assignment, userId);
        Assertions.assertEquals(submissions, returnedSubmissions);
    }

    @Test
    public void invalidateSubmissionsByExerciseIdsTest() {
        CodeSubmission testCodeSubmissionUser1 = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        CodeSubmission testMCSubmissionUser1 = TestObjectFactory.createCodeAnswer(userId, MCExercise.getId());
        CodeSubmission testTextSubmissionUser2 = TestObjectFactory.createCodeAnswer("test-user-2", textExercise.getId());
        submissionService.initSubmission(testCodeSubmissionUser1);
        Assertions.assertFalse(testCodeSubmissionUser1.isInvalid());
        submissionService.initSubmission(testMCSubmissionUser1);
        Assertions.assertFalse(testMCSubmissionUser1.isInvalid());
        submissionService.initSubmission(testTextSubmissionUser2);
        Assertions.assertFalse(testTextSubmissionUser2.isInvalid());
        doAnswer(invocation -> {
            tempDB.stream()
                    .filter(submission -> submission.getExerciseId().equals(invocation.getArgument(0)))
                    .forEach(submission -> submission.setInvalid(true));
            return null;
        }).when(submissionRepository).invalidateSubmissionsByExerciseId(anyString());
        submissionService.invalidateSubmissionsByExerciseIdIn(List.of(codeExercise.getId(), textExercise.getId()));
        Assertions.assertTrue(testCodeSubmissionUser1.isInvalid());
        Assertions.assertTrue(testTextSubmissionUser2.isInvalid());
        Assertions.assertFalse(testMCSubmissionUser1.isInvalid());
    }

    @Test
    public void invalidateSubmissionsByExerciseAndUserTest() {
        CodeSubmission submissionUser1A = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        CodeSubmission submissionUser1B = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        CodeSubmission submissionUser2 = TestObjectFactory.createCodeAnswer("test-user-2", codeExercise.getId());
        submissionService.initSubmission(submissionUser1A);
        Assertions.assertFalse(submissionUser1A.isInvalid());
        submissionService.initSubmission(submissionUser1B);
        Assertions.assertFalse(submissionUser1B.isInvalid());
        submissionService.initSubmission(submissionUser2);
        Assertions.assertFalse(submissionUser2.isInvalid());
        doAnswer(invocation -> {
            tempDB.stream()
                    .filter(submission -> submission.getUserId().equals(invocation.getArgument(1)))
                    .forEach(submission -> submission.setInvalid(true));
            return null;
        }).when(submissionRepository).invalidateSubmissionsByExerciseIdAndUserId(codeExercise.getId(), userId);
        submissionService.invalidateSubmissionsByExerciseAndUser(codeExercise.getId(), userId);
        Assertions.assertTrue(submissionUser1A.isInvalid());
        Assertions.assertTrue(submissionUser1B.isInvalid());
        Assertions.assertFalse(submissionUser2.isInvalid());

        doReturn(Optional.of(submissionUser1B)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), userId);
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(textExercise.getId(), userId);
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(MCExercise.getId(), userId);
        List<StudentSubmission> returnedSubmissions = submissionService
                .findLatestGradedInvalidatedSubmissionsByAssignment(assignment, userId);
        Assertions.assertEquals(List.of(submissionUser1B), returnedSubmissions);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    public void getSubmissionWithPermissionAsStudentTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        doReturn(Optional.of(submission)).when(submissionRepository).findById(submission.getId());
        StudentSubmission returnedSubmission = submissionService.getSubmissionWithPermission(submission.getId());
        Assertions.assertEquals(submission, returnedSubmission);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    public void getSubmissionOfOtherUserAsStudentTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer("test-user-2", codeExercise.getId());
        doReturn(Optional.of(submission)).when(submissionRepository).findById(submission.getId());
        Assertions.assertThrows(AccessDeniedException.class,
                () -> submissionService.getSubmissionWithPermission(submission.getId()));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student", "course-2", "assistant", "course-2-assistant"}, username = userId)
    public void getSubmissionOfOtherCourse1UserAsCourse1StudentAndCourse2AssistantTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer("test-user-2", codeExercise.getId());
        doReturn(Optional.of(submission)).when(submissionRepository).findById(submission.getId());
        Assertions.assertThrows(AccessDeniedException.class,
                () -> submissionService.getSubmissionWithPermission(submission.getId()));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"}, username = assistantId)
    public void getSubmissionWithPermissionAsAssistantTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer(assistantId, codeExercise.getId());
        doReturn(Optional.of(submission)).when(submissionRepository).findById(submission.getId());
        StudentSubmission returnedSubmission = submissionService.getSubmissionWithPermission(submission.getId());
        Assertions.assertEquals(submission, returnedSubmission);
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"}, username = assistantId)
    public void getSubmissionOfOtherUserAsAssistantTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        submission.setCourseId("course-1");
        doReturn(Optional.of(submission)).when(submissionRepository).findById(submission.getId());
        StudentSubmission returnedSubmission = submissionService.getSubmissionWithPermission(submission.getId());
        Assertions.assertEquals(submission, returnedSubmission);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    public void getSubmissionWithPermissionNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> submissionService.getSubmissionWithPermission("123"));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    public void getValidSubmissionCountWithPermissionAsStudentTest() {
        List<StudentSubmission> submissions = new ArrayList<>();
        CodeSubmission submission1 = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        CodeSubmission submission2 = TestObjectFactory.createCodeAnswer(userId, codeExercise.getId());
        submission1.setCourseId("course-1");
        submission2.setCourseId("course-1");
        submissions.add(submission1);
        submissions.add(submission2);
        doReturn(submissions).when(submissionRepository)
            .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(codeExercise.getId(), userId);
        Assertions.assertEquals(submissions, submissionService.filterValidSubmissionsByPermission(codeExercise.getId(), userId));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"}, username = assistantId)
    public void getValidSubmissionCountWithPermissionAsAssistantTest() {
        CodeSubmission submission = TestObjectFactory.createCodeAnswer(assistantId, codeExercise.getId());
        submission.setCourseId("course-1");
        List<StudentSubmission> submissions = new ArrayList<>();
        submissions.add(submission);
        doReturn(submissions).when(submissionRepository)
            .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(codeExercise.getId(), assistantId);
        Assertions.assertTrue(submissionService.filterValidSubmissionsByPermission(codeExercise.getId(), assistantId).isEmpty());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = userId)
    public void getValidSubmissionCountNoSubmissionsYetTest() {
        List<StudentSubmission> submissions = new ArrayList<>();
        doReturn(submissions).when(submissionRepository)
                .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(codeExercise.getId(), userId);
        Assertions.assertEquals(submissions, submissionService.filterValidSubmissionsByPermission(codeExercise.getId(), userId));
    }

    @Test
    public void isUserRateLimitedTest() {
        doReturn(false).when(submissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(userId);
        Assertions.assertFalse(submissionService.isUserRateLimited(userId));

        doReturn(true).when(submissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(userId);
        Assertions.assertFalse(submissionService.isUserRateLimited(userId));

        doReturn(true).when(submissionProperties).isUserRateLimit();
        Assertions.assertTrue(submissionService.isUserRateLimited(userId));

        doReturn(false).when(submissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(userId);
        Assertions.assertFalse(submissionService.isUserRateLimited(userId));
    }
}