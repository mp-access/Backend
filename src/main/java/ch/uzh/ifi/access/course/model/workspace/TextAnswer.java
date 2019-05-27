package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TextAnswer extends StudentAnswer {

    private String answer;

    @Builder
    public TextAnswer(String id, int version, String userId, String commitId, String courseId, String assignmentId, String exerciseId, Exercise exercise, LocalDateTime timestamp, String answer) {
        super(id, version, userId, commitId, courseId, assignmentId, exerciseId, exercise, timestamp);
        this.answer = answer;
    }
}
