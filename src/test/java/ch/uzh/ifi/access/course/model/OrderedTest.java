package ch.uzh.ifi.access.course.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OrderedTest {

    @Test
    public void hasSameOrder() {
        Exercise ex1 = new Exercise.ExerciseBuilder().order(1).build();
        Exercise ex2 = new Exercise.ExerciseBuilder().order(2).build();

        Assertions.assertThat(ex1.hasSameOrder(ex2)).isFalse();

        ex2.setOrder(ex1.getOrder());
        Assertions.assertThat(ex1.hasSameOrder(ex2)).isTrue();
    }

    @Test
    public void hasSameIndexNull() {
        Exercise ex1 = new Exercise.ExerciseBuilder().order(1).build();
        Assertions.assertThat(ex1.hasSameOrder(null)).isFalse();
    }
}