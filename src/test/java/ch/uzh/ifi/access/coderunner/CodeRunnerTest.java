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
    public void runCode() throws DockerCertificateException, IOException, DockerException, InterruptedException {
        CodeRunner runner = new CodeRunner();

        final String code = "print('Hello, world!')";
        final String expectedOutput = "Hello, world!\n";

        File tempFile = folder.newFile("test.py");
        Files.writeString(tempFile.toPath(), code);

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