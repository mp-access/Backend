package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionLimits {
    /**
     * Max 64 Mib RAM
     */
    private static final long DEFAULT_MAX_RAM_USAGE = 64 * 1000000L;

    /**
     * Max 1 core
     */
    private static final long DEFAULT_MAX_1_CORE = 100000L;

    /**
     * Max 5 seconds timeout
     */
    private static final long DEFAULT_TIMEOUT = 5 * 1000;

    public static final CodeExecutionLimits DEFAULTS = new CodeExecutionLimits(DEFAULT_MAX_RAM_USAGE, DEFAULT_MAX_1_CORE, DEFAULT_TIMEOUT, false);

    private long memory;
    private long cpuCores;
    private long timeout;
    private boolean networking;
}