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
@TypeAlias("singleChoice")
public class SingleChoiceSubmission extends StudentSubmission {

    private Integer choice;

    @Builder
    public SingleChoiceSubmission(String id, int version, String userId, String commitId, String exerciseId, boolean isGraded, Instant timestamp, Integer choice, boolean isInvalid, boolean isTriggeredReSubmission) {
        super(id, version, userId, commitId, exerciseId, isGraded, timestamp, null, isInvalid, isTriggeredReSubmission);
        this.choice = choice;
    }

    @Override
    public StudentSubmission stripSubmissionForReEvaluation() {
        SingleChoiceSubmission stripped = new SingleChoiceSubmission();
        stripped.setUserId(this.getUserId());
        stripped.setCommitId(this.getCommitId());
        stripped.setExerciseId(this.getExerciseId());
        stripped.setGraded(this.isGraded());
        stripped.setChoice(this.getChoice());
        return stripped;
    }

}
