package ch.uzh.ifi.access.student.evaluation.runner;

import ch.uzh.ifi.access.coderunner.CodeRunner;
import ch.uzh.ifi.access.coderunner.RunResult;
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

    private CodeRunner runner;

    @Autowired
    public SubmissionCodeRunner(CodeRunner runner) {
        this.runner = runner;
    }

    public ExecResult execSubmissionForExercise(CodeSubmission submission, Exercise exercise) throws InterruptedException, DockerException, IOException {

        Path path = Paths.get(LOCAL_RUNNER_DIR + "/" + submission.getId());
        logger.debug(path.toAbsolutePath().normalize().toString());

        persistFilesIntoFolder(String.format("%s/%s", path.toString(), PUBLIC_FOLDER), submission.getPublicFiles());
        persistFilesIntoFolder(String.format("%s/%s", path.toString(), PRIVATE_FOLDER), exercise.getPrivate_files());
        Files.createFile(Paths.get(path.toAbsolutePath().toString(), "__init__.py"));

        String[] cmd = {"python", "-m", "unittest", "discover", PRIVATE_FOLDER, "-v"};
        RunResult res = runner.attachVolumeAndRunCommand(path.toString(), cmd);

        logger.debug("CodeRunner result: " + res.getOutput());

        logger.debug("Removing temp directory @ " + path);
        removeDirectory(path);

        return new ExecResult(res.getOutput(), res.getStdErr());
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

    private void removeDirectory(Path path) throws IOException {
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
