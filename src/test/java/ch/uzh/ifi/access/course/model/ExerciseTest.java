package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.TestObjectFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExerciseTest {

    @Test
    public void isNotBreakingChange() {
        Exercise exerciseBefore1 = TestObjectFactory.createCodeExercise();
        Exercise exerciseAfter1 = TestObjectFactory.createCodeExercise();
        exerciseBefore1.setGitHash("ex1");
        exerciseBefore1.setOrder(0);
        exerciseAfter1.setGitHash("ex1.1");
        exerciseAfter1.setOrder(exerciseBefore1.getOrder());

        Assertions.assertFalse(exerciseBefore1.isBreakingChange(exerciseAfter1));
    }

    @Test
    public void isBreakingChange2() {
        Exercise exerciseBefore1 = TestObjectFactory.createCodeExercise();
        Exercise exerciseAfter1 = TestObjectFactory.createCodeExercise();
        exerciseBefore1.setGitHash("ex1");
        exerciseBefore1.setOrder(0);
        exerciseAfter1.setGitHash(exerciseBefore1.getGitHash());
        exerciseAfter1.setOrder(exerciseBefore1.getOrder());

        Assertions.assertFalse(exerciseBefore1.isBreakingChange(exerciseAfter1));
    }
}