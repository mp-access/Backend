package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
@TypeAlias("text")
public class TextSubmission extends StudentSubmission {

    private String answer;

    @Builder
    public TextSubmission(String id, int version, String userId, String commitId, String exerciseId, Exercise exercise, LocalDateTime timestamp, String answer) {
        super(id, version, userId, commitId, exerciseId, exercise, timestamp, null);
        this.answer = answer;
    }

    public void setExercise(Exercise exercise) {
        if (!ExerciseType.text.equals(exercise.getType())) {
            throw new IllegalArgumentException(String.format("Can only set exercise of type 'text'. Got %s", exercise.getType()));
        }
        super.setExercise(exercise);
    }
}
