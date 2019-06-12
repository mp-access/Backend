package ch.uzh.ifi.access.coderunner;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class CodeRunnerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder(new File(System.getProperty("user.dir")));

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

        RunResult runResult1 = runner.attachVolumeAndRunCommand(folder.getRoot().getPath(), cmd1);
        RunResult runResult2 = runner.attachVolumeAndRunCommand(folder.getRoot().getPath(), cmd2);

        Assertions.assertThat(runResult1.getOutput()).isEqualTo(expectedOutput1);
        Assertions.assertThat(runResult2.getOutput()).isEqualTo(expectedOutput2);


        String[] cmd3 = new String[]{"ls", "-l"};
        RunResult runResult3 = runner.attachVolumeAndRunCommand(folder.getRoot().getPath(), cmd3);
        Assertions.assertThat(runResult3.getOutput()).contains(tempFile1.getName());
        Assertions.assertThat(runResult3.getOutput()).contains(tempFile2.getName());
    }

    @Test
    public void runCode() throws DockerCertificateException, IOException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        final String code = "print('Hello, world!')";
        final String expectedOutput = "Hello, world!\n";

        File tempFile = createTempFileWithContent(code, "test.py");

        RunResult runResult = runner.runCode(folder.getRoot().getPath(), tempFile.getName());

        Assertions.assertThat(runResult.getOutput()).isEqualTo(expectedOutput);
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

        createTempFileWithContent(dependencyCode, "/a.py");
        File main = createTempFileWithContent(mainCode, "/test.py");

        RunResult runResult = runner.runCode(folder.getRoot().getPath(), main.getName());

        Assertions.assertThat(runResult.getOutput()).isEqualTo(expectedOutput);
    }

    private File createTempFileWithContent(String content, String filename) throws IOException {
        File tempFile = folder.newFile(filename);
        Files.writeString(tempFile.toPath(), content);
        return tempFile;
    }
}