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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SubmissionCodeRunner {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionCodeRunner.class);

    private static final String LOCAL_RUNNER_DIR = "./runner";

    private CodeRunner runner;

    @Autowired
    public SubmissionCodeRunner(CodeRunner runner) {
        this.runner = runner;
    }

    public ExecResult execSubmissionForExercise(CodeSubmission submission, Exercise exercise) throws InterruptedException, DockerException, IOException {

        Path path = Paths.get(LOCAL_RUNNER_DIR + "/" + submission.getId());
        logger.debug(path.toAbsolutePath().normalize().toString());

        persistFilesIntoFolder(path.toString() + "/code", submission.getPublicFiles());
        persistFilesIntoFolder(path.toString() + "/test", exercise.getPrivate_files());

        String[] cmd = {"python", "-m", "unittest", "-v"};
        RunResult res = runner.attachVolumeAndRunCommand(path.toString(), cmd);

        logger.debug("CodeRunner result: " + res.getOutput());

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
                    File f = new File(folderPath, vf.getName() + "." + vf.getExtension());
                    f.createNewFile();
                    Files.writeString(Paths.get(f.getPath()), vf.getContent());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
