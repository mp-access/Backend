package ch.uzh.ifi.access.coderunner;

import ch.uzh.ifi.access.course.model.CodeExecutionLimits;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties("access.runner")
public class PythonImageConfig {

    public static final String STUDENT_CODE_FOLDER = "/usr/src/";

    @Value("python")
    private String pythonImage = "hoal/access-python:3.7";

    private HostConfig hostConfig(CodeExecutionLimits executionLimits) {
        if (executionLimits == null) {
            executionLimits = CodeExecutionLimits.DEFAULTS;
        }

        if (executionLimits.isTesting()) {
            return HostConfig.builder().build();
        }

        return HostConfig.builder()
                .memory(executionLimits.getMemoryInMb())
                .cpuQuota(executionLimits.getCpuQuota())
                .build();
    }

    public ContainerConfig containerConfig(String[] cmd, CodeExecutionLimits executionLimits) {
        return ContainerConfig
                .builder()
                .hostConfig(hostConfig(executionLimits))
                .image(pythonImage)
                .networkDisabled(!executionLimits.isNetworking())
                .workingDir(STUDENT_CODE_FOLDER)
                .attachStdout(true)
                .attachStderr(true)
                .cmd(cmd)
                .build();
    }
}
