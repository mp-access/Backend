package ch.uzh.ifi.access.student.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.Instant;

@SuppressWarnings("unused")
@Value
@Data
@Builder
public class SubmissionEvaluation {

    public static SubmissionEvaluation NO_SUBMISSION = new SubmissionEvaluation(0, 0, 0, Instant.MIN);

    private int correctPoints;

    private int maxPoints;

    private int score;

    private Instant timestamp;

    @JsonProperty
    public boolean hasSubmitted() {
        return !NO_SUBMISSION.equals(this);
    }
}
