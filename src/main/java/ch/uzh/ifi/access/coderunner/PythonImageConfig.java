package ch.uzh.ifi.access.coderunner;

import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;

public class PythonImageConfig {

    public static final String PYTHON_DOCKER_IMAGE = "python:3.7-alpine";

    public static final String STUDENT_CODE_FOLDER = "/usr/src/";

    private ContainerProperties containerProperties = ContainerProperties.DEFAULT;

    private HostConfig hostConfig() {
        return HostConfig.builder()
                .memory(containerProperties.getMaxRamUsage()) // max 64 Mib RAM
                .cpuQuota(containerProperties.getMaxCpus()) // max 1 core
                .build();
    }

    public ContainerConfig containerConfig(String[] cmd) {
        return ContainerConfig
                .builder()
                .hostConfig(hostConfig())
                .image(PYTHON_DOCKER_IMAGE)
                .networkDisabled(true)
                .workingDir(STUDENT_CODE_FOLDER)
                .attachStdout(true)
                .attachStderr(true)
                .cmd(cmd)
                .build();
    }
}
