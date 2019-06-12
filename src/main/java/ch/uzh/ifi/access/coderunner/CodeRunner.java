package ch.uzh.ifi.access.coderunner;


import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class CodeRunner {

    private static final Logger logger = LoggerFactory.getLogger(CodeRunner.class);

    private final DockerClient docker;

    public CodeRunner() throws DockerCertificateException {
        this.docker = DefaultDockerClient.fromEnv().build();
    }

    public RunResult runCode(boolean withAttachedVolume, String path) throws DockerException, InterruptedException, IOException {
        String absolutePath = new FileSystemResource(path).getFile().getAbsolutePath();

        HostConfig hostConfig;
        if (withAttachedVolume) {
            hostConfig = this.hostConfigWithAttachedVolume(absolutePath);
            logger.info("Running with attached volume");
        } else {
            hostConfig = this.hostConfig();
            logger.info("Running by copying files into container");
        }

        ContainerConfig containerConfig = ContainerConfig
                .builder()
                .hostConfig(hostConfig)
                .image("python:3.8.0a4-alpine3.9")
                .networkDisabled(true)
                .cmd(new String[]{"python", "/usr/src/test.py"})
                .build();

        long startExecutionTime = System.nanoTime();
        ContainerCreation creation = this.docker.createContainer(containerConfig);

        final String id = creation.id();
        if (!withAttachedVolume) {
            this.docker.copyToContainer((new File(absolutePath)).toPath(), id, "/usr/src");
        }

        this.docker.startContainer(id);
        this.docker.waitContainer(id);
        String logs = this.docker.logs(id, DockerClient.LogsParam.stdout()).readFully();
        long endExecutionTime = System.nanoTime();

        stopAndRemoveContainer(id);

        return new RunResult(logs, endExecutionTime - startExecutionTime);
    }

    private HostConfig hostConfig() {
        return HostConfig.builder().build();
    }

    private HostConfig hostConfigWithAttachedVolume(String path) {
        return HostConfig.builder().appendBinds(new String[]{path + ":/usr/src"}).build();
    }

    private void stopAndRemoveContainer(String id) throws DockerException, InterruptedException {
        logger.info("Stopping container " + id + "...");
        this.docker.stopContainer(id, 1);
        this.docker.removeContainer(id);
    }
}
