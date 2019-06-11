package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
import ch.uzh.ifi.access.course.model.workspace.SubmissionEvaluation;

public interface StudentSubmissionEvaluator {

    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise);

}
