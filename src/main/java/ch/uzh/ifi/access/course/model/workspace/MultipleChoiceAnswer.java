package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MultipleChoiceAnswer extends StudentAnswer {

    private Set<Integer> choices;

    @Builder
    public MultipleChoiceAnswer(String id, int version, String userId, String commitId, String courseId, String assignmentId, String exerciseId, Exercise exercise, LocalDateTime timestamp, Set<Integer> choices) {
        super(id, version, userId, commitId, courseId, assignmentId, exerciseId, exercise, timestamp);
        this.choices = choices;
    }
}
