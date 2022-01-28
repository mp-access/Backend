package ch.uzh.ifi.access.student.model;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
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
    public MultipleChoiceSubmission(String id, int version, String userId, String commitId, String exerciseId, String courseId, boolean isGraded, Instant timestamp, boolean isInvalid, boolean isTriggeredReSubmission, Set<Integer> choices) {
        super(id, version, userId, commitId, exerciseId, courseId, isGraded, timestamp, null, isInvalid, isTriggeredReSubmission);
        this.choices = choices;
    }

    @Override
    public StudentSubmission stripSubmissionForReEvaluation() {
        MultipleChoiceSubmission stripped = new MultipleChoiceSubmission();
        stripped.setUserId(this.getUserId());
        stripped.setCommitId(this.getCommitId());
        stripped.setExerciseId(this.getExerciseId());
        stripped.setGraded(this.isGraded());
        stripped.setChoices(this.getChoices());
        return stripped;
    }

}
