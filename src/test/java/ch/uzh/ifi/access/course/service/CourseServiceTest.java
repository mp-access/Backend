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
        var a1 = new Assignment();
        a1.setCourse(course);
        a1.setExercises(Arrays.asList(new Exercise(),new Exercise(),new Exercise()));

        var a2 = new Assignment();
        a2.setCourse(course);
        a2.setExercises(Arrays.asList(new Exercise(),new Exercise()));

        course.addAssignment(a1);
        course.addAssignment(a2);

        when(dao.selectCourseById(any())).thenReturn(Optional.of(course));

        CourseService service = new CourseService(dao);
        var optResult = service.getExercisesByCourseAndAssignmentId(course.getId(), a1.getId());

        assertThat(optResult.isPresent()).isTrue();
        assertThat(optResult.get()).hasSize(3);

    }

}
