package ch.uzh.ifi.access.student.model;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
@TypeAlias("text")
public class TextSubmission extends StudentSubmission {

    private String answer;

    @Builder
    public TextSubmission(String id, int version, String userId, String commitId, String exerciseId, boolean isGraded, Instant timestamp, String answer, boolean isInvalid, boolean isTriggeredReSubmission) {
        super(id, version, userId, commitId, exerciseId, isGraded, timestamp, null, isInvalid, isTriggeredReSubmission);
        this.answer = answer;
    }

    @Override
    public StudentSubmission stripSubmissionForReEvaluation() {
        TextSubmission stripped = new TextSubmission();
        stripped.setUserId(this.getUserId());
        stripped.setCommitId(this.getCommitId());
        stripped.setExerciseId(this.getExerciseId());
        stripped.setGraded(this.isGraded());
        stripped.setAnswer(this.getAnswer());
        return stripped;
    }

}
