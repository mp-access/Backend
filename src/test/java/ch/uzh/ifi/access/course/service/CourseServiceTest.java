package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(classes = {CourseService.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CourseServiceTest {

    Course course = TestObjectFactory.createCourseWithAssignments("Course 1", List.of(
            TestObjectFactory.createAssignmentWithExercises(true, false),
            TestObjectFactory.createAssignmentWithExercises(false, false),
            TestObjectFactory.createAssignmentWithExercises(true, true))
    );
    Exercise publishedExercise = course.getAssignments().get(0).getExercises().get(0);
    Exercise unpublishedExercise = course.getAssignments().get(1).getExercises().get(0);
    Exercise pastDueExercise = course.getAssignments().get(2).getExercises().get(0);

    @MockBean
    private CourseServiceSetup courseSetup;

    @MockBean(answer = Answers.CALLS_REAL_METHODS)
    @Qualifier("gitrepo")
    private CourseDAO courseDAO;

    @Autowired
    private CourseService courseService;

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(courseDAO, "courseList", List.of(course));
        ReflectionTestUtils.setField(courseDAO, "exerciseIndex", Map.of(
                publishedExercise.getId(), publishedExercise,
                unpublishedExercise.getId(), unpublishedExercise,
                pastDueExercise.getId(), pastDueExercise)
        );
    }

    @Test
    public void getAllCoursesTest() {
        Assertions.assertEquals(List.of(course), courseService.getAllCourses());
    }

    @Test
    public void getCourseByRoleNameTest() {
        Assertions.assertEquals("course-1", course.getRoleName());
        Optional<Course> returnedCourse = courseService.getCourseByRoleName(course.getRoleName());
        Assertions.assertTrue(returnedCourse.isPresent());
        Assertions.assertEquals(course, returnedCourse.get());
    }

    @Test
    @WithMockUser(roles = {"course-1", "course-2"})
    public void getCourseWithPermissionAllowedTest() {
        Course returnedCourse = courseService.getCourseWithPermission(course.getRoleName());
        Assertions.assertEquals(course, returnedCourse);
    }

    @Test
    @WithMockUser(roles = {"course-2"})
    public void getCourseWithPermissionDeniedTest() {
        Assertions.assertThrows(AccessDeniedException.class,
                () -> courseService.getCourseWithPermission(course.getRoleName()));
    }

    @Test
    @WithMockUser(roles = {"course-4"})
    public void getCourseWithPermissionNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> courseService.getCourseWithPermission("course-4"));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getPublishedExerciseWithViewPermissionAsStudentTest() {
        Exercise returnedExercise = courseService.getExerciseWithViewPermission(publishedExercise.getId());
        Assertions.assertEquals(publishedExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getPublishedExerciseWithViewPermissionAsAssistantTest() {
        Exercise returnedExercise = courseService.getExerciseWithViewPermission(publishedExercise.getId());
        Assertions.assertEquals(publishedExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getUnpublishedExerciseWithViewPermissionAsStudentTest() {
        Assertions.assertThrows(AccessDeniedException.class,
                () -> courseService.getExerciseWithViewPermission(unpublishedExercise.getId()));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getUnpublishedExerciseWithViewPermissionAsAssistantTest() {
        Exercise returnedExercise = courseService.getExerciseWithViewPermission(unpublishedExercise.getId());
        Assertions.assertEquals(unpublishedExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getPastDueExerciseWithViewPermissionAsStudentTest() {
        Exercise returnedExercise = courseService.getExerciseWithViewPermission(pastDueExercise.getId());
        Assertions.assertEquals(pastDueExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getPastDueExerciseWithViewPermissionAsAssistantTest() {
        Exercise returnedExercise = courseService.getExerciseWithViewPermission(pastDueExercise.getId());
        Assertions.assertEquals(pastDueExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getExerciseWithViewPermissionNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> courseService.getExerciseWithViewPermission("123"));
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getPublishedExerciseWithSubmitPermissionAsStudentTest() {
        Exercise returnedExercise = courseService.getExerciseWithPermission(publishedExercise.getId(), true);
        Assertions.assertEquals(publishedExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getPublishedExerciseWithSubmitPermissionAsAssistantTest() {
        Exercise returnedExercise = courseService.getExerciseWithPermission(publishedExercise.getId(), true);
        Assertions.assertEquals(publishedExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getUnpublishedExerciseWithSubmitPermissionAsStudentTest() {
        Assertions.assertFalse(unpublishedExercise.isPublished());
        Assertions.assertThrows(AccessDeniedException.class,
                () -> courseService.getExerciseWithPermission(unpublishedExercise.getId(), true));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getUnpublishedExerciseWithSubmitPermissionAsAssistantTest() {
        Exercise returnedExercise = courseService.getExerciseWithPermission(unpublishedExercise.getId(), true);
        Assertions.assertEquals(unpublishedExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getPastDueExerciseWithSubmitPermissionAsStudentTest() {
        Assertions.assertThrows(AccessDeniedException.class,
                () -> courseService.getExerciseWithPermission(pastDueExercise.getId(), true));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getPastDueExerciseWithSubmitPermissionAsAssistantTest() {
        Exercise returnedExercise = courseService.getExerciseWithPermission(pastDueExercise.getId(), true);
        Assertions.assertEquals(pastDueExercise, returnedExercise);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getExerciseWithSubmitPermissionNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> courseService.getExerciseWithPermission("123", true));
    }
}
