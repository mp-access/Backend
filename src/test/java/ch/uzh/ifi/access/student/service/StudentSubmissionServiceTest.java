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

    Assignment testAssignment = TestObjectFactory.createAssignmentWithExercises(true, false);
    Exercise codeExercise = testAssignment.getExercises().get(0);
    Exercise textExercise = testAssignment.getExercises().get(1);
    Exercise MCExercise = testAssignment.getExercises().get(2);
    final String testUserId = "test-user";
    final String testAssistantId = "test-assistant";
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
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer("", "");
        doReturn(Optional.of(testSubmission)).when(submissionRepository).findById(testSubmission.getId());
        Optional<StudentSubmission> returnedSubmission = submissionService.findById(testSubmission.getId());
        Assertions.assertTrue(returnedSubmission.isPresent());
        Assertions.assertEquals(testSubmission, returnedSubmission.get());
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
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        doReturn(testSubmission).when(submissionRepository).save(testSubmission);
        CodeSubmission returnedSubmission = submissionService.saveSubmission(testSubmission);
        Assertions.assertEquals(testSubmission, returnedSubmission);
    }

    @Test
    public void findAllSubmissionsTest() {
        CodeSubmission codeSubmission = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        TextSubmission textSubmission = TestObjectFactory.createTextAnswer(testUserId, textExercise.getId());
        MultipleChoiceSubmission MCSubmission = TestObjectFactory.createMultipleChoiceAnswer(testUserId, MCExercise.getId());
        submissionService.saveSubmission(codeSubmission);
        submissionService.saveSubmission(textSubmission);
        submissionService.saveSubmission(MCSubmission);
        doReturn(tempDB).when(submissionRepository).findAll();
        List<StudentSubmission> returnedSubmissions = submissionService.findAll();
        Assertions.assertEquals(List.of(codeSubmission, textSubmission, MCSubmission), returnedSubmissions);
    }

    @Test
    public void findAllSubmissionsByGradingTest() {
        CodeSubmission testSubmission1 = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        CodeSubmission testSubmission2 = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        testSubmission2.setGraded(false);
        doReturn(Optional.of(testSubmission1)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), testUserId);
        submissionService.initSubmission(testSubmission1);
        Assertions.assertEquals(1, testSubmission1.getVersion());
        Assertions.assertTrue(testSubmission1.isGraded());
        submissionService.initSubmission(testSubmission2);
        Assertions.assertEquals(2, testSubmission2.getVersion());
        Assertions.assertFalse(testSubmission2.isGraded());
        when(submissionRepository.findAllByExerciseIdAndUserIdAndIsGradedOrderByVersionDesc(anyString(), anyString(), anyBoolean()))
                .thenAnswer(invocation -> tempDB.stream()
                        .filter(submission -> submission.isGraded() == (boolean) invocation.getArgument(2))
                        .collect(Collectors.toList()));
        Assertions.assertEquals(List.of(testSubmission1), submissionService.findAllGradedSubmissions(codeExercise.getId(), testUserId));
        Assertions.assertEquals(List.of(testSubmission2), submissionService.findAllTestRuns(codeExercise.getId(), testUserId));
    }

    @Test
    public void findLatestExerciseSubmissionNoSubmissionsYetTest() {
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), testUserId);
        Optional<StudentSubmission> latestSubmission = submissionService.findLatestExerciseSubmission(codeExercise.getId(), testUserId);
        Assertions.assertFalse(latestSubmission.isPresent());
    }

    @Test
    public void findLatestSubmissionsByAssignmentTest() {
        CodeSubmission codeSubmission1 = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        TextSubmission textSubmission = TestObjectFactory.createTextAnswer(testUserId, textExercise.getId());
        submissionService.initSubmission(codeSubmission1);
        submissionService.initSubmission(textSubmission);
        submissionService.initSubmission(codeSubmission2);
        List<StudentSubmission> testSubmissions = List.of(codeSubmission2, textSubmission);
        doReturn(Optional.of(codeSubmission2)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), testUserId);
        doReturn(Optional.of(textSubmission)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(textExercise.getId(), testUserId);
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(MCExercise.getId(), testUserId);
        List<StudentSubmission> returnedSubmissions = submissionService.findLatestGradedSubmissionsByAssignment(testAssignment, testUserId);
        Assertions.assertEquals(testSubmissions, returnedSubmissions);
    }

    @Test
    public void invalidateSubmissionsByExerciseIdsTest() {
        CodeSubmission testCodeSubmissionUser1 = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        CodeSubmission testMCSubmissionUser1 = TestObjectFactory.createCodeAnswer(testUserId, MCExercise.getId());
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
        CodeSubmission testSubmissionUser1A = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        CodeSubmission testSubmissionUser1B = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        CodeSubmission testSubmissionUser2 = TestObjectFactory.createCodeAnswer("test-user-2", codeExercise.getId());
        submissionService.initSubmission(testSubmissionUser1A);
        Assertions.assertFalse(testSubmissionUser1A.isInvalid());
        submissionService.initSubmission(testSubmissionUser1B);
        Assertions.assertFalse(testSubmissionUser1B.isInvalid());
        submissionService.initSubmission(testSubmissionUser2);
        Assertions.assertFalse(testSubmissionUser2.isInvalid());
        doAnswer(invocation -> {
            tempDB.stream()
                    .filter(submission -> submission.getUserId().equals(invocation.getArgument(1)))
                    .forEach(submission -> submission.setInvalid(true));
            return null;
        }).when(submissionRepository).invalidateSubmissionsByExerciseIdAndUserId(codeExercise.getId(), testUserId);
        submissionService.invalidateSubmissionsByExerciseAndUser(codeExercise.getId(), testUserId);
        Assertions.assertTrue(testSubmissionUser1A.isInvalid());
        Assertions.assertTrue(testSubmissionUser1B.isInvalid());
        Assertions.assertFalse(testSubmissionUser2.isInvalid());

        doReturn(Optional.of(testSubmissionUser1B)).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(codeExercise.getId(), testUserId);
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(textExercise.getId(), testUserId);
        doReturn(Optional.empty()).when(submissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(MCExercise.getId(), testUserId);
        List<StudentSubmission> returnedSubmissions = submissionService
                .findLatestGradedInvalidatedSubmissionsByAssignment(testAssignment, testUserId);
        Assertions.assertEquals(List.of(testSubmissionUser1B), returnedSubmissions);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getSubmissionWithPermissionAsStudentTest() {
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        doReturn(Optional.of(testSubmission)).when(submissionRepository).findById(testSubmission.getId());
        StudentSubmission returnedSubmission = submissionService.getSubmissionWithPermission(testSubmission.getId());
        Assertions.assertEquals(testSubmission, returnedSubmission);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getSubmissionOfOtherUserAsStudentTest() {
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer("test-user-2", codeExercise.getId());
        doReturn(Optional.of(testSubmission)).when(submissionRepository).findById(testSubmission.getId());
        Assertions.assertThrows(AccessDeniedException.class,
                () -> submissionService.getSubmissionWithPermission(testSubmission.getId()));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student", "course-2", "assistant", "course-2-assistant"}, username = testUserId)
    public void getSubmissionOfOtherCourse1UserAsCourse1StudentAndCourse2AssistantTest() {
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer("test-user-2", codeExercise.getId());
        doReturn(Optional.of(testSubmission)).when(submissionRepository).findById(testSubmission.getId());
        Assertions.assertThrows(AccessDeniedException.class,
                () -> submissionService.getSubmissionWithPermission(testSubmission.getId()));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"}, username = testAssistantId)
    public void getSubmissionWithPermissionAsAssistantTest() {
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer(testAssistantId, codeExercise.getId());
        doReturn(Optional.of(testSubmission)).when(submissionRepository).findById(testSubmission.getId());
        StudentSubmission returnedSubmission = submissionService.getSubmissionWithPermission(testSubmission.getId());
        Assertions.assertEquals(testSubmission, returnedSubmission);
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"}, username = testAssistantId)
    public void getSubmissionOfOtherUserAsAssistantTest() {
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        testSubmission.setCourseId("course-1");
        doReturn(Optional.of(testSubmission)).when(submissionRepository).findById(testSubmission.getId());
        StudentSubmission returnedSubmission = submissionService.getSubmissionWithPermission(testSubmission.getId());
        Assertions.assertEquals(testSubmission, returnedSubmission);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getSubmissionWithPermissionNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> submissionService.getSubmissionWithPermission("123"));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getValidSubmissionCountWithPermissionAsStudentTest() {
        List<StudentSubmission> testSubmissions = new ArrayList<>();
        CodeSubmission testSubmission1 = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        CodeSubmission testSubmission2 = TestObjectFactory.createCodeAnswer(testUserId, codeExercise.getId());
        testSubmission1.setCourseId("course-1");
        testSubmission2.setCourseId("course-1");
        testSubmissions.add(testSubmission1);
        testSubmissions.add(testSubmission2);
        doReturn(testSubmissions).when(submissionRepository)
            .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(codeExercise.getId(), testUserId);
        Assertions.assertEquals(testSubmissions, submissionService.filterValidSubmissionsByPermission(codeExercise.getId(), testUserId));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"}, username = testAssistantId)
    public void getValidSubmissionCountWithPermissionAsAssistantTest() {
        CodeSubmission testSubmission = TestObjectFactory.createCodeAnswer(testAssistantId, codeExercise.getId());
        testSubmission.setCourseId("course-1");
        List<StudentSubmission> testSubmissions = new ArrayList<>();
        testSubmissions.add(testSubmission);
        doReturn(testSubmissions).when(submissionRepository)
            .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(codeExercise.getId(), testAssistantId);
        Assertions.assertTrue(submissionService.filterValidSubmissionsByPermission(codeExercise.getId(), testAssistantId).isEmpty());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = testUserId)
    public void getValidSubmissionCountNoSubmissionsYetTest() {
        List<StudentSubmission> testSubmissions = new ArrayList<>();
        doReturn(testSubmissions).when(submissionRepository)
                .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(codeExercise.getId(), testUserId);
        Assertions.assertEquals(testSubmissions, submissionService.filterValidSubmissionsByPermission(codeExercise.getId(), testUserId));
    }

    @Test
    public void isUserRateLimitedTest() {
        doReturn(false).when(submissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(testUserId);
        Assertions.assertFalse(submissionService.isUserRateLimited(testUserId));

        doReturn(true).when(submissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(testUserId);
        Assertions.assertFalse(submissionService.isUserRateLimited(testUserId));

        doReturn(true).when(submissionProperties).isUserRateLimit();
        Assertions.assertTrue(submissionService.isUserRateLimited(testUserId));

        doReturn(false).when(submissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(testUserId);
        Assertions.assertFalse(submissionService.isUserRateLimited(testUserId));
    }
}