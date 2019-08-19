package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.SingleChoiceSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.springframework.util.Assert;

import java.time.Instant;

public class SingleChoiceEvaluator implements StudentSubmissionEvaluator {

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);

        SingleChoiceSubmission sub = (SingleChoiceSubmission) submission;

        Integer solution = exercise.getSingleChoiceSolution();
        Integer answer = sub.getChoice();
        var point = solution.equals(answer) ? 1 : 0;

        return SubmissionEvaluation.builder()
                .points(new SubmissionEvaluation.Points(point, 1))
                .maxScore(exercise.getMaxScore())
                .timestamp(Instant.now())
                .build();
    }

    private void validate(StudentSubmission submission, Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(submission, "Submission object for evaluation cannot be null.");
        Assert.isInstanceOf(SingleChoiceSubmission.class, submission);

        Assert.notNull(exercise, "Exercise object for evaluation cannot be null.");
        Assert.isTrue(ExerciseType.singleChoice.equals(exercise.getType()), "Exercise object for evaluation must be of type " + ExerciseType.singleChoice);
        Assert.isTrue(exercise.getSolutions() != null && exercise.getSolutions().size() > 0, "Exercise does not provide a solution!");
    }
}
