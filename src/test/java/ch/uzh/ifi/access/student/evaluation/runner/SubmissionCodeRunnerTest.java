package ch.uzh.ifi.access.student.evaluation.runner;

import ch.uzh.ifi.access.coderunner.CodeRunner;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class SubmissionCodeRunnerTest {

    private VirtualFile src;
    private VirtualFile test;
    private VirtualFile init;

    @Before
    public void setUp() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get("./runner/s1"));

        Path psrc = Paths.get("./src/test/resources/test_code/solutioncode.py");
         src = new VirtualFile(psrc.toAbsolutePath().normalize().toString(), "");

        Path ptest = Paths.get("./src/test/resources/test_code/test_suite.py");
        test = new VirtualFile(ptest.toAbsolutePath().normalize().toString(), "");

        init = new VirtualFile();
        init.setName("__init__");
        init.setExtension("py");
        init.setContent("");
    }

    @Test
    public void test() throws DockerCertificateException, InterruptedException, DockerException, IOException {
        Exercise ex = Exercise.builder()
                .id("e1")
                .private_files(Arrays.asList(init, test))
                .type(ExerciseType.code).build();

        CodeSubmission sub = CodeSubmission.builder()
                .id("s1")
                .exerciseId(ex.getId())
                .publicFiles(Arrays.asList(init, src))
                .build();

        ExecResult result = new SubmissionCodeRunner(new CodeRunner()).execSubmissionForExercise(sub, ex);
        Assertions.assertThat(result.getStdout()).isNotEmpty();
    }

}