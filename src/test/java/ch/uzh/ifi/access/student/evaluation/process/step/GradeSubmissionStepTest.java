package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.student.evaluation.evaluator.CodeEvaluator;
import ch.uzh.ifi.access.student.evaluation.evaluator.MultipleChoiceEvaluator;
import ch.uzh.ifi.access.student.evaluation.evaluator.StudentSubmissionEvaluator;
import ch.uzh.ifi.access.student.evaluation.evaluator.TextEvaluator;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
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

}