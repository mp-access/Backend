package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
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

    private ExecResults execResults;

    @Builder
    public CodeSubmission(String id, int version, String userId, String commitId, String exerciseId, Exercise exercise, Instant timestamp, List<VirtualFile> publicFiles, boolean isGraded) {
        super(id, version, userId, commitId, exerciseId, exercise, timestamp, null);
        this.publicFiles = publicFiles;
        this.isGraded = isGraded;
    }

    public void setExercise(Exercise exercise) {
        if (exercise == null) {
            return;
        }

        if (!ExerciseType.code.equals(exercise.getType())) {
            throw new IllegalArgumentException(String.format("Can only set exercise of type 'code'. Got %s", exercise.getType()));
        }
        super.setExercise(exercise);
    }
}
