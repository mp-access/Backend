package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {CourseController.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CourseControllerTest {

    List<Course> courseList = List.of(
            TestObjectFactory.createCourseWithAssignments("Course 1",
                    List.of(
                            TestObjectFactory.createAssignmentWithExercises(true, false),
                            TestObjectFactory.createAssignmentWithExercises(false, false)
                    )
            ),
            TestObjectFactory.createCourseWithAssignmentAndExercises("Course 2"),
            TestObjectFactory.createCourseWithAssignmentAndExercises("Course 3")
    );

    @MockBean
    private CourseService courseService;

    @Autowired
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        doReturn(courseList).when(courseService).getAllCourses();
        doReturn(courseList.get(0)).when(courseService).getCourseWithPermission("course-1");
    }

    @Test
    @WithMockUser(roles = {"course-1", "course-2", "course-3"})
    public void enrolledInAllCoursesTest() {
        List<CourseMetadataDTO> testCourses = courseList.stream()
                .map(CourseMetadataDTO::new).collect(Collectors.toList());
        List<CourseMetadataDTO> returnedCourses = courseController.getEnrolledCourses();
        Assertions.assertEquals(testCourses, returnedCourses);
    }

    @Test
    @WithMockUser(roles = {"course-1"})
    public void enrolledInSomeCoursesTest() {
        List<CourseMetadataDTO> testCourses = List.of(new CourseMetadataDTO(courseList.get(0)));
        List<CourseMetadataDTO> returnedCourses = courseController.getEnrolledCourses();
        Assertions.assertEquals(testCourses, returnedCourses);
    }

    @Test
    @WithMockUser(roles = {"course-4"})
    public void notEnrolledInAnyCourseTest() {
        List<CourseMetadataDTO> returnedCourses = courseController.getEnrolledCourses();
        Assertions.assertTrue(returnedCourses.isEmpty());
    }

    @Test
    public void getEnrolledCourseTest() {
        CourseMetadataDTO testCourse = new CourseMetadataDTO(courseList.get(0));
        CourseMetadataDTO returnedCourse = courseController.getEnrolledCourse(testCourse.getRoleName());
        Assertions.assertEquals(testCourse, returnedCourse);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getAllAssignmentsByCourseAsStudentTest() {
        Assignment publishedAssignment = courseList.get(0).getAssignments().get(0);
        List<AssignmentMetadataDTO> testAssignments = List.of(new AssignmentMetadataDTO(publishedAssignment));
        List<AssignmentMetadataDTO> returnedAssignments = courseController.getAllAssignmentsByCourse("course-1");
        Assertions.assertEquals(testAssignments, returnedAssignments);
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getAllAssignmentsByCourseAsAssistantTest() {
        List<AssignmentMetadataDTO> testAssignments = courseList.get(0).getAssignments().stream()
                .map(AssignmentMetadataDTO::new).collect(Collectors.toList());
        List<AssignmentMetadataDTO> returnedAssignments = courseController.getAllAssignmentsByCourse("course-1");
        Assertions.assertEquals(testAssignments, returnedAssignments);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getUnpublishedCourseAssignmentAsStudentTest() {
        Assignment unpublishedAssignment = courseList.get(0).getAssignments().get(1);
        Assertions.assertThrows(AccessDeniedException.class,
                () -> courseController.getCourseAssignment("course-1", unpublishedAssignment.getId()));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getUnpublishedCourseAssignmentAsAssistantTest() {
        Assignment unpublishedAssignment = courseList.get(0).getAssignments().get(1);
        AssignmentMetadataDTO testAssignment = new AssignmentMetadataDTO(unpublishedAssignment);
        AssignmentMetadataDTO returnedAssignment = courseController.getCourseAssignment("course-1", unpublishedAssignment.getId());
        Assertions.assertEquals(testAssignment, returnedAssignment);
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void courseAssignmentNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> courseController.getCourseAssignment("course-1", ""));
    }
}
