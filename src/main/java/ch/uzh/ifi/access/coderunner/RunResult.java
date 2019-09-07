package ch.uzh.ifi.access.coderunner;

import lombok.Value;

import java.time.Instant;

@Value
public final class RunResult {

    private final String console;
    private final String stdOut;
    private final String stdErr;

    private final Instant timestamp;
    private final double timeInSeconds;
    private final double timeInMilliseconds;
    private final long timeInNanoseconds;

    public RunResult(String console, String stdOut, String stdErr, long timeInNanoseconds) {
        this.console = console;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
        this.timeInNanoseconds = timeInNanoseconds;
        this.timeInSeconds = this.timeInNanoseconds / 1.0E9D;
        this.timeInMilliseconds = this.timeInNanoseconds / 1000000.0D;
        this.timestamp = Instant.now();
    }


}
