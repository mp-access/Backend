package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OrderedCollectionTest {

    @Test
    public void updateOnlyExercises() {
        Assignment assignment = new Assignment("Assignment_1");
        Assignment updateAssignment = new Assignment("Assignment_1");

        Exercise ex1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1").order(0).build();
        Exercise updateEx1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").order(0).build();

        Exercise ex2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1").order(1).build();
        Exercise updateEx2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").order(1).build();

        assignment.addExercise(ex1);
        assignment.addExercise(ex2);

        updateAssignment.addExercise(updateEx1);
        updateAssignment.addExercise(updateEx2);

        assignment.update(updateAssignment);
        List<Exercise> updatedExerciseList = assignment.getExercises();

        // ex1 is still ex1 but fields are updated
        Assertions.assertEquals(ex1, updatedExerciseList.get(0));
        Assertions.assertEquals(updateEx1.getGitHash(), updatedExerciseList.get(0).getGitHash());

        // ex2 is still ex2 but fields are updated
        Assertions.assertEquals(ex2, updatedExerciseList.get(1));
        Assertions.assertEquals(updateEx2.getGitHash(), updatedExerciseList.get(1).getGitHash());
    }

    @Test
    public void updateMixed() {
        Assignment assignment = new Assignment("Assignment_1");
        Assignment updateAssignment = new Assignment("Assignment_1");

        Exercise ex1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1").order(0).build();
        Exercise updateEx1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").order(0).build();

        Exercise ex2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).order(1).build();

        Exercise newExercise3 = new Exercise.ExerciseBuilder().id(new Utils().getID()).order(2).build();

        assignment.addExercise(ex1);
        assignment.addExercise(ex2);

        updateAssignment.addExercise(updateEx1);
        updateAssignment.addExercise(newExercise3);

        assignment.update(updateAssignment);
        List<Exercise> updatedExerciseList = assignment.getExercises();

        // Ex1 is still ex1 but fields are updated
        Assertions.assertEquals(ex1, updatedExerciseList.get(0));
        Assertions.assertEquals(updateEx1.getGitHash(), updatedExerciseList.get(0).getGitHash());

        // Ex2 was removed as it was not in the updated exercise list
        Assertions.assertFalse(updatedExerciseList.contains(ex2));

        // Ex3 was added to the list
        Assertions.assertEquals(newExercise3, updatedExerciseList.get(1));
    }

    @Test
    public void updatePreviousExercisesEmpty() {
        Assignment assignment = new Assignment("Assignment_1");
        Assignment updateAssignment = new Assignment("Assignment_1");


        Exercise newExercise1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").order(0).build();
        Exercise newExercise2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).order(1).build();

        updateAssignment.addExercise(newExercise1);
        updateAssignment.addExercise(newExercise2);

        // assignment has no exercises
        Assertions.assertTrue(assignment.getExercises().isEmpty());

        assignment.update(updateAssignment);
        List<Exercise> updatedExerciseList = assignment.getExercises();

        // assignment had no exercises, now has 2 exercises
        Assertions.assertEquals(newExercise1, updatedExerciseList.get(0));
        Assertions.assertEquals(newExercise2, updatedExerciseList.get(1));
    }

    @Test
    public void updateHasNoExercises() {
        Assignment assignment = new Assignment("Assignment_1");
        Assignment updateAssignment = new Assignment("Assignment_1");

        Exercise exercise1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").order(0).build();
        Exercise exercise2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).order(1).build();

        assignment.addExercise(exercise1);
        assignment.addExercise(exercise2);

        // assignment has 2 exercises
        Assertions.assertEquals(2, assignment.getExercises().size());
        Assertions.assertTrue(updateAssignment.getExercises().isEmpty());

        assignment.update(updateAssignment);

        List<Exercise> updatedExerciseList = assignment.getExercises();

        // Ex1 has no exercises
        Assertions.assertTrue(updatedExerciseList.isEmpty());
    }
}