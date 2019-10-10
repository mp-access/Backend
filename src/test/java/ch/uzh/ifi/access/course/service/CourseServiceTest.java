package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CourseServiceTest {

    @Mock
    private CourseDAO dao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getExercisesByCourseAndAssignmentId() {
        var course = new Course("");
        var a1 = new Assignment("Assignment_1");
        a1.setCourse(course);
        a1.setExercises(Arrays.asList(new Exercise("Exercise_1"), new Exercise("Exercise_2"), new Exercise("Exercise_3")));

        var a2 = new Assignment("Assignment_2");
        a2.setCourse(course);
        a2.setExercises(Arrays.asList(new Exercise("Exercise_1"), new Exercise("Exercise_2")));

        course.addAssignment(a1);
        course.addAssignment(a2);

        when(dao.selectCourseById(any())).thenReturn(Optional.of(course));

        CourseService service = new CourseService(dao, null);
        var optResult = service.getExercisesByCourseAndAssignmentId(course.getId(), a1.getId());

        assertThat(optResult.isPresent()).isTrue();
        assertThat(optResult.get()).hasSize(3);
    }

    @Test
    public void getFileCheckingPrivilegesHasNoAccessToSolutions() {
        Course course = TestObjectFactory.createCourse("");
        Assignment assignment = TestObjectFactory.createAssignment("");
        Exercise exercise = TestObjectFactory.createCodeExercise("");
        course.addAssignment(assignment);
        assignment.addExercise(exercise);
        assignment.setPublishDate(ZonedDateTime.now().minusYears(1));
        assignment.setDueDate(ZonedDateTime.now().plusYears(1));

        VirtualFile vFile1 = TestObjectFactory.createVirtualFile("name1", "py", false);
        VirtualFile vFile2 = TestObjectFactory.createVirtualFile("name2", "py", false);
        VirtualFile privateFile = TestObjectFactory.createVirtualFile("private", ".py", false);
        VirtualFile solutionFile = TestObjectFactory.createVirtualFile("solution", ".py", false);

        exercise.setPublic_files(List.of(vFile1, vFile2));
        exercise.setPrivate_files(List.of(privateFile));
        exercise.setSolution_files(List.of(solutionFile));

        CourseAuthentication student = TestObjectFactory.createCourseAuthentication(Set.of(TestObjectFactory.createStudentAccess(course.getId())));

        CourseService courseService = new CourseService(dao, null);

        Optional<FileSystemResource> shouldGetVFile1 = courseService.getFileCheckingPrivileges(exercise, vFile1.getId(), student);
        Optional<FileSystemResource> shouldGetVFile2 = courseService.getFileCheckingPrivileges(exercise, vFile2.getId(), student);
        Optional<FileSystemResource> shouldNotGetPrivateFile = courseService.getFileCheckingPrivileges(exercise, privateFile.getId(), student);
        Optional<FileSystemResource> shouldNotGetSolutionFile = courseService.getFileCheckingPrivileges(exercise, solutionFile.getId(), student);

        assertThat(shouldGetVFile1.isPresent()).isTrue();
        assertThat(shouldGetVFile2.isPresent()).isTrue();
        assertThat(shouldNotGetPrivateFile.isPresent()).isFalse();
        assertThat(shouldNotGetSolutionFile.isPresent()).isFalse();
        assertThat(shouldGetVFile1.get().getFilename()).isEqualTo(vFile1.getNameWithExtension());
        assertThat(shouldGetVFile2.get().getFilename()).isEqualTo(vFile2.getNameWithExtension());
    }

    @Test
    public void getFileCheckingPrivilegesStudentHasAccessToSolutionsAfterDueDate() {
        Course course = TestObjectFactory.createCourse("");
        Assignment assignment = TestObjectFactory.createAssignment("");
        Exercise exercise = TestObjectFactory.createCodeExercise("");
        course.addAssignment(assignment);
        assignment.addExercise(exercise);
        assignment.setPublishDate(ZonedDateTime.now().minusYears(1));
        assignment.setDueDate(ZonedDateTime.now().minusDays(1));

        VirtualFile vFile1 = TestObjectFactory.createVirtualFile("name1", "py", false);
        VirtualFile vFile2 = TestObjectFactory.createVirtualFile("name2", "py", false);
        VirtualFile privateFile = TestObjectFactory.createVirtualFile("private", ".py", false);
        VirtualFile solutionFile = TestObjectFactory.createVirtualFile("solution", ".py", false);

        exercise.setPublic_files(List.of(vFile1, vFile2));
        exercise.setPrivate_files(List.of(privateFile));
        exercise.setSolution_files(List.of(solutionFile));

        CourseAuthentication student = TestObjectFactory.createCourseAuthentication(Set.of(TestObjectFactory.createStudentAccess(course.getId())));

        CourseService courseService = new CourseService(dao, null);

        Optional<FileSystemResource> shouldGetVFile1 = courseService.getFileCheckingPrivileges(exercise, vFile1.getId(), student);
        Optional<FileSystemResource> shouldGetVFile2 = courseService.getFileCheckingPrivileges(exercise, vFile2.getId(), student);
        Optional<FileSystemResource> shouldGetSolutionFile = courseService.getFileCheckingPrivileges(exercise, solutionFile.getId(), student);
        Optional<FileSystemResource> shouldNotGetPrivateFile = courseService.getFileCheckingPrivileges(exercise, privateFile.getId(), student);

        assertThat(shouldGetVFile1.isPresent()).isTrue();
        assertThat(shouldGetVFile2.isPresent()).isTrue();
        assertThat(shouldGetSolutionFile.isPresent()).isTrue();
        assertThat(shouldNotGetPrivateFile.isPresent()).isFalse();
        assertThat(shouldGetVFile1.get().getFilename()).isEqualTo(vFile1.getNameWithExtension());
        assertThat(shouldGetVFile2.get().getFilename()).isEqualTo(vFile2.getNameWithExtension());
        assertThat(shouldGetSolutionFile.get().getFilename()).isEqualTo(solutionFile.getNameWithExtension());
    }

    @Test
    public void getFileCheckingPrivilegesAdminAccess() {
        Course course = TestObjectFactory.createCourseWithOneAssignmentAndOneExercise("Course", "Assignment", "exercise question");
        Assignment assignment = course.getAssignments().get(0);
        Exercise exercise = assignment.getExercises().get(0);
        assignment.setPublishDate(ZonedDateTime.now().minusYears(1));
        assignment.setDueDate(ZonedDateTime.now().plusYears(1));

        VirtualFile vFile1 = TestObjectFactory.createVirtualFile("name1", "py", false);
        VirtualFile vFile2 = TestObjectFactory.createVirtualFile("name2", "py", false);
        VirtualFile privateFile = TestObjectFactory.createVirtualFile("private", ".py", false);
        VirtualFile solutionFile = TestObjectFactory.createVirtualFile("solution", ".py", false);

        exercise.setPublic_files(List.of(vFile1, vFile2));
        exercise.setPrivate_files(List.of(privateFile));
        exercise.setSolution_files(List.of(solutionFile));

        CourseAuthentication admin = TestObjectFactory.createCourseAuthentication(Set.of(TestObjectFactory.createAdminAccess(course.getId())));

        CourseService courseService = new CourseService(dao, null);

        Optional<FileSystemResource> shouldGetVFile1 = courseService.getFileCheckingPrivileges(exercise, vFile1.getId(), admin);
        Optional<FileSystemResource> shouldGetVFile2 = courseService.getFileCheckingPrivileges(exercise, vFile2.getId(), admin);
        Optional<FileSystemResource> shouldGetPrivateFile = courseService.getFileCheckingPrivileges(exercise, privateFile.getId(), admin);
        Optional<FileSystemResource> shouldGetSolutionFile = courseService.getFileCheckingPrivileges(exercise, solutionFile.getId(), admin);

        assertThat(shouldGetVFile1.isPresent()).isTrue();
        assertThat(shouldGetVFile2.isPresent()).isTrue();
        assertThat(shouldGetPrivateFile.isPresent()).isFalse();
        assertThat(shouldGetSolutionFile.isPresent()).isTrue();
        assertThat(shouldGetVFile1.get().getFilename()).isEqualTo(vFile1.getNameWithExtension());
        assertThat(shouldGetVFile2.get().getFilename()).isEqualTo(vFile2.getNameWithExtension());
        assertThat(shouldGetSolutionFile.get().getFilename()).isEqualTo(solutionFile.getNameWithExtension());
    }
}
