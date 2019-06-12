package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.workspace.SubmissionEvaluation;
import ch.uzh.ifi.access.course.model.workspace.TextSubmission;
import org.junit.Assert;
import org.junit.Test;

public class TextEvaluatorTest {

    @Test
    public void test() {
        Exercise ex = Exercise.builder().type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("Abz")
                .exercise(ex)
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub);

        Assert.assertEquals(1, grade.getScore());
    }

}
