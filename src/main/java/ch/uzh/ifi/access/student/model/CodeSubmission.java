package ch.uzh.ifi.access.student.model;

import ch.uzh.ifi.access.course.model.VirtualFile;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
@TypeAlias("code")
public class CodeSubmission extends StudentSubmission {

    private List<VirtualFile> publicFiles;

    private boolean graded;

    private int selectedFile;

    private ExecResult console;

    @Builder
    public CodeSubmission(String id, int version, String userId, String commitId, String exerciseId, Instant timestamp, List<VirtualFile> publicFiles, boolean graded, int selectedFile, ExecResult console, boolean isInvalid) {
        super(id, version, userId, commitId, exerciseId, timestamp, null, isInvalid);
        this.publicFiles = publicFiles;
        this.graded = graded;
        this.selectedFile = selectedFile;
        this.console = console;
    }

    public VirtualFile getPublicFile(int index) {
        if (index < publicFiles.size()) {
            return publicFiles.get(index);
        }
        throw new IllegalArgumentException(String.format("Cannot access index %d of public files (size %d)", index, publicFiles.size()));
    }
}
