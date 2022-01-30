package ch.uzh.ifi.access.course.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderedTest {

    @Test
    public void hasSameOrder() {
        Exercise ex1 = new Exercise.ExerciseBuilder().order(1).build();
        Exercise ex2 = new Exercise.ExerciseBuilder().order(2).build();

        Assertions.assertFalse(ex1.hasSameOrder(ex2));

        ex2.setOrder(ex1.getOrder());
        Assertions.assertTrue(ex1.hasSameOrder(ex2));
    }

    @Test
    public void hasSameIndexNull() {
        Exercise ex1 = new Exercise.ExerciseBuilder().order(1).build();
        Assertions.assertFalse(ex1.hasSameOrder(null));
    }
}