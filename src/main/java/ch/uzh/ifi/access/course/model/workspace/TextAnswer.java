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
@Document(collection = "answers")
@TypeAlias("text")
public class TextAnswer extends StudentAnswer {

    private String answer;

    @Builder
    public TextAnswer(String id, int version, String userId, String commitId, String courseId, String assignmentId, String exerciseId, Exercise exercise, LocalDateTime timestamp, String answer) {
        super(id, version, userId, commitId, courseId, assignmentId, exerciseId, exercise, timestamp);
        this.answer = answer;
    }

    public void setExercise(Exercise exercise) {
        if (!ExerciseType.text.equals(exercise.getType())) {
            throw new IllegalArgumentException(String.format("Can only set exercise of type 'text'. Got %s", exercise.getType()));
        }
        super.setExercise(exercise);
    }
}
