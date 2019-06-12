package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.springframework.util.Assert;

import java.time.Instant;

public class TextEvaluator implements StudentSubmissionEvaluator {

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);

        TextSubmission textSub = (TextSubmission) submission;

        if (textSub.getAnswer().length() > 0) {
            // TODO: Get correct solution from exercise.
            if ("Abz".equalsIgnoreCase(textSub.getAnswer().trim())) {
                return new SubmissionEvaluation(1, Instant.now());
            }
        }

        return new SubmissionEvaluation(0, Instant.now());
    }

    private void validate(StudentSubmission submission, Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(submission, "Submission object for evaluation cannot be null.");
        Assert.isInstanceOf(TextSubmission.class, submission);

        Assert.notNull(exercise, "Exercise object for evaluation cannot be null.");
        Assert.isTrue(ExerciseType.text.equals(exercise.getType()), "Exercise object for evaluation must be of type " + ExerciseType.text);
    }
}
