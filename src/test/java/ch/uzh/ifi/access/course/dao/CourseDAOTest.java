package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.event.BreakingChangeNotifier;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.util.RepoCacher;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CourseDAOTest {

    private CourseDAO courseDAO;

    @Mock
    private RepoCacher repoCacher;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ApplicationEventPublisher noOpPublisher = (event) -> {
        };
        BreakingChangeNotifier breakingChangeNotifier = new BreakingChangeNotifier(noOpPublisher);

        courseDAO = new CourseDAO(breakingChangeNotifier, repoCacher);
    }

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

    @Test
    public void lookForBreakingChangesSingleExerciseBroken() {
        Course before = TestObjectFactory.createCourse("title");
        Course after = TestObjectFactory.createCourse(before.getTitle());
        Assignment assignmentBefore = TestObjectFactory.createAssignment("assignment");
        Assignment assignmentAfter = TestObjectFactory.createAssignment("assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createTextExercise("");
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise("");
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise("");
        Exercise exerciseAfter2 = TestObjectFactory.createTextExercise("");
        exerciseBefore2.setOrder(3);
        exerciseAfter2.setOrder(exerciseBefore2.getOrder());

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);
        assignmentAfter.addExercise(exerciseAfter2);

        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);

        Assertions.assertThat(breakingChanges).size().isEqualTo(1);
        Assertions.assertThat(breakingChanges).contains(exerciseBefore1);
    }

    @Test
    public void lookForBreakingChangesTwoExerciseBroken() {
        Course before = TestObjectFactory.createCourse("title");
        Course after = TestObjectFactory.createCourse(before.getTitle());
        Assignment assignmentBefore = TestObjectFactory.createAssignment("assignment");
        Assignment assignmentAfter = TestObjectFactory.createAssignment("assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createTextExercise("");
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise("");
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise("");
        Exercise exerciseAfter2 = TestObjectFactory.createTextExercise("");
        exerciseBefore2.setOrder(3);
        exerciseAfter2.setOrder(4);

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);
        assignmentAfter.addExercise(exerciseAfter2);

        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);

        Assertions.assertThat(breakingChanges).size().isEqualTo(2);
        Assertions.assertThat(breakingChanges).contains(exerciseBefore1);
        Assertions.assertThat(breakingChanges).contains(exerciseBefore2);
    }

    @Test
    public void lookForBreakingChangesExerciseWasRemovedExerciseWasUpdated() {
        Course before = TestObjectFactory.createCourse("title");
        Course after = TestObjectFactory.createCourse(before.getTitle());
        Assignment assignmentBefore = TestObjectFactory.createAssignment("assignment");
        Assignment assignmentAfter = TestObjectFactory.createAssignment("assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createTextExercise("");
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise("");
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise("");
        exerciseBefore2.setOrder(3);

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);

        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);

        Assertions.assertThat(breakingChanges).size().isEqualTo(2);
        Assertions.assertThat(breakingChanges).contains(exerciseBefore1);
        Assertions.assertThat(breakingChanges).contains(exerciseBefore2);
    }

    @Test
    public void addNewAssignmentShouldNotBeBreakingChange() {
        Course before = TestObjectFactory.createCourse("title");
        Course after = TestObjectFactory.createCourse(before.getTitle());

        // Create 2 assignment for before and after updates
        Assignment assignment1 = TestObjectFactory.createAssignment("assignment 1");
        Assignment assignment1AfterUpdate = TestObjectFactory.createAssignment("assignment 1");
        Assignment assignment2 = TestObjectFactory.createAssignment("assignment 2");
        Assignment assignment2AfterUpdate = TestObjectFactory.createAssignment("assignment 2");

        Exercise a1Ex1 = TestObjectFactory.createTextExercise("Question 1");
        Exercise a1Ex1AfterUpdate = TestObjectFactory.createTextExercise(a1Ex1.getQuestion());
        Exercise a1Ex2 = TestObjectFactory.createCodeExercise("Question 2");
        Exercise a1Ex2AfterUpdate = TestObjectFactory.createCodeExercise(a1Ex2.getQuestion());
        a1Ex1.setGitHash("123");
        a1Ex1AfterUpdate.setGitHash("123");
        a1Ex2.setGitHash("234");
        a1Ex2AfterUpdate.setGitHash("234");
        a1Ex1.setOrder(1);
        a1Ex1AfterUpdate.setOrder(1);
        a1Ex2.setOrder(2);
        a1Ex2AfterUpdate.setOrder(2);

        // The second assignment has exercises with different types and question than those in assignment 1
        Exercise a2Ex1 = TestObjectFactory.createCodeExercise("Question xyz");
        Exercise a2Ex1AfterUpdate = TestObjectFactory.createCodeExercise("Question xyz");
        Exercise a2Ex2 = TestObjectFactory.createTextExercise("Question asd");
        Exercise a2Ex2AfterUpdate = TestObjectFactory.createTextExercise("Question asd");
        a2Ex1.setGitHash("345");
        a2Ex1AfterUpdate.setGitHash("456");
        a2Ex2.setGitHash("345");
        a2Ex2AfterUpdate.setGitHash("456");

        a2Ex1.setOrder(1);
        a2Ex1AfterUpdate.setOrder(1);
        a2Ex2.setOrder(2);
        a2Ex2AfterUpdate.setOrder(2);

        assignment1.addExercise(a1Ex1);
        assignment1.addExercise(a1Ex2);
        assignment1AfterUpdate.addExercise(a1Ex1AfterUpdate);
        assignment1AfterUpdate.addExercise(a1Ex2AfterUpdate);
        assignment2.addExercise(a2Ex1);
        assignment2.addExercise(a2Ex2);
        assignment2AfterUpdate.addExercise(a2Ex1AfterUpdate);
        assignment2AfterUpdate.addExercise(a2Ex2AfterUpdate);

        before.addAssignment(assignment1);
        before.addAssignment(assignment2);

        after.addAssignment(assignment1AfterUpdate);
        after.addAssignment(assignment2AfterUpdate);

        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);

        Assertions.assertThat(breakingChanges).size().isEqualTo(0);
    }

    @Test
    public void rollbackNoGitUrlSetTest() {
        Course before = TestObjectFactory.createCourse("title");
        Course after = TestObjectFactory.createCourse(before.getTitle());
        Assignment assignmentBefore = TestObjectFactory.createAssignment("assignment");
        Assignment assignmentAfter = TestObjectFactory.createAssignment("assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createCodeExercise("");
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise("");
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);
        exerciseBefore1.setPublic_files(List.of(TestObjectFactory.createVirtualFile("name", "py", false)));

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise("");
        Exercise exerciseAfter2 = TestObjectFactory.createTextExercise("");
        exerciseBefore2.setOrder(3);
        exerciseAfter2.setOrder(exerciseBefore2.getOrder());

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);
        assignmentAfter.addExercise(exerciseAfter2);

        Course updated = courseDAO.updateCourse(before);
        Assertions.assertThat(updated).isNull();
    }

    @Test
    public void rollbackDuringUpdateTest() {
        String oldTitle = "title";
        String newTitle = "New title";
        Course before = TestObjectFactory.createCourse(oldTitle);
        Course after = Mockito.spy(TestObjectFactory.createCourse(newTitle));

        when(repoCacher.retrieveCourseData(any(String[].class))).thenReturn(List.of(after));
        when(after.getOrderedItems()).thenThrow(new UnsupportedOperationException());

        Course updated = courseDAO.updateCourse(before);
        // Should have rolled back -> title should still be oldTitle
        Assertions.assertThat(updated).isNull();
        Assertions.assertThat(before.getTitle()).isEqualTo(oldTitle);
    }
}