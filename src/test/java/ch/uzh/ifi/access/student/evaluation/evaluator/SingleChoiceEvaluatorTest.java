package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.SingleChoiceSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class SingleChoiceEvaluatorTest {

    @Test
    public void fullScore() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(List.of("Hans", "Peter", "Frieda"))
                .solutions(List.of("Hans"))
                .maxScore(2)
                .type(ExerciseType.singleChoice).build();

        SingleChoiceSubmission sub = SingleChoiceSubmission.builder()
                .choice(0)
                .exerciseId(ex.getId())
                .build();

        StudentSubmissionEvaluator evaluator = new SingleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(2, grade.getScore(), 0.25);
    }

    @Test
    public void zeroScore() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(List.of("Hans", "Peter", "Frieda"))
                .solutions(List.of("Hans"))
                .maxScore(2)
                .type(ExerciseType.singleChoice).build();

        SingleChoiceSubmission sub = SingleChoiceSubmission.builder()
                .choice(1)
                .exerciseId(ex.getId())
                .build();

        StudentSubmissionEvaluator evaluator = new SingleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(0.0, grade.getScore(), 0.25);
    }

    @Test
    public void wrongAnswerShowsHint() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .options(List.of("Hans", "Peter", "Frieda"))
                .solutions(List.of("Hans"))
                .maxScore(2)
                .hints(List.of("Hinweis"))
                .type(ExerciseType.singleChoice).build();

        SingleChoiceSubmission sub = SingleChoiceSubmission.builder()
                .choice(1)
                .exerciseId(ex.getId())
                .build();

        StudentSubmissionEvaluator evaluator = new SingleChoiceEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertNotNull(grade.getHints());
        Assertions.assertTrue(grade.getHints().contains("Hinweis"));
    }
}
