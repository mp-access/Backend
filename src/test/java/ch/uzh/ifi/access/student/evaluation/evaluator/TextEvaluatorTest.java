package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TextEvaluatorTest {

    @Test
    public void test() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(List.of("Abz"))
                .maxScore(1)
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("Abz")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(1.0, grade.getScore(), 0.25);
    }
    
    @Test
    public void testZeroPoints() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(List.of("Abz"))
                .maxScore(1)
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("a")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(0.0, grade.getScore(), 0.25);
    }

    @Test
    public void wrongAnswerShowsHint() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(List.of("Abz"))
                .maxScore(1)
                .hints(Arrays.asList("Hinweis 1", "Hinweis 2"))
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("a")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(0.0, grade.getScore(), 0.25);
        Assertions.assertNotNull(grade.getHints());
        Assertions.assertTrue(grade.getHints().contains("Hinweis 1"));
    }

    @Test
    public void exerciseHasNoHints() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(List.of("Abz"))
                .maxScore(1)
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("a")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assertions.assertEquals(0.0, grade.getScore(), 0.25);
        Assertions.assertNull(grade.getHints());
    }


}
