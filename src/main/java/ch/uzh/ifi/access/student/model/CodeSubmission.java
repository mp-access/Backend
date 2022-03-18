package ch.uzh.ifi.access.student.model;

import ch.uzh.ifi.access.course.model.VirtualFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String selectedFileId;

    private ExecResult console;

    @Builder
    public CodeSubmission(String id, int version, String userId, String commitId, String exerciseId, String courseId, boolean isGraded, Instant timestamp, boolean isInvalid, boolean isTriggeredReSubmission, List<VirtualFile> publicFiles, String selectedFileId, ExecResult console) {
        super(id, version, userId, commitId, exerciseId, courseId, isGraded, timestamp, null, isInvalid, isTriggeredReSubmission);
        this.publicFiles = publicFiles;
        this.selectedFileId = selectedFileId;
        this.console = console;
    }

    @JsonIgnore
    public VirtualFile getSelectedFile() {
        return getPublicFile(selectedFileId);
    }

    private VirtualFile getPublicFile(String id) {
        if (("-1").equals(id)) {
            return publicFiles.get(0);
        } else {
            return publicFiles.stream().filter(file -> file.getId().equals(id)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Cannot find file with id %s in public folder", id)));
        }
    }

    @Override
    public StudentSubmission stripSubmissionForReEvaluation() {
        CodeSubmission stripped = new CodeSubmission();
        stripped.setUserId(this.getUserId());
        stripped.setCommitId(this.getCommitId());
        stripped.setExerciseId(this.getExerciseId());
        stripped.setCourseId(this.getCourseId());
        stripped.setGraded(this.isGraded());
        stripped.setPublicFiles(this.publicFiles);
        stripped.setSelectedFileId(this.getSelectedFileId());
        return stripped;
    }

}
