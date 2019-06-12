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

    private static final String DOCKER_CODE_FOLDER = "/usr/src/";

    private static final String PYTHON_DOCKER_IMAGE = "python:3.8.0a4-alpine3.9";

    private final DockerClient docker;

    public CodeRunner() throws DockerCertificateException {
        docker = DefaultDockerClient.fromEnv().build();
    }

    /**
     * Mounts the folder at path inside the container and runs the given command
     * Note: Mounts the host's folder at '/usr/src' and sets it as the working directory
     *
     * @param folderPath path to folder to mount inside container
     * @param cmd        command to execute inside container
     * @return stdout from container and execution time {@link RunResult}
     * @throws DockerException
     * @throws InterruptedException
     */
    public RunResult attachVolumeAndRunCommand(String folderPath, String[] cmd) throws DockerException, InterruptedException {
        HostConfig hostConfig = hostConfigWithAttachedVolume(folderPath);

        ContainerConfig containerConfig = containerConfig(hostConfig, cmd);

        return createAndRunContainer(containerConfig);
    }

    /**
     * Mounts the folder at path inside the container and runs the given python file
     * Note: Mounts the host's folder at '/usr/src' and sets it as the working directory
     *
     * @param folderPath     path to folder to mount inside container
     * @param filenameToExec python file to run
     * @return stdout from container and execution time {@link RunResult}
     * @throws DockerException
     * @throws InterruptedException
     */
    public RunResult runPythonCode(String folderPath, String filenameToExec) throws DockerException, InterruptedException, IOException {
        HostConfig hostConfig = hostConfigWithAttachedVolume(folderPath);

        String[] cmd = {"python", filenameToExec};
        ContainerConfig containerConfig = containerConfig(hostConfig, cmd);

        return createAndRunContainer(containerConfig);
    }

    private RunResult createAndRunContainer(ContainerConfig containerConfig) throws DockerException, InterruptedException {
        long startExecutionTime = System.nanoTime();
        ContainerCreation creation = docker.createContainer(containerConfig);

        if (creation.warnings() != null) {
            creation.warnings().forEach(logger::warn);
        }

        String containerId = creation.id();
        startAndWaitContainer(containerId);

        String logs = readLogs(containerId);
        long endExecutionTime = System.nanoTime();

        stopAndRemoveContainer(containerId);

        return new RunResult(logs, endExecutionTime - startExecutionTime);
    }

    private HostConfig hostConfigWithAttachedVolume(String hostPath) {
        String absolutePath = new FileSystemResource(hostPath).getFile().getAbsolutePath();
        return HostConfig.builder().appendBinds(new String[]{absolutePath + ":" + DOCKER_CODE_FOLDER}).build();
    }

    private ContainerConfig containerConfig(HostConfig hostConfig, String[] cmd) {
        return ContainerConfig
                .builder()
                .hostConfig(hostConfig)
                .image(PYTHON_DOCKER_IMAGE)
                .networkDisabled(true)
                .workingDir(DOCKER_CODE_FOLDER)
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
