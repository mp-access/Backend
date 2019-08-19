package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CourseServiceTest {

    @Mock
    private CourseDAO dao;

    @Test
    public void getExercisesByCourseAndAssignmentId() {
        MockitoAnnotations.initMocks(this);
        var course = new Course("");
        var a1 = new Assignment("Assignment_1");
        a1.setCourse(course);
        a1.setExercises(Arrays.asList(new Exercise("Exercise_1"),new Exercise("Exercise_2"),new Exercise("Exercise_3")));

        var a2 = new Assignment("Assignment_2");
        a2.setCourse(course);
        a2.setExercises(Arrays.asList(new Exercise("Exercise_1"),new Exercise("Exercise_2")));

        course.addAssignment(a1);
        course.addAssignment(a2);

        when(dao.selectCourseById(any())).thenReturn(Optional.of(course));

        CourseService service = new CourseService(dao);
        var optResult = service.getExercisesByCourseAndAssignmentId(course.getId(), a1.getId());

        assertThat(optResult.isPresent()).isTrue();
        assertThat(optResult.get()).hasSize(3);

    }

}
