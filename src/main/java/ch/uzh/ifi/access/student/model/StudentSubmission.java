package ch.uzh.ifi.access.student.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
@CompoundIndexes({
        @CompoundIndex(name = "user_and_exercise", def = "{'userId' : 1, 'exerciseId': -1}")
})
public abstract class StudentSubmission {

    @Id
    private String id;

    private int version;

    @Indexed
    private String userId;

    private String commitId;

    @Indexed(direction = IndexDirection.DESCENDING)
    private String exerciseId;

    private boolean isGraded;

    private Instant timestamp;

    private SubmissionEvaluation result;

    private boolean isInvalid;

    private boolean isTriggeredReSubmission;

    /**
     * Checks whether the given user id matches this submission's id.
     *
     * @return If this submission's user id is null, always return false, otherwise return true if ids match.
     */
    public boolean userIdMatches(String otherId) {
        return this.userId != null && this.userId.equals(otherId);
    }

    /**
     * Returns the final score of this submission.
     *
     * @return 0 if submission was not graded (result == null) or score
     */
    public double getScore() {
        return result == null ? 0.0 : result.getScore();
    }

    public abstract StudentSubmission stripSubmissionForReEvaluation();

}
