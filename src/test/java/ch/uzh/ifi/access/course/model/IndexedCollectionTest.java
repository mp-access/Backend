package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

public class IndexedCollectionTest {

    @Test
    public void updateOnlyExercises() {
        Assignment assignment = new Assignment();
        Assignment updateAssignment = new Assignment();

        Exercise ex1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1").index(0).build();
        Exercise updateEx1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").index(0).build();

        Exercise ex2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1").index(1).build();
        Exercise updateEx2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").index(1).build();

        assignment.addExercise(ex1);
        assignment.addExercise(ex2);

        updateAssignment.addExercise(updateEx1);
        updateAssignment.addExercise(updateEx2);

        assignment.update(updateAssignment);
        List<Exercise> updatedExerciseList = assignment.getExercises();

        // ex1 is still ex1 but fields are updated
        Assertions.assertThat(updatedExerciseList.get(0)).isEqualTo(ex1);
        Assertions.assertThat(updatedExerciseList.get(0).getGitHash()).isEqualTo(updateEx1.getGitHash());

        // ex2 is still ex2 but fields are updated
        Assertions.assertThat(updatedExerciseList.get(1)).isEqualTo(ex2);
        Assertions.assertThat(updatedExerciseList.get(1).getGitHash()).isEqualTo(updateEx2.getGitHash());
    }

    @Test
    public void updateMixed() {
        Assignment assignment = new Assignment();
        Assignment updateAssignment = new Assignment();

        Exercise ex1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1").index(0).build();
        Exercise updateEx1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").index(0).build();

        Exercise ex2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).index(1).build();

        Exercise newExercise3 = new Exercise.ExerciseBuilder().id(new Utils().getID()).index(2).build();

        assignment.addExercise(ex1);
        assignment.addExercise(ex2);

        updateAssignment.addExercise(updateEx1);
        updateAssignment.addExercise(newExercise3);

        assignment.update(updateAssignment);
        List<Exercise> updatedExerciseList = assignment.getExercises();

        // Ex1 is still ex1 but fields are updated
        Assertions.assertThat(updatedExerciseList.get(0)).isEqualTo(ex1);
        Assertions.assertThat(updatedExerciseList.get(0).getGitHash()).isEqualTo(updateEx1.getGitHash());

        // Ex2 was removed as it was not in the updated exercise list
        Assertions.assertThat(updatedExerciseList).doesNotContain(ex2);

        // Ex3 was added to the list
        Assertions.assertThat(updatedExerciseList.get(1)).isEqualTo(newExercise3);
    }

    @Test
    public void updatePreviousExercisesEmpty() {
        Assignment assignment = new Assignment();
        Assignment updateAssignment = new Assignment();


        Exercise newExercise1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").index(0).build();
        Exercise newExercise2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).index(1).build();

        updateAssignment.addExercise(newExercise1);
        updateAssignment.addExercise(newExercise2);

        // assignment has no exercises
        Assertions.assertThat(assignment.getExercises()).isEmpty();

        assignment.update(updateAssignment);
        List<Exercise> updatedExerciseList = assignment.getExercises();

        // assignment had no exercises, now has 2 exercises
        Assertions.assertThat(updatedExerciseList.get(0)).isEqualTo(newExercise1);
        Assertions.assertThat(updatedExerciseList.get(1)).isEqualTo(newExercise2);
    }

    @Test
    public void updateHasNoExercises() {
        Assignment assignment = new Assignment();
        Assignment updateAssignment = new Assignment();

        Exercise exercise1 = new Exercise.ExerciseBuilder().id(new Utils().getID()).gitHash("ex1.1").index(0).build();
        Exercise exercise2 = new Exercise.ExerciseBuilder().id(new Utils().getID()).index(1).build();

        assignment.addExercise(exercise1);
        assignment.addExercise(exercise2);

        // assignment has 2 exercises
        Assertions.assertThat(assignment.getExercises()).size().isEqualTo(2);
        Assertions.assertThat(updateAssignment.getExercises()).isEmpty();

        assignment.update(updateAssignment);

        List<Exercise> updatedExerciseList = assignment.getExercises();

        // Ex1 has no exercises
        Assertions.assertThat(updatedExerciseList).isEmpty();
    }
}