package ch.uzh.ifi.access.coderunner;

import lombok.Value;

import java.time.Instant;

@Value
public final class RunResult {
    private final String codeOutput;
    private final String testOutput;
    private final Instant timestamp;
    private final double timeInSeconds;
    private final double timeInMilliseconds;
    private final long timeInNanoseconds;

    public RunResult(String codeOutput, String testOutput, long timeInNanoseconds) {
        this.codeOutput = codeOutput;
        this.testOutput = testOutput;
        this.timeInNanoseconds = timeInNanoseconds;
        this.timeInSeconds = this.timeInNanoseconds / 1.0E9D;
        this.timeInMilliseconds = this.timeInNanoseconds / 1000000.0D;
        this.timestamp = Instant.now();
    }

    public RunResult trimCodeOutput(String delimiter) {
        int indexOfDelimiter = codeOutput.lastIndexOf(delimiter);
        final String trimmedCodeOutput = codeOutput.substring(0, indexOfDelimiter);
        return new RunResult(trimmedCodeOutput, testOutput, timeInNanoseconds);
    }
}
