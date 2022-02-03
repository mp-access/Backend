package ch.uzh.ifi.access;

import ch.uzh.ifi.access.config.AccessProperties;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.controller.CourseController;
import ch.uzh.ifi.access.course.controller.ExerciseController;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import ch.uzh.ifi.access.student.SubmissionProperties;
import ch.uzh.ifi.access.student.controller.AssistantController;
import ch.uzh.ifi.access.student.controller.ResultController;
import ch.uzh.ifi.access.student.controller.SubmissionController;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.student.dto.UserMigration;
import ch.uzh.ifi.access.student.evaluation.EvalProcessService;
import ch.uzh.ifi.access.student.service.AdminSubmissionService;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import ch.uzh.ifi.access.student.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        properties = {"spring.main.allow-bean-definition-overriding=true", "keycloak.enabled=true", "keycloak.realm=test"},
        controllers = {AccessProperties.class, SubmissionProperties.class, KeycloakClient.class, CourseServiceSetup.class,
                CourseService.class, StudentSubmissionService.class, AdminSubmissionService.class, CourseController.class,
                ExerciseController.class, ResultController.class, SubmissionController.class, AssistantController.class})
class IntegrationTests {

    Course course1 = TestObjectFactory.createCourseWithAssignments("Course 1", List.of(
            TestObjectFactory.createAssignmentWithExercises(true, false),
            TestObjectFactory.createAssignmentWithExercises(false, false)));
    Course course2 = TestObjectFactory.createCourseWithAssignmentAndExercises("Course 2");
    List<Course> courseList = List.of(course1, course2);

    Assignment publishedAssignment = course1.getAssignments().get(0);
    Assignment unpublishedAssignment = course1.getAssignments().get(1);
    Exercise publishedExercise = publishedAssignment.getExercises().get(0);
    Exercise unpublishedExercise = unpublishedAssignment.getExercises().get(0);
    final String studentId = "test-student";
    final String assistantId = "test-assistant";
    final String testProcessId = "test-process";

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("gitrepo")
    private CourseDAO courseDAO;

    @MockBean
    private StudentSubmissionRepository studentSubmissionRepository;

