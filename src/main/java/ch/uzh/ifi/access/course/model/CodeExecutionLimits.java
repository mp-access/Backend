package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionLimits {

    private static final long MB_TO_Bytes = 1000000L;
    /**
     * Max 64 Mib RAM
     */
    private static final long DEFAULT_MAX_RAM_USAGE_IN_MB = 64;

    /**
     * Max 1 core
     */
    private static final long CORES_TO_QUOTA = 100000L;

    private static final long DEFAULT_MAX_CORES = 1;

    /**
     * Max 5 seconds timeout
     */
    private static final long DEFAULT_TIMEOUT = 5 * 1000;

    public static final CodeExecutionLimits DEFAULTS = new CodeExecutionLimits(DEFAULT_MAX_RAM_USAGE_IN_MB, DEFAULT_MAX_CORES, DEFAULT_TIMEOUT, false, false);

    public static final CodeExecutionLimits TESTING_UNLIMITED = new CodeExecutionLimits(-1L, -1L, -1L, true, true);

    private long memory;
    private long cpuCores;
    private long timeout;
    private boolean networking;
    private boolean testing;

    public long getMemoryInMb() {
        return memory * MB_TO_Bytes;
    }

    public long getCpuQuota() {
        return cpuCores * CORES_TO_QUOTA;
    }
}