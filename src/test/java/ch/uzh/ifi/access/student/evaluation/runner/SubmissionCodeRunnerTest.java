package ch.uzh.ifi.access.student.evaluation.runner;

import ch.uzh.ifi.access.coderunner.CodeRunner;
import ch.uzh.ifi.access.coderunner.RunResult;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SubmissionCodeRunnerTest {

    private VirtualFile src;
    private VirtualFile test;
    private VirtualFile init;

    @BeforeEach
    public void setUp() throws IOException {
        Path path = Paths.get("./runner/s1");
        FileSystemUtils.deleteRecursively(path);

        Path psrc = Paths.get("./src/test/resources/test_code/solutioncode.py");
        src = new VirtualFile(psrc.toAbsolutePath().normalize().toString(), "/solutioncode.py");

        Path ptest = Paths.get("./src/test/resources/test_code/test_suite.py");
        test = new VirtualFile(ptest.toAbsolutePath().normalize().toString(), "/test_suite.py");

        Path pInit = Paths.get("./src/test/resources/test_code/__init__.py");
        init = new VirtualFile(pInit.toAbsolutePath().normalize().toString(), "/__init__.py");
    }

    @Test
    public void testSubmission() throws DockerCertificateException, InterruptedException, DockerException, IOException {
        Exercise ex = Exercise.builder()
                .id("e1")
                .private_files(Arrays.asList(init, test))
                .type(ExerciseType.code).build();

        CodeSubmission sub = CodeSubmission.builder()
                .id("s1")
                .exerciseId(ex.getId())
                .publicFiles(Arrays.asList(init, src))
                .selectedFileId(src.getId())
                .isGraded(true)
                .build();

        ExecResult result = new SubmissionCodeRunner(new CodeRunner(), new FSHierarchySerializer()).execSubmissionForExercise(sub, ex);
        Assertions.assertTrue(result.getStdout().isEmpty());
        Assertions.assertTrue(result.getTestLog().isEmpty());
        Assertions.assertTrue(result.getEvalLog().toLowerCase().contains("ran 8 tests in"));
    }


    @Test
    public void testSmoketest() throws DockerCertificateException, InterruptedException, DockerException, IOException {
        Exercise ex = Exercise.builder()
                .id("e1")
                .private_files(Arrays.asList(init, test))
                .type(ExerciseType.code).build();

        CodeSubmission sub = CodeSubmission.builder()
                .id("s1")
                .exerciseId(ex.getId())
                .publicFiles(Arrays.asList(init, src))
                .selectedFileId(src.getId())
                .isGraded(false)
                .build();

        ExecResult result = new SubmissionCodeRunner(new CodeRunner(), new FSHierarchySerializer()).execSubmissionForExercise(sub, ex);
        Assertions.assertFalse(result.getStdout().isEmpty());
        Assertions.assertFalse(result.getTestLog().isEmpty());
        Assertions.assertTrue(result.getEvalLog().isEmpty());
    }


    @Test
    public void limitConsole() {
        StringBuffer sb = new StringBuffer();
        for(int i=0 ; i < 100000; i++){
            sb.append("attis nulla, eu vestibulum orci. Aenean a ipsum maximus erat pellentesque accumsan.");
        }
        RunResult rr = new RunResult(sb.toString(), null, null, 1000, false, false);

        ExecResult execResult = new SubmissionCodeRunner(null, new FSHierarchySerializer()).mapSmokeToExecResult(rr, null, null);

        Assertions.assertTrue(execResult.getStdout().length() < 100055);
        Assertions.assertTrue(execResult.getStdout().contains("Logs size exceeded limit. Log has been truncated."));
    }


    @Test
    public void limitEvallog() {
        StringBuffer sb = new StringBuffer();
        for(int i=0 ; i < 1000; i++){
            sb.append("attis nulla, eu vestibulum orci. Aenean a ipsum maximus erat pellentesque accumsan.");
        }
        sb.append("======================----------------------======================");
        for(int i=0 ; i < 10000; i++){
            sb.append("attis nulla, eu vestibulum orci. Aenean a ipsum maximus erat pellentesque accumsan.");
        }
        RunResult rr = new RunResult(null, null, sb.toString(), 1000, false, false);

        ExecResult execResult = new SubmissionCodeRunner(null, new FSHierarchySerializer()).mapSubmissionToExecResult(rr);

        Assertions.assertTrue(execResult.getEvalLog().length() < 100075);
        Assertions.assertTrue(execResult.getEvalLog().contains("Logs size exceeded limit. Beginning of log has been truncated "));
    }

    @Test
    public void evalMissesDelimterReturnsEmpty() {
        StringBuffer sb = new StringBuffer();
        for(int i=0 ; i < 100000; i++){
            sb.append("attis nulla, eu vestibulum orci. Aenean a ipsum maximus erat pellentesque accumsan.");
        }
        RunResult rr = new RunResult(null, null, sb.toString(), 1000, false, false);

        ExecResult execResult = new SubmissionCodeRunner(null, new FSHierarchySerializer()).mapSubmissionToExecResult(rr);

        Assertions.assertTrue(execResult.getEvalLog().isEmpty());
    }

    @Test
    public void emptyEval() {

        RunResult rr = new RunResult("SomeText", null, "", 1000, false, false);

        ExecResult execResult = new SubmissionCodeRunner(null, new FSHierarchySerializer()).mapSubmissionToExecResult(rr);

        Assertions.assertTrue(execResult.getStdout().isEmpty());
        Assertions.assertTrue(execResult.getEvalLog().isEmpty());
    }

}