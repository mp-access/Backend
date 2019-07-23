package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEvaluator implements StudentSubmissionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(CodeEvaluator.class);

    private final String runNTestPattern = "^Ran (\\d++) test.*";
    private final String nokNTestPattern = "^FAILED \\p{Punct}(failures|errors)=(\\d++)\\p{Punct}.*";

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);
        CodeSubmission codeSub = (CodeSubmission) submission;
        return parseEvalFromConsoleLog(codeSub.getConsole().getStderr());
    }

    private SubmissionEvaluation parseEvalFromConsoleLog(String console) {
        int score = 0;

        if (console != null) {
            List<String> lines = Arrays.asList(console.split("\n"));
            String resultLine = lines.get(lines.size() - 1);

            int nrOfTest = extractNrOfTests(lines.get(lines.size() - 3));

            if (resultLine.startsWith("OK")) {
                score = nrOfTest;
            } else if (resultLine.startsWith("FAILED")) {
                score = nrOfTest - extractNrOfNOKTests(resultLine);
            }
        } else {
            logger.info("No console log to evaluate.");
        }

        return new SubmissionEvaluation(score, Instant.now());
    }

    private int extractNrOfTests(String line) {
        int nrTests = 0;
        Pattern p = Pattern.compile(runNTestPattern);
        Matcher m = p.matcher(line);
        if (m.find()) {
            // group0 = line
            // group1 = nr of tests
            String group1 = m.group(1);
            nrTests = Integer.parseInt(group1);
        }
        logger.debug(String.format("Exracted nr of test (%s) from line: %s", nrTests, line));
        return nrTests;
    }

    private int extractNrOfNOKTests(String line) {
        int nrTests = 0;
        Pattern p = Pattern.compile(nokNTestPattern);
        Matcher m = p.matcher(line);
        if (m.find()) {
            // group0 = line
            // group1 = failures / errors
            // group2 = nr of tests
            String nrOfTests = m.group(2);
            nrTests = Integer.parseInt(nrOfTests);
        }
        logger.debug(String.format("Exracted nr of NOK tests (%s) from line: %s", nrTests, line));
        return nrTests;
    }

    private void validate(StudentSubmission submission, Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(submission, "Submission object for evaluation cannot be null.");
        Assert.isInstanceOf(CodeSubmission.class, submission);

        Assert.notNull(exercise, "Exercise object for evaluation cannot be null.");
        Assert.isTrue(exercise.getType().isCodeType(), String.format("Exercise object for evaluation must be of type %s or %s", ExerciseType.code, ExerciseType.codeSnippet));
    }

}