    @MockBean
    private EvalProcessService processService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        doReturn(courseList).when(courseDAO).selectAllCourses();
        doReturn(Optional.of(course1)).when(courseDAO).selectCourseByRoleName(course1.getRoleName());
        doReturn(Optional.of(course2)).when(courseDAO).selectCourseByRoleName(course2.getRoleName());
        doReturn(Optional.of(publishedExercise)).when(courseDAO).selectExerciseById(publishedExercise.getId());
        doReturn(Optional.of(unpublishedExercise)).when(courseDAO).selectExerciseById(unpublishedExercise.getId());
        doReturn(testProcessId).when(processService).initEvalProcess(any());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = studentId)
    void getEnrolledCoursesTest() throws Exception {
        String expectedCourses = mapper.writeValueAsString(List.of(new CourseMetadataDTO(course1)));
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedCourses));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getEnrolledCourseAllowedTest() throws Exception {
        String expectedCourse = mapper.writeValueAsString(new CourseMetadataDTO(course1));
        mockMvc.perform(get("/courses/course-1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedCourse));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getEnrolledCourseDeniedTest() throws Exception {
        mockMvc.perform(get("/courses/course-2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getAllAssignmentsByCourseAsStudentTest() throws Exception {
        String expectedAssignments = mapper.writeValueAsString(List.of(new AssignmentMetadataDTO(publishedAssignment)));
        mockMvc.perform(get("/courses/course-1/assignments"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedAssignments));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void getAllAssignmentsByCourseAsAssistantTest() throws Exception {
        String expectedAssignments = mapper.writeValueAsString(course1.getAssignments().stream()
                .map(AssignmentMetadataDTO::new).collect(Collectors.toList()));
        mockMvc.perform(get("/courses/course-1/assignments"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedAssignments));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student", "course-2", "assistant", "course-2-assistant"})
    void getAllAssignmentsByCourseAsCourse1StudentAndCourse2AssistantTest() throws Exception {
        String expectedAssignments = mapper.writeValueAsString(List.of(new AssignmentMetadataDTO(publishedAssignment)));
        mockMvc.perform(get("/courses/course-1/assignments"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedAssignments));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getPublishedCourseAssignmentAsStudentTest() throws Exception {
        String expectedAssignment = mapper.writeValueAsString(new AssignmentMetadataDTO(publishedAssignment));
        mockMvc.perform(get("/courses/course-1/assignments/" + publishedAssignment.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedAssignment));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getUnpublishedCourseAssignmentAsStudentTest() throws Exception {
        mockMvc.perform(get("/courses/course-1/assignments/" + unpublishedAssignment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void getUnpublishedCourseAssignmentAsAssistantTest() throws Exception {
        String expectedAssignment = mapper.writeValueAsString(new AssignmentMetadataDTO(unpublishedAssignment));
        mockMvc.perform(get("/courses/course-1/assignments/" + unpublishedAssignment.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedAssignment));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student", "course-2", "assistant", "course-2-assistant"})
    void getUnpublishedCourseAssignmentAsCourse1StudentAndCourse2AssistantTest() throws Exception {
        mockMvc.perform(get("/courses/course-1/assignments/" + unpublishedAssignment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getCourseAssignmentNotFoundTest() throws Exception {
        mockMvc.perform(get("/courses/course-1/assignments/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = studentId)
    void getCourseResultsAllowedTest() throws Exception {
        mockMvc.perform(get("/courses/course-1/results"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = studentId)
    void getCourseResultsADeniedTest() throws Exception {
        mockMvc.perform(get("/courses/course-2/results"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getExerciseTest() throws Exception {
        String expectedExercise = mapper.writeValueAsString(publishedExercise);
        mockMvc.perform(get("/exercises/" + publishedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedExercise));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getExerciseWithSolutionsAsStudentTest() throws Exception {
        mockMvc.perform(get("/exercises/" + unpublishedExercise.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void getExerciseWithSolutionsAsAssistantTest() throws Exception {
        String expectedExercise = mapper.writeValueAsString(unpublishedExercise);
        mockMvc.perform(get("/exercises/" + unpublishedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedExercise));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getExerciseNotFoundTest() throws Exception {
        mockMvc.perform(get("/exercises/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = studentId)
    void submitSolutionToPublishedExerciseAsStudentTest() throws Exception {
        StudentAnswerDTO solution = new StudentAnswerDTO(publishedExercise.getType(),
                mapper.convertValue(TestObjectFactory.createCodeAnswer(studentId, publishedExercise.getId()), JsonNode.class));
        doReturn(false).when(studentSubmissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(studentId);
        doReturn(Optional.empty()).when(studentSubmissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(publishedExercise.getId(), studentId);
        doReturn(new ArrayList<>()).when(studentSubmissionRepository)
            .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(publishedExercise.getId(), studentId);
        mockMvc.perform(post("/exercises/{exerciseId}/submissions/users/{userId}/submit", publishedExercise.getId(), studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(solution))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"}, username = studentId)
    void submitSolutionToUnpublishedExerciseAsStudentTest() throws Exception {
        StudentAnswerDTO solution = new StudentAnswerDTO(unpublishedExercise.getType(),
                mapper.convertValue(TestObjectFactory.createCodeAnswer(studentId, unpublishedExercise.getId()), JsonNode.class));
        doReturn(false).when(studentSubmissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(studentId);
        doReturn(Optional.empty()).when(studentSubmissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(unpublishedExercise.getId(), studentId);
        doReturn(new ArrayList<>()).when(studentSubmissionRepository)
        .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(unpublishedExercise.getId(), studentId);
        mockMvc.perform(post("/exercises/{exerciseId}/submissions/users/{userId}/submit", unpublishedExercise.getId(), studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(solution))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"}, username = assistantId)
    void submitSolutionToUnpublishedExerciseAsAssistantTest() throws Exception {
        StudentAnswerDTO solution = new StudentAnswerDTO(unpublishedExercise.getType(),
                mapper.convertValue(TestObjectFactory.createCodeAnswer(assistantId, unpublishedExercise.getId()), JsonNode.class));
        doReturn(false).when(studentSubmissionRepository).existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(assistantId);
        doReturn(Optional.empty()).when(studentSubmissionRepository)
                .findTopByExerciseIdAndUserIdOrderByVersionDesc(unpublishedExercise.getId(), assistantId);
        doReturn(new ArrayList<>()).when(studentSubmissionRepository)
        .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(unpublishedExercise.getId(), assistantId);
        mockMvc.perform(post("/exercises/{exerciseId}/submissions/users/{userId}/submit", unpublishedExercise.getId(), assistantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(solution))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void exportAssignmentResultsAsStudentTest() throws Exception {
        mockMvc.perform(get("/admins/courses/course-1/assignments/{assignmentId}/results", publishedAssignment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void exportAssignmentResultsAsAssistantTest() throws Exception {
        mockMvc.perform(get("/admins/courses/course-1/assignments/{assignmentId}/results", publishedAssignment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "admin", "course-1-course-admin"})
    void exportAssignmentResultsAsAdminTest() throws Exception {
        doReturn(new UserService.UserQueryResult(List.of(), List.of())).when(userService).getCourseStudents(course1);
        mockMvc.perform(get("/admins/courses/course-1/assignments/{assignmentId}/results", publishedAssignment.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void reevaluateInvalidateSubmissionsForAssignmentAsStudentTest() throws Exception {
        mockMvc.perform(get("/admins/courses/course-1/assignments/{assignmentId}/reevaluate", publishedAssignment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void reevaluateInvalidateSubmissionsForAssignmentAsAssistantTest() throws Exception {
        mockMvc.perform(get("/admins/courses/course-1/assignments/{assignmentId}/reevaluate", publishedAssignment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "admin", "course-1-course-admin"})
    void reevaluateInvalidateSubmissionsForAssignmentAsAdminTest() throws Exception {
        doReturn(new UserService.UserQueryResult(List.of(), List.of())).when(userService).getCourseStudents(course1);
        mockMvc.perform(get("/admins/courses/course-1/assignments/{assignmentId}/reevaluate", publishedAssignment.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void migrateUserAsStudentTest() throws Exception {
        doReturn(new UserService.UserQueryResult(List.of(), List.of())).when(userService).getUsersByIds(List.of("from", "to"));
        mockMvc.perform(post("/admins/courses/course-1/participants/migrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserMigration("from", "to")))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void migrateUserAsAssistantTest() throws Exception {
        doReturn(new UserService.UserQueryResult(List.of(), List.of())).when(userService).getUsersByIds(List.of("from", "to"));
        mockMvc.perform(post("/admins/courses/course-1/participants/migrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserMigration("from", "to")))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "admin", "course-1-course-admin"})
    void migrateUserAsAdminTest() throws Exception {
        doReturn(new UserService.UserQueryResult(List.of(), List.of())).when(userService).getUsersByIds(List.of("from", "to"));
        mockMvc.perform(post("/admins/courses/course-1/participants/migrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserMigration("from", "to")))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void getCourseParticipantsAsStudentTest() throws Exception {
        mockMvc.perform(get("/admins/courses/course-1/participants"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void getCourseParticipantsAsAssistantTest() throws Exception {
        doReturn(new UserService.UserQueryResult(List.of(), List.of())).when(userService).getCourseStudents(course1);
        mockMvc.perform(get("/admins/courses/course-1/participants"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    void resetSubmissionCountAsStudentTest() throws Exception {
        mockMvc.perform(
                get("/admins/courses/course-1/exercises/{exerciseId}/users/{userId}/reset", publishedExercise.getId(), studentId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    void resetSubmissionCountAsAssistantTest() throws Exception {
        mockMvc.perform(
                get("/admins/courses/course-1/exercises/{exerciseId}/users/{userId}/reset", publishedExercise.getId(), studentId))
                .andExpect(status().isOk());
    }
}