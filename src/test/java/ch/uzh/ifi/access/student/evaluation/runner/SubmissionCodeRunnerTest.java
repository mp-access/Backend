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
import java.util.List;

public class SubmissionCodeRunnerTest {

    String longText = "attis nulla, eu vestibulum orci. Aenean a ipsum maximus erat pellentesque accumsan.";
    String filesDir = "./src/test/resources/test_code";
    String solutionsFile = "/solutions.py";
    String solutionsFilePath = Paths.get(filesDir + solutionsFile).toAbsolutePath().normalize().toString();
    String testSuiteFile = "/test_suite.py";
    String testSuiteFilePath = Paths.get(filesDir + testSuiteFile).toAbsolutePath().normalize().toString();
    String initFile = "/__init__.py";
    String initFilePath = Paths.get(filesDir + initFile).toAbsolutePath().normalize().toString();
    private VirtualFile solutions;
    private VirtualFile test;
    private VirtualFile init;

    @BeforeEach
    public void setUp() throws IOException {
        Path path = Paths.get("./runner/s1");
        FileSystemUtils.deleteRecursively(path);
        solutions = new VirtualFile(solutionsFilePath, solutionsFile);
        test = new VirtualFile(testSuiteFilePath, testSuiteFile);
        init = new VirtualFile(initFilePath, initFile);
    }

    @Test
    public void testSubmission() throws DockerCertificateException, DockerException, IOException, InterruptedException {
        Exercise ex = Exercise.builder()
                .id("e1")
                .private_files(Arrays.asList(init, test))
                .type(ExerciseType.code).build();
        CodeSubmission sub = CodeSubmission.builder()
                .id("s1")
                .exerciseId(ex.getId())
                .publicFiles(List.of(init, solutions))
                .selectedFileId(solutions.getId())
                .isGraded(true)
                .build();

        ExecResult result = new SubmissionCodeRunner(new CodeRunner(), new FSHierarchySerializer()).execSubmissionForExercise(sub, ex);
        Assertions.assertTrue(result.getStdout().isEmpty());
        Assertions.assertTrue(result.getTestLog().isEmpty());
        Assertions.assertTrue(result.getEvalLog().toLowerCase().contains("ran 8 tests in"));
    }

    @Test
    public void smokeTest() throws DockerCertificateException, InterruptedException, DockerException, IOException {
        Exercise ex = Exercise.builder()
                .id("e1")
                .private_files(List.of(init, test))
                .type(ExerciseType.code).build();

        CodeSubmission sub = CodeSubmission.builder()
                .id("s1")
                .exerciseId(ex.getId())
                .publicFiles(List.of(init, solutions))
                .selectedFileId(solutions.getId())
                .isGraded(false)
                .build();

        ExecResult result = new SubmissionCodeRunner(new CodeRunner(), new FSHierarchySerializer()).execSubmissionForExercise(sub, ex);
        Assertions.assertFalse(result.getStdout().isEmpty());
        Assertions.assertFalse(result.getTestLog().isEmpty());
        Assertions.assertTrue(result.getEvalLog().isEmpty());
    }

    @Test
    public void limitConsole() {
        RunResult runResult = new RunResult(longText.repeat(100000), null, null, 1000, false, false);
        ExecResult execResult = new SubmissionCodeRunner(null,
                new FSHierarchySerializer()).mapSmokeToExecResult(runResult, null, null);

        Assertions.assertTrue(execResult.getStdout().length() < 100055);
        Assertions.assertTrue(execResult.getStdout().contains("Logs size exceeded limit. Log has been truncated."));
    }

    @Test
    public void limitEvalLog() {
        String stdErr = longText.repeat(1000) + "======================----------------------======================" + longText.repeat(10000);
        RunResult runResult = new RunResult(null, null, stdErr, 1000, false, false);
        ExecResult execResult = new SubmissionCodeRunner(null, new FSHierarchySerializer()).mapSubmissionToExecResult(runResult);
        Assertions.assertTrue(execResult.getEvalLog().length() < 100075);
        Assertions.assertTrue(execResult.getEvalLog().contains("Logs size exceeded limit. Beginning of log has been truncated"));
    }

    @Test
    public void evalMissesDelimiterReturnsEmpty() {
        RunResult runResult = new RunResult(null, null, longText.repeat(100000), 1000, false, false);
        ExecResult execResult = new SubmissionCodeRunner(null, new FSHierarchySerializer()).mapSubmissionToExecResult(runResult);
        Assertions.assertTrue(execResult.getEvalLog().isEmpty());
    }

    @Test
    public void emptyEval() {
        RunResult runResult = new RunResult("SomeText", null, "", 1000, false, false);
        ExecResult execResult = new SubmissionCodeRunner(null, new FSHierarchySerializer()).mapSubmissionToExecResult(runResult);
        Assertions.assertTrue(execResult.getStdout().isEmpty());
        Assertions.assertTrue(execResult.getEvalLog().isEmpty());
    }
}