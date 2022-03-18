package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CodeEvaluator implements StudentSubmissionEvaluator {

    static final String TEST_FAILED_WITHOUT_HINTS = "Test failed without solution hints";
    private static final String HINT_ANNOTATION = "@@";
    private static final String HINT_PATTERN = "^Assertion.*?:.*?(" + HINT_ANNOTATION + ".*?" + HINT_ANNOTATION + ")$";
    private static final String LAST_CRASH_PATTERN = "^(.*?Error):.*?";
    private static final String PYTHON_ASSERTION_ERROR = "AssertionError";

    private Pattern hintPattern;
    private Pattern crashPattern;

    private Pattern failedTestPattern;

    public CodeEvaluator() {
        this.hintPattern = Pattern.compile(HINT_PATTERN, Pattern.MULTILINE | Pattern.DOTALL);
        this.crashPattern = Pattern.compile(LAST_CRASH_PATTERN, Pattern.MULTILINE);
        String nokNTestPattern = "^FAILED \\((errors|failures)=(\\d*)(, (errors|failures)=(\\d*))?\\)";
        this.failedTestPattern = Pattern.compile(nokNTestPattern, Pattern.MULTILINE);
    }

    @Override
    public SubmissionEvaluation evaluate(StudentSubmission submission, Exercise exercise) {
        validate(submission, exercise);
        CodeSubmission codeSub = (CodeSubmission) submission;

        String consoleLog = codeSub.getConsole().getEvalLog();

        SubmissionEvaluation.Points testResults = parseScoreFromLog(consoleLog);
        List<String> hints = testResults.isEverythingCorrect() ? new ArrayList<>() : parseHintsFromLog(consoleLog);

        return SubmissionEvaluation.builder()
				.rounding(exercise.getRounding())
				.points(testResults)
				.maxScore(exercise.getMaxScore())
				.hints(hints)
				.build();
    }

    public List<String> parseHintsFromLog(String evalLog) {
        List<String> hints = new ArrayList<>();

        Matcher matcher = hintPattern.matcher(evalLog);
        while (matcher.find()) {
            String possibleHint = matcher.group(1);
            if (possibleHint.contains(HINT_ANNOTATION)) {
                hints.add(possibleHint.replace(HINT_ANNOTATION, ""));
            }
        }

        boolean hasFailedTests = failedTestPattern.matcher(evalLog).find();
        if (hints.isEmpty() && hasFailedTests) {
            matcher = crashPattern.matcher(evalLog);

            String lastCrash = null;
            while (matcher.find()) {
                String error = matcher.group(1);
                if (!error.equals(PYTHON_ASSERTION_ERROR))
                    lastCrash = error;
            }
            if (lastCrash != null)
                hints.add("Error during execution: " + lastCrash);

            if (hints.isEmpty())
                hints.add(TEST_FAILED_WITHOUT_HINTS);

        }

        if (hints.isEmpty()) {
            String[] lines = evalLog.split("\n");
            if (lines.length > 0) {
                String lastLine = lines[lines.length - 1];
                int idxColon = lastLine.indexOf(':');
                if (idxColon != -1) {
                    String everythingBeforeColon = lastLine.substring(0, Math.min(idxColon, 30)).trim();
                    hints.add("Error during import: " + everythingBeforeColon);
                }
            }
        }

        if (hints.isEmpty()) {
            hints.add("No hint could be provided. This is likely caused by a crash during the execution.");
        }

        return hints;
    }

    private SubmissionEvaluation.Points parseScoreFromLog(String consoleLog) {
        int points = 0;
        int nrOfTest = -1;

        if (consoleLog != null && !consoleLog.trim().isEmpty()) {
            List<String> lines = Arrays.asList(consoleLog.split("\n"));
            if (lines.size() >= 3) {
                String resultLine = lines.get(lines.size() - 1);

                nrOfTest = extractNrOfTests(lines.get(lines.size() - 3));

                if (resultLine.startsWith("OK")) {
                    points = nrOfTest;
                } else if (resultLine.startsWith("FAILED")) {
                    points = nrOfTest - extractNrOfNOKTests(resultLine);
                }
            } else {
                nrOfTest = 1;
                log.info("Log is too short, likely not a valid test output.");
            }
        } else
            log.info("No console log to evaluate.");

        return new SubmissionEvaluation.Points(points, nrOfTest);
    }

    private int extractNrOfTests(String line) {
        int nrTests = 0;
        String runNTestPattern = "^Ran (\\d++) test.*";
        Pattern p = Pattern.compile(runNTestPattern);
        Matcher m = p.matcher(line);
        if (m.find()) {
            // group0 = line
            // group1 = nr of tests
            String group1 = m.group(1);
            nrTests = Integer.parseInt(group1);
        }
        log.debug("Extracted nr of test ({}) from line: {}", nrTests, line);
        return nrTests;
    }

    private int extractNrOfNOKTests(String line) {
        int nrTests1 = 0;
        int nrTests2 = 0;
        Matcher m = failedTestPattern.matcher(line);
        if (m.find()) {
            if(m.group(2) != null && m.group(5) != null){   // matches FAILED (failures=3, errors=72) or FAILED (errors=3, failures=72)
                nrTests1 = Integer.parseInt(m.group(2));
                nrTests2 = Integer.parseInt(m.group(5));
            }else if(m.group(2) != null){  // matches FAILED (errors=72) or FAILED (failures=3)
                nrTests1 = Integer.parseInt(m.group(2));
            }
        }
        int total = nrTests1 + nrTests2;
        log.debug("Extracted nr of NOK tests ({}) from line: {}", total, line);
        return total;
    }

    private void validate(StudentSubmission submission, Exercise exercise) throws IllegalArgumentException {
        Assert.notNull(submission, "Submission object for evaluation cannot be null.");
        Assert.isInstanceOf(CodeSubmission.class, submission);

        Assert.notNull(exercise, "Exercise object for evaluation cannot be null.");
        Assert.isTrue(exercise.getType().isCodeType(),
                String.format("Exercise object for evaluation must be of type %s or %s", ExerciseType.code, ExerciseType.codeSnippet));
    }

}
