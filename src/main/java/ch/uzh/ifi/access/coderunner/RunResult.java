package ch.uzh.ifi.access.coderunner;

import lombok.Value;

import java.time.Instant;

@Value
public final class RunResult {
    private final String codeOutput;
    private final String testOutput;

    private final String stdOut;
    private final String stdErr;

    private final Instant timestamp;
    private final double timeInSeconds;
    private final double timeInMilliseconds;
    private final long timeInNanoseconds;

    public RunResult(String codeOutput, String testOutput, String stdOut, String stdErr, long timeInNanoseconds) {
        this.codeOutput = codeOutput;
        this.testOutput = testOutput;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
        this.timeInNanoseconds = timeInNanoseconds;
        this.timeInSeconds = this.timeInNanoseconds / 1.0E9D;
        this.timeInMilliseconds = this.timeInNanoseconds / 1000000.0D;
        this.timestamp = Instant.now();
    }

    public RunResult trimOutput(String delimiter) {
        int indexOfDelimiterStdOut = codeOutput.lastIndexOf(delimiter);
        int indexOfDelimiterStdErr = testOutput.lastIndexOf(delimiter);

        final String trimmedCodeOutput = codeOutput.substring(0, indexOfDelimiterStdOut);
        final String trimmedTestOutput = testOutput.substring(indexOfDelimiterStdErr).replace(delimiter, "");

        return new RunResult(trimmedCodeOutput, trimmedTestOutput, stdOut, stdErr, timeInNanoseconds);
    }
}
