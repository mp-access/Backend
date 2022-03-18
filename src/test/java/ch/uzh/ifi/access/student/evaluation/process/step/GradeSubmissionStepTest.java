package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.student.evaluation.evaluator.*;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.SingleChoiceSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GradeSubmissionStepTest {

    @Test
    public void mcEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new MultipleChoiceSubmission());
        Assertions.assertInstanceOf(MultipleChoiceEvaluator.class, evaluator);
    }

    @Test
    public void textEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new TextSubmission());
        Assertions.assertInstanceOf(TextEvaluator.class, evaluator);
    }

    @Test
    public void codeEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new CodeSubmission());
        Assertions.assertInstanceOf(CodeEvaluator.class, evaluator);
    }

    @Test
    public void singleChoiceEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new SingleChoiceSubmission());
        Assertions.assertInstanceOf(SingleChoiceEvaluator.class, evaluator);
    }

}