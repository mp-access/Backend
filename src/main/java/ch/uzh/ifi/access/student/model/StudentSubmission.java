package ch.uzh.ifi.access.student.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
public abstract class StudentSubmission {

    @Id
    private String id;

    private int version;

    private String userId;

    private String commitId;

    private String exerciseId;

    private Instant timestamp;

    private SubmissionEvaluation result;

    private boolean isInvalid;

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
}
