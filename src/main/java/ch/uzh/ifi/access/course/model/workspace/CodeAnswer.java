package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.VirtualFile;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "answers")
@TypeAlias("code")
public class CodeAnswer extends StudentAnswer {

    private List<VirtualFile> publicFiles;

    private boolean isOfficialSubmission;

    @Builder
    public CodeAnswer(String id, int version, String userId, String commitId, String courseId, String assignmentId, String exerciseId, Exercise exercise, LocalDateTime timestamp, List<VirtualFile> publicFiles, boolean isOfficialSubmission) {
        super(id, version, userId, commitId, courseId, assignmentId, exerciseId, exercise, timestamp);
        this.publicFiles = publicFiles;
        this.isOfficialSubmission = isOfficialSubmission;
    }

    public void setExercise(Exercise exercise) {
        if (!ExerciseType.code.equals(exercise.getType())) {
            throw new IllegalArgumentException(String.format("Can only set exercise of type 'code'. Got %s", exercise.getType()));
        }
        super.setExercise(exercise);
    }
}
