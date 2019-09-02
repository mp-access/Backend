package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;

public interface StudentSubmissionEvaluator {

    SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise);

}
