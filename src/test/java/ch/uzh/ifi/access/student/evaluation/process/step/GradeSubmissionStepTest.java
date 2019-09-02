package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.student.evaluation.evaluator.*;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.SingleChoiceSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class GradeSubmissionStepTest {

    @Test
    public void mcEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new MultipleChoiceSubmission());
        Assertions.assertThat(evaluator).isExactlyInstanceOf(MultipleChoiceEvaluator.class);
    }

    @Test
    public void textEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new TextSubmission());
        Assertions.assertThat(evaluator).isExactlyInstanceOf(TextEvaluator.class);
    }

    @Test
    public void codeEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new CodeSubmission());
        Assertions.assertThat(evaluator).isExactlyInstanceOf(CodeEvaluator.class);
    }

    @Test
    public void singleChoiceEvaluator() {
        GradeSubmissionStep gradeSubmissionStep = new GradeSubmissionStep(null, null);

        StudentSubmissionEvaluator evaluator = gradeSubmissionStep.evaluator(new SingleChoiceSubmission());
        Assertions.assertThat(evaluator).isExactlyInstanceOf(SingleChoiceEvaluator.class);
    }
}