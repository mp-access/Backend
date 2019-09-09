package ch.uzh.ifi.access.student.evaluation.runner;

import ch.uzh.ifi.access.coderunner.CodeRunner;
import ch.uzh.ifi.access.coderunner.RunResult;
import ch.uzh.ifi.access.course.model.CodeExecutionLimits;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import com.spotify.docker.client.exceptions.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@Service
public class SubmissionCodeRunner {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionCodeRunner.class);

    private static final String LOCAL_RUNNER_DIR = "./runner";

    private static final String PUBLIC_FOLDER = "public";

    private static final String PRIVATE_FOLDER = "private";

    private static final String INIT_FILE = "__init__.py";

    private static final String DELIMITER = "======================----------------------======================";
    private static final String DELIMITER_CMD = String.format("echo \"%s\" >&2", DELIMITER);

    private CodeRunner runner;

    @Autowired
    public SubmissionCodeRunner(CodeRunner runner) {
        this.runner = runner;
    }


    public ExecResult execSubmissionForExercise(CodeSubmission submission, Exercise exercise) throws InterruptedException, DockerException, IOException {

        Path path = Paths.get(LOCAL_RUNNER_DIR + "/" + submission.getId());
        logger.debug(path.toAbsolutePath().normalize().toString());

        CodeExecutionLimits executionLimits = exercise.getExecutionLimits();
        ExecResult res = submission.isGraded() ? executeSubmission(path, submission, exercise, executionLimits) : executeSmokeTest(path, submission, executionLimits);

        removeDirectory(path);

        return res;
    }

    private ExecResult executeSmokeTest(Path workPath, CodeSubmission submission, CodeExecutionLimits executionLimits) throws IOException, DockerException, InterruptedException {
        persistFilesIntoFolder(String.format("%s/%s", workPath.toString(), PUBLIC_FOLDER), submission.getPublicFiles());
        Files.createFile(Paths.get(workPath.toAbsolutePath().toString(), INIT_FILE));

        VirtualFile selectedFileForRun = submission.getPublicFile(submission.getSelectedFile());
        String executeScriptCommand = buildExecScriptCommand(selectedFileForRun);
        String testCommand = buildExecTestSuiteCommand(PUBLIC_FOLDER);

        final String fullCommand = String.join(" ; ", executeScriptCommand, DELIMITER_CMD, testCommand);
        return mapSmokeToExecResult(runner.attachVolumeAndRunBash(workPath.toString(), fullCommand, executionLimits));
    }

    private ExecResult executeSubmission(Path workPath, CodeSubmission submission, Exercise exercise, CodeExecutionLimits executionLimits) throws IOException, DockerException, InterruptedException {
        persistFilesIntoFolder(String.format("%s/%s", workPath.toString(), PUBLIC_FOLDER), submission.getPublicFiles());
        persistFilesIntoFolder(String.format("%s/%s", workPath.toString(), PRIVATE_FOLDER), exercise.getPrivate_files());
        Files.createFile(Paths.get(workPath.toAbsolutePath().toString(), INIT_FILE));

        VirtualFile selectedFileForRun = submission.getPublicFile(submission.getSelectedFile());
        String executeScriptCommand = buildExecScriptCommand(selectedFileForRun);
        String testCommand = buildExecTestSuiteCommand(PRIVATE_FOLDER);

        final String fullCommand = String.join(" ; ", executeScriptCommand, DELIMITER_CMD, testCommand);
        return mapSubmissionToExecResult(runner.attachVolumeAndRunBash(workPath.toString(), fullCommand, executionLimits));
    }

    private ExecResult mapSubmissionToExecResult(RunResult runResult) {
        int indexOfDelimiterStdOut = runResult.getConsole().lastIndexOf(DELIMITER);
        int indexOfDelimiterStdErr = runResult.getStdErr().lastIndexOf(DELIMITER);

        final String trimmedConsoleOutput = runResult.getConsole().substring(0, indexOfDelimiterStdOut);
        final String trimmedTestOutput = runResult.getStdErr().substring(indexOfDelimiterStdErr).replace(DELIMITER + "\n", "");

        return new ExecResult(trimmedConsoleOutput, "", trimmedTestOutput);
    }

    private ExecResult mapSmokeToExecResult(RunResult runResult) {
        int indexOfDelimiterConsole = runResult.getConsole().lastIndexOf(DELIMITER);
        int indexOfDelimiterStdErr = runResult.getStdErr().lastIndexOf(DELIMITER);

        if (!runResult.isTimeout()) {
            final String trimmedConsoleOutput = runResult.getConsole().substring(0, indexOfDelimiterConsole);
            final String trimmedTestOutput = runResult.getStdErr().substring(indexOfDelimiterStdErr).replace(DELIMITER + "\n", "");
            return new ExecResult(trimmedConsoleOutput, trimmedTestOutput, "");
        }
        return new ExecResult(runResult.getConsole(), runResult.getStdErr(), "");
    }

    private void persistFilesIntoFolder(String folderPath, List<VirtualFile> files) {
        if (files == null) {
            logger.debug("No files to persist into " + folderPath);
            return;
        }

        Path path = Paths.get(folderPath);
        logger.debug(path.toAbsolutePath().normalize().toString());

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);

                for (VirtualFile vf : files) {
                    Path file = Files.createFile(Paths.get(folderPath, vf.getName() + "." + vf.getExtension()));
                    Files.writeString(file, vf.getContent());
                }

                if (!Files.exists(Paths.get(folderPath, INIT_FILE))) {
                    Files.createFile(Paths.get(folderPath, INIT_FILE));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildExecScriptCommand(VirtualFile script) {
        final String path = String.format("%s/%s", PUBLIC_FOLDER, script.getNameWithExtension());
        return String.format("python %s", path);
    }

    private String buildExecTestSuiteCommand(String testFolder) {
        return String.format("python -m unittest discover %s -v", testFolder);
    }

    private void removeDirectory(Path path) throws IOException {
        logger.debug("Removing temp directory @ " + path);
        Files
                .walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(this::removeFile);

    }

    private void removeFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error(String.format("Failed to remove file @ %s", path), e);
        }
    }
}
