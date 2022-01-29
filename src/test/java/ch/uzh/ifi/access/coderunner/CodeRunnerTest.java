package ch.uzh.ifi.access.coderunner;

import ch.uzh.ifi.access.course.model.CodeExecutionLimits;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class CodeRunnerTest {

    @TempDir
    Path tempDir;

    @Test
    public void runCommand() throws DockerCertificateException, IOException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        final String code1 = "print('Hello 1!')";
        final String expectedOutput1 = "Hello 1!\n";

        final String code2 = "print('Hello 2!')";
        final String expectedOutput2 = "Hello 2!\n";

        File tempFile1 = createTempFileWithContent(code1, "test1.py");
        File tempFile2 = createTempFileWithContent(code2, "test2.py");

        String[] cmd1 = new String[]{"python", tempFile1.getName()};
        String[] cmd2 = new String[]{"python", tempFile2.getName()};

        RunResult runResult1 = runner.attachVolumeAndRunCommand(tempDir.toAbsolutePath().toString(), cmd1, CodeExecutionLimits.TESTING_UNLIMITED);
        RunResult runResult2 = runner.attachVolumeAndRunCommand(tempDir.toAbsolutePath().toString(), cmd2, CodeExecutionLimits.TESTING_UNLIMITED);

        Assertions.assertEquals(expectedOutput1, runResult1.getConsole());
        Assertions.assertEquals(expectedOutput2, runResult2.getConsole());


        String[] cmd3 = new String[]{"ls", "-l"};
        RunResult runResult3 = runner.attachVolumeAndRunCommand(tempDir.toAbsolutePath().toString(), cmd3, CodeExecutionLimits.TESTING_UNLIMITED);
        Assertions.assertTrue(runResult3.getConsole().contains(tempFile1.getName()));
        Assertions.assertTrue(runResult3.getConsole().contains(tempFile2.getName()));
    }

    @Test
    public void runCode() throws DockerCertificateException, IOException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        final String code = "print('Hello, world!')";
        final String expectedOutput = "Hello, world!\n";

        File tempFile = createTempFileWithContent(code, "test.py");

        RunResult runResult = runner.runPythonCode(tempDir.toAbsolutePath().toString(), tempFile.getName(), CodeExecutionLimits.TESTING_UNLIMITED);

        Assertions.assertEquals(expectedOutput, runResult.getConsole());
    }

    @Test
    public void runMultipleFilesCode() throws IOException, DockerCertificateException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        String dependencyCode = "class A:\n" +
                "    def __init__(self, a):\n" +
                "        self.a = a\n" +
                "\n" +
                "    def __str__(self):\n" +
                "        return \"This is the value of a: \" + str(self.a)";

        String mainCode = "from a import A\n" +
                "if __name__ == \"__main__\":\n" +
                "    t = A(42)\n" +
                "    print(t)\n";

        String expectedOutput = "This is the value of a: 42\n";

        createTempFileWithContent(dependencyCode, "a.py");
        File main = createTempFileWithContent(mainCode, "test.py");

        RunResult runResult = runner.runPythonCode(tempDir.toAbsolutePath().toString(), main.getName(), CodeExecutionLimits.TESTING_UNLIMITED);

        Assertions.assertEquals(expectedOutput, runResult.getConsole());
    }

    private File createTempFileWithContent(String content, String filename) throws IOException {
        Path tempFile = Files.createFile(tempDir.resolve(filename));
        Files.writeString(tempFile, content);
        return tempFile.toFile();
    }

    @Test
    public void runMultipleCommands() throws DockerCertificateException, IOException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        final String code1 = "print('Hello 1!')";
        final String code2 = "print('Hello 2!')";

        File tempFile1 = createTempFileWithContent(code1, "test1.py");
        File tempFile2 = createTempFileWithContent(code2, "test2.py");

        final String delimiter = "======";
        final String expectedOutput = String.format("Hello 1!\n%s\nHello 2!\n", delimiter);

        RunResult runResult1 = runner.attachVolumeAndRunBash(tempDir.toAbsolutePath().toString(), String.format("python %s && echo \"%s\" && python %s", tempFile1.getName(), delimiter, tempFile2.getName()), CodeExecutionLimits.TESTING_UNLIMITED);

        Assertions.assertEquals(expectedOutput, runResult1.getConsole());
    }

    @Test
    public void runOutOfMemory() throws DockerCertificateException, IOException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        String mainCode = "some_array = []\n"+
                "while True:\n" +
                "    some_array.append('Lorem ipsum dolor sit amet, consectetur adipiscing elit.')\n";


        File main = createTempFileWithContent(mainCode, "test.py");

        RunResult runResult = runner.runPythonCode(tempDir.toAbsolutePath().toString(), main.getName(), new CodeExecutionLimits(1, 1, 10 * 1000, false, false));
        Assertions.assertTrue(runResult.getConsole().isEmpty());
    }

    @Test
    public void runIntoTimeout() throws DockerCertificateException, IOException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        String mainCode = "while True:\n" +
                "    x = 1+1\n";

        File main = createTempFileWithContent(mainCode, "test.py");

        RunResult runResult = runner.runPythonCode(tempDir.toAbsolutePath().toString(), main.getName(), new CodeExecutionLimits(64, 1, 1000, false, false));

        Assertions.assertTrue(runResult.isTimeout());
        Assertions.assertTrue(runResult.getConsole().startsWith("Timeout"));
    }

}