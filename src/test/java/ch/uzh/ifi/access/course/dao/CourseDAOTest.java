package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseDAOTest {

    private CourseDAO courseDAO = new CourseDAO();

    @Test
    public void emptyExerciseIndex() {
        Map<String, Exercise> exerciseMap = courseDAO.buildExerciseIndex(new ArrayList<>());
        Assertions.assertThat(exerciseMap).isEmpty();
    }

    @Test
    public void buildExerciseIndex() {
        Course course = TestObjectFactory.createCourse("c");

        Assignment a1 = TestObjectFactory.createAssignment("a1");
        Assignment a2 = TestObjectFactory.createAssignment("a2");
        Assignment a3 = TestObjectFactory.createAssignment("a23");
        course.addAssignments(a1, a2, a3);

        Exercise ex1 = TestObjectFactory.createCodeExercise("ex1");
        Exercise ex2 = TestObjectFactory.createCodeExercise("ex2");
        Exercise ex3 = TestObjectFactory.createCodeExercise("ex3");
        Exercise ex4 = TestObjectFactory.createCodeExercise("ex4");
        Exercise ex5 = TestObjectFactory.createCodeExercise("ex5");
        Exercise ex6 = TestObjectFactory.createCodeExercise("ex6");

        a1.addExercises(ex1, ex2);
        a2.addExercise(ex3);
        a3.addExercises(ex4, ex5, ex6);

        Map<String, Exercise> exerciseMap = courseDAO.buildExerciseIndex(List.of(course));
        Assertions.assertThat(exerciseMap).size().isEqualTo(6);
        Assertions.assertThat(exerciseMap.get(ex1.getId())).isEqualTo(ex1);
        Assertions.assertThat(exerciseMap.get(ex2.getId())).isEqualTo(ex2);
        Assertions.assertThat(exerciseMap.get(ex3.getId())).isEqualTo(ex3);
        Assertions.assertThat(exerciseMap.get(ex4.getId())).isEqualTo(ex4);
        Assertions.assertThat(exerciseMap.get(ex5.getId())).isEqualTo(ex5);
        Assertions.assertThat(exerciseMap.get(ex6.getId())).isEqualTo(ex6);
    }

    @Test
    public void selectExerciseById() {
        List<Course> courses = courseDAO.selectAllCourses();
        for (Course c : courses) {
            for (Assignment a : c.getAssignments()) {
                for (Exercise e : a.getExercises()) {
                    Exercise exercise = courseDAO.selectExerciseById(e.getId()).orElseThrow();
                    Assertions.assertThat(exercise).isEqualTo(e);
                }
            }
        }
    }
}