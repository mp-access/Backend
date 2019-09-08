package ch.uzh.ifi.access.coderunner;

import lombok.Value;

@Value
public class ContainerProperties {

    private static final long DEFAULT_MAX_RAM_USAGE = 64 * 1000000L;

    private static final long DEFAULT_MAX_1_CORE = 100000L;

    public static final ContainerProperties DEFAULT = new ContainerProperties(DEFAULT_MAX_RAM_USAGE, DEFAULT_MAX_1_CORE);

    private final long maxRamUsage;

    private final long maxCpus;
}
