package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.dto.ExerciseWithSolutionsDTO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.course.service.CourseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {ExerciseController.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ExerciseControllerTest {

    Course testCourse = TestObjectFactory.createCourseWithAssignmentAndExercises("Course 1");
    Exercise testExercise = testCourse.getAssignments().get(0).getExercises().get(0);
    VirtualFile testFile = testExercise.getPublic_files().get(0);

    @MockBean
    private CourseService courseService;

    @Autowired
    private ExerciseController exerciseController;

    @BeforeEach
    void setUp() {
        testExercise.setPublic_files(List.of(testFile));
        doReturn(testExercise).when(courseService).getExerciseWithViewPermission(testExercise.getId());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getExerciseTest() {
        ResponseEntity<Exercise> returnedExercise = exerciseController.getExercise(testExercise.getId());
        Assertions.assertEquals(200, returnedExercise.getStatusCodeValue());
        Assertions.assertEquals(testExercise, returnedExercise.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getExerciseWithSolutionsAsStudentTest() {
        Assertions.assertThrows(AccessDeniedException.class,
                () -> exerciseController.getExerciseWithSolutions(testExercise.getId()));
    }

    @Test
    @WithMockUser(roles = {"course-1", "assistant", "course-1-assistant"})
    public void getExerciseWithSolutionsAsAssistantTest() {
        ResponseEntity<ExerciseWithSolutionsDTO> returnedExercise = exerciseController.getExerciseWithSolutions(testExercise.getId());
        Assertions.assertEquals(200, returnedExercise.getStatusCodeValue());
        Assertions.assertEquals(new ExerciseWithSolutionsDTO(testExercise), returnedExercise.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getFileByIdTest() {
        ResponseEntity<Resource> returnedExercise = exerciseController.getFile(testExercise.getId(), testFile.getId());
        Assertions.assertEquals(200, returnedExercise.getStatusCodeValue());
        Assertions.assertEquals(new FileSystemResource(testFile.getFile()), returnedExercise.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getFileByFilenameTest() {
        ResponseEntity<Resource> returnedExercise = exerciseController.getFile(testExercise.getId(), testFile.getName());
        Assertions.assertEquals(200, returnedExercise.getStatusCodeValue());
        Assertions.assertEquals(new FileSystemResource(testFile.getFile()), returnedExercise.getBody());
    }

    @Test
    @WithMockUser(roles = {"course-1", "student", "course-1-student"})
    public void getFileNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> exerciseController.getFile(testExercise.getId(), "123"));
    }
}