package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.TestObjectFactory;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ExerciseTest {

    @Test
    public void isNotBreakingChange() {
        Exercise exerciseBefore1 = TestObjectFactory.createCodeExercise("");
        Exercise exerciseAfter1 = TestObjectFactory.createCodeExercise("");
        exerciseBefore1.setGitHash("ex1");
        exerciseBefore1.setIndex(0);
        exerciseAfter1.setGitHash("ex1.1");
        exerciseAfter1.setIndex(exerciseBefore1.getIndex());

        Assertions.assertThat(exerciseBefore1.isBreakingChange(exerciseAfter1)).isFalse();
    }

    @Test
    public void isBreakingChange2() {
        Exercise exerciseBefore1 = TestObjectFactory.createCodeExercise("");
        Exercise exerciseAfter1 = TestObjectFactory.createCodeExercise("");
        exerciseBefore1.setGitHash("ex1");
        exerciseBefore1.setIndex(0);
        exerciseAfter1.setGitHash(exerciseBefore1.getGitHash());
        exerciseAfter1.setIndex(exerciseBefore1.getIndex());

        Assertions.assertThat(exerciseBefore1.isBreakingChange(exerciseAfter1)).isFalse();
    }
}