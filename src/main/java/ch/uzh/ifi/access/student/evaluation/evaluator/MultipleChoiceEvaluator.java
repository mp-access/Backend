package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Collection;

public class MultipleChoiceEvaluator implements StudentSubmissionEvaluator {

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);

        MultipleChoiceSubmission mcSub = (MultipleChoiceSubmission) submission;

        Collection solution = exercise.getMultipleChoiceSolution();
        Collection answer = mcSub.getChoices();

        answer.retainAll(solution);

        return SubmissionEvaluation.builder()
                .points(new SubmissionEvaluation.Points(answer.size(), solution.size()))
                .maxScore(exercise.getMaxScore())
                .timestamp(Instant.now())
                .build();

    }

    private void validate(StudentSubmission submission, Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(submission, "Submission object for evaluation cannot be null.");
        Assert.isInstanceOf(MultipleChoiceSubmission.class, submission);

        Assert.notNull(exercise, "Exercise object for evaluation cannot be null.");
        Assert.isTrue(ExerciseType.multipleChoice.equals(exercise.getType()), "Exercise object for evaluation must be of type " + ExerciseType.text);
        Assert.isTrue(exercise.getSolutions() != null && exercise.getSolutions().size() > 0, "Exercise has for for submission does not provide a solution!");
    }
}
