package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.List;

public class TextEvaluator implements StudentSubmissionEvaluator {

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);

        TextSubmission textSub = (TextSubmission) submission;
        List<String> solutions = exercise.getSolutions();

        if(solutions != null && solutions.size()>0 && solutions.get(0).length() > 0) {
            if (solutions.get(0).trim().equalsIgnoreCase(textSub.getAnswer().trim())) {
                return SubmissionEvaluation.builder()
                        .correctPoints(1)
                        .maxPoints(exercise.getMaxScore())
                        .timestamp(Instant.now())
                        .build();
            }
        }

        return SubmissionEvaluation.builder()
                .correctPoints(0)
                .maxPoints(exercise.getMaxScore())
                .timestamp(Instant.now())
                .build();
    }

    private void validate(StudentSubmission submission, Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(submission, "Submission object for evaluation cannot be null.");
        Assert.isInstanceOf(TextSubmission.class, submission);

        Assert.notNull(exercise, "Exercise object for evaluation cannot be null.");
        Assert.isTrue(ExerciseType.text.equals(exercise.getType()), "Exercise object for evaluation must be of type " + ExerciseType.text);
        Assert.isTrue(exercise.getSolutions() != null && exercise.getSolutions().size() > 0, "Exercise has for for submission does not provide a solution!");
    }
}
