package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.junit.Assert;
import org.junit.Test;

public class TextEvaluatorTest {

    @Test
    public void test() {
        Exercise ex = Exercise.builder()
                .id("e1")
                .type(ExerciseType.text).build();

        TextSubmission sub = TextSubmission.builder()
                .answer("Abz")
                .exerciseId(ex.getId())
                .build();

        TextEvaluator evaluator = new TextEvaluator();
        SubmissionEvaluation grade = evaluator.evaluate(sub, ex);

        Assert.assertEquals(1, grade.getScore());
        Assert.assertEquals(1, grade.getMaxScore());
    }

}
