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

import java.io.IOException;

@Service
public class CodeRunner {

    private static final Logger logger = LoggerFactory.getLogger(CodeRunner.class);

    private static final String DOCKER_CODE_FOLDER = "/usr/src";

    private static final String PYTHON_DOCKER_IMAGE = "python:3.8.0a4-alpine3.9";

    private final DockerClient docker;

    public CodeRunner() throws DockerCertificateException {
        docker = DefaultDockerClient.fromEnv().build();
    }

    public RunResult runCode(String folderPath, String filenameToExec) throws DockerException, InterruptedException, IOException {
        String absolutePath = new FileSystemResource(folderPath).getFile().getAbsolutePath();

        HostConfig hostConfig = hostConfigWithAttachedVolume(absolutePath);

        String[] cmd = {"python", DOCKER_CODE_FOLDER + filenameToExec};
        ContainerConfig containerConfig = containerConfig(hostConfig, cmd);

        long startExecutionTime = System.nanoTime();
        ContainerCreation creation = docker.createContainer(containerConfig);
        creation.warnings().forEach(logger::warn);

        String containerId = creation.id();
        startAndWaitContainer(containerId);

        String logs = readLogs(containerId);
        long endExecutionTime = System.nanoTime();

        stopAndRemoveContainer(containerId);

        return new RunResult(logs, endExecutionTime - startExecutionTime);
    }

    private HostConfig hostConfigWithAttachedVolume(String hostPath) {
        return HostConfig.builder().appendBinds(new String[]{hostPath + ":" + DOCKER_CODE_FOLDER}).build();
    }

    private ContainerConfig containerConfig(HostConfig hostConfig, String[] cmd) {
        return ContainerConfig
                .builder()
                .hostConfig(hostConfig)
                .image(PYTHON_DOCKER_IMAGE)
                .networkDisabled(true)
                .cmd(cmd)
                .build();
    }

    private void startAndWaitContainer(String id) throws DockerException, InterruptedException {
        docker.startContainer(id);
        docker.waitContainer(id);
    }

    private String readLogs(String containerId) throws DockerException, InterruptedException {
        return docker.logs(containerId, DockerClient.LogsParam.stdout()).readFully();
    }

    private void stopAndRemoveContainer(String id) throws DockerException, InterruptedException {
        logger.debug("Stopping container " + id + "...");
        docker.stopContainer(id, 1);
        docker.removeContainer(id);
    }
}
