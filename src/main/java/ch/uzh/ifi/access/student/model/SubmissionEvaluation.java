package ch.uzh.ifi.access.student.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.Instant;

@SuppressWarnings("unused")
@Value
public class SubmissionEvaluation {

    public static SubmissionEvaluation NO_SUBMISSION = new SubmissionEvaluation(0, Instant.MIN);

    private int score;

    private Instant timestamp;

    @JsonProperty
    public boolean hasSubmitted() {
        return !NO_SUBMISSION.equals(this);
    }
}
