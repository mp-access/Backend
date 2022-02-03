package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.event.BreakingChangeNotifier;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.util.RepoCacher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CourseDAO.class})
class CourseDAOTest {

    @MockBean
    private RepoCacher repoCacher;

    @MockBean
    private BreakingChangeNotifier breakingChangeNotifier;

    @Autowired
    private CourseDAO courseDAO;

    @Test
    void emptyExerciseIndex() {
        Map<String, Exercise> exerciseMap = courseDAO.buildExerciseIndex(new ArrayList<>());
        Assertions.assertTrue(exerciseMap.isEmpty());
    }

    @Test
    void buildExerciseIndex() {
        Course course = new Course("c");

        Assignment a1 = new Assignment("a1");
        Assignment a2 = new Assignment("a2");
        Assignment a3 = new Assignment("a23");
        course.addAssignments(a1, a2, a3);

        Exercise ex1 = TestObjectFactory.createCodeExercise();
        Exercise ex2 = TestObjectFactory.createCodeExercise();
        Exercise ex3 = TestObjectFactory.createCodeExercise();
        Exercise ex4 = TestObjectFactory.createCodeExercise();
        Exercise ex5 = TestObjectFactory.createCodeExercise();
        Exercise ex6 = TestObjectFactory.createCodeExercise();

        a1.addExercises(ex1, ex2);
        a2.addExercise(ex3);
        a3.addExercises(ex4, ex5, ex6);

        Map<String, Exercise> exerciseMap = courseDAO.buildExerciseIndex(List.of(course));
        Assertions.assertEquals(6, exerciseMap.size());
        Assertions.assertEquals(ex1, exerciseMap.get(ex1.getId()));
        Assertions.assertEquals(ex2, exerciseMap.get(ex2.getId()));
        Assertions.assertEquals(ex3, exerciseMap.get(ex3.getId()));
        Assertions.assertEquals(ex4, exerciseMap.get(ex4.getId()));
        Assertions.assertEquals(ex5, exerciseMap.get(ex5.getId()));
        Assertions.assertEquals(ex6, exerciseMap.get(ex6.getId()));
    }

    @Test
    void selectExerciseById() {
        List<Course> courses = courseDAO.selectAllCourses();
        for (Course c : courses) {
            for (Assignment a : c.getAssignments()) {
                for (Exercise e : a.getExercises()) {
                    Exercise exercise = courseDAO.selectExerciseById(e.getId()).orElseThrow();
                    Assertions.assertEquals(e, exercise);
                }
            }
        }
    }

    @Test
    void lookForBreakingChangesSingleExerciseBroken() {
        Course before = new Course("title");
        Course after = new Course(before.getTitle());
        Assignment assignmentBefore = new Assignment("Assignment");
        Assignment assignmentAfter = new Assignment("Assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createTextExercise();
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise();
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise();
        Exercise exerciseAfter2 = TestObjectFactory.createTextExercise();
        exerciseBefore2.setOrder(3);
        exerciseAfter2.setOrder(exerciseBefore2.getOrder());

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);
        assignmentAfter.addExercise(exerciseAfter2);

        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);

