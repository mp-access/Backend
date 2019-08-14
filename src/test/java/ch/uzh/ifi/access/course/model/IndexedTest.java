package ch.uzh.ifi.access.course.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class IndexedTest {

    @Test
    public void hasSameIndex() {
        Exercise ex1 = new Exercise.ExerciseBuilder().index(1).build();
        Exercise ex2 = new Exercise.ExerciseBuilder().index(2).build();

        Assertions.assertThat(ex1.hasSameIndex(ex2)).isFalse();

        ex2.setIndex(ex1.getIndex());
        Assertions.assertThat(ex1.hasSameIndex(ex2)).isTrue();
    }

    @Test
    public void hasSameIndexNull() {
        Exercise ex1 = new Exercise.ExerciseBuilder().index(1).build();
        Assertions.assertThat(ex1.hasSameIndex(null)).isFalse();
    }
}