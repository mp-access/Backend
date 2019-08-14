package ch.uzh.ifi.access.coderunner;


import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

@Service
public class CodeRunner {

    private static final Logger logger = LoggerFactory.getLogger(CodeRunner.class);

    private static final String DOCKER_CODE_FOLDER = "/usr/src/";

    private static final String PYTHON_DOCKER_IMAGE = "python:3.7-alpine";

    private final DockerClient docker;

    public CodeRunner() throws DockerCertificateException {
        docker = DefaultDockerClient.fromEnv().build();
        pullImageIfNotPresent();
    }

    private void pullImageIfNotPresent() {
        try {
            List<Image> images = docker.listImages(DockerClient.ListImagesParam.byName(PYTHON_DOCKER_IMAGE));
            if (images.isEmpty()) {
                docker.pull(PYTHON_DOCKER_IMAGE);
            }
        } catch (DockerException | InterruptedException e) {
            logger.warn("Failed to pull python docker image", e);
        }
    }

    /**
     * Mounts the folder at path inside the container and runs the given command
     * Note: Mounts the host's folder at '/usr/src' and sets it as the working directory
     *
     * @param folderPath path to folder to mount inside container
     * @param bashCmd    command to execute in bash
     * @return stdout from container and execution time {@link RunResult}
     * @throws DockerException
     * @throws InterruptedException
     */
    public RunResult attachVolumeAndRunBash(String folderPath, String bashCmd) throws DockerException, InterruptedException, IOException {
        HostConfig hostConfig = hostConfigWithAttachedVolume(folderPath);

        String[] cmd = new String[3];
        cmd[0] = "/bin/sh";
        cmd[1] = "-c";
        cmd[2] = bashCmd;
        ContainerConfig containerConfig = containerConfig(hostConfig, cmd);

        return createAndRunContainer(containerConfig, folderPath);
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
    public RunResult attachVolumeAndRunCommand(String folderPath, String[] cmd) throws DockerException, InterruptedException, IOException {
        HostConfig hostConfig = hostConfigWithAttachedVolume(folderPath);

        ContainerConfig containerConfig = containerConfig(hostConfig, cmd);

        return createAndRunContainer(containerConfig, folderPath);
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

        return createAndRunContainer(containerConfig, folderPath);
    }

    private RunResult createAndRunContainer(ContainerConfig containerConfig, String folderPath) throws DockerException, InterruptedException, IOException {
        long startExecutionTime = System.nanoTime();

        ContainerCreation creation = docker.createContainer(containerConfig);
        String containerId = creation.id();
        logger.trace(String.format("Created container %s", containerId));

        if (creation.warnings() != null) {
            creation.warnings().forEach(logger::warn);
        }

        copyDirectoryToContainer(containerId, Paths.get(folderPath));
        startAndWaitContainer(containerId);

        String logs = readStdOutAndErr(containerId);
        String stdOut = readStdOut(containerId);
        String stdErr = readStdErr(containerId);
        long endExecutionTime = System.nanoTime();
        long executionTime = endExecutionTime - startExecutionTime;

        stopAndRemoveContainer(containerId);

        return new RunResult(logs, stdErr, stdOut, stdErr, executionTime);
    }

    private void copyDirectoryToContainer(String containerId, Path folder) throws InterruptedException, DockerException, IOException {
        docker.copyToContainer(folder, containerId, DOCKER_CODE_FOLDER);
        StringJoiner joiner = new StringJoiner("\n", String.format("Files copied to container @%s:\n", DOCKER_CODE_FOLDER), "").setEmptyValue("No files");
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.filter(Files::isRegularFile)
                    .forEach(path -> joiner.add(path.toString()));

        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        logger.trace(joiner.toString());
    }

    private HostConfig hostConfigWithAttachedVolume(String hostPath) {
        String absolutePath = new FileSystemResource(hostPath).getFile().getAbsolutePath();
        return HostConfig.builder()
                // TODO: Bind volume or copy files to container? As a quickfix will simply copy them (slower but actually more secure)
//                .appendBinds(new String[]{absolutePath + ":" + DOCKER_CODE_FOLDER})
                .build();
    }

    private ContainerConfig containerConfig(HostConfig hostConfig, String[] cmd) {
        return ContainerConfig
                .builder()
                .hostConfig(hostConfig)
                .image(PYTHON_DOCKER_IMAGE)
                .networkDisabled(true)
                .workingDir(DOCKER_CODE_FOLDER)
                .attachStdout(true)
                .attachStderr(true)
                .cmd(cmd)
                .build();
    }

    private void startAndWaitContainer(String id) throws DockerException, InterruptedException {
        docker.startContainer(id);
        docker.waitContainer(id);
    }

    private String readStdOutAndErr(String containerId) throws DockerException, InterruptedException {
        return docker.logs(containerId, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr()).readFully();
    }

    private String readStdOut(String containerId) throws DockerException, InterruptedException {
        return docker.logs(containerId, DockerClient.LogsParam.stdout()).readFully();
    }

    private String readStdErr(String containerId) throws DockerException, InterruptedException {
        return docker.logs(containerId, DockerClient.LogsParam.stderr()).readFully();
    }

    private void stopAndRemoveContainer(String id) throws DockerException, InterruptedException {
        logger.debug(String.format("Stopping and removing container %s", id));
        docker.stopContainer(id, 1);
        docker.removeContainer(id);
    }
}