        Assertions.assertEquals(1, breakingChanges.size());
        Assertions.assertTrue(breakingChanges.contains(exerciseBefore1));
    }

    @Test
    void lookForBreakingChangesTwoExerciseBroken() {
        Course before = new Course("title");
        Course after = new Course(before.getTitle());
        Assignment assignmentBefore = new Assignment("Assignment");
        Assignment assignmentAfter = new Assignment("Assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createTextExercise();
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise();
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise();
        Exercise exerciseAfter2 = TestObjectFactory.createTextExercise();
        exerciseBefore2.setOrder(3);
        exerciseAfter2.setOrder(4);

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);
        assignmentAfter.addExercise(exerciseAfter2);

        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);

        Assertions.assertEquals(2, breakingChanges.size());
        Assertions.assertTrue(breakingChanges.contains(exerciseBefore1));
        Assertions.assertTrue(breakingChanges.contains(exerciseBefore2));
    }

    @Test
    void lookForBreakingChangesExerciseWasRemovedExerciseWasUpdated() {
        Course before = new Course("title");
        Course after = new Course(before.getTitle());
        Assignment assignmentBefore = new Assignment("Assignment");
        Assignment assignmentAfter = new Assignment("Assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createTextExercise();
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise();
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise();
        exerciseBefore2.setOrder(3);

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);

        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);

        Assertions.assertEquals(2, breakingChanges.size());
        Assertions.assertTrue(breakingChanges.contains(exerciseBefore1));
        Assertions.assertTrue(breakingChanges.contains(exerciseBefore2));
    }

    @Test
    void addNewAssignmentShouldNotBeBreakingChange() {
        Course before = new Course("title");
        Course after = new Course(before.getTitle());

        // Create 2 assignment for before and after updates
        Assignment assignment1 = new Assignment("assignment 1");
        Assignment assignment1AfterUpdate = new Assignment("assignment 1");
        Assignment assignment2 = new Assignment("assignment 2");
        Assignment assignment2AfterUpdate = new Assignment("assignment 2");

        Exercise a1Ex1 = TestObjectFactory.createTextExercise();
        Exercise a1Ex1AfterUpdate = TestObjectFactory.createTextExercise();
        a1Ex1AfterUpdate.setQuestion(a1Ex1.getQuestion());
        Exercise a1Ex2 = TestObjectFactory.createCodeExercise();
        Exercise a1Ex2AfterUpdate = TestObjectFactory.createCodeExercise();
        a1Ex2AfterUpdate.setQuestion(a1Ex2.getQuestion());
        a1Ex1.setGitHash("123");
        a1Ex1AfterUpdate.setGitHash("123");
        a1Ex2.setGitHash("234");
        a1Ex2AfterUpdate.setGitHash("234");
        a1Ex1.setOrder(1);
        a1Ex1AfterUpdate.setOrder(1);
        a1Ex2.setOrder(2);
        a1Ex2AfterUpdate.setOrder(2);

        // The second assignment has exercises with different types and question than those in assignment 1
        Exercise a2Ex1 = TestObjectFactory.createCodeExercise();
        Exercise a2Ex1AfterUpdate = TestObjectFactory.createCodeExercise();
        Exercise a2Ex2 = TestObjectFactory.createTextExercise();
        Exercise a2Ex2AfterUpdate = TestObjectFactory.createTextExercise();
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

        Assertions.assertEquals(0, breakingChanges.size());
    }

    @Test
    void rollbackNoGitUrlSetTest() {
        Course before = new Course("title");
        Course after = new Course(before.getTitle());
        Assignment assignmentBefore = new Assignment("Assignment");
        Assignment assignmentAfter = new Assignment("Assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createCodeExercise();
        Exercise exerciseAfter1 = TestObjectFactory.createTextExercise();
        exerciseBefore1.setOrder(1);
        exerciseAfter1.setOrder(2);
        exerciseBefore1.setPublic_files(List.of(TestObjectFactory.createVirtualFile("name", "py", false)));

        Exercise exerciseBefore2 = TestObjectFactory.createTextExercise();
        Exercise exerciseAfter2 = TestObjectFactory.createTextExercise();
        exerciseBefore2.setOrder(3);
        exerciseAfter2.setOrder(exerciseBefore2.getOrder());

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);
        assignmentAfter.addExercise(exerciseAfter2);

        Course updated = courseDAO.updateCourse(before);
        Assertions.assertNull(updated);
    }

    @Test
    void rollbackDuringUpdateTest() {
        String oldTitle = "title";
        String newTitle = "New title";
        Course before = new Course(oldTitle);
        Course after = Mockito.spy(new Course(newTitle));

        when(repoCacher.retrieveCourseData(any())).thenReturn(List.of(after));
        when(after.getOrderedItems()).thenThrow(new UnsupportedOperationException());

        Course updated = courseDAO.updateCourse(before);
        // Should have rolled back -> title should still be oldTitle
        Assertions.assertNull(updated);
        Assertions.assertEquals(oldTitle, before.getTitle());
    }
}