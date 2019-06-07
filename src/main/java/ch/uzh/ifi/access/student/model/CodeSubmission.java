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

    private boolean isGraded;

    @Builder
    public CodeSubmission(String id, int version, String userId, String commitId, String exerciseId, Instant timestamp, List<VirtualFile> publicFiles, boolean isGraded) {
        super(id, version, userId, commitId, exerciseId, timestamp, null);
        this.publicFiles = publicFiles;
        this.isGraded = isGraded;
    }
}
