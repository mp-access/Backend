package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CodeAnswer extends StudentAnswer {

    private List<VirtualFile> publicFiles;

    private boolean isOfficialSubmission;

    @Builder
    public CodeAnswer(String id, int version, String userId, String commitId, String courseId, String assignmentId, String exerciseId, Exercise exercise, LocalDateTime timestamp, List<VirtualFile> publicFiles, boolean isOfficialSubmission) {
        super(id, version, userId, commitId, courseId, assignmentId, exerciseId, exercise, timestamp);
        this.publicFiles = publicFiles;
        this.isOfficialSubmission = isOfficialSubmission;
    }
}
