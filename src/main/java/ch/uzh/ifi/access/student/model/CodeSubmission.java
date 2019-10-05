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

    private int selectedFile;

    private ExecResult console;

    @Builder
    public CodeSubmission(String id, int version, String userId, String commitId, String exerciseId, boolean isGraded, Instant timestamp, boolean isInvalid, boolean isTriggeredReSubmission, List<VirtualFile> publicFiles, int selectedFile, ExecResult console) {
        super(id, version, userId, commitId, exerciseId, isGraded, timestamp, null, isInvalid, isTriggeredReSubmission);
        this.publicFiles = publicFiles;
        this.selectedFile = selectedFile;
        this.console = console;
    }

    public VirtualFile getPublicFile(int index) {
        if (index < publicFiles.size()) {
            return publicFiles.get(index);
        }
        throw new IllegalArgumentException(String.format("Cannot access index %d of public files (size %d)", index, publicFiles.size()));
    }

    @Override
    public StudentSubmission stripSubmissionForReEvaluation() {
        CodeSubmission stripped = new CodeSubmission();
        stripped.setUserId(this.getUserId());
        stripped.setCommitId(this.getCommitId());
        stripped.setExerciseId(this.getExerciseId());
        stripped.setGraded(this.isGraded());
        stripped.setPublicFiles(this.publicFiles);
        stripped.setSelectedFile(this.getSelectedFile());
        return stripped;
    }

}
