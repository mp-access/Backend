package ch.uzh.ifi.access.student.evaluation.runner;

import ch.uzh.ifi.access.course.model.VirtualFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FSHierarchySerializerTest {

    private final String root = "./tmp/";

    private FSHierarchySerializer serializer;

    @Test
    public void persistFilesIntoFolder() {
        Path path = Paths.get("./src/test/resources/test_code/solutions.py");
        VirtualFile file1 = new VirtualFile(path.toAbsolutePath().normalize().toString(), "/test_code/solutions.py");

        path = Paths.get("./src/test/resources/test_code/test_suite.py");
        VirtualFile file2 = new VirtualFile(path.toAbsolutePath().normalize().toString(), "/test_code/test_suite.py");

        path = Paths.get("./src/test/resources/test_code/sub/sub.py");
        VirtualFile sub = new VirtualFile(path.toAbsolutePath().normalize().toString(), "/test_code/sub/sub.py");

        serializer = new FSHierarchySerializer();
        serializer.persistFilesIntoFolder(root, List.of(sub, file1, file2));

        Assertions.assertTrue(Paths.get("./tmp").toFile().exists());
        Assertions.assertTrue(Paths.get("./tmp/test_code/" + file1.getNameWithExtension()).toFile().exists());
        Assertions.assertTrue(Paths.get("./tmp/test_code/" + file2.getNameWithExtension()).toFile().exists());
        Assertions.assertTrue(Paths.get("./tmp/test_code/__init__.py").toFile().exists());
        Assertions.assertTrue(Paths.get("./tmp/test_code/sub/" + sub.getNameWithExtension()).toFile().exists());
        Assertions.assertTrue(Paths.get("./tmp/test_code/sub/__init__.py").toFile().exists());
    }

    @AfterEach
    public void tearDown() {
        try {
            serializer.removeDirectory(Path.of(root));
        } catch (Exception ignored) {

        }
    }
}