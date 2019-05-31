package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
@TypeAlias("multipleChoice")
public class MultipleChoiceSubmission extends StudentSubmission {

    private Set<Integer> choices;

    @Builder
    public MultipleChoiceSubmission(String id, int version, String userId, String commitId, String exerciseId, Exercise exercise, LocalDateTime timestamp, Set<Integer> choices) {
        super(id, version, userId, commitId, exerciseId, exercise, timestamp, null);
        this.choices = choices;
    }

    public void setExercise(Exercise exercise) {
        if (!ExerciseType.multipleChoice.equals(exercise.getType())) {
            throw new IllegalArgumentException(String.format("Can only set exercise of type 'multipleChoice'. Got %s", exercise.getType()));
        }
        super.setExercise(exercise);
    }
}
