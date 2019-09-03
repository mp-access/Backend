package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TextEvaluatorTest {

    @Test
    public void test() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(Arrays.asList("Abz"))
                .maxScore(1)
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("Abz")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assert.assertEquals(1.0, grade.getScore(), 0.25);
    }
    
    @Test
    public void testZeroPoints() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(Arrays.asList("Abz"))
                .maxScore(1)
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("a")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assert.assertEquals(0.0, grade.getScore(), 0.25);
    }

    @Test
    public void incorrectAnswerWithHints() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(Arrays.asList("Abz"))
                .maxScore(1)
                .hints(Arrays.asList("Hinweis 1", "Hinweis 2"))
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("a")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assert.assertEquals(0.0, grade.getScore(), 0.25);
        Assert.assertNotNull("Hints not empty", grade.getHints());
        Assert.assertTrue("Contains a certain hint.", grade.getHints().contains("Hinweis 1"));
    }

    @Test
    public void exerciseHasNoHints() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .solutions(Arrays.asList("Abz"))
                .maxScore(1)
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("a")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assert.assertEquals(0.0, grade.getScore(), 0.25);
        Assert.assertNull(grade.getHints());
    }


}
