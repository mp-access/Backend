package ch.uzh.ifi.access.coderunner;


import ch.uzh.ifi.access.course.model.CodeExecutionLimits;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private DockerClient docker;

    public CodeRunner() throws DockerCertificateException {
        docker = DefaultDockerClient.fromEnv().build();
        logDebugInfo();
        pullImageIfNotPresent();
    }

    private void logDebugInfo() {
        try {
            Info info = docker.info();
            logger.debug("Connected to docker daemon @ " + docker.getHost());
            logger.debug("Docker info:\n" + info.toString());
        } catch (DockerException | InterruptedException e) {
            logger.warn("Failed to connect to docker daemon", e);
        }
    }

    private void pullImageIfNotPresent() {
        try {
            List<Image> images = docker.listImages(DockerClient.ListImagesParam.byName(PythonImageConfig.PYTHON_DOCKER_IMAGE));
            if (images.isEmpty()) {
                logger.debug(String.format("No suitable python image found (searched for %s), pulling new image from registry", PythonImageConfig.PYTHON_DOCKER_IMAGE));
                docker.pull(PythonImageConfig.PYTHON_DOCKER_IMAGE);
            }
        } catch (DockerException | InterruptedException e) {
            logger.warn("Failed to pull python docker image", e);
        }
    }

    /**
     * Mounts the folder at path inside the container and runs the given command
     * Note: Mounts the host's folder at '/usr/src' and sets it as the working directory
     *
     * @param folderPath      path to folder to mount inside container
     * @param bashCmd         command to execute in bash
     * @param executionLimits
     * @return stdout from container and execution time {@link RunResult}
     */
    public RunResult attachVolumeAndRunBash(String folderPath, String bashCmd, CodeExecutionLimits executionLimits) throws DockerException, InterruptedException, IOException {
        String[] cmd = new String[3];
        cmd[0] = "/bin/sh";
        cmd[1] = "-c";
        cmd[2] = bashCmd;
        ContainerConfig containerConfig = new PythonImageConfig(executionLimits).containerConfig(cmd);

        return createAndRunContainer(containerConfig, folderPath);
    }

    /**
     * Mounts the folder at path inside the container and runs the given command
     * Note: Mounts the host's folder at '/usr/src' and sets it as the working directory
     *
     * @param folderPath path to folder to mount inside container
     * @param cmd        command to execute inside container
     * @return stdout from container and execution time {@link RunResult}
     */
    public RunResult attachVolumeAndRunCommand(String folderPath, String[] cmd) throws DockerException, InterruptedException, IOException {
        ContainerConfig containerConfig = new PythonImageConfig().containerConfig(cmd);

        return createAndRunContainer(containerConfig, folderPath);
    }

    /**
     * Mounts the folder at path inside the container and runs the given python file
     * Note: Mounts the host's folder at '/usr/src' and sets it as the working directory
     *
     * @param folderPath     path to folder to mount inside container
     * @param filenameToExec python file to run
     * @return stdout from container and execution time {@link RunResult}
     */
    public RunResult runPythonCode(String folderPath, String filenameToExec) throws DockerException, InterruptedException, IOException {
        String[] cmd = {"python", filenameToExec};
        ContainerConfig containerConfig = new PythonImageConfig().containerConfig(cmd);

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

        String console = readStdOutAndErr(containerId);
        String stdOut = readStdOut(containerId);
        String stdErr = readStdErr(containerId);
        long endExecutionTime = System.nanoTime();
        long executionTime = endExecutionTime - startExecutionTime;

        stopAndRemoveContainer(containerId);

        return new RunResult(console, stdOut, stdErr, executionTime);
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
