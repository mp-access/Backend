package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEvaluator implements StudentSubmissionEvaluator {

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);

        TextSubmission textSub = (TextSubmission) submission;

        Pattern p = Pattern.compile(exercise.getTextSolution());
        Matcher m = p.matcher(textSub.getAnswer().trim());
        if (m.find( )) {
            return SubmissionEvaluation.builder()
                    .points(new SubmissionEvaluation.Points(1, 1))
                    .maxScore(exercise.getMaxScore())
                    .timestamp(Instant.now())
                    .build();
        }

        return SubmissionEvaluation.builder()
                .points(new SubmissionEvaluation.Points(0, 1))
                .maxScore(exercise.getMaxScore())
                .timestamp(Instant.now())
                .hints(exercise.getHints())
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
