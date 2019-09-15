package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEvaluator implements StudentSubmissionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(CodeEvaluator.class);

    private static final String HINT_ANNOTATION = "@#@";
    private static final String HINT_PATTERN = HINT_ANNOTATION+".*"+HINT_ANNOTATION;

    private final String runNTestPattern = "^Ran (\\d++) test.*";
    private final String nokNTestPattern = "^FAILED \\p{Punct}(failures|errors)=(\\d++)\\p{Punct}.*";

    private Pattern hintPattern;

    public CodeEvaluator() {
        this.hintPattern = Pattern.compile(HINT_PATTERN, Pattern.MULTILINE);
        //this.hintPattern = Pattern.compile(HINT_PATTERN);
    }

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);
        CodeSubmission codeSub = (CodeSubmission) submission;

        SubmissionEvaluation.Points scoredPoints = parseScoreFromLog(codeSub.getConsole().getEvalLog());
        List<String> hints = parseHintsFromLog(codeSub.getConsole().getEvalLog());

        return  SubmissionEvaluation.builder()
                .points(scoredPoints)
                .maxScore(exercise.getMaxScore())
                .hints(hints)
                .build();
    }

    private List<String> parseHintsFromLog(String evalLog) {
        List<String> hints = new ArrayList<>();

        Matcher matcher = hintPattern.matcher(evalLog);
        while (matcher.find()) {
            hints.add(matcher.group(0).replace(HINT_ANNOTATION, ""));
        }

        return hints;
    }

    private SubmissionEvaluation.Points parseScoreFromLog(String log) {
        int points = 0;
        int nrOfTest = -1;

        if (log != null && !log.trim().isEmpty()) {
            List<String> lines = Arrays.asList(log.split("\n"));
            String resultLine = lines.get(lines.size() - 1);

             nrOfTest = extractNrOfTests(lines.get(lines.size() - 3));

            if (resultLine.startsWith("OK")) {
                points = nrOfTest;
            } else if (resultLine.startsWith("FAILED")) {
                points = nrOfTest - extractNrOfNOKTests(resultLine);
            }
        } else {
            logger.info("No console log to evaluate.");
        }

        return new SubmissionEvaluation.Points(points, nrOfTest);
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
