package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MultipleChoiceEvaluatorTest {

    @Test
    public void fullScore() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(Arrays.asList("Hans", "Peter", "Frieda"))
                .solutions(Arrays.asList("Hans", "Peter"))
                .maxScore(2)
                .type(ExerciseType.multipleChoice).build();

        MultipleChoiceSubmission sub = MultipleChoiceSubmission.builder()
                .choices(new HashSet<>(Arrays.asList(0,1)))
                .exerciseId(ex.getId())
                .build();

        MultipleChoiceEvaluator evaluator = new MultipleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(2.0, grade.getScore(), 0.25);
    }

    @Test
    public void partialScore() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(Arrays.asList("Hans", "Peter", "Frieda"))
                .solutions(Arrays.asList("Hans", "Peter"))
                .maxScore(2)
                .type(ExerciseType.multipleChoice).build();

        MultipleChoiceSubmission sub = MultipleChoiceSubmission.builder()
                .choices(new HashSet<>(List.of(1)))
                .exerciseId(ex.getId())
                .build();

        MultipleChoiceEvaluator evaluator = new MultipleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(1.0, grade.getScore(), 0.25);
    }

    @Test
    public void zeroScore() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(Arrays.asList("Hans", "Peter", "Frieda", "Gretel"))
                .solutions(Arrays.asList("Hans", "Peter"))
                .maxScore(2)
                .type(ExerciseType.multipleChoice).build();

        MultipleChoiceSubmission sub = MultipleChoiceSubmission.builder()
                .choices(new HashSet<>(Arrays.asList(2, 3)))
                .exerciseId(ex.getId())
                .build();

        MultipleChoiceEvaluator evaluator = new MultipleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(0.0, grade.getScore(), 0.25);
    }

    @Test
    public void wrongAnswerShowsHint() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(Arrays.asList("Hans", "Peter", "Frieda", "Gretel"))
                .solutions(Arrays.asList("Hans", "Peter"))
                .maxScore(2)
                .hints(List.of("Hinweis"))
                .type(ExerciseType.multipleChoice).build();

        MultipleChoiceSubmission sub = MultipleChoiceSubmission.builder()
                .choices(new HashSet<>(Arrays.asList(2, 3)))
                .exerciseId(ex.getId())
                .build();

        MultipleChoiceEvaluator evaluator = new MultipleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertNotNull(grade.getHints());
        Assertions.assertTrue(grade.getHints().contains("Hinweis"));
    }

    @Test
    public void XOR() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(Arrays.asList("Hans", "Peter", "Frieda"))
                .solutions(Arrays.asList("Hans", "Peter"))
                .maxScore(2)
                .type(ExerciseType.multipleChoice).build();

        MultipleChoiceSubmission sub = MultipleChoiceSubmission.builder()
                .choices(new HashSet<>(Arrays.asList(0,1,2)))
                .exerciseId(ex.getId())
                .build();

        MultipleChoiceEvaluator evaluator = new MultipleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(1.0, grade.getScore(), 0.25);
    }

}
