package ch.uzh.ifi.access.coderunner;

import ch.uzh.ifi.access.course.model.CodeExecutionLimits;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PythonImageConfig {

    public static final String PYTHON_DOCKER_IMAGE = "python:3.7-alpine";

    public static final String STUDENT_CODE_FOLDER = "/usr/src/";

    private CodeExecutionLimits codeExecutionLimits = CodeExecutionLimits.DEFAULTS;

    private HostConfig hostConfig() {
        return HostConfig.builder()
                .memory(codeExecutionLimits.getMemoryInMb())
                .cpuQuota(codeExecutionLimits.getCpuQuota())
                .build();
    }

    public ContainerConfig containerConfig(String[] cmd) {
        return ContainerConfig
                .builder()
                .hostConfig(hostConfig())
                .image(PYTHON_DOCKER_IMAGE)
                .networkDisabled(codeExecutionLimits.isNetworking())
                .workingDir(STUDENT_CODE_FOLDER)
                .attachStdout(true)
                .attachStderr(true)
                .cmd(cmd)
                .build();
    }
}
