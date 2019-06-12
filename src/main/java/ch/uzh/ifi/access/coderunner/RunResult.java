package ch.uzh.ifi.access.coderunner;

import lombok.Data;

import java.time.Instant;

@Data
public final class RunResult {
    private double timeInSeconds;
    private double timeInMilliseconds;
    private final Instant timestamp;
    private final String output;
    private final long timeInNanoseconds;

    public RunResult(String output, long timeInNanoseconds) {
        this.output = output;
        this.timeInNanoseconds = timeInNanoseconds;
        this.timeInSeconds = this.timeInNanoseconds / 1.0E9D;
        this.timeInMilliseconds = this.timeInNanoseconds / 1000000.0D;
        this.timestamp = Instant.now();
    }
}
