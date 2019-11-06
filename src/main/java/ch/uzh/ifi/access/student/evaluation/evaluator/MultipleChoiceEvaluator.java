package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

public class MultipleChoiceEvaluator implements StudentSubmissionEvaluator {

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);

        MultipleChoiceSubmission mcSub = (MultipleChoiceSubmission) submission;
        var solution =  exercise.getMultipleChoiceSolution();
        var answer = mcSub.getChoices();

        var correctAnswers = getNrCorrectAnswers(answer, solution);
        var wrongAnswers = getNrWrongAnswers(answer, solution);

        var calc = correctAnswers - wrongAnswers;
        var points = Math.max(calc, 0);

        var hints = points < exercise.getMaxScore() ? exercise.getHints() : null;

        return SubmissionEvaluation.builder()
                .points(new SubmissionEvaluation.Points(points, solution.size()))
                .maxScore(exercise.getMaxScore())
                .timestamp(Instant.now())
                .hints(hints)
                .build();
    }

    private int getNrCorrectAnswers(final Collection<Integer> answers, final Collection<Integer> solutions) {
        var ans = new HashSet<>(answers);
        ans.retainAll(solutions);
        return ans.size();
    }

    private int getNrWrongAnswers(final Collection<Integer> answers, final Collection<Integer> solutions) {
        var ans = new HashSet<>(answers);
        ans.removeAll(solutions);
        return ans.size();
    }

    private void validate(StudentSubmission submission, Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(submission, "Submission object for evaluation cannot be null.");
        Assert.isInstanceOf(MultipleChoiceSubmission.class, submission);

        Assert.notNull(exercise, "Exercise object for evaluation cannot be null.");
        Assert.isTrue(ExerciseType.multipleChoice.equals(exercise.getType()), "Exercise object for evaluation must be of type " + ExerciseType.multipleChoice);
        Assert.isTrue(exercise.getSolutions() != null && exercise.getSolutions().size() > 0, "Exercise does not provide a solution!");
    }
}
