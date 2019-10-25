package ch.uzh.ifi.access.student.evaluation.runner;

import ch.uzh.ifi.access.course.model.VirtualFile;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FSHierarchySerializerTest {

    private final String root = "./tmp/";

    private FSHierarchySerializer serializer;

    @Test
    public void persistFilesIntoFolder() {
        Path path = Paths.get("./src/test/resources/test_code/solutioncode.py");
        VirtualFile file1 = new VirtualFile(path.toAbsolutePath().normalize().toString(), "/test_code/solutioncode.py");

        path = Paths.get("./src/test/resources/test_code/test_suite.py");
        VirtualFile file2 = new VirtualFile(path.toAbsolutePath().normalize().toString(), "/test_code/test_suite.py");

        path = Paths.get("./src/test/resources/test_code/sub/sub.py");
        VirtualFile sub = new VirtualFile(path.toAbsolutePath().normalize().toString(), "/test_code/sub/sub.py");

        serializer = new FSHierarchySerializer();
        serializer.persistFilesIntoFolder(root, List.of(sub, file1, file2));

        Assertions.assertThat(Paths.get("./tmp")).exists();
        Assertions.assertThat(Paths.get("./tmp/test_code/" + file1.getNameWithExtension())).exists();
        Assertions.assertThat(Paths.get("./tmp/test_code/" + file2.getNameWithExtension())).exists();
        Assertions.assertThat(Paths.get("./tmp/test_code/__init__.py")).exists();
        Assertions.assertThat(Paths.get("./tmp/test_code/sub/" + sub.getNameWithExtension())).exists();
        Assertions.assertThat(Paths.get("./tmp/test_code/sub/__init__.py")).exists();
    }

    @After
    public void tearDown() {
        try {
            serializer.removeDirectory(Path.of(root));
        } catch (Exception ignored) {

        }
    }
}